#include <dlfcn.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <errno.h>
#include <dirent.h>
#include <jni.h>

static int dirExists(const char* name);
char* launcher_getBaseDirectory(void);
typedef jint (*createJVM)(JavaVM **, void **, void *);

void* loadJavaLibrary() {
	char* directory;
	if (dirExists("/opt/java-15")) {
		char *s = "/opt/java-15/";
		directory = calloc(strlen(s) + 1, 1);
		assert(directory);
		memcpy(directory, s, strlen(s));
	} else {
		char *base = launcher_getBaseDirectory();
		assert(base);
		directory = calloc(strlen(base) + 20, 1);
		assert(directory);
		sprintf(directory, "%s%s", base, "/java-15/");
		free(base);
	}
	char *path = "lib/server/libjvm.so";
	char *pathToSo = calloc(strlen(directory) + strlen(path) + 3, 1);
	assert(pathToSo);
	sprintf(pathToSo, "%s/%s", directory, path);
	void *handle = dlopen(pathToSo, RTLD_LAZY);
	free(pathToSo);
	free(directory);
	if(!handle) {
		perror("dlopen");
		return NULL;
	}
	return handle;
}
extern void closeLibrary(void* handle) {
	dlclose(handle);
}
extern createJVM findFunctionvoid *file) {
	return (createJVM)dlsym(file, "JNI_CreateJavaVM");
}
static int dirExists(const char *name) {
	DIR *dir = opendir(name);
	if (dir) {
		closedir(dir);
		return 1;
	} else if (ENOENT == errno) {
		return 0;
	} else {
		perror("opendir");
		abort();
	}
}
