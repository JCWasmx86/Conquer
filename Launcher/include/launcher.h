#include <curl/curl.h>
#include <jni.h>
#include <stddef.h>
#ifndef LAUNCHER_H_
#define LAUNCHER_H_
#ifndef _WIN32
#define SEP ":"
#define DELIM "/"
#include <unistd.h>
#else
#define SEP ";"
#define DELIM "\\"
#endif
#define NUM_PREDEFINED_ARGS 4
typedef jint (*createJVM)(JavaVM **, void **, void *);
typedef struct _config {
	// JVM Options
	char **options;
	size_t numOptions;
	// Additional classpaths
	char **classpaths;
	size_t numClasspaths;
	// Use a specified jvm or download a JVM (if null)
	char *usedJVM;
} * Configuration;
#define DIR_DELIM '/'
#define BUFFER_SIZE (1024 * 1024 * 16)
void downloadJDK(void *,
				 int (*)(void *, curl_off_t, curl_off_t, curl_off_t,
						 curl_off_t),
				 void (*)(void *, const char *, int, int));
void initDirectoryStructure(void);
char *getBaseDirectory(void);
Configuration getConfiguration(void);
void freeConfiguration(Configuration);
void extract(const char *, void (*)(void *, const char *, int, int), void *);
int dirExists(const char *);
int checkForJava(const char *);
void *loadJavaLibrary(Configuration);
createJVM getHandleToFunction(void *);
void runJVM(Configuration configuration);
// Return NULL if not, otherwise the output file
char *hasToDownloadJava(void);
// Get Java URL
char *getURL(void);
char *generateClasspath(Configuration);
#endif
