#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "launcher.h"
#include <gtk/gtk.h>

typedef struct _programData {
	GtkWidget *jvmOptions;
	GtkWidget *classpaths;
	GtkWidget *window;
	GtkWidget *table;
} *ProgramData;
typedef struct _downloadData {
	GtkWidget *window;
	GtkWidget *progressBar;
} *DownloadData;
typedef struct _dataStruct {
	GtkWidget *listWidget;
	GtkWidget *inputDialog;
} *DataStruct;

static int updateProgressBar(void*, curl_off_t, curl_off_t, curl_off_t,
		curl_off_t);
static void destroyWindow(GtkWidget *widget, gpointer data) {
	gtk_main_quit();
}
static void updateProgressBar2(void *ptr, const char *s, int current, int count) {
	DownloadData data = ptr;
	char *string = calloc(1, 250);
	double percentage = ((double) current) / count;
	sprintf(string, "%s (%d/%d)", s, current, count);
	gtk_progress_bar_set_fraction(GTK_PROGRESS_BAR(data->progressBar),
			percentage);
	gtk_progress_bar_set_text(GTK_PROGRESS_BAR(data->progressBar), string);
	gtk_widget_show_all(data->window);
	gtk_widget_show_all(data->progressBar);
	gtk_main_iteration();
}
static int updateProgressBar(void *clientp, curl_off_t dltotal,
		curl_off_t dlnow, curl_off_t ultotal, curl_off_t ulnow) {
	DownloadData data = clientp;
	gtk_widget_show_all(data->window);
	if (dltotal == 0) {
		return 0;
	}
	double percentage = dltotal != 0 ? ((double) dlnow) / dltotal : 0;
	char *string = calloc(1, 250);
	sprintf(string, "Downloaded %ld of %ld bytes (%.2f%%)", dlnow, dltotal,
			percentage * 100);
	gtk_progress_bar_set_fraction(GTK_PROGRESS_BAR(data->progressBar),
			percentage);
	gtk_progress_bar_set_text(GTK_PROGRESS_BAR(data->progressBar), string);
	gtk_widget_show_all(data->window);
	gtk_widget_show_all(data->progressBar);
	return 0;
}
static void initialize_window(GtkWidget *window) {
	gtk_window_set_title(GTK_WINDOW(window), "Conquer - 1.0.0");
	gtk_window_set_default_size(GTK_WINDOW(window), 400, 200);
	g_signal_connect(window, "destroy", G_CALLBACK(destroyWindow), NULL);
}
static GtkWidget* newEntry(const gchar *s) {
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
static GtkWidget* newInput(GtkWidget *list, const gchar *label) {
	GtkWidget *table = gtk_grid_new();
	GtkWidget *inputDialog = newEntry("");
	gtk_grid_attach(GTK_GRID(table), inputDialog, 0, 0, 1, 1);
	GtkWidget *button = gtk_button_new_with_label(label);
	gtk_grid_attach(GTK_GRID(table), button, 1, 0, 1, 1);
	DataStruct ds = calloc(1, sizeof(*ds));
	ds->listWidget = list;
	ds->inputDialog = inputDialog;
	g_signal_connect(button, "clicked", G_CALLBACK(insertNewElement), ds);
	gtk_widget_set_hexpand(table, TRUE);
	gtk_widget_set_hexpand(inputDialog, TRUE);
	gtk_widget_set_hexpand(button, TRUE);
	return table;
}
void* makeProgressBar(void*);
static volatile int completed;
void* makeProgressBar(void *data) {
	DownloadData dataP = data;
	completed = 0;
	downloadJDK(dataP, updateProgressBar, updateProgressBar2);
	free(
			(gchar*) gtk_progress_bar_get_text(
					GTK_PROGRESS_BAR(dataP->progressBar)));
	free(dataP);
	completed = 1;
	return data;
}
static void onStartPressed(GtkWidget *widget, gpointer data) {
	ProgramData pd = data;
	//TODO: Crashdialog
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
		DownloadData dataP = calloc(1, sizeof(*dataP));
		dataP->window = pd->window;
		dataP->progressBar = progressBar;
		pthread_t thread;
		pthread_create(&thread, NULL, makeProgressBar, dataP);
		while (!completed) {
			gtk_main_iteration();
		}
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
	data->jvmOptions = jvmOptionsList;
	data->classpaths = classpathsList;
	data->window = window;
	data->table = table;
	g_signal_connect(startButton, "clicked", G_CALLBACK(onStartPressed), data);
	gtk_widget_show_all(window);
	gtk_main();
	return 0;
}
