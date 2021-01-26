#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

void depreview(char*);

int main(int argc, char** argv) {
	for(int i = 1;i<argc;i++) {
		size_t len=strlen(argv[i]);
		if(strcmp(&argv[i][len-5],".class")) {
			depreview(argv[i]);
		}else {
			printf("Skipping: %s\n",argv[i]);
		}
	}
}
//Remove preview feature flag from classfile.
void depreview(char* file) {
	FILE *fp =fopen(file, "rb");
	assert(fp);
	fseek(fp, 0, SEEK_END);
	//Get size of file
	size_t l = ftell(fp);
	fseek(fp, 0, SEEK_SET);
	unsigned char* buffer=calloc(l,1);
	assert(buffer);
	//Read entire file into buffer
	size_t cnt=fread(buffer,1,l,fp);
	assert(cnt==l);
	//A classfile header consists of:
	//4 bytes magic number: 0xca 0xfe 0xba 0xbe
	//2 bytes minor version (Only set in really old Java, or if the classfile was compiled with preview features)
	//2 bytes major version (E.g. 59 for Java 15)
	//If compiled with preview features, minor is 0xffff.
	if(buffer[4]==0xff&&buffer[5]==0xff) {
		buffer[4]=0x00;
		buffer[5]=0x00;
		printf("Converting %s...\n",file);
		fclose(fp);
		fp=fopen(file,"wb");
		size_t written = fwrite(buffer,1,l,fp);
		assert(written == cnt);
	}else {
		printf("Skipping %s, as it wasn't compiled with preview features enabled!\n",file);
	}
	free(buffer);
	fclose(fp);
}
