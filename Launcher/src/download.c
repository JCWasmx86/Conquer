#include <assert.h>
#include <math.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <curl/curl.h>
static int progress(void*, curl_off_t, curl_off_t, curl_off_t, curl_off_t);
static size_t writeData(void*, size_t, size_t, FILE*);
static int lastInteger = 0;
static char *urlToDownload;
static int downloadFile(const char*, const char*, void*,
		int (*)(void*, curl_off_t, curl_off_t, curl_off_t, curl_off_t));

#include "launcher.h"

char* hasToDownloadJava(void) {
#ifdef _WIN32
	char *base = getBaseDirectory();
	assert(base);
	char *dirBase = calloc(strlen(base) + 20, 1);
	assert(dirBase);
	sprintf(dirBase, "%s%s", base, "/java-15");
	char *outputFile = calloc(strlen(base) + 25, 1);
	assert(outputFile);
	sprintf(outputFile, "%s%s", base, "/java-15.tar.gz");
#else
	char *dirBase;
	char *outputFile;
	char *base;
	if (geteuid()) {
		base = getBaseDirectory();
	} else {
		fprintf(stderr,
				"You are running as root! Installing Java to /opt/java-15!\n");
		base = strdup("/opt");
	}
	assert(base);
	dirBase = calloc(strlen(base) + 20, 1);
	assert(dirBase);
	sprintf(dirBase, "%s%s", base, "/java-15");
	outputFile = calloc(strlen(base) + 25, 1);
	assert(outputFile);
	sprintf(outputFile, "%s%s", base, "/java-15.tar.gz");
#endif
	int shouldDownload = !dirExists(dirBase);
#ifndef _WIN32
	shouldDownload &= !dirExists("/opt/java-15");
#endif
	free(base);
	free(dirBase);
	if (shouldDownload) {
		return outputFile;
	} else {
		free(outputFile);
		return NULL;
	}
}
char* getURL() {
#ifndef _WIN32
#ifndef __aarch64__
	return "https://mirrors.huaweicloud.com/openjdk/15/openjdk-15_linux-x64_bin.tar.gz";
#else
	return "https://mirrors.huaweicloud.com/openjdk/15/openjdk-15_linux-aarch64_bin.tar.gz";
#endif
#else
	return "https://mirrors.huaweicloud.com/openjdk/15/openjdk-15_windows-x64_bin.zip";
#endif
}
void downloadJDK(void *data,
		int (*progressFunc)(void*, curl_off_t, curl_off_t, curl_off_t,
				curl_off_t),
		void (*extractCallback)(void*, const char*, int, int)) {
	char *outputFile = hasToDownloadJava();
	if (outputFile) {
		if (downloadFile(getURL(), outputFile, data, progressFunc)) {
			fputs("Download of java 15 failed!", stderr);
			fflush(stderr);
			goto cleanup;
		}
		extract(outputFile, extractCallback, data);
		remove(outputFile);
	}
	cleanup: free(outputFile);
}
static int downloadFile(const char *url, const char *outputFileName, void *data,
		int (*progressFunc)(void*, curl_off_t, curl_off_t, curl_off_t,
				curl_off_t)) {
	printf("Starting download of %s to %s!\n", url, outputFileName);
	fflush(stdout);
#ifndef _WIN32
	urlToDownload = (char*) url;
	CURLcode res = 1;
	CURL *curl = curl_easy_init();
	if (curl) {
		FILE *fp = fopen(outputFileName, "wb");
		if (!fp) {
			perror("fopen");
			return 1;
		}
		curl_easy_setopt(curl, CURLOPT_URL, url);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeData);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);
		curl_easy_setopt(curl, CURLOPT_PROGRESSDATA, data);
		curl_easy_setopt(curl, CURLOPT_NOPROGRESS, 0);
		curl_easy_setopt(curl, CURLOPT_XFERINFOFUNCTION,
				progressFunc==NULL?progress:progressFunc);
		res = curl_easy_perform(curl);
		curl_easy_cleanup(curl);
		fclose(fp);
	}
	puts("");
	lastInteger = 0;
	return res;
#else
	return URLDownloadToFile(NULL, url, outputFileName, 0, NULL) != S_OK;
#endif
}
static size_t writeData(void *ptr, size_t size, size_t nmemb, FILE *stream) {
	return fwrite(ptr, size, nmemb, stream);
}
static int progress(void *clientp, curl_off_t dltotal, curl_off_t dlnow,
		curl_off_t ultotal, curl_off_t ulnow) {
	double expected = dltotal;
	double current = dlnow;
	double percentage = (current / expected) * 100;
	if (isnan(percentage)) {
		return 0;
	}
	int integer = (int) round(percentage);
	if (integer == lastInteger) {
		return 0;
	}
	lastInteger = integer;
	printf("Downloading: %s %d%%\n", urlToDownload, integer);
	printf("\033[1A");
	return 0;
}
