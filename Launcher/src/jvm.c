#include "launcher.h"
#include <assert.h>
#include <dirent.h>
#include <jni.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#ifdef _WIN32
#include <shlobj.h>
#include <windows.h>
#endif
#define NUM_PREDEFINED_ARGS 2
char *generateClasspath(Configuration);
void runJVM(Configuration configuration) {
	char *classpath = generateClasspath(configuration);
	JavaVMOption *jvmoptions = calloc(
		configuration->numOptions + NUM_PREDEFINED_ARGS, sizeof(JavaVMOption));
	assert(jvmoptions);
	// Just free the first optionstring.
	jvmoptions[0].optionString = classpath;
	jvmoptions[1].optionString = "--enable-preview";
	for (size_t i = 0; i < configuration->numOptions; i++)
		jvmoptions[NUM_PREDEFINED_ARGS + i].optionString =
			configuration->classpaths[i];
	JavaVMInitArgs vmArgs = {JNI_VERSION_10,
							 configuration->numOptions + NUM_PREDEFINED_ARGS,
							 jvmoptions, 0};
	JavaVM *jvm;
	JNIEnv *env = NULL;
	void *handle = loadJavaLibrary(configuration);
	createJVM func = getHandleToFunction(handle);
	jint status = func(&jvm, (void **)&env, &vmArgs);
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
	free(jvmoptions[0].optionString);
	free(jvmoptions);
}
char *generateClasspath(Configuration configuration) {
	char *ret = calloc(1024 * 1024 * 16, 1);
	assert(ret);
	strcat(ret, "-Djava.class.path=");
	char *c = "/";
#ifndef _WIN32
	strcat(ret,
		   "/usr/share/java/Conquer.jar:/usr/share/java/Conquer_frontend.jar:");
#else
#ifdef UNICODE
#error UNICODE has to be undefined!
#endif
	TCHAR pf[MAX_PATH];
	SHGetSpecialFolderPathA(NULL, pf, CSIDL_PROGRAM_FILES, FALSE);
	sprintf(ret, "%s%s%s;%s%s;", ret, pf, "\\Conquer\\Conquer.jar", pf,
			"\\Conquer\\Conquer_frontend.jar");
#endif
	for (size_t i = 0; i < configuration->numClasspaths; i++) {
		sprintf(ret, "%s%s%s", ret, configuration->classpaths[i], c);
	}
	char *base = getBaseDirectory();
	char *libs = calloc(strlen(base) + 10, 1);
	assert(libs);
	sprintf(libs, "%s%s%s", base, "/libs", c);
	DIR *dir = opendir(libs);
	if (dir != NULL) {
		struct dirent *ent;
		while ((ent = readdir(dir)) != NULL) {
#ifndef _WIN32
			if (ent->d_type == DT_REG) {
#endif
				char *name = ent->d_name;
				size_t ll = strlen(name);
				if (ll >= 4 && memcmp(&name[ll - 4], ".jar", 4) == 0) {
#ifndef _WIN32
					sprintf(ret, "%s%s%s%s", ret, libs, name, ":");
#else
				sprintf(ret, "%s%s%s%s", ret, libs, name, ";");
#endif
				}
#ifndef _WIN32
			}
#endif
		}
		closedir(dir);
	}
#ifndef _WIN32
	sprintf(ret, "%s%s%s%s", ret, libs, "music", ":");
	sprintf(ret, "%s%s%s%s", ret, libs, "sounds", ":");
	sprintf(ret, "%s%s%s%s", ret, libs, "images", ":");
#else
	sprintf(ret, "%s%s%s%s", ret, libs, "music", ";");
	sprintf(ret, "%s%s%s%s", ret, libs, "sounds", ";");
	sprintf(ret, "%s%s%s%s", ret, libs, "images", ";");
#endif
	sprintf(ret, "%s%s", ret, ".");
	return ret;
}
