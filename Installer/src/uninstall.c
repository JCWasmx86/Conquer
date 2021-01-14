#include <assert.h>
#include <shlobj.h>
#include <shfolder.h>
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

char *getBaseDirectory(void);
void deleteFile(const char *fileName);

int main() {
	deleteFile("launcher.exe");
	deleteFile("Conquer.jar");
	deleteFile("Conquer_frontend.jar");
	deleteFile("uninstall.exe");
	deleteFile("Conquer.license");
	deleteFile("LICENSE.txt");
	deleteFile("jlayer.jar");
	deleteFile("mp3spi.jar");
	deleteFile("tritonus.jar");
	deleteFile("jorbis.jar");
	deleteFile("vorbisspi.jar");
	deleteFile("");
	MessageBox(
		NULL,
		"Uninstalled Conquer! To finish the removal, please restart sometime.",
		"Success",
		MB_OK | MB_ICONINFORMATION | MB_DEFBUTTON1 | MB_APPLMODAL | MB_TOPMOST);
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
void deleteFile(const char *fileName) {
	char *name = getBaseDirectory();
	strcat(name, fileName);
	// Try to remove the corresponding file.
	if (!remove(name)) {
		// If it fails, register the file for deletion after reboot.
		if (!MoveFileExA(name, NULL, MOVEFILE_DELAY_UNTIL_REBOOT)) {
			fprintf(stderr, "MoveFileExA failed!\n");
			DWORD error = GetLastError();
			char *s = NULL;
			FormatMessageA(
				FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM |
					FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL, error, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
				(LPSTR)&s, 0, NULL);
			fprintf(stderr, "Error: %s\n", s);
			LocalFree(s);
		}
	}
	free(name);
}
