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
