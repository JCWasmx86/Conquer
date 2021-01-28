#include <assert.h>
#include <stdlib.h>
#include <string.h>
#ifndef _WIN32
#include <dlfcn.h>
#else
#include <shlobj.h>
#include <windows.h>
#endif
#include "launcher.h"
void *loadJavaLibrary(Configuration conf) {
	char *directory;
	if (conf->usedJVM == NULL) {
#ifdef _WIN32
		char *name = calloc(MAX_PATH * 2, 1);
		assert(name);
		if (SHGetSpecialFolderPathA(NULL, name, CSIDL_PROGRAM_FILES, FALSE) ==
			FALSE) {
			fprintf(stderr, "SHGetSpecialFolderPathA failed!\n");
			perror("SHGetSpecialFolderPathA");
			exit(-1);
		}
		strcat(name, "\\Conquer\\jdk-15\\");
		directory = name;
#else
		if (dirExists("/opt/java-15")) {
			char *s = "/opt/java-15/";
			directory = calloc(strlen(s) + 1, 1);
			assert(directory);
			memcpy(directory, s, strlen(s));
		} else {
			char *base = getBaseDirectory();
			assert(base);
			directory = calloc(strlen(base) + 20, 1);
			assert(directory);
			sprintf(directory, "%s%s", base, "/java-15/");
			free(base);
		}
#endif
	} else {
		directory = strdup(conf->usedJVM);
	}
#ifdef _WIN32
	char *binDir = calloc(strlen(directory) + 5, 1);
	assert(binDir);
	sprintf(binDir, "%s%s", directory, "bin");
	SetDllDirectoryA(binDir);
	free(binDir);
#endif
#ifndef _WIN32
	char *path = "lib/server/libjvm.so";
	char *pathToSo = calloc(strlen(directory) + strlen(path) + 1, 1);
	assert(pathToSo);
	sprintf(pathToSo, "%s%s", directory, path);
	void *handle = dlopen(pathToSo, RTLD_LAZY);
	free(pathToSo);
#else
	char *path = "bin\\server\\jvm.dll";
	char *pathToDll = calloc(strlen(directory) + strlen(path) + 1, 1);
	assert(pathToDll);
	sprintf(pathToDll, "%s%s", directory, path);
	fflush(stdout);
	void *handle = LoadLibrary(pathToDll);
	free(pathToDll);
#endif
	free(directory);
	if (!handle) {
#ifndef _WIN32
		perror("dlopen");
#else
		perror("LoadLibrary");
#endif
		fflush(stderr);
	}
	return handle;
}
createJVM getHandleToFunction(void *file) {
#ifndef _WIN32
	return (createJVM)dlsym(file, "JNI_CreateJavaVM");
#else
	return (createJVM)GetProcAddress(file, "JNI_CreateJavaVM");
#endif
}
