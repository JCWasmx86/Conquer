#include "launcher.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
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
	if (fp)
		fclose(fp);
	free(p2);
	return exists;
}
