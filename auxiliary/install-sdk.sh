#!/usr/bin/env bash
type -P mvn &>/dev/null && echo Found maven || echo No maven was found! Try running \"install-sdk.sh\" manually!
type -P mvn &>/dev/null || exit 0
mvn install:install-file -Dfile=/usr/share/conquer/m2/Conquer.jar -DpomFile=/usr/share/conquer/m2/conquer-2.1.0.pom
mvn install:install-file -Dfile=/usr/share/conquer/m2/Conquer_resources.jar -DpomFile=/usr/share/conquer/m2/conquer_resources-2.1.0.pom
mvn install:install-file -Dfile=/usr/share/conquer/m2/ConquerFrontendSPI.jar -DpomFile=/usr/share/conquer/m2/conquer_spi-2.1.0.pom
echo To install the jars in the local repo, run \"install-sdk.sh\"!
