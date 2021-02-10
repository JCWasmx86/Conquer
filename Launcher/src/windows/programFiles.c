#include <shlobj.h>
#include <windows.h>
#include <stdlib.h>

char* launcher_getProgramFiles() {
	TCHAR pf[MAX_PATH];
	SHGetSpecialFolderPathA(NULL, pf, CSIDL_PROGRAM_FILES, FALSE);
	return strdup(pf);
}