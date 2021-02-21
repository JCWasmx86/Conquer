#include <string.h>

char* launcher_getSeparator(void) {
#ifdef _WIN32
	return strdup(";");
#else
	return strdup(":");
#endif
}

char* launcher_getPathSeparator(void) {
#ifdef _WIN32
	return strdup("\\");
#else
	return strdup("/");
#endif
}
