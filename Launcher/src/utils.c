#include "launcher.h"
#include <assert.h>
#include <dirent.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifndef _WIN32
#include <pwd.h>
#include <sys/types.h>
static const char *getLinuxJavaDirectory(void);
#endif
static int readReleaseFile(const char *directory);
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
char *findExistingJavaInstallWithMatchingVersion(void) {
#ifdef _WIN32
	return NULL; // TODO
#else
	const char *jvmDirectory = getLinuxJavaDirectory();
	if (jvmDirectory) {
		DIR *dir = opendir(jvmDirectory);
		if (dir) {
			int version = readReleaseFile(jvmDirectory);
			closedir(dir);
			if (version == 15) {
				return strdup(jvmDirectory);
			}
		}
	}
	const char *javaHome = getenv("JAVA_HOME");
	if (javaHome) {
		int version = readReleaseFile(javaHome);
		if (version == 15) {
			return strdup(javaHome);
		}
	}
	return NULL;
#endif
}
static int readReleaseFile(const char *directory) {
#ifndef _WIN32
	char *release = calloc(strlen(directory) + 20, 1);
	assert(release);
	sprintf(release, "%s/%s", directory, "release");
	FILE *fp = fopen(release, "r");
	char *line = NULL;
	size_t len = 0;
	size_t read = 0;
	const char *variable = "JAVA_VERSION=\"";
	size_t cmp = strlen(variable);
	while ((read = getline(&line, &len, fp)) != -1) {
		if (strncmp(line, variable, cmp) == 0) {
			char *version = &line[cmp];
			// Will fail on Java 15xxxx
			if (version[0] == '1' && version[1] == '5') {
				free(line);
				fclose(fp);
				return 15;
			}
		}
	}
	free(line);
	fclose(fp);
	free(release);
#endif
	return -1;
}
#ifndef _WIN32
static const char *getLinuxJavaDirectory(void) {
#ifdef __x86_64__
	return "/usr/lib/jvm/java-15-openjdk-amd64/";
#endif
#ifdef __i386__
	return "/usr/lib/jvm/java-15-openjdk-i386/";
#endif
#ifdef __aarch64__
	return "/usr/lib/jvm/java-15-openjdk-arm64/";
#endif
	return NULL;
}
#endif
