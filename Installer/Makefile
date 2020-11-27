all: setup
	$(CC) $(CFLAGS) -Wall -Wextra -pedantic -O3 src/main.c -c -o bin/main.o
	$(CC) $(CFLAGS) bin/main.o bin/resource.o bin/inc.o -o Installer.exe
all-debug: setup
	$(CC) $(CFLAGS) -Wall -Wextra -pedantic -Og -g3 src/main.c -c -o bin/main.o
	$(CC) $(CFLAGS) bin/main.o bin/resource.o bin/inc.o -o Installer.exe
clean:
	rm -rf bin
setup:
	mkdir -p bin
	windres -I include -I src src/uninstallResource.rc bin/uninstallResource.o
	$(CC) -O3 -Wall -Wextra -pedantic -c src/uninstall.c -o bin/uninstall.o
	$(CC) bin/uninstall.o bin/uninstallResource.o -o uninstall.exe
	$(CC) src/inc.s -c -o bin/inc.o
	windres -I include -I src src/resource.rc bin/resource.o
format:
	clang-format --style=file -verbose -i src/*.c include/ids.h