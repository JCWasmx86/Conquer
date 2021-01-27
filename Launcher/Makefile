CFLAGS2 = libcjson.a
ifeq ($(OS),Windows_NT)
	INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
	LDCFLAGS = -Wl,--no-insert-timestamp -Wl,-Bstatic -lm -ldl -larchive -lurlmon -lz -lbz2 -llzma -lbcrypt -liconv -lzstd -llz4 -lexpat
	CFLAGS2 += -mwindows
else
	INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
	LDCFLAGS = -Wl,-Bstatic -lm -Wl,-Bdynamic -ldl -lcurl -larchive
endif

CFLAGS += -Wno-deprecated-declarations -Wno-incompatible-pointer-types

INCLUDES += -I"include" -I"cJSON"
all: launcher
buildCJSON:
	if [ ! -d cJSON ]; then git clone https://github.com/DaveGamble/cJSON;cd cJSON; git config pull.rebase false;cd ..; else cd cJSON&&git pull origin master&&cd ..; fi
	rm -f cJSON/*.so
	rm -f cJSON/*.a
	rm -f cJSON/*.dll
	cd cJSON&&make CFLAGS="-O3 -s" &&mkdir -p cjson&&cp cJSON.h cjson&&cp libcjson.a ..&&cd ..
all-gtk: gtklauncher launcher
all-win: launcher

config.o: buildCJSON
	$(CC) $(CFLAGS) $(INCLUDES) -c -o config.o src/config.c
directoryStructure.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o directoryStructure.o src/directoryStructure.c
download.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o download.o src/download.c
extract.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o extract.o src/extract.c
load.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o load.o src/load.c
utils.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o utils.o src/utils.c
main.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o main.o src/main.c
jvm.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o jvm.o src/jvm.c
gtk.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c -o gtk.o src/gtkmain.c `pkg-config --cflags-only-I --libs gtk+-3.0`
launcher: config.o directoryStructure.o download.o extract.o load.o utils.o main.o jvm.o
	$(CC) $^ -o launcher $(CFLAGS2) $(LDCFLAGS)
gtklauncher: config.o directoryStructure.o download.o extract.o load.o utils.o jvm.o gtk.o
	$(CC) $^ -o conquer_launcher $(CFLAGS2) $(LDCFLAGS) `pkg-config --libs gtk+-3.0` -pthread


format:
	clang-format --style=file -verbose -i src/*.c include/launcher.h
clean:
	rm -f launcher
	rm -f launcher.exe
	rm -f libcjson.a
	rm -rf cJSON
	rm -f conquer_launcher
	rm -f *.o
