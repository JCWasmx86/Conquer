dependencies = [
	dependency('gtk+-3.0'),
	dependency('gio-2.0'),
	dependency('gee-0.8'),
	dependency('json-glib-1.0'),
	dependency('libarchive'),
	dependency('libcurl'),
	meson.get_compiler('c').find_library('dl'),
	meson.get_compiler('vala').find_library('posix'),
]
sources = [
	'JVM.vala',
	'createDirectory.c',
	'invokeJVM.c',
	'main.vala',
	'utils.c',
	'utils.vala'
]

subdir('gen')
#sources += ['gen/adoptopenJDKURL.vala', 'gen/libericaJDKURL.vala']
pymodule = import('python3')
python3 = pymodule.find_python()
sources += [gen_adopt,gen_liberica]
res = run_command(python3, '-c', 'import os; print(os.environ["JAVA_HOME"])')
if res.returncode() == 0
  sdk_var = res.stdout()
else 
  exit
endif
if build_machine.system() == 'linux'
  inc_path = 'linux'
  sources += [gen_linuxJava, 'linux/JVM.vala','linux/load.c','linux/utils.vala']
else
  inc_path = 'win32' 
  source += ['windows/JVM.vala', 'windows/load.c', 'windows/programFiles.c', 'windows/utils.vala']
endif
executable('conquer_launcher',
	sources,
	dependencies: dependencies,
	include_directories : [include_directories(sdk_var.substring(0,-1)+'/include/'),include_directories(sdk_var.substring(0,-1)+'/include/'+inc_path)]
)
