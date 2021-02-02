# Compiling

## Debian and its descendents

### Prerequisites

1. You need Debian sid or bullseye, as these are the only versions with the package `openjdk-15-jdk`.
2. This packages are required, too: git, libarchive-dev, libcurl4-openssl-dev, bash, musescore3, libgtk-3-dev, pkg-config
3. Gradle is required. Conquer is only tested with 6.8

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
sudo apt install -y openjdk-15-jdk libarchive-dev git libcurl4-openssl-dev bash musescore3 pkg-config libgtk-3-dev
git clone https://github.com/JCWasmx86/Conquer
cd Conquer
gradle assemble # Or "gradle :ConquerFrontend:run" to run Conquer
```

### Notes

Only arm64 and amd64 are supported.

## Windows

### Prerequisites

1. MSYS2
2. Use the MINGW64 shell and install this packages:
	git base-devel mingw-w64-x86_64-libarchive mingw-w64-x86_64-curl mingw-w64-x86_64-gcc mingw-w64-x86_64-dlfcn mingw-w64-x86_64-headers-git zip unzip
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
git clone https://github.com/JCWasmx86/Conquer
cd Conquer
gradle assemble # Or "gradle :ConquerFrontend:run" to run Conquer
```

### Notes

Only amd64 is supported.


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
