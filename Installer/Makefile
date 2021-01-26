all: Installer.exe

Installer.exe: setup main.o resource.o inc.o uinstall.exe
	$(CC) -mwindows -Wl,--no-insert-timestamp $(CFLAGS) bin/main.o bin/resource.o bin/inc.o -o Installer.exe
main.o:
	$(CC) -mwindows -Wl,--no-insert-timestamp $(CFLAGS) -Wall -Wextra -pedantic -O3 src/main.c -c -o bin/main.o
setup: makeDir uninstallResource.o uninstall.exe inc.o
makeDir:
	mkdir -p bin
uninstallResource.o:
	windres -I include -I src src/uninstallResource.rc bin/uninstallResource.o
uninstall.o:
	$(CC) -mwindows -O3 -Wall -Wextra -pedantic -c src/uninstall.c -o bin/uninstall.o
uninstall.exe: uninstall.o
	$(CC) -mwindows -Wl,--no-insert-timestamp bin/uninstall.o bin/uninstallResource.o -o uninstall.exe
inc.o:
	$(CC) -mwindows src/inc.s -c -o bin/inc.o
resource.o:
	windres -I include -I src src/resource.rc bin/resource.o
format:
	clang-format --style=file -verbose -i src/*.c include/ids.h
clean:
	rm -rf bin
	rm -f uninstall.exe
	rm -f Installer.exe
	rm -f Launcher.exe
