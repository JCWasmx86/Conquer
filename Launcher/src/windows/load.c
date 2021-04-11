#include <assert.h>
#include <jni.h>
#include <shlobj.h>
#include <windows.h>

typedef jint (*createJVM)(JavaVM **, void **, void *);

void closeLibrary(void *handle) {
	assert(handle);
	FreeLibrary(handle);
}
createJVM findFunction(void *file) {
	assert(file);
	return (createJVM)GetProcAddress(file, "JNI_CreateJavaVM");
}

void *loadJavaLibrary(char *givenDirectory) {
	char *directory = calloc(MAX_PATH * 2, 1);
	assert(directory);
	if (!givenDirectory) {
		if (SHGetSpecialFolderPathA(NULL, directory, CSIDL_PROGRAM_FILES, FALSE) == FALSE) {
			fprintf(stderr, "SHGetSpecialFolderPathA failed!\n");
			perror("SHGetSpecialFolderPathA");
			exit(-1);
		}
		strcat(directory, "\\Conquer\\java-16\\");
	} else {
		strcat(directory, givenDirectory);
	}
	char *binDir = calloc(strlen(directory) + 10, 1);
	assert(binDir);
	sprintf(binDir, "%s\\%s", directory, "bin");
	SetDllDirectoryA(binDir);
	free(binDir);
	char *path = "bin\\server\\jvm.dll";
	char *pathToDll = calloc(strlen(directory) + strlen(path) + 1, 1);
	assert(pathToDll);
	sprintf(pathToDll, "%s%s", directory, path);
	fflush(stdout);
	void *handle = LoadLibrary(pathToDll);
	free(pathToDll);
	free(directory);
	if (!handle) {
		perror("LoadLibrary");
		fflush(stderr);
	}
	return handle;
}
