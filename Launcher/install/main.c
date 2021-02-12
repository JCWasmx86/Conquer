#include <aclapi.h>
#include <archive.h>
#include <archive_entry.h>
#include <assert.h>
#include <shfolder.h>
#include <shlobj.h>
#include <stdint.h>
#include <winbase.h>

#define BUFFER_SIZE (1000 * 1000 * 8)
extern uint8_t zipfile;
extern uint64_t zipfileSize;
static int copyData(struct archive *in, struct archive *out);
char *getBaseDirectory(void);
void writeFile(const char *fileName, void *buf, uint64_t size);
char *getDesktopDirectory(void);
void createLinkToLauncher(void);

int main(int argc, char **argv) {
	char *name = getBaseDirectory();
	CreateDirectory(name, NULL);
	HANDLE h = CreateFileA(name, GENERIC_WRITE, FILE_SHARE_DELETE, NULL,
						   CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	SetSecurityInfo(h, SE_FILE_OBJECT, DACL_SECURITY_INFORMATION, NULL, NULL,
					NULL, NULL);
	writeFile("data.zip", &zipfile, zipfileSize);
	char *input = calloc(strlen(name) + 20, 1);
	sprintf(input, "%s\\data.zip", name);
	char *output = calloc(strlen(name) + 20, 1);
	sprintf(output, "%s\\", name);
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
	if ((result = archive_read_open_filename(in, input, BUFFER_SIZE))) {
		fprintf(stderr, "Couldn't open %s: %d\n", input, result);
		exit(1);
	}
	struct archive_entry *entry;
	int cnt = 0;
	for (;;) {
		errno = 0;
		result = archive_read_next_header(in, &entry);
		if (result == ARCHIVE_EOF) {
			break;
		}
		char *cc = calloc(
			strlen(output) + strlen(archive_entry_pathname(entry)) + 1, 1);
		assert(cc);
		printf("%s ", archive_entry_pathname(entry));
		sprintf(cc, "%s%s%s", output, "/", archive_entry_pathname(entry));
		archive_entry_set_pathname(entry, cc);
		printf(" -> %s\n", archive_entry_pathname(entry));
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
	free(name);
	createLinkToLauncher();
	remove(input);
	MessageBox(NULL, "Installed Conquer!", "Success",
			   MB_OK | MB_ICONINFORMATION | MB_DEFBUTTON1 | MB_APPLMODAL |
				   MB_TOPMOST);
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
char *getBaseDirectory(void) {
	char *name = calloc(MAX_PATH * 2, 1);
	assert(name);
	if (SHGetSpecialFolderPathA(NULL, name, CSIDL_PROGRAM_FILES, FALSE) ==
		FALSE) {
		fprintf(stderr, "SHGetSpecialFolderPathA failed!\n");
		exit(-1);
	}
	strcat(name, "\\Conquer\\");
	return name;
}
void writeFile(const char *fileName, void *buf, uint64_t size) {
	printf("%s, %lld\n", fileName, size);
	char *name = getBaseDirectory();
	strcat(name, fileName);
	FILE *fp = fopen(name, "wb");
	if (fp == NULL) {
		fprintf(stderr, "Couldn't open %s!\n", name);
		perror("fopen");
		return;
	}
	size_t count = fwrite(buf, 1, size, fp);
	if (count != (size_t)size) {
		fprintf(stderr,
				"Expected to write 0x%zx bytes into %s, but only wrote 0x%zx\n",
				size, name, count);
		perror("fwrite");
		free(name);
		return;
	}
	fclose(fp);
	free(name);
}
void createLinkToLauncher(void) {
	char *name = getDesktopDirectory();
	strcat(name, "/Conquer.lnk");
	char *launcher = getBaseDirectory();
	strcat(launcher, "/conquer_launcher.exe");
	if (!CreateSymbolicLinkA(name, launcher, 0)) {
		fprintf(stderr, "CreateSymbolicLinkA failed!\n");
		DWORD error = GetLastError();
		char *s = NULL;
		FormatMessageA(FORMAT_MESSAGE_ALLOCATE_BUFFER |
						   FORMAT_MESSAGE_FROM_SYSTEM |
						   FORMAT_MESSAGE_IGNORE_INSERTS,
					   NULL, error, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
					   (LPSTR)&s, 0, NULL);
		fprintf(stderr, "Error: %s\n", s);
		LocalFree(s);
	}
	free(name);
	free(launcher);
}
char *getDesktopDirectory(void) {
	char *name = calloc(MAX_PATH * 2, 1);
	assert(name);
	if (SHGetSpecialFolderPathA(HWND_DESKTOP, name, CSIDL_DESKTOP, FALSE) ==
		FALSE) {
		fprintf(stderr, "SHGetSpecialFolderPathA failed!\n");
		exit(-1);
	}
	return name;
}
