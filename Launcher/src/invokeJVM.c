#include <assert.h>
#include <gmodule.h>
#include <jni.h>
#include <stdlib.h>

extern void *loadJavaLibrary(char *directory);
typedef jint(JNICALL *createJVM)(JavaVM **, void **, void *);
typedef void (*onErrorFunc)(gchar *stacktrace, gchar *reportLocation);

extern createJVM findFunction(void *);
extern void closeLibrary(void *);

static char *getStacktrace(JNIEnv *env, jthrowable throwable);

void launcher_invokeJVM(char **options, int numOptions, char *directory,
						onErrorFunc func) {
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
		jclass reporter = (*env)->FindClass(env, "conquer/gui/ErrorReporter");
		assert(reporter);
		jmethodID report = (*env)->GetStaticMethodID(
			env, reporter, "writeErrorLog",
			"(Ljava/lang/Throwable;)Ljava/lang/String;");
		jobject string =
			(*env)->CallStaticObjectMethod(env, reporter, report, thrown);
		char *reportLocation =
			(char *)(*env)->GetStringUTFChars(env, string, NULL);
		printf("%s\n%s\n", reportLocation, stacktrace);
		if (func) {
			func(stacktrace, reportLocation);
		}
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
