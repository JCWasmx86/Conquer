.section rodata

.global zipfile
.align 8
zipfile:
.incbin "data.zip"

.global zipfileSize
.align 8
zipfileSize:
.int zipfileSize - zipfile
