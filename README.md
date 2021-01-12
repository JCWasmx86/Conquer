# Conquer
![Linux](https://github.com/JCWasmx86/Conquer/workflows/Linux/badge.svg)
![Windows](https://github.com/JCWasmx86/Conquer/workflows/Windows/badge.svg)
![GitHub all releases](https://img.shields.io/github/downloads/JCWasmx86/Conquer/total?style=social)
![GitHub issues by-label](https://img.shields.io/github/issues-raw/JCWasmx86/Conquer/good%20first%20issue)

You are the proud leader of a clan! Manage your resources! Move your troops! Conquer the cities of your enemies!
Will you fail and get defeated or will you lead your clan to endless glory and wealth?


*Conquer* is a turn-based strategy game with unique features.

### Features

1. Builtin modding support
  - A plugin API for adding new, fancy stuff
  - An API for adding new strategies for the computer enemy to the game.
2. Embeddability
3. Exchangeable GUI
4. Highly advanced algorithms
5. A steadily growing set of default plugins.
6. Adding new scenarios is easily possible.

### Installing

#### Linux
```
# Add repo to the package manager sources (apt)
echo "deb https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/  main extras" | sudo tee -a /etc/apt/sources.list
# Add public key
wget https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/jcwasmx86.pgp&&sudo apt-key add jcwasmx86.pgp&&rm jcwasmx86.pgp
# Install conquer
sudo apt update&&sudo apt install conquer
```
#### Windows
Fetch `Installer.exe` from [the latest release](https://github.com/JCWasmx86/Conquer/releases/tag/1.1.0) and execute it. (Needs admin rights)


### Contributing

[General](contributing/general.md) \
[Contribute as a programmer](contributing/code.md) \
[Contribute as a non-programmer](contributing/non-code.md)

### Compiling

See [docs/compiling.md](docs/compiling.md)

### History

*Conquer* first was a project for school. Because I already planned writing a separate frontend, I decoupled the logic/the game engine (In the subdirectory Conquer) from the GUI (Originally written with greenfoot), as
greenfoot had a lot of limitations and a lot of ugly and spooky code (Like reflection) was required to remove this restrictions.
Another reason were the missing capabilites for managing a huge project (E.g. no package support, the IDE was lacking a lot of features, ...)

After I gave my project to the teacher, I started to remove some little greenfoot-specific code in the game-engine and started to write a far, far superior GUI using Java Swing. I improved
the code structure and made some changes. Around 70% are unchanged and are the original code of my assignment. (The greenfoot-based code may be pushed to the Legacy branch)


### Credits

1. [cJSON](https://github.com/DaveGamble/cJSON) for the JSON-Parser
2. [libarchive](https://libarchive.org/) for the library to extract the downloaded zip files
3. [libcurl](https://curl.se/libcurl/) to download java.
4. [Musescore](https://musescore.org/en) that made it possible to provide music.
5. [flaticon.com](https://www.flaticon.com/) for the huge majority of images
