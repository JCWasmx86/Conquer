#include <assert.h>
#include <dirent.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static int dirExists(const char *name);
char *launcher_getBaseDirectory(void);
typedef jint (*createJVM)(JavaVM **, void **, void *);
extern char *launcher_findExistingJavaInstallWithMatchingVersion();

void *loadJavaLibrary(char *givenDirectory) {
	char *directory = NULL;
	if (!givenDirectory) {
		directory = launcher_findExistingJavaInstallWithMatchingVersion();
		if (directory == NULL) {
			if (dirExists("/opt/java-16")) {
				char *s = "/opt/java-16/";
				directory = calloc(strlen(s) + 1, 1);
				assert(directory);
				memcpy(directory, s, strlen(s));
			} else {
				char *base = launcher_getBaseDirectory();
				assert(base);
				directory = calloc(strlen(base) + 20, 1);
				assert(directory);
				sprintf(directory, "%s%s", base, "/java-16/");
				free(base);
			}
		}
	} else {
		directory = strdup(givenDirectory);
	}
	char *path = "lib/server/libjvm.so";
	char *pathToSo = calloc(strlen(directory) + strlen(path) + 3, 1);
	assert(pathToSo);
	sprintf(pathToSo, "%s/%s", directory, path);
	void *handle = dlopen(pathToSo, RTLD_LAZY);
	free(pathToSo);
	free(directory);
	if (!handle) {
		perror("dlopen");
		return NULL;
	}
	return handle;
}
void closeLibrary(void *handle) { dlclose(handle); }
createJVM findFunction(void *file) {
	assert(file);
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
