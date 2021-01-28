#include <assert.h>
#include <dirent.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifndef _WIN32
#include <errno.h>
#include <pwd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#else
#include <shlobj.h>
#include <windows.h>
#endif
#include "launcher.h"

static char *getHomeDirectory(void);

// Make ~/.config/.conquer or %APPDATA%\\.conquer
void initDirectoryStructure(void) {
	char *configDirectory = getBaseDirectory();
#ifndef _WIN32
	mkdir(configDirectory, S_IRWXU);
#else
	mkdir(configDirectory);
#endif
	free(configDirectory);
}
int dirExists(const char *name) {
#ifndef _WIN32
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
#else
	DWORD dwAttr = GetFileAttributes(name);
	return dwAttr != 0xffffffff && (dwAttr & FILE_ATTRIBUTE_DIRECTORY);
#endif
}
