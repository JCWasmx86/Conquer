This is the SDK for Conquer, version 1.6.0

To install the jar files to the local maven repo, use either:

install.bat

on windows or

./install.sh

on linux.

Furthermore this zip file contains an executable, depreview.

It solves the problem of the preview features.

Conquer uses preview features. Every classfile (e.g. from plugins), that uses
Conquer has to be compiled with preview features enabled.

The problem:
-A classfile compiled with preview features for Java version N can only be executed by Java version N.
==>A JVM Upgrade would break every plugin, as it is not loadable anymore.

The (quite hacky) solution:

Use the "depreview" executable to change the flags of the classfile to normal, so you can't see a difference to 
normal classfiles.


Usage:

depreview some/path/to/your/Class.class some/path/someJar.jar ...


