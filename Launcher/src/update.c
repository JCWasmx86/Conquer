#include <archive.h>
#include <archive_entry.h>
#include <assert.h>
#include <dirent.h>
#include <stdio.h>
#include <sys/stat.h>

#include "launcher.h"

int endsWith(const char *str, const char *end) {
    size_t lenStr = strlen(str);
    size_t lenEnd = strlen(end);
    if (lenEnd >  lenStr) {
        return 0;
	}
    return strncmp(str + lenStr - lenEnd, end, lenEnd) == 0;
}

void removeFiles(char *path) {
	DIR *dir;
	struct dirent *ent;
	struct stat s;
	if ((dir = opendir(path)) != NULL) {
		while ((ent = readdir(dir)) != NULL) {
			char *current = malloc(strlen(path) + sizeof(ent->d_name) + 3);
            sprintf(current, "%s%s%s", path, DELIM, ent->d_name);
			stat(current, &s);

			if (strcmp(ent->d_name, ".") != 0 &&
				strcmp(ent->d_name, "..") != 0 &&
				strcmp(ent->d_name, "saves") != 0 &&
				strcmp(ent->d_name, "updates") != 0 &&
				strcmp(ent->d_name, "sounds") != 0 &&
                !endsWith(ent->d_name, ".log") &&
                !endsWith(ent->d_name, ".properties")) {
                if (S_ISDIR(s.st_mode)) {
					removeFiles(current);
				} else {
                    printf("Removing %s: %i\n", ent->d_name, remove(current));
                }
			}
		}
		closedir(dir);
	}
}

void update() {
	char *baseDir = getBaseDirectory();
	assert(baseDir);
	char *updateDir = malloc(strlen(baseDir) + 3 + 9);
	assert(updateDir);
	sprintf(updateDir, "%s%s%s", baseDir, DELIM, "updates");
	char *updateFile = malloc(strlen(updateDir) + 3 + 10);
	assert(updateFile);
	sprintf(updateFile, "%s%s%s", updateDir, DELIM, "data.zip");

    struct stat s;
    if (stat(updateFile, &s) != 0) {
        printf("No update-file found in %s\n", updateDir);
		free(baseDir);
		free(updateDir);
		free(updateFile);
		return;
    }
	printf("Update found\n");

	removeFiles(baseDir);

	int flags = ARCHIVE_EXTRACT_TIME;
	flags |= ARCHIVE_EXTRACT_PERM;
	flags |= ARCHIVE_EXTRACT_ACL;
	flags |= ARCHIVE_EXTRACT_FFLAGS;

	struct archive *a = archive_read_new();
	struct archive *out = archive_write_disk_new();
	archive_write_disk_set_options(out, flags);
	archive_write_disk_set_standard_lookup(out);
	struct archive_entry *entry;
	int r;

	archive_read_support_compression_all(a);
	archive_read_support_format_zip(a);
	if (r = archive_read_open_filename(a, updateFile, BUFFER_SIZE)) {
		fprintf(stderr, "Couldn't open %s: %s\n", updateFile,
				archive_error_string(a));
		exit(-1);
	}
	printf("Extracting archive...\n");
	for (;;) {
		r = archive_read_next_header(a, &entry);
		if (r == ARCHIVE_EOF) {
			break;
		}
		char *cc = calloc(
			strlen(baseDir) + strlen(archive_entry_pathname(entry)) + 1, 1);
		assert(cc);
		sprintf(cc, "%s%s%s", baseDir, DELIM, archive_entry_pathname(entry));
		archive_entry_set_pathname(entry, cc);
		r = archive_write_header(out, entry);
		if (r != ARCHIVE_OK) {
			fprintf(stderr, "Error writing header %s: %d %s",
					archive_entry_pathname(entry), r, archive_error_string(a));
			exit(-1);
		}
		if (archive_entry_size(entry) > 0) {
			r = copyData(a, out);
			if (r != ARCHIVE_OK) {
				fprintf(stderr, "Error while copying data: %d\n", r);
				exit(-1);
			}
		}
		r = archive_write_finish_entry(out);
		if (r != ARCHIVE_OK) {
			fprintf(stderr, "Error finishing entry: %d %s\n", r,
					archive_error_string(a));
			exit(-1);
		}
		free(cc);
	}
	archive_read_close(a);
	archive_read_free(a);
	
	remove(updateFile);
	
	printf("Starting Installer...\n");
	char* installer = malloc(strlen(updateDir) + 3 + 14);
	sprintf(installer, "%s%s%s", updateDir, DELIM, "Installer.exe");
	char* cmd = malloc(strlen(installer) * 2 + 24);
	sprintf(cmd, "cmd.exe /c %s && del %s", installer, installer);
	system(cmd);

	free(installer);
	free(cmd);
	free(baseDir);
	free(updateDir);
	free(updateFile);

	exit(0);
}