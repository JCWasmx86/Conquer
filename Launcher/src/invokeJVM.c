#include <jni.h>
#include <stdlib.h>
#include <assert.h>

extern void* loadJavaLibrary(void);
typedef jint (*createJVM)(JavaVM **, void **, void *);
typedef void (*onErrorFunc)(char* stacktrace, char* systemProperties, char* environmentVariables);

extern createJVM findFunction(void*);
extern void closeLibrary(void*);

void launcher_invokeJVM(char**options, int numOptions) {
	void* handle = loadJavaLibrary();
	assert(handle);
	createJVM create = findFunction(handle);
	JavaVMOption *jvmoptions = calloc(numOptions, sizeof(JavaVMOption));
	for(int i = 0; i < numOptions; i++) {
		jvmoptions[i].optionString = options[i];
	}
	JavaVMInitArgs vmArgs = {JNI_VERSION_10,numOptions,jvmoptions, 1};
	JavaVM *jvm;
	JNIEnv *env = NULL;
	jint status = create(&jvm, (void **)&env, &vmArgs);
	if (status != JNI_OK) {
		fprintf(stderr, "Couldn't create JVM: %d\n", status);
		goto cleanup;
	}
	jclass introClass = (*env)->FindClass(env, "org/jel/gui/Intro");
	assert(introClass);
	jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	assert(stringClass);
	jmethodID mainMethod = (*env)->GetStaticMethodID(env, introClass, "main", "([Ljava/lang/String;)V");
	jobjectArray arr = (*env)->NewObjectArray(env, 0, stringClass, NULL);
	(*env)->CallStaticVoidMethod(env, introClass, mainMethod, arr);
	if ((*env)->ExceptionOccurred(env)) {
		(*env)->ExceptionDescribe(env);
	}
	(*jvm)->DestroyJavaVM(jvm);
	cleanup:
		free(jvmoptions);
		closeLibrary(handle);
}
