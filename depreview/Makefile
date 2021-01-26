CFLAGS += -s -O3 -Wall -Wextra -pedantic
all:
	$(CC) $(CFLAGS) -fanalyzer -o depreview src/depreview.c
clean:
	rm -f depreview
	rm -f depreview.exe
format:
	clang-format --style=file -verbose -i src/*.c
