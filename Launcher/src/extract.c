#include "launcher.h"
#include <archive.h>
#include <archive_entry.h>
#include <assert.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static int copyData(struct archive *in, struct archive *out);

void extract(const char *filename) {
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
	if ((result = archive_read_open_filename(in, filename, BUFFER_SIZE)))
		exit(1);
	char *c = "/";
	struct archive_entry *entry;
#ifdef _WIN32
	char *baseName = getBaseDirectory();
	char *java15 = calloc(strlen(baseName) + 8, 1);
	assert(java15);
	sprintf(java15, "%s%s%s", baseName, c, "java-15");
#else
	char *baseName;
	char *java15;
	if (geteuid()) {
		baseName = getBaseDirectory();
		java15 = calloc(strlen(baseName) + 8, 1);
		assert(java15);
		sprintf(java15, "%s%s%s", baseName, c, "java-15");
	} else {
		baseName = strdup("/opt");
		java15 = calloc(strlen(baseName) + 8, 1);
		assert(java15);
		sprintf(java15, "%s%s%s", baseName, c, "java-15");
	}
#endif
	for (;;) {
		errno = 0;
		result = archive_read_next_header(in, &entry);
		if (result == ARCHIVE_EOF) {
			break;
		}
		char *cc = calloc(
				strlen(java15) + strlen(archive_entry_pathname(entry)) + 1, 1);
		assert(cc);
		sprintf(cc, "%s%s%s", java15, c, &archive_entry_pathname(entry)[6]);
		archive_entry_set_pathname(entry, cc);
		result = archive_write_header(out, entry);
		if (archive_entry_size(entry) > 0) {
			result = copyData(in, out);
		}
		result = archive_write_finish_entry(out);
		free(cc);
	}
	archive_read_close(in);
	archive_read_free(in);
	archive_write_close(out);
	archive_write_free(out);
	free(baseName);
	free(java15);
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
