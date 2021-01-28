#include "launcher.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// Check whether a specified path contains a java installation. This is checked
// by looking for bin/java(.exe)
int checkForJava(const char *path) {
	size_t len = strlen(path);
	char *p2 = calloc(len + 30, 1);
	assert(p2);
	strcat(p2, path);
#ifndef _WIN32
	strcat(p2, "/bin/java");
#else
	strcat(p2, "\\bin\\java.exe");
#endif
	FILE *fp = fopen(p2, "rb");
	int exists = fp != NULL;
	if (fp) {
		fclose(fp);
	}
	free(p2);
	return exists;
}
char *getURL(void) {
#ifndef _WIN32
#ifndef __aarch64__
	return "https://mirrors.huaweicloud.com/openjdk/15/"
		   "openjdk-15_linux-x64_bin.tar.gz";
#else
	return "https://mirrors.huaweicloud.com/openjdk/15/"
		   "openjdk-15_linux-aarch64_bin.tar.gz";
#endif
#else
	return "https://mirrors.huaweicloud.com/openjdk/15/"
		   "openjdk-15_windows-x64_bin.zip";
#endif
}
static char *getHomeDirectory(void) {
#ifndef _WIN32
	char *homedir;
	if ((homedir = getenv("HOME")) == NULL) {
		homedir = getpwuid(getuid())->pw_dir;
	}
	return homedir;
#else
	return getenv("USERPROFILE");
#endif
}
char *getBaseDirectory(void) {
#ifndef _WIN32
	char *home = getHomeDirectory();
	assert(home);
	size_t lenHome = strlen(home) + 1;
	char *append = "/.config/.conquer";
	char *configDirectory = calloc(lenHome + strlen(append) + 1, 1);
	assert(configDirectory);
	sprintf(configDirectory, "%s%s", home, append);
	return configDirectory;
#else
	char *home = strdup(getenv("APPDATA"));
	size_t lenHome = strlen(home) + 1;
	char *append = "\\.conquer";
	char *configDirectory = calloc(lenHome + strlen(append) + 1, 1);
	assert(configDirectory);
	sprintf(configDirectory, "%s%s", home, append);
	return configDirectory;
#endif
}