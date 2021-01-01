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
} * DataStruct;

// 0 for downloading, 1 for extracting
static volatile int status;
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
static void destroyWindow(GtkWidget *widget, gpointer data);
static void onExtractFunc(void *ptr, const char *s, int current, int count);
static int onDownloadProgress(void *clientp, curl_off_t dltotal,
							  curl_off_t dlnow, curl_off_t ultotal,
							  curl_off_t ulnow);
static void initialize_window(GtkWidget *window);
static GtkWidget *newEntry(const gchar *s);
static void insertNewElement(GtkWidget *widget, gpointer data);
static GtkWidget *newInput(GtkWidget *list, const gchar *label);
void *eventFunc(void *);
static void eventLoop(GtkWidget *, GtkWidget *);

static void destroyWindow(GtkWidget *widget, gpointer data) { gtk_main_quit(); }

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
static GtkWidget *newEntry(const gchar *s) {
	GtkWidget *view = gtk_entry_new();
	GtkEntryBuffer *buffer = gtk_entry_get_buffer(GTK_ENTRY(view));
	gtk_entry_buffer_set_text(buffer, s, -1);
	return view;
}
static void insertNewElement(GtkWidget *widget, gpointer data) {
	DataStruct ds = data;
	GtkEntryBuffer *buffer = gtk_entry_get_buffer(GTK_ENTRY(ds->inputDialog));
	if (gtk_entry_buffer_get_length(buffer)) {
		GtkWidget *item = newEntry(gtk_entry_buffer_get_text(buffer));
		gtk_list_box_insert(GTK_LIST_BOX(ds->listWidget), item, -1);
		gtk_entry_buffer_delete_text(buffer, 0, -1);
		gtk_widget_queue_draw(ds->listWidget);
		gtk_widget_show_all(ds->listWidget);
	}
}
static GtkWidget *newInput(GtkWidget *list, const gchar *label) {
	GtkWidget *table = gtk_grid_new();
	GtkWidget *inputDialog = newEntry("");
	gtk_grid_attach(GTK_GRID(table), inputDialog, 0, 0, 1, 1);
	GtkWidget *button = gtk_button_new_with_label(label);
	gtk_grid_attach(GTK_GRID(table), button, 1, 0, 1, 1);
	DataStruct ds = calloc(1, sizeof(*ds));
	assert(ds);
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
}
int main(int argc, char **argv) {
	gtk_init(&argc, &argv);
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
	GtkWidget *addClasspath = newInput(classpathsList, "Add classpath");
	gtk_grid_attach(GTK_GRID(table), addClasspath, 0, 2, 1, 1);
	GtkWidget *jvmOptionsLabel = gtk_label_new("JVM-Options");
	gtk_widget_set_hexpand(jvmOptionsLabel, TRUE);
	gtk_grid_attach(GTK_GRID(table), jvmOptionsLabel, 0, 3, 1, 1);
	GtkWidget *jvmOptionsList = gtk_list_box_new();
	gtk_widget_set_hexpand(jvmOptionsList, TRUE);
	gtk_grid_attach(GTK_GRID(table), jvmOptionsList, 0, 4, 1, 1);
	GtkWidget *addJVMOption = newInput(jvmOptionsList, "Add JVM Option");
	gtk_grid_attach(GTK_GRID(table), addJVMOption, 0, 5, 1, 1);
	GtkWidget *startButton = gtk_button_new_with_label("Start");
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
