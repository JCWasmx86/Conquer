@echo off
mvn install:install-file -Dfile=Conquer.jar -DpomFile=conquer-2.0.0.pom
mvn install:install-file -Dfile=Conquer_resources.jar -DpomFile=conquer_resources-2.0.0.pom
mvn install:install-file -Dfile=ConquerFrontendSPI.jar -DpomFile=conquer_spi-2.0.0.pom
