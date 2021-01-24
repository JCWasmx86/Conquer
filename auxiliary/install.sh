#!/usr/bin/sh
mvn install:install-file -Dfile=Conquer.jar -DpomFile=conquer-1.5.0.pom
mvn install:install-file -Dfile=Conquer_resources.jar -DpomFile=conquer_resources-1.5.0.pom
mvn install:install-file -Dfile=ConquerFrontendSPI.jar -DpomFile=conquer_spi-1.5.0.pom
