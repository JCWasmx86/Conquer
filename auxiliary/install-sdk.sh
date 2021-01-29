#!/usr/bin/sh
mvn install:install-file -Dfile=/usr/share/conquer/m2/Conquer.jar -DpomFile=/usr/share/conquer/m2/conquer-1.6.0.pom
mvn install:install-file -Dfile=/usr/share/conquer/m2/Conquer_resources.jar -DpomFile=/usr/share/conquer/m2/conquer_resources-1.6.0.pom
mvn install:install-file -Dfile=/usr/share/conquer/m2/ConquerFrontendSPI.jar -DpomFile=/usr/share/conquer/m2/conquer_spi-1.6.0.pom
