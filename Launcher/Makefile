ARGS = --disable-version-header -X -O2 --vapidir=. -X -I"$(JAVA_HOME)/include" -X -D__FILE__=\"redactedForPrivacy\" -X -Wno-builtin-macro-redefined -X -flto
ifeq ($(OS),Windows_NT)
	OS_TYPE = windows
	ARGS += -X -Wl,--no-insert-timestamp -X -I"$(JAVA_HOME)/include/win32" -X -mwindows -X -lgcc
else
	OS_TYPE = linux
	ARGS += -X -I$(JAVA_HOME)/include/linux -X -ldl
endif

FLAGS = --pkg=gtk+-3.0 --pkg=posix --pkg=gio-2.0 --pkg=gee-0.8 --pkg=json-glib-1.0 --pkg=libarchive --pkg=libcurl
# Second way to find JAVA_HOME
JENV = `readlink -f /usr/bin/java|sed "s:bin/java::"`
all: generateFiles downloadVAPIFiles build
generateFiles: src/gen/getLinuxJavaDirectory.vala.in src/gen/adoptopenJDKURL.vala.in src/gen/libericaJDKURL.vala.in
	cpp -E -P src/gen/getLinuxJavaDirectory.vala.in >src/gen/getLinuxJavaDirectory.vala
	cpp -E -P src/gen/adoptopenJDKURL.vala.in >src/gen/adoptopenJDKURL.vala
	cpp -E -P src/gen/libericaJDKURL.vala.in >src/gen/libericaJDKURL.vala
build: src/*.vala src/$(OS_TYPE)/*.vala src/*.c src/$(OS_TYPE)/*.c
	valac $(ARGS) src/*.vala src/$(OS_TYPE)/*.vala src/gen/*.vala $(FLAGS) src/$(OS_TYPE)/*.c src/*.c -o conquer_launcher -X -I$(JENV)/include -X -I$(JENV)/include/linux -X -I$(JENV)/include/win32
downloadVAPIFiles:
	wget -nc https://gitlab.gnome.org/GNOME/vala-extra-vapis/-/raw/master/libcurl.deps
	wget -nc https://gitlab.gnome.org/GNOME/vala-extra-vapis/-/raw/master/libcurl.vapi
clean:
	rm -f libcurl.deps libcurl.vapi
	rm -f conquer_launcher conquer_launcher.exe
	rm -f src/gen/*.vala
	rm -f *.o
	rm -f *.exe
	rm -rf data
	rm -f *.zip
	rm -rf Windows-10
	rm -f *.zst
	rm -f *.dll
	rm -rf nsisunz
	rm -rf AccessControl
	rm -f *.nsi
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
