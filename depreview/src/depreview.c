#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <archive.h>
#include <archive_entry.h>
#include <errno.h>
#include <sys/types.h>
#include <dirent.h>

#ifndef _WIN32
#include <sys/stat.h>
#include <fts.h>
#else
#include  <shellapi.h>
#include <direct.h>
#endif

#define BUFFER_SIZE 8192
void depreview(char*);
void depreviewJar(char*);
static int copyData(struct archive*, struct archive*);
void extractJar(char *jar);
void enumerateClassfiles(void);
void enumerateDirectory(char*);
void buildJar(char*);
void addToArchive(struct archive*, char*);
int removeDirectory(void);

int main(int argc, char **argv) {
	for (int i = 1; i < argc; i++) {
		size_t len = strlen(argv[i]);
		if (strcmp(&argv[i][len - 6], ".class") == 0) {
			depreview(argv[i]);
		} else if (strcmp(&argv[i][len - 4], ".jar") == 0) {
			depreviewJar(argv[i]);
		} else {
			printf("Skipping: %s\n", argv[i]);
		}
	}
}
// Remove preview feature flag from classfile.
void depreview(char *file) {
	FILE *fp = fopen(file, "rb");
	assert(fp);
	fseek(fp, 0, SEEK_END);
	// Get size of file
	size_t l = ftell(fp);
	fseek(fp, 0, SEEK_SET);
	unsigned char *buffer = calloc(l, 1);
	assert(buffer);
	// Read entire file into buffer
	size_t cnt = fread(buffer, 1, l, fp);
	assert(cnt == l);
	// A classfile header consists of:
	// 4 bytes magic number: 0xca 0xfe 0xba 0xbe
	// 2 bytes minor version (Only set in really old Java, or if the classfile
	// was compiled with preview features) 2 bytes major version (E.g. 59 for
	// Java 15) If compiled with preview features, minor is 0xffff.
	if (buffer[4] == 0xff && buffer[5] == 0xff) {
		buffer[4] = 0x00;
		buffer[5] = 0x00;
		printf("Converting %s...\n", file);
		fclose(fp);
		fp = fopen(file, "wb");
		size_t written = fwrite(buffer, 1, l, fp);
		assert(written == cnt);
	} else {
		printf("Skipping %s, as it wasn't compiled with preview features "
				"enabled!\n", file);
	}
	free(buffer);
	fclose(fp);
}
void depreviewJar(char *jar) {
	extractJar(jar);
	enumerateClassfiles();
	buildJar(jar);
	removeDirectory();
}
#ifdef _WIN32
int removeDirectory(void) {
	char *dir = "depreview_tmp_archive";
	size_t len = strlen(dir) + 2;
	char *tempdir = (char*) malloc(len);
	assert (tempdir)
	memset(tempdir, 0, len);
	strcpy(tempdir, dir);
	SHFILEOPSTRUCT file_op = { NULL, FO_DELETE, tempdir, NULL,
			FOF_NOCONFIRMATION | FOF_NOERRORUI | FOF_SILENT, false, 0, "" };
	int ret = SHFileOperation(&file_op);
	free(tempdir);
	return ret;
}
#else
int removeDirectory(void) {
	int ret = 0;
	FTS *ftsp = NULL;
	FTSENT *curr;
	char *dir = "/tmp/depreview_tmp_archive";
	char *files[] = { dir, NULL };
	ftsp = fts_open(files, FTS_NOCHDIR | FTS_PHYSICAL | FTS_XDEV, NULL);
	if (!ftsp) {
		fprintf(stderr, "%s: fts_open failed: %s\n", dir, strerror(errno));
		ret = -1;
		goto finish;
	}
	while ((curr = fts_read(ftsp))) {
		switch (curr->fts_info) {
		case FTS_NS:
		case FTS_DNR:
		case FTS_ERR:
			fprintf(stderr, "%s: fts_read error: %s\n", curr->fts_accpath,
					strerror(curr->fts_errno));
			break;
		case FTS_DC:
		case FTS_DOT:
		case FTS_NSOK:
		case FTS_D:
			break;
		case FTS_DP:
		case FTS_F:
		case FTS_SL:
		case FTS_SLNONE:
		case FTS_DEFAULT:
			if (remove(curr->fts_accpath) < 0) {
				fprintf(stderr, "%s: Failed to remove: %s\n", curr->fts_path,
						strerror(curr->fts_errno));
				ret = -1;
			}
			break;
		}
	}
	finish: if (ftsp) {
		fts_close(ftsp);
	}
	return ret;
}

