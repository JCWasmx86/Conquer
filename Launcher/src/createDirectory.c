#ifndef _WIN32
#include <sys/stat.h>
#else
#include <direct.h>
#endif
void launcher_makeDirectory(char *s) {
#ifndef _WIN32
	mkdir(s, S_IRWXU);
#else
	_mkdir(s);
#endif
}