#include "launcher.h"
#include <assert.h>
#include <curl/curl.h>
#include <gtk/gtk.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

// Structure with options that are used for invoking the JVM.
typedef struct _programData {
	GtkWidget *jvmOptions;
	GtkWidget *classpaths;
	// Used for the progressbar
	GtkWidget *window;
	// Used as shortcut.
	GtkWidget *table;
} * ProgramData;
// Structure that allows to add an entry to the list.
typedef struct _dataStruct {
	GtkWidget *listWidget;
	GtkWidget *inputDialog;
	int index;
} * DataStruct;

// 0 for downloading, 1 for extracting
static volatile int status;
// counts[0] for classpaths, counts[1] for options
static volatile int counts[2];
// If status==1
static volatile const char *fileName;
static volatile int currentFileIndex;
static volatile int totalCount;
// If status==0
static volatile curl_off_t dlnowG;
static volatile curl_off_t dltotalG;
// Only repaint the whole thing, if there are changes
static volatile int hasChange = 0;
// We can exit the custom event loop
static volatile int completed;

static int onDownloadProgress(void *, curl_off_t, curl_off_t, curl_off_t,
							  curl_off_t);
static void destroyWindow(GtkWidget *, gpointer);
static void onExtractFunc(void *, const char *, int, int);
static int onDownloadProgress(void *, curl_off_t, curl_off_t, curl_off_t,
							  curl_off_t);
static void initialize_window(GtkWidget *);
static GtkWidget *newInputField(const gchar *);
static void insertNewElement(GtkWidget *, gpointer);
static GtkWidget *newInput(GtkWidget *, const gchar *, int);
void *eventFunc(void *);
static void eventLoop(GtkWidget *, GtkWidget *);
static Configuration buildFromProgramData(ProgramData);

static void destroyWindow(GtkWidget *widget, gpointer data) { gtk_main_quit(); }

static void startConquer(ProgramData);
static void onExtractFunc(void *ptr, const char *s, int current, int count) {
	status = 1;
	fileName = s;
	currentFileIndex = current;
	totalCount = count;
	hasChange = 1;
}
static int onDownloadProgress(void *clientp, curl_off_t dltotal,
							  curl_off_t dlnow, curl_off_t ultotal,
							  curl_off_t ulnow) {
	status = 0;
	dlnowG = dlnow;
	dltotalG = dltotal;
	hasChange = 1;
	return 0;
}
static void initialize_window(GtkWidget *window) {
	gtk_window_set_title(GTK_WINDOW(window), "Conquer - 1.0.0");
	gtk_window_set_default_size(GTK_WINDOW(window), 400, 200);
	g_signal_connect(window, "destroy", G_CALLBACK(destroyWindow), NULL);
}
static GtkWidget *newInputField(const gchar *s) {
	GtkWidget *view = gtk_entry_new();
	GtkEntryBuffer *buffer = gtk_entry_get_buffer(GTK_ENTRY(view));
	gtk_entry_buffer_set_text(buffer, s, -1);
	return view;
}
static void insertNewElement(GtkWidget *widget, gpointer data) {
	DataStruct ds = data;
	GtkEntryBuffer *buffer = gtk_entry_get_buffer(GTK_ENTRY(ds->inputDialog));
	if (gtk_entry_buffer_get_length(buffer)) {
		GtkWidget *item =
			gtk_accel_label_new(gtk_entry_buffer_get_text(buffer));
		gtk_list_box_insert(GTK_LIST_BOX(ds->listWidget), item, -1);
		gtk_entry_buffer_delete_text(buffer, 0, -1);
		gtk_widget_queue_draw(ds->listWidget);
		gtk_widget_show_all(ds->listWidget);
		counts[ds->index]++;
	}
}
static GtkWidget *newInput(GtkWidget *list, const gchar *label, int index) {
	GtkWidget *table = gtk_grid_new();
	GtkWidget *inputDialog = newInputField("");
	gtk_grid_attach(GTK_GRID(table), inputDialog, 0, 0, 1, 1);
	GtkWidget *button = gtk_button_new_with_label(label);
	gtk_grid_attach(GTK_GRID(table), button, 1, 0, 1, 1);
	DataStruct ds = calloc(1, sizeof(*ds));
	assert(ds);
	ds->index = index;
	ds->listWidget = list;
	ds->inputDialog = inputDialog;
	g_signal_connect(button, "clicked", G_CALLBACK(insertNewElement), ds);
	gtk_widget_set_hexpand(table, TRUE);
	gtk_widget_set_hexpand(inputDialog, TRUE);
	gtk_widget_set_hexpand(button, TRUE);
	return table;
}
void *eventFunc(void *data) {
	completed = 0;
	downloadJDK(NULL, onDownloadProgress, onExtractFunc);
	completed = 1;
	return data;
}

