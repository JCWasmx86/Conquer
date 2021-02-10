ARGS = --disable-version-header -X -O2 --vapidir=. -X -I"$(JAVA_HOME)/include"
ifeq ($(OS),Windows_NT)
	OS_TYPE = windows
	ARGS += -X -Wl,--no-insert-timestamp -X -I"$(JAVA_HOME)/include/win32" # -X -mwindows
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
