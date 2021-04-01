#include <string.h>

// A duplicated string is returned, as otherwise
// vala tries to free it and it will crash.
char *launcher_getSeparator(void) {
#ifdef _WIN32
	return strdup(";");
#elif defined(linux)
	return strdup(":");
#else
#error Unsupported OS! Only Linux and windows are supported!
#endif
}

char *launcher_getPathSeparator(void) {
#ifdef _WIN32
	return strdup("\\");
#else
	return strdup("/");
#endif
}
