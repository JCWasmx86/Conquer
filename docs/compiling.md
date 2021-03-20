# Compiling

## Hardware requisites

1. 8 GB RAM (4 GB could work, but at least 8 GB are recommended)
2. 250 MB disk space + space for all dependencies.
3. An internet connection for every build.

## Debian and its descendants

### Prerequisites

1. You need Debian sid or bullseye, as these are the only versions with the package `openjdk-15-jdk`.
2. These packages are required, too: git, libarchive-dev, libcurl4-openssl-dev, bash, musescore3, libgtk-3-dev,
   pkg-config, valac, libgee-0.8-dev, libjson-glib-dev, make
3. `JAVA_HOME` has to be set.

### Compiling

Just type:

```
gradle assemble
```

This will need around 2-3 minutes, (Further builds are faster) and will build these .deb files in `debs`:

```
debs/conquer.deb
debs/conquer-engine.deb
debs/conquer-gui.deb
debs/conquer-default.deb
debs/conquer-sdk.deb
debs/conquer-default-scenarios.deb
debs/conquer-default-music.deb
```

### TL;DR

```
sudo apt install -y openjdk-15-jdk libarchive-dev git libcurl4-openssl-dev bash musescore3 pkg-config libgtk-3-dev valac libgee-0.8-dev libjson-glib-dev make wget
git clone https://github.com/JCWasmx86/Conquer
cd Conquer
#You can replace gradle with ./gradlew
gradle assemble # Or "gradle :ConquerFrontend:run" to run Conquer
```

### Notes

Only arm64, arm (Although a bit untested), i386 and amd64 are supported.

## Windows

### Prerequisites

1. MSYS2
2. Use the MINGW64 shell and install these packages:
   git base-devel mingw-w64-x86_64-libarchive mingw-w64-x86_64-curl mingw-w64-x86_64-gcc mingw-w64-x86_64-dlfcn
   mingw-w64-x86_64-headers-git zip unzip mingw-w64-x86_64-gtk3 mingw-w64-x86_64-libgee mingw-w64-x86_64-json-glib mingw-w64-i686-nsis
3. Install musescore3: You have to download the installer and run it.
4. Install Java 15: For example AdoptOpenJDK. You must have JAVA_HOME defined and accessible from the MSYS Shell.
5. Install gradle (Version 6.8)

### Compiling

Just type:

```
gradle assemble
```

This will need around 6-10 minutes, and will build `Launcher/Installer.exe`.

### TL;DR

```
pacman -S --noconfirm git base-devel mingw-w64-x86_64-libarchive mingw-w64-x86_64-curl mingw-w64-x86_64-gcc mingw-w64-x86_64-dlfcn mingw-w64-x86_64-headers-git zip unzip 
pacman -S --noconfirm mingw-w64-x86_64-wget mingw-w64-x86_64-json-glib mingw-w64-x86_64-libgee mingw-w64-x86_64-gtk3 mingw-w64-i686-nsis
git clone https://github.com/JCWasmx86/Conquer
cd Conquer
gradle assemble # Or "gradle :ConquerFrontend:run" to run Conquer
```

### Notes

Only amd64 is supported. i386 support is available, but it seems to crash. (Maybe an ABI mismatch between the MSVC-compiled jvm.dll and the GCC compiled conquer_launcher.exe)


## MacOS

Not supported (Contributions are welcome)

## Other linux distributions

Semi-supported with a bit of manual work. (Contributions are welcome)

# Using in eclipse or IntelliJ Idea

## eclipse

For generating the eclipse project files, type:

```
gradle eclipse
```

After that, you must open eclipse and import a gradle project:

File->Import...->Gradle->Existing Gradle Project

Now you just have to follow the wizard.

## IntelliJ Idea

Use:

```
gradle idea
gradle openIdea
```

# FAQ

## The gradle daemon is crashing!

This means you don't have enough RAM available. Try closing programs that use too much (E.g. MS Teams, Skype, Chrome,
...)

## The build is failing while building the music!

Try again. Sometimes the conversion fails, but that's quite rare.

## On linux the conversion is failing on headless systems!

Set `QT_QPA_PLATFORM` to `offscreen` (`export QT_QPA_PLATFORM=offscreen`), otherwise Qt will look for a running X
server.

## Some music doesn't seem to be generated/included in jar files.

The build system is currently quite unstable. Because of this, it is possible, that `Conquer_frontend_resources.jar`
will be packaged before the music is copied. A bugfix would be appreciated. The workaround would be to build again.
