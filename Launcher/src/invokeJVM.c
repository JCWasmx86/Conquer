#include <assert.h>
#include <gmodule.h>
#include <jni.h>
#include <stdlib.h>

#ifndef _WIN32
#include <pwd.h>
#include <sys/types.h>
#include <unistd.h>
#endif
extern void *loadJavaLibrary(char *directory);
typedef jint(JNICALL *createJVM)(JavaVM **, void **, void *);

extern createJVM findFunction(void *);
extern void closeLibrary(void *);
extern void launcher_makeDirectory(char *);

static char *getStacktrace(JNIEnv *, jthrowable);
static void formatTime(char *);
static char *getBaseDir(void);
static void writeFile(JNIEnv *env, char *file);

void launcher_invokeJVM(char **options, int numOptions, char *directory) {
	void *handle = loadJavaLibrary(directory);
	assert(handle && "Couldn't load Java library! Is the path correct?");
	createJVM create = findFunction(handle);
	JavaVMOption *jvmoptions = calloc(numOptions, sizeof(JavaVMOption));
	for (int i = 0; i < numOptions; i++) {
		jvmoptions[i].optionString = options[i];
	}
	JavaVMInitArgs vmArgs = {JNI_VERSION_10, numOptions, jvmoptions, 1};
	JavaVM *jvm;
	JNIEnv *env = NULL;
	jint status = create(&jvm, (void **)&env, &vmArgs);
	if (status != JNI_OK) {
		fprintf(stderr, "Couldn't create JVM: %d\n", status);
		goto cleanup;
	}
	jclass introClass = (*env)->FindClass(env, "conquer/gui/Intro");
	if ((*env)->ExceptionOccurred(env)) {
		(*env)->ExceptionDescribe(env);
	}
	assert(introClass);
	jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	assert(stringClass);
	jmethodID mainMethod = (*env)->GetStaticMethodID(env, introClass, "main",
													 "([Ljava/lang/String;)V");
	jobjectArray arr = (*env)->NewObjectArray(env, 0, stringClass, NULL);
	(*env)->CallStaticVoidMethod(env, introClass, mainMethod, arr);
	jthrowable thrown = (*env)->ExceptionOccurred(env);
	if (thrown) {
		(*env)->ExceptionClear(env);
		char *stacktrace = getStacktrace(env, thrown);
		// 249 chars should be enough for time
		char *dateAndTime = calloc(250, 1);
		assert(dateAndTime);
		formatTime(dateAndTime);
		char *baseDir = getBaseDir();
		char *reportsDir = calloc(strlen(baseDir) + strlen("/reports/") + 2, 1);
		assert(reportsDir);
		sprintf(reportsDir, "%s/reports/", baseDir);
		launcher_makeDirectory(reportsDir);
		char *fileName =
			calloc(strlen(dateAndTime) + strlen(reportsDir) + 50, 1);
		assert(fileName);
		sprintf(fileName, "%s%s__report.txt", reportsDir, dateAndTime);
		writeFile(env, fileName);
		free(fileName);
		free(reportsDir);
		free(baseDir);
		free(dateAndTime);
		jclass reporter = (*env)->FindClass(env, "conquer/gui/ErrorReporter");
		assert(reporter);
		jmethodID report = (*env)->GetStaticMethodID(
			env, reporter, "writeErrorLog",
			"(Ljava/lang/Throwable;)Ljava/lang/String;");
		(*env)->CallStaticObjectMethod(env, reporter, report, thrown);
	}
	(*jvm)->DestroyJavaVM(jvm);
cleanup:
	free(jvmoptions);
	closeLibrary(handle);
}
static char *getStacktrace(JNIEnv *env, jthrowable throwable) {
	jclass stringWriter = (*env)->FindClass(env, "java/io/StringWriter");
	jclass printWriter = (*env)->FindClass(env, "java/io/PrintWriter");
	jmethodID noArgsConstructor =
		(*env)->GetMethodID(env, stringWriter, "<init>", "()V");
	jmethodID printWriterConstructor =
		(*env)->GetMethodID(env, printWriter, "<init>", "(Ljava/io/Writer;)V");
	jobject sw = (*env)->NewObject(env, stringWriter, noArgsConstructor);
	jobject pw =
		(*env)->NewObject(env, printWriter, printWriterConstructor, sw);
	jmethodID printStackTrace =
		(*env)->GetMethodID(env, (*env)->GetObjectClass(env, throwable),
							"printStackTrace", "(Ljava/io/PrintWriter;)V");
	(*env)->CallVoidMethod(env, throwable, printStackTrace, pw);
	jmethodID mid = (*env)->GetMethodID(env, stringWriter, "toString",
										"()Ljava/lang/String;");
	assert(mid);
	jstring string = (*env)->CallObjectMethod(env, sw, mid);
	return (char *)(*env)->GetStringUTFChars(env, string, NULL);
}
static void formatTime(char *output) {
	assert(output);
	time_t rawtime;
	time(&rawtime);
	struct tm *timeinfo = localtime(&rawtime);
	sprintf(output, "%d_%d_%d;;%d__%d__%d___", timeinfo->tm_mday,
			timeinfo->tm_mon + 1, timeinfo->tm_year + 1900, timeinfo->tm_hour,
			timeinfo->tm_min, timeinfo->tm_sec);
}
static char *getBaseDir(void) {
#ifdef _WIN32
	char *env = getenv("APPDATA");
	char *ret = calloc(strlen(env) + strlen("\\.conquer") + 2, 1);
	assert(ret);
	sprintf(ret, "%s\\.conquer", env);
	return ret;
#else
	char *env = getenv("APPDATA");
	if (env == NULL) {
		env = getpwuid(getuid())->pw_dir;
	}
	char *ret = calloc(strlen(env) + strlen("/.config/.conquer") + 2, 1);
	assert(ret);
	sprintf(ret, "%s/.config/.conquer", env);
#endif
}
static void writeFile(JNIEnv *env, char *file) {
	jclass reporter = (*env)->FindClass(env, "conquer/gui/ErrorReporterUtils");
	assert(reporter);
	jmethodID mid = (*env)->GetStaticMethodID(env, reporter, "getString",
											  "()Ljava/lang/String;");
	FILE *fp = fopen(file, "w");
	assert(fp);
	fputs((*env)->GetStringUTFChars(
			  env, (*env)->CallStaticObjectMethod(env, reporter, mid), NULL),
		  fp);
	fclose(fp);
}