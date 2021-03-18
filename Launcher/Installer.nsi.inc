//#include "x64.nsi"
!include "MUI2.nsh"
Name "Conquer"
OutFile "Installer.exe"
!addplugindir /x86-ansi AccessControl/Plugins/i386-ansi
!addplugindir .
RequestExecutionLevel highest
#include "instdir.nsi"
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "../auxiliary/Conquer.license"
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH
!insertmacro MUI_LANGUAGE "English"
Section
	SetOutPath $INSTDIR
	CreateDirectory $INSTDIR
	File "data.zip"
	Pop $0
	AccessControl::GrantOnFile "$INSTDIR" "(BU)" "GenericRead + GenericWrite"
	Pop $0
	nsisunz::UnzipToStack "$INSTDIR\data.zip" "$INSTDIR"
	Pop $0
	StrCmp $0 "success" ok
		DetailPrint "$0"
		Goto skiplist
	ok:
	next:
		Pop $0
		DetailPrint $0
	StrCmp $0 "" 0 next
	skiplist:
	Pop $0
	WriteUninstaller $INSTDIR\uninst.exe
	SetShellVarContext all
	CreateShortcut "$DESKTOP\Conquer.lnk" "$INSTDIR\conquer_launcher.exe"
	Delete "$INSTDIR\data.zip"
SectionEnd
Section uninstall
	RMDir /r /REBOOTOK $INSTDIR
	;TODO: Doesn't work.
	Delete "$DESKTOP\Conquer.lnk"
SectionEnd
