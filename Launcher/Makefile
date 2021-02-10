ARGS = --disable-version-header -X -O2 --vapidir=. -X -I"$(JAVA_HOME)/include"
ifeq ($(OS),Windows_NT)
	OS_TYPE = windows
	ARGS += -X -Wl,--no-insert-timestamp -X -I"$(JAVA_HOME)/include/win32" -X -mwindows
else
	OS_TYPE = linux
	ARGS += -X -I$(JAVA_HOME)/include/linux -X -ldl
endif

FLAGS = --pkg=gtk+-3.0 --pkg=posix --pkg=gio-2.0 --pkg=gee-0.8 --pkg=json-glib-1.0 --pkg=libarchive --pkg=libcurl

all: generateFiles downloadVAPIFiles build

generateFiles: src/gen/getLinuxJavaDirectory.vala.in
	cpp -E -P src/gen/getLinuxJavaDirectory.vala.in >src/gen/getLinuxJavaDirectory.vala
	cpp -E -P src/gen/adoptopenJDKURL.vala.in >src/gen/adoptopenJDKURL.vala
	cpp -E -P src/gen/libericaJDKURL.vala.in >src/gen/libericaJDKURL.vala
build:
	valac $(ARGS) src/*.vala src/$(OS_TYPE)/*.vala src/gen/*.vala $(FLAGS) src/$(OS_TYPE)/*.c src/*.c -o conquer_launcher
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
	rm -f data.zip
	rm -rf Windows-10
	rm -f *.zst
Installer.exe: all buildInstallRC buildIncludeFile install.o
	$(CC) -Wl,--no-insert-timestamp $(CFLAGS) install.o installResource.o inc.o -o Installer.exe --static -larchive -lz -llzma -lbcrypt -lbz2 -liconv -lzstd -lexpat -llz4 -mwindows
install.o: install/main.c
	$(CC) $(CFLAGS) -c -o install.o install/main.c
buildIncludeFile: install/inc.s
	$(CC) -c -o inc.o install/inc.s
buildInstallRC: install/resource.rc
	windres -I include -I install install/resource.rc installResource.o
uninstall.exe: buildUninstallRC uninstallObject
	$(CC) -mwindows -Wl,--no-insert-timestamp uninstall.o uninstallResource.o -o uninstall.exe
buildUninstallRC: uninstall/resource.rc
	windres -I include -I uninstall uninstall/resource.rc uninstallResource.o
uninstallObject: uninstall/main.c
	$(CC) $(CFLAGS) -O3 -Wall -Wextra -pedantic -c uninstall/main.c -o uninstall.o
