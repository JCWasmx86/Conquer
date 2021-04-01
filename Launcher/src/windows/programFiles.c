#include <shlobj.h>
#include <stdlib.h>
#include <windows.h>

char *launcher_getProgramFiles() {
	TCHAR pf[MAX_PATH];
	SHGetSpecialFolderPathA(NULL, pf, CSIDL_PROGRAM_FILES, FALSE);
	return strdup(pf);
}
