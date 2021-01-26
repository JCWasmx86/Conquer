INCLUDES += -I"include" -I"cJSON"
FILES = src/config.c src/directoryStructure.c src/download.c src/extract.c src/load.c src/jvm.c src/utils.c
all: buildCJSON
	$(CC) $(INCLUDES) -o launcher $(FILES) src/main.c -O3 -fanalyzer -s -static-libgcc libcjson.a -Wl,-Bstatic -lm -Wl,-Bdynamic -ldl -lcurl -larchive -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -Wno-deprecated-declarations
debug: buildCJSON
	$(CC) $(INCLUDES) -DDEBUG_BUILD -o launcher $(FILES) src/main.c -Og -g3 -rdynamic -fanalyzer -static-libgcc libcjson.a -Wl,-Bstatic -lm -Wl,-Bdynamic -larchive -ldl -lcurl -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -Wno-deprecated-declarations
	$(CC) $(INCLUDES) -o conquer_launcher $(FILES) src/gtkmain.c -O3 -fanalyzer -s -static-libgcc libcjson.a -Wl,-Bstatic -lm -Wl,-Bdynamic -ldl -lcurl -larchive -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -Wno-deprecated-declarations `pkg-config --cflags --libs gtk+-3.0`
buildCJSON:
	if [ ! -d cJSON ]; then git clone https://github.com/DaveGamble/cJSON;cd cJSON; git config pull.rebase false;cd ..; else cd cJSON&&git pull origin master&&cd ..; fi
	rm -f cJSON/*.so
	rm -f cJSON/*.a
	rm -f cJSON/*.dll
	cd cJSON&&make CFLAGS="-O3 -s" &&mkdir -p cjson&&cp cJSON.h cjson&&cp libcjson.a ..&&cd ..
all-gtk: all
	$(CC) $(INCLUDES) -o conquer_launcher $(FILES) src/gtkmain.c -O3 -fanalyzer -s -static-libgcc libcjson.a -Wl,-Bstatic -lm -Wl,-Bdynamic -ldl -lcurl -larchive -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -Wno-deprecated-declarations `pkg-config --cflags --libs gtk+-3.0`
all-win: buildCJSON
	cc -mwindows $(INCLUDES) -Wl,--no-insert-timestamp -o launcher $(FILES) src/main.c -O3 -s -static-libgcc libcjson.a -Wl,-Bstatic -lm -ldl -larchive -lurlmon -lz -lbz2 -llzma -lbcrypt -liconv -lzstd -llz4 -lexpat -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" -Wno-deprecated-declarations -Wno-incompatible-pointer-types
all-win-debug: buildCJSON
	cc -mwindows $(INCLUDES) -DDEBUG_BUILD -o launcher $(FILES) src/main.c -Og -g3 libcjson.a -static-libgcc -Wl,-Bstatic -lm -ldl -larchive -lurlmon -lz -lbz2 -llzma -lbcrypt -liconv -lzstd -llz4 -lexpat -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" -Wno-deprecated-declarations -Wno-incompatible-pointer-types
format:
	clang-format --style=file -verbose -i $(FILES) src/main.c src/gtkmain.c include/launcher.h
clean:
	rm -f launcher
	rm -f launcher.exe
	rm -f libcjson.a
	rm -rf cJSON
	rm -f conquer_launcher
	rm -f *.o
