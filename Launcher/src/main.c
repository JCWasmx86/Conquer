#include "launcher.h"
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv) {
	// First, initialize the directory structure that is later used by the game.
	initDirectoryStructure();
	// Try to read the configuration file
	Configuration config = getConfiguration();
	if (config->usedJVM == NULL) {
		downloadJDK();
	} else {
		// If the specified path is no usable Java installation(==Not there),
		// still download it.
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
	if (!geteuid()) {
		fputs("Don't run conquer as root!\n", stderr);
		return EXIT_SUCCESS;
	}
#endif
	runJVM(config);
	freeConfiguration(config);
}
