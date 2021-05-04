cpp = find_program('cpp')
gen_adopt = custom_target('gen_adopt', input: ['adoptopenJDKURL.vala.in'], output: ['adoptopenJDKURL.vala'], command: [cpp, '-E', '-P', '@INPUT0@', '-o', '@OUTPUT0@'])
gen_liberica = custom_target('gen_liberica', input: ['libericaJDKURL.vala.in'], output: ['libericaJDKURL.vala'], command: [cpp, '-E', '-P', '@INPUT0@', '-o', '@OUTPUT0@'])
gen_linuxJava = custom_target('gen_linuxJava', input: ['getLinuxJavaDirectory.vala.in'], output: ['getLinuxJavaDirectory.vala'], command: [cpp, '-E', '-P', '@INPUT0@', '-o', '@OUTPUT0@'])
