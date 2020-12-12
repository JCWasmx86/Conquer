#include <assert.h>
#include <shfolder.h>
#include <shlobj.h>

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

extern uint8_t launcher;
extern uint8_t conquer;
extern uint8_t conquerFrontend;
extern uint8_t uninstall;
extern uint64_t launcherSize;
extern uint64_t conquerSize;
extern uint64_t conquerFrontendSize;
extern uint64_t uninstallSize;

char *getBaseDirectory(void);
void writeFile(const char *fileName, void *buf, uint64_t size);
char *getDesktopDirectory(void);
void createLinkToLauncher(void);

int main(int argc, char **argv) {
	char *name = getBaseDirectory();
	CreateDirectory(name, NULL);
	free(name);
	writeFile("launcher.exe", &launcher, launcherSize);
	writeFile("Conquer.jar", &conquer, conquerSize);
	writeFile("Conquer_frontend.jar", &conquerFrontend, conquerFrontendSize);
	writeFile("uninstall.exe", &uninstall, uninstallSize);
	createLinkToLauncher();
	return EXIT_SUCCESS;
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
void createLinkToLauncher(void) {
	char *name = getDesktopDirectory();
	strcat(name, "/Conquer.lnk");
	char *launcher = getBaseDirectory();
	strcat(launcher, "/launcher.exe");
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
