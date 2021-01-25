all: setup
	$(CC) -mwindows -Wl,--no-insert-timestamp $(CFLAGS) -Wall -Wextra -pedantic -O3 src/main.c -c -o bin/main.o
	$(CC) -mwindows -Wl,--no-insert-timestamp $(CFLAGS) bin/main.o bin/resource.o bin/inc.o -o Installer.exe
all-debug: setup
	$(CC) -mwindows $(CFLAGS) -Wall -Wextra -pedantic -Og -g3 src/main.c -c -o bin/main.o
	$(CC) -mwindows $(CFLAGS) bin/main.o bin/resource.o bin/inc.o -o Installer.exe
clean:
	rm -rf bin
	rm -f uninstall.exe
	rm -f Installer.exe
	rm -f Launcher.exe
setup:
	mkdir -p bin
	windres -I include -I src src/uninstallResource.rc bin/uninstallResource.o
	$(CC) -mwindows -O3 -Wall -Wextra -pedantic -c src/uninstall.c -o bin/uninstall.o
	$(CC) -mwindows -Wl,--no-insert-timestamp bin/uninstall.o bin/uninstallResource.o -o uninstall.exe
	$(CC) -mwindows src/inc.s -c -o bin/inc.o
	windres -I include -I src src/resource.rc bin/resource.o
format:
	clang-format --style=file -verbose -i src/*.c include/ids.h
