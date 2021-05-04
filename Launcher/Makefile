ARGS = --disable-version-header --vapidir=.
C_ARGS = -O2 -g3 -flto -Wincompatible-pointer-types
ifeq ($(OS),Windows_NT)
	OS_TYPE = windows
	C_ARGS += -Wl,--no-insert-timestamp -mwindows -lgcc
	JNI_OS = win32
else
	OS_TYPE = linux
	C_ARGS = -ldl
	JNI_OS = linux
endif

FLAGS = --pkg=gtk+-3.0 --pkg=posix --pkg=gio-2.0 --pkg=gee-0.8 --pkg=json-glib-1.0 --pkg=libarchive --pkg=libcurl

all: generateFiles downloadVAPIFiles build
generateFiles: src/gen/getLinuxJavaDirectory.vala.in src/gen/adoptopenJDKURL.vala.in src/gen/libericaJDKURL.vala.in
	cpp -E -P src/gen/getLinuxJavaDirectory.vala.in >src/gen/getLinuxJavaDirectory.vala
	cpp -E -P src/gen/adoptopenJDKURL.vala.in >src/gen/adoptopenJDKURL.vala
	cpp -E -P src/gen/libericaJDKURL.vala.in >src/gen/libericaJDKURL.vala
build: src/*.vala src/$(OS_TYPE)/*.vala src/*.c src/$(OS_TYPE)/*.c
	valac $(ARGS) src/*.vala src/$(OS_TYPE)/*.vala src/gen/*.vala $(FLAGS) -C -d out
	$(CC) out/src/*.c out/src/gen/*.c src/*.c out/src/$(OS_TYPE)/*.c src/$(OS_TYPE)/*.c `pkg-config --cflags --libs gtk+-3.0 gio-2.0 gee-0.8 json-glib-1.0 libarchive libcurl` -o conquer_launcher -I "$(JAVA_HOME)"/include -I "$(JAVA_HOME)"/include/$(JNI_OS) $(C_ARGS)
downloadVAPIFiles:
	wget -nc https://gitlab.gnome.org/GNOME/vala-extra-vapis/-/raw/master/libcurl.deps
	wget -nc https://gitlab.gnome.org/GNOME/vala-extra-vapis/-/raw/master/libcurl.vapi
clean:
	rm -f conquer_launcher conquer_launcher.exe
	rm -f src/gen/*.vala
	rm -f *.o
	rm -f *.exe
	rm -rf out
	rm -rf data
	rm -f *.zip
	rm -rf Windows-10
	rm -f *.zst
	rm -f *.dll
	rm -rf nsisunz
	rm -rf AccessControl
	rm -f *.nsi
	touch src/main.vala
Installer.exe: all buildInstallerDir downloadNsisunz downloadAccessControl
	/mingw32/bin/makensis -V4 Installer.nsi
buildIncludeFile: instdir.nsi.inc
	cpp -E -P instdir.nsi.inc>instdir.nsi
buildX64: x64.nsi.inc
	cpp -E -P x64.nsi.inc>x64.nsi
buildInstallerDir: buildIncludeFile buildX64 Installer.nsi.inc
	cpp -E -P Installer.nsi.inc>Installer.nsi
#TODO: Look for licensing issues
downloadNsisunz:
	wget -nc https://nsis.sourceforge.io/mediawiki/images/1/1c/Nsisunz.zip
	unzip -o Nsisunz.zip
	cp nsisunz/Release/nsisunz.dll .
downloadAccessControl:
	wget -nc https://nsis.sourceforge.io/mediawiki/images/4/4a/AccessControl.zip
	mkdir AccessControl && mv AccessControl.zip AccessControl && cd AccessControl && unzip -o AccessControl.zip||true
format:
	clang-format --style=file -verbose -i src/*.c src/**/*.c
	uncrustify -c format.cfg src/*.vala src/windows/*.vala src/linux/*.vala --no-backup
