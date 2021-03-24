#if _WIN32 || _WIN64
	#if _WIN64
		InstallDir "$PROGRAMFILES64\Conquer"
	#else
		InstallDir "$PROGRAMFILES\Conquer"
	#endif
#endif