static void eventLoop(GtkWidget *window, GtkWidget *progressBar) {
	char *string = calloc(1, 250);
	// Custom eventloop
	while (!completed) {
		gtk_main_iteration();
		if (hasChange) {
			if (status == 0) {
				double percentage =
					dltotalG != 0 ? ((double)dlnowG) / dltotalG : 0;
				sprintf(string, "Downloaded %ld of %ld bytes (%.2f%%)", dlnowG,
						dltotalG, percentage * 100);
				gtk_progress_bar_set_fraction(GTK_PROGRESS_BAR(progressBar),
											  percentage);
			} else {
				assert(status == 1);
				double percentage = ((double)currentFileIndex) / totalCount;
				sprintf(string, "%s (%d/%d)", fileName, currentFileIndex,
						totalCount);
				gtk_progress_bar_set_fraction(GTK_PROGRESS_BAR(progressBar),
											  percentage);
			}
			gtk_progress_bar_set_text(GTK_PROGRESS_BAR(progressBar), string);
			gtk_widget_show_all(window);
			gtk_widget_show_all(progressBar);
			hasChange = 0;
		}
	}
	free(string);
}
static void onStartPressed(GtkWidget *widget, gpointer data) {
	ProgramData pd = data;
	// TODO: Crashdialog
	gtk_widget_set_sensitive(widget, FALSE);
	if (hasToDownloadJava()) {
		GtkWidget *progressBar = gtk_progress_bar_new();
		gtk_progress_bar_set_show_text(GTK_PROGRESS_BAR(progressBar), TRUE);
		gtk_widget_unrealize(
			GTK_WIDGET(gtk_grid_get_child_at(GTK_GRID(pd->table), 0, 6)));
		gtk_grid_attach(GTK_GRID(pd->table), progressBar, 0, 6, 1, 1);
		gtk_widget_show_all(pd->table);
		gtk_widget_set_hexpand(progressBar, TRUE);
		gtk_widget_show_all(pd->window);
		pthread_t thread;
		if (pthread_create(&thread, NULL, eventFunc, NULL)) {
			perror("pthread_create");
			exit(-1);
		}
		eventLoop(pd->window, progressBar);
		pthread_join(thread, NULL);
	}
	gtk_widget_hide(pd->window);
	startConquer(pd);
	free(pd);
	gtk_main_quit();
}
static Configuration buildFromProgramData(ProgramData pd) {
	Configuration ret = calloc(1, sizeof(struct _config));
	GtkWidget *classpaths = pd->classpaths;
	ret->numClasspaths = counts[0];
	ret->classpaths = calloc(ret->numClasspaths, sizeof(char *));
	for (size_t i = 0; i < ret->numClasspaths; i++) {
		GtkListBoxRow *row =
			gtk_list_box_get_row_at_y(GTK_LIST_BOX(classpaths), i);
		GtkWidget *label = gtk_bin_get_child(GTK_BIN(row));
		ret->classpaths[i] = strdup(gtk_label_get_text(GTK_LABEL(label)));
	}
	GtkWidget *jvmOptions = pd->jvmOptions;
	ret->numOptions = counts[1];
	ret->options = calloc(ret->numOptions, sizeof(char *));
	for (size_t i = 0; i < ret->numOptions; i++) {
		GtkListBoxRow *row =
			gtk_list_box_get_row_at_y(GTK_LIST_BOX(jvmOptions), i);
		GtkWidget *label = gtk_bin_get_child(GTK_BIN(row));
		ret->options[i] = strdup(gtk_label_get_text(GTK_LABEL(label)));
	}
	return ret;
}
static void startConquer(ProgramData pd) {
	Configuration configuration = buildFromProgramData(pd);
	char *classpath = generateClasspath(configuration);
	JavaVMOption *jvmoptions = calloc(
		configuration->numOptions + NUM_PREDEFINED_ARGS, sizeof(JavaVMOption));
	assert(jvmoptions);
	// Just free the first optionstring.
	jvmoptions[0].optionString = classpath;
	jvmoptions[1].optionString = "--enable-preview";
	jvmoptions[2].optionString = "-XX:+ShowCodeDetailsInExceptionMessages";
	for (size_t i = 0; i < configuration->numOptions; i++)
		jvmoptions[NUM_PREDEFINED_ARGS + i].optionString =
			configuration->options[i];
	JavaVMInitArgs vmArgs = {JNI_VERSION_10,
							 configuration->numOptions + NUM_PREDEFINED_ARGS,
							 jvmoptions, 1};
	JavaVM *jvm;
	JNIEnv *env = NULL;
	void *handle = loadJavaLibrary(configuration);
	createJVM func = getHandleToFunction(handle);
	jint status = func(&jvm, (void **)&env, &vmArgs);
	if (status != JNI_OK) {
		fprintf(stderr, "Couldn't create JVM: %d\n", status);
		GtkDialogFlags flags = GTK_DIALOG_MODAL;
		GtkWidget *dialog = gtk_message_dialog_new(
			NULL, flags, GTK_MESSAGE_ERROR, GTK_BUTTONS_OK,
			"Couldn't create JVM: %d\n", status);
		gtk_widget_show_all(dialog);
		gtk_dialog_run(GTK_DIALOG(dialog));
		gtk_widget_destroy(dialog);
		goto cleanup;
	}
	jclass introClass = (*env)->FindClass(env, "org/jel/gui/Intro");
	assert(introClass);
	jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	assert(stringClass);
	jmethodID mainMethod = (*env)->GetStaticMethodID(env, introClass, "main",
													 "([Ljava/lang/String;)V");
	jobjectArray arr = (*env)->NewObjectArray(env, 0, stringClass, NULL);
	(*env)->CallStaticVoidMethod(env, introClass, mainMethod, arr);
	if ((*env)->ExceptionOccurred(env)) {
		(*env)->ExceptionDescribe(env);
	}
	(*jvm)->DestroyJavaVM(jvm);
cleanup:
	free(jvmoptions[0].optionString);
	free(jvmoptions);
	freeConfiguration(configuration);
}
int main(int argc, char **argv) {
	gtk_init(&argc, &argv);
	//Hack, TODO
	counts[0] = 0;
	counts[1] = 0;
	GtkWidget *window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
	initialize_window(window);
	GtkWidget *scrolledWindow = gtk_scrolled_window_new(NULL, NULL);
	GtkWidget *table = gtk_grid_new();
	gtk_container_add(GTK_CONTAINER(scrolledWindow), table);
	gtk_container_add(GTK_CONTAINER(window), scrolledWindow);
	GtkWidget *classpathLabel = gtk_label_new("Classpaths");
	gtk_widget_set_hexpand(classpathLabel, TRUE);
	gtk_grid_attach(GTK_GRID(table), classpathLabel, 0, 0, 1, 1);
	GtkWidget *classpathsList = gtk_list_box_new();
	gtk_widget_set_hexpand(classpathsList, TRUE);
	gtk_grid_attach(GTK_GRID(table), classpathsList, 0, 1, 1, 1);
	GtkWidget *addClasspath = newInput(classpathsList, "Add classpath", 0);
	gtk_grid_attach(GTK_GRID(table), addClasspath, 0, 2, 1, 1);
	GtkWidget *jvmOptionsLabel = gtk_label_new("JVM-Options");
	gtk_widget_set_hexpand(jvmOptionsLabel, TRUE);
	gtk_grid_attach(GTK_GRID(table), jvmOptionsLabel, 0, 3, 1, 1);
	GtkWidget *jvmOptionsList = gtk_list_box_new();
	gtk_widget_set_hexpand(jvmOptionsList, TRUE);
	gtk_grid_attach(GTK_GRID(table), jvmOptionsList, 0, 4, 1, 1);
	GtkWidget *addJVMOption = newInput(jvmOptionsList, "Add JVM Option", 1);
	gtk_grid_attach(GTK_GRID(table), addJVMOption, 0, 5, 1, 1);
	GtkWidget *startButton = gtk_button_new_with_label("Start Conquer");
	gtk_widget_set_hexpand(startButton, TRUE);
	gtk_grid_attach(GTK_GRID(table), startButton, 0, 6, 1, 1);
	ProgramData data = calloc(1, sizeof(*data));
	assert(data);
	data->jvmOptions = jvmOptionsList;
	data->classpaths = classpathsList;
	data->window = window;
	data->table = table;
	g_signal_connect(startButton, "clicked", G_CALLBACK(onStartPressed), data);
	gtk_widget_show_all(window);
	gtk_main();
	return 0;
}
