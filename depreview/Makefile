CFLAGS += -s -O3 -Wall -Wextra -pedantic
ifeq ($(OS),Windows_NT)
	LDCFLAGS = -Wl,--no-insert-timestamp -Wl,-Bstatic -larchive -lz -lbz2 -llzma -lbcrypt -liconv -lzstd -llz4 -lexpat
else
	LDCFLAGS = -larchive
endif
all: src/depreview.c
	$(CC) $(CFLAGS) -o depreview src/depreview.c $(LDCFLAGS)
clean:
	rm -f depreview
	rm -f depreview.exe
format:
	clang-format --style=file -verbose -i src/*.c
