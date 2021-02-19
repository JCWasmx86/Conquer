#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <shellapi.h>
#include <shlobj.h>
char *getBaseDirectory(void);
void deleteFile(const char *fileName);
int deleteDirectory(char *lpszDir);

int main() {
	deleteFile("uninstall.exe");
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
		deleteDirectory(name);
	}
	free(name);
}
int deleteDirectory(char *lpszDir) {
	int len = strlen(lpszDir);
	char *pszFrom = calloc(len + 2, 1);
	assert(pszFrom);
	strcpy(pszFrom, lpszDir);
	pszFrom[len] = 0;
	pszFrom[len + 1] = 0;
	SHFILEOPSTRUCT fileop;
	fileop.hwnd = NULL;
	fileop.wFunc = FO_DELETE;
	fileop.pFrom = pszFrom;
	fileop.pTo = NULL;
	fileop.fFlags = FOF_NOCONFIRMATION | FOF_SILENT;
	fileop.fAnyOperationsAborted = FALSE;
	fileop.lpszProgressTitle = NULL;
	fileop.hNameMappings = NULL;
	int ret = SHFileOperation(&fileop);
	free(pszFrom);
	return ret == 0;
}
