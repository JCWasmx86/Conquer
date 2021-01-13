.section rodata

.global launcher
.align 8
launcher:
.incbin "launcher.exe"

.global launcherSize
.align 8
launcherSize:
.int launcherSize - launcher

.global conquer
.align 8
conquer:
.incbin "Conquer.jar"

.global conquerSize
.align 8
conquerSize:
.int conquerSize - conquer

.global conquerFrontend
.align 8
conquerFrontend:
.incbin "Conquer_frontend.jar"

.global conquerFrontendSize
.align 8
conquerFrontendSize:
.int conquerFrontendSize - conquerFrontend

.global uninstall
.align 8
uninstall:
.incbin "uninstall.exe"

.global uninstallSize
.align 8
uninstallSize:
.int uninstallSize - uninstall

.global conquerLicense
.align 8
conquerLicense:
.incbin "Conquer.license"

.global conquerLicenseSize
.align 8
conquerLicenseSize:
.int conquerLicenseSize - conquerLicense

.global license
.align 8
license:
.incbin "LICENSE.txt"

.global licenseSize
.align 8
licenseSize:
.int licenseSize - license


.global jlayerJar
.align 8
jlayerJar:
.incbin "jlayer.jar"

.global jlayerJarSize
.align 8
jlayerJarSize:
.int jlayerJarSize - jlayerJar


.global mp3SpiJar
.align 8
mp3SpiJar:
.incbin "mp3spi.jar"

.global mp3SpiJarSize
.align 8
mp3SpiJarSize:
.int mp3SpiJarSize - mp3SpiJar


.global tritonusJar
.align 8
tritonusJar:
.incbin "tritonus.jar"

.global tritonusJarSize
.align 8
tritonusJarSize:
.int tritonusJarSize - tritonusJar


.global jorbisJar
.align 8
jorbisJar:
.incbin "jorbis.jar"

.global jorbisJarSize
.align 8
jorbisJarSize:
.int jorbisJarSize - jorbisJar


.global vorbisspiJar
.align 8
vorbisspiJar:
.incbin "vorbisspi.jar"

.global vorbisspiJarSize
.align 8
vorbisspiJarSize:
.int vorbisspiJarSize - vorbisspiJar
