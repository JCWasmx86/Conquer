#include <aclapi.h>
#include <assert.h>
#include <shfolder.h>
#include <shlobj.h>
#include <winbase.h>

#include <archive.h>
#include <archive_entry.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

extern uint8_t launcher;
extern uint8_t conquer;
extern uint8_t conquerFrontend;
extern uint8_t uninstall;
extern uint8_t conquerLicense;
extern uint8_t license;
extern uint8_t jlayerJar;
extern uint8_t mp3SpiJar;
extern uint8_t tritonusJar;
extern uint8_t jorbisJar;
extern uint8_t vorbisspiJar;
extern uint8_t resourcesJar;
extern uint8_t guiResourcesJar;
extern uint8_t conquerFrontendSPIJar;
extern uint64_t launcherSize;
extern uint64_t conquerSize;
extern uint64_t conquerFrontendSize;
extern uint64_t uninstallSize;
extern uint64_t conquerLicenseSize;
extern uint64_t licenseSize;
extern uint64_t jlayerJarSize;
extern uint64_t mp3SpiJarSize;
extern uint64_t tritonusJarSize;
extern uint64_t jorbisJarSize;
extern uint64_t vorbisspiJarSize;
extern uint64_t resourcesJarSize;
extern uint64_t guiResourcesJarSize;
extern uint64_t conquerFrontendSPIJarSize;
static char *getBaseDirectory(void);
static void writeFile(const char *fileName, void *buf, uint64_t size);
static char *getDesktopDirectory(void);
static void createLinkToLauncher(void);
static void downloadJava(void);
extern char *getURL(void);
static void extractJava(void);
extern void extract(const char *filename, const char *outputDir,
					void (*callback)(void *, const char *, int, int),
					void *data);
int main(int argc, char **argv) {
	char *name = getBaseDirectory();
	CreateDirectory(name, NULL);
	HANDLE h = CreateFileA(name, GENERIC_WRITE, FILE_SHARE_DELETE, NULL,
						   CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	SetSecurityInfo(h, SE_FILE_OBJECT, DACL_SECURITY_INFORMATION, NULL, NULL,
					NULL, NULL);
	free(name);
	writeFile("launcher.exe", &launcher, launcherSize);
	writeFile("Conquer.jar", &conquer, conquerSize);
	writeFile("Conquer_frontend.jar", &conquerFrontend, conquerFrontendSize);
	writeFile("uninstall.exe", &uninstall, uninstallSize);
	writeFile("Conquer.license", &conquerLicense, conquerLicenseSize);
	writeFile("LICENSE.txt", &license, licenseSize);
	writeFile("jlayer.jar", &jlayerJar, jlayerJarSize);
	writeFile("mp3spi.jar", &mp3SpiJar, mp3SpiJarSize);
	writeFile("tritonus.jar", &tritonusJar, tritonusJarSize);
	writeFile("jorbis.jar", &jorbisJar, jorbisJarSize);
	writeFile("vorbisspi.jar", &vorbisspiJar, vorbisspiJarSize);
	writeFile("Conquer_resources.jar", &resourcesJar, resourcesJarSize);
	writeFile("Conquer_frontend_resources.jar", &guiResourcesJar,
			  guiResourcesJarSize);
	writeFile("ConquerFrontendSPI.jar", &conquerFrontendSPIJar,
			  conquerFrontendSPIJarSize);
	createLinkToLauncher();
	downloadJava();
	extractJava();
	MessageBox(NULL, "Installed Conquer!", "Success",
			   MB_OK | MB_ICONINFORMATION | MB_DEFBUTTON1 | MB_APPLMODAL |
				   MB_TOPMOST);
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
static void downloadJava(void) {
	MessageBox(NULL, "Downloading Java 15! Please wait!", "Please wait!",
			   MB_OK | MB_ICONINFORMATION | MB_DEFBUTTON1 | MB_APPLMODAL |
				   MB_TOPMOST);
	char *base = getBaseDirectory();
	char *output = calloc(strlen(base) + 20, 1);
	sprintf(output, "%s\\java-15.zip", base);
	assert(URLDownloadToFile(NULL, getURL(), output, 0, NULL) == S_OK);
	free(base);
	free(output);
}
static void extractJava(void) {
	char *base = getBaseDirectory();
	char *input = calloc(strlen(base) + 20, 1);
	sprintf(input, "%s\\java-15.zip", base);
	char *output = calloc(strlen(base) + 20, 1);
	sprintf(output, "%s\\java-15", base);
	extract(input, output, NULL, NULL);
	free(base);
	free(output);
	free(input);
}
