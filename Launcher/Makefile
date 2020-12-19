INCLUDES += -I"include" -I"cJSON"
FILES = src/config.c src/directoryStructure.c src/download.c src/extract.c src/load.c src/jvm.c src/main.c src/utils.c
all: buildCJSON
	$(CC) $(INCLUDES) -o launcher $(FILES) -O3 -fanalyzer -s -static-libgcc libcjson.a -Wl,-Bstatic -lm -Wl,-Bdynamic -ldl -lcurl -larchive -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -Wno-deprecated-declarations
debug: buildCJSON
	$(CC) $(INCLUDES) -o launcher $(FILES) -Og -g3 -rdynamic -fanalyzer -static-libgcc libcjson.a -Wl,-Bstatic -lm -Wl,-Bdynamic -larchive -ldl -lcurl -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -Wno-deprecated-declarations
buildCJSON:
	if [ ! -d cJSON ]; then git clone https://github.com/DaveGamble/cJSON;cd cJSON; git config pull.rebase false;cd ..; else cd cJSON&&git pull origin master&&cd ..; fi
	rm -f cJSON/*.so
	rm -f cJSON/*.a
	rm -f cJSON/*.dll
	cd cJSON&&make CFLAGS="-O3 -s" &&mkdir -p cjson&&cp cJSON.h cjson&&cp libcjson.a ..&&cd ..
all-win: buildCJSON
	cc $(INCLUDES) -o launcher $(FILES) -O3 -s -static-libgcc libcjson.a -Wl,-Bstatic -lm -ldl -larchive -lurlmon -lz -lbz2 -llzma -lbcrypt -liconv -lzstd -llz4 -lexpat -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" -Wno-deprecated-declarations -Wno-incompatible-pointer-types
all-win-debug: buildCJSON
	cc $(INCLUDES) -o launcher $(FILES) -Og -g3 libcjson.a -static-libgcc -Wl,-Bstatic -lm -ldl -larchive -lurlmon -lz -lbz2 -llzma -lbcrypt -liconv -lzstd -llz4 -lexpat -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" -Wno-deprecated-declarations -Wno-incompatible-pointer-types
format:
	clang-format --style=file -verbose -i $(FILES) include/launcher.h
clean:
	rm -f launcher
	rm -f launcher.exe
	rm -f libcjson.a
	rm -rf cJSON
