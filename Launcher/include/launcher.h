#include <jni.h>
#include <stddef.h>

#ifndef LAUNCHER_H_
#define LAUNCHER_H_
#ifndef _WIN32
#define SEP ':'
#define DELIM '/'
#include <unistd.h>
#else
#define SEP ';'
#define DELIM '\\'
#endif
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
void downloadJDK(void);
void initDirectoryStructure(void);
char *getBaseDirectory(void);
Configuration getConfiguration(void);
void freeConfiguration(Configuration);
void extract(const char *);
int dirExists(const char *);
int checkForJava(const char *);
void *loadJavaLibrary(Configuration);
createJVM getHandleToFunction(void *file);
void runJVM(Configuration configuration);
#endif
