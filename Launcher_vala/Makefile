ARGS = --disable-version-header -X -O2 --vapidir=.
ifeq ($(OS),Windows_NT)
	OS_TYPE = windows
	ARGS += -X -Wl,--no-insert-timestamp -X -mwindows
else
	OS_TYPE = linux
endif

FLAGS = --pkg=gtk+-3.0 --pkg=posix --pkg=gio-2.0 --pkg=gee-0.8 --pkg=json-glib-1.0 --pkg=libarchive --pkg=libcurl

all: generateFiles downloadVAPIFiles build

generateFiles: src/gen/getLinuxJavaDirectory.vala.in
	cpp -E -P src/gen/getLinuxJavaDirectory.vala.in >src/gen/getLinuxJavaDirectory.vala
build:
	valac $(ARGS) src/*.vala src/$(OS_TYPE)/*.vala src/gen/getLinuxJavaDirectory.vala $(FLAGS) -o conquer_launcher
downloadVAPIFiles:
	wget -nc https://gitlab.gnome.org/GNOME/vala-extra-vapis/-/raw/master/libcurl.deps
	wget -nc https://gitlab.gnome.org/GNOME/vala-extra-vapis/-/raw/master/libcurl.vapi
