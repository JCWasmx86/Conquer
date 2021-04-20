#!/usr/bin/sh
type -P mvn &>/dev/null && echo "Found maven" || echo "No maven was found!"
type -P mvn &>/dev/null || exit
mvn install:install-file -Dfile=/usr/share/conquer/m2/Conquer.jar -DpomFile=/usr/share/conquer/m2/conquer-2.0.0.pom
mvn install:install-file -Dfile=/usr/share/conquer/m2/Conquer_resources.jar -DpomFile=/usr/share/conquer/m2/conquer_resources-2.0.0.pom
mvn install:install-file -Dfile=/usr/share/conquer/m2/ConquerFrontendSPI.jar -DpomFile=/usr/share/conquer/m2/conquer_spi-2.0.0.pom
echo To install the jars in the local repo, run \"install-sdk.sh\"!
