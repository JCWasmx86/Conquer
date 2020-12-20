#include "launcher.h"
#include <assert.h>
#include <cJSON.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

Configuration emptyConfiguration(void);
Configuration buildConfiguration(cJSON *cjson);

Configuration getConfiguration(void) {
	char *base = getBaseDirectory();
	// The file will be ~/.config/.conquer/config.json
	char *jsonFile = calloc(strlen(base) + strlen("/config.json") + 1, 1);
	assert(jsonFile);
	sprintf(jsonFile, "%s%s", base, "/config.json");
	FILE *fp = fopen(jsonFile, "r");
	// Just return an empty configuration. No critical error
	if (fp == NULL) {
		fprintf(stderr, "config.json wasn't found!\n");
		fflush(stderr);
		free(jsonFile);
		free(base);
		Configuration c = emptyConfiguration();
		assert(c);
		return c;
	}
	fseek(fp, 0, SEEK_END);
	long size = ftell(fp);
	fseek(fp, 0, SEEK_SET);
	char *buf = malloc(size + 1);
	if (!buf) {
		perror("malloc");
		exit(-1);
	}
	buf[size] = 0;
	// Read the entire file into the buffer.
	fread(buf, 1, size, fp);
	cJSON *json = cJSON_Parse(buf);
	if (!json) {
		fputs("Bad config.json\n", stderr);
		const char *ptr = cJSON_GetErrorPtr();
		if (ptr != NULL) {
			fputs(ptr, stderr);
		}
		Configuration c = emptyConfiguration();
		free(buf);
		fclose(fp);
		free(jsonFile);
		free(base);
		assert(c);
		return c;
	}
	free(buf);
	fclose(fp);
	free(jsonFile);
	free(base);
	// Make configuration from the json
	return buildConfiguration(json);
}
Configuration emptyConfiguration(void) {
	return calloc(1, sizeof(struct _config));
}
Configuration buildConfiguration(cJSON *cjson) {
	Configuration ret = emptyConfiguration();
	assert(ret);
	// This paths will be appended to the classpath.
	cJSON *classpath = cJSON_GetObjectItem(cjson, "classpath");
	if (classpath != NULL && cJSON_IsArray(classpath)) {
		ret->numClasspaths = cJSON_GetArraySize(classpath);
		ret->classpaths = calloc(ret->numClasspaths, sizeof(char*));
		for (size_t i = 0; i < ret->numClasspaths; i++) {
			cJSON *str = cJSON_GetArrayItem(classpath, i);
			assert(cJSON_IsString(str));
			ret->classpaths[i] = strdup(cJSON_GetStringValue(str));
		}
	}
	// JVM Options
	cJSON *jvmOptions = cJSON_GetObjectItem(cjson, "options");
	if (jvmOptions != NULL && cJSON_IsArray(jvmOptions)) {
		ret->numOptions = cJSON_GetArraySize(jvmOptions);
		ret->options = calloc(ret->numOptions, sizeof(char*));
		for (size_t i = 0; i < ret->numOptions; i++) {
			cJSON *str = cJSON_GetArrayItem(jvmOptions, i);
			assert(cJSON_IsString(str));
			ret->options[i] = strdup(cJSON_GetStringValue(str));
		}
	}
	// Which JVM should be used.
	cJSON *usedJVM = cJSON_GetObjectItem(cjson, "jvm");
	if (usedJVM != NULL) {
		ret->usedJVM = strdup(cJSON_GetStringValue(usedJVM));
	}
	cJSON_Delete(cjson);
	return ret;
}
void freeConfiguration(Configuration c) {
	for (size_t i = 0; i < c->numOptions; i++)
		free(c->options[i]);
	free(c->options);
	for (size_t i = 0; i < c->numClasspaths; i++)
		free(c->classpaths[i]);
	free(c->classpaths);
	free(c->usedJVM);
	free(c);
}
