#include "launcher.h"
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv) {
	initDirectoryStructure();
	Configuration config = getConfiguration();
	if (config->usedJVM == NULL) {
		downloadJDK();
	} else {
		if (!checkForJava(config->usedJVM)) {
			fprintf(stderr,
					"The specified java installation %s is not suitable.\n",
					config->usedJVM);
			downloadJDK();
			free(config->usedJVM);
			config->usedJVM = NULL;
		}
	}
#ifndef _WIN32
	if(!geteuid()){
		fputs("Don't run conquer as root!\n",stderr);
		return EXIT_SUCCESS;
	}
#endif
	runJVM(config);
	freeConfiguration(config);
}
