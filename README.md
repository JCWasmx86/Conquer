# Conquer
![Linux](https://github.com/JCWasmx86/Conquer/workflows/Linux/badge.svg)
![Windows](https://github.com/JCWasmx86/Conquer/workflows/Windows/badge.svg)

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

### Contributing

[General](contributing/general.md) \
[Contribute as a programmer](contributing/code.md) \
[Contribute as a non-programmer](contributing/non-code.md)

### Compiling

See [compiling](docs/compiling.md)

### History

*Conquer* first was a project for school. Because I already planned writing a separate frontend, I decoupled the logic/the game engine (In the subdirectory Conquer) from the GUI (Originally written with greenfoot), as
greenfoot had a lot of limitations and a lot of ugly and spooky code (Like reflection) was required to remove this restrictions. Furthermore the dependence on JavaFX and no package support were quite important.

After I gave my project to the teacher, I started to remove some little greenfoot-specific code in the game-engine and started to write a far, far superior GUI using Java Swing. I improved
the code structure and made some changes. Around 70% are unchanged and are the original code of my assignment. (The greenfoot-based code may be pushed to the Legacy branch)


### Credits

1. [cJSON](https://github.com/DaveGamble/cJSON) for the JSON-Parser
2. [libarchive](https://libarchive.org/) for the library to extract the downloaded zip files
3. [libcurl](https://curl.se/libcurl/) to download java.
4. [Musescore](https://musescore.org/en) that made it possible to provide music.
5. [flaticon.com](href="https://www.flaticon.com/) for a huge majority images