#endif
void extractJar(char *jar) {
	int flags = ARCHIVE_EXTRACT_TIME;
	flags |= ARCHIVE_EXTRACT_PERM;
	flags |= ARCHIVE_EXTRACT_ACL;
	flags |= ARCHIVE_EXTRACT_FFLAGS;
	struct archive *in = archive_read_new();
	archive_read_support_format_all(in);
	archive_read_support_compression_all(in);
	struct archive *out = archive_write_disk_new();
	archive_write_disk_set_options(out, flags);
	archive_write_disk_set_standard_lookup(out);
	int result = 0;
	if ((result = archive_read_open_filename(in, jar, BUFFER_SIZE))) {
		fprintf(stderr, "Couldn't open %s: %d\n", jar, result);
		exit(1);
	}
	char *c = "/";
#ifdef _WIN32
	char *o = "depreview_tmp_archive";
	_mkdir(o);
#else
	char *o = "/tmp/depreview_tmp_archive";
	mkdir(o, S_IRWXU);
#endif
	struct archive_entry *entry;
	int cnt = 0;
	for (;;) {
		errno = 0;
		result = archive_read_next_header(in, &entry);
		if (result == ARCHIVE_EOF) {
			break;
		}
		char *cc = calloc(
				strlen(o) + strlen(archive_entry_pathname(entry)) + 20, 1);
		assert(cc);
		sprintf(cc, "%s%s%s", o, c, archive_entry_pathname(entry));
		printf("Extracting %s to %s!\n", archive_entry_pathname(entry), cc);
		archive_entry_set_pathname(entry, cc);
		result = archive_write_header(out, entry);
		if (result != ARCHIVE_OK) {
			fprintf(stderr, "Error writing header: %s %d",
					archive_entry_pathname(entry), result);
			exit(-1);
		}
		if (archive_entry_size(entry) > 0) {
			result = copyData(in, out);
			if (result != ARCHIVE_OK) {
				fprintf(stderr, "Error while copying data: %d\n", result);
				exit(-1);
			}
		}
		result = archive_write_finish_entry(out);
		if (result != ARCHIVE_OK) {
			fprintf(stderr, "Error finishing entry: %d\n", result);
			exit(-1);
		}
		free(cc);
		cnt++;
	}
	archive_read_close(in);
	archive_read_free(in);
	archive_write_close(out);
	archive_write_free(out);
}
static int copyData(struct archive *in, struct archive *out) {
	const void *buff;
	size_t size;
	la_int64_t offset;
	while (1) {
		int result = archive_read_data_block(in, &buff, &size, &offset);
		if (result == ARCHIVE_EOF)
			return ARCHIVE_OK;
		else if (result < ARCHIVE_OK)
			return result;
		result = archive_write_data_block(out, buff, size, offset);
		if (result < ARCHIVE_OK) {
			return result;
		}
	}
	return ARCHIVE_OK;
}

void enumerateClassfiles(void) {
#ifdef _WIN32
	char* start = strdup("depreview_tmp_archive");
#else
	char *start = strdup("/tmp/depreview_tmp_archive");
#endif
	enumerateDirectory(start);
	free(start);
}
void enumerateDirectory(char *start) {
	DIR *dir = opendir(start);
	if (!dir) { //File
		size_t len = strlen(start);
		if (strcmp(&start[len - 6], ".class") == 0) {
			depreview(start);
		}
		return;
	}
	struct dirent *dp;
	while ((dp = readdir(dir)) != NULL) {
		if (strcmp(dp->d_name, ".") != 0 && strcmp(dp->d_name, "..") != 0) {
			char *path = calloc(3000, 1);
			assert(path);
			strcpy(path, start);
			strcat(path, "/");
			strcat(path, dp->d_name);
			enumerateDirectory(path);
			free(path);
		}
	}
	closedir(dir);
}
void buildJar(char *jar) {
#ifdef _WIN32
	char *input = "depreview_tmp_archive";
#else
	char *input = "/tmp/depreview_tmp_archive";
#endif
	struct archive *a = archive_write_new();
	archive_write_set_format_zip(a);
	archive_write_set_filter_option(a, "zip", "compression", "deflate");
	archive_write_set_filter_option(a, "zip", "compression-level", "9");
	archive_write_open_filename(a, jar);
	addToArchive(a, input);
	archive_write_close(a);
	archive_write_free(a);
}
void addToArchive(struct archive *a, char *start) {
#ifdef _WIN32
	char *input = "depreview_tmp_archive";
#else
	char *input = "/tmp/depreview_tmp_archive";
#endif
	DIR *dir = opendir(start);
	if (!dir) { //File
		FILE *fp = fopen(start, "rb");
		if (fp == NULL) {
			return;
		}
		fseek(fp, 0L, SEEK_END);
		long sz = ftell(fp);
		rewind(fp);
		struct archive_entry *entry = archive_entry_new();
		archive_entry_set_pathname(entry, &start[strlen(input)]);
		archive_entry_set_size(entry, sz);
		archive_entry_set_filetype(entry, AE_IFREG);
		archive_entry_set_perm(entry, 0644);
		archive_write_header(a, entry);
		void *buf = malloc(sz);
		assert(buf);
		fread(buf, 1, sz, fp);
		archive_write_data(a, buf, sz);
		free(buf);
		fclose(fp);
		archive_entry_free(entry);
		return;
	}
	struct dirent *dp;
	while ((dp = readdir(dir)) != NULL) {
		if (strcmp(dp->d_name, ".") != 0 && strcmp(dp->d_name, "..") != 0) {
			char *path = calloc(3000, 1);
			assert(path);
			strcpy(path, start);
			strcat(path, "/");
			strcat(path, dp->d_name);
			addToArchive(a, path);
			free(path);
		}
	}
	closedir(dir);
}
