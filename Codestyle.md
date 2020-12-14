# Codestyle

### C

An .clang-format file is provided. Write with compatibility with POSIX in mind and try to avoid Windows API functions as much as possible in order to reduce
the number of `#ifdef`s. 
Use PascalCase for structure names and camelCase for variable/function names. You can always assume the latest compiler (Only gcc is supported, but clang *should* work most of the time).
Use as much `assert`s as needed. (For example if `malloc` fails and there is no other way to save the program)

### Java

User-facing strings should be localized (Like in Messages), but not in exception messages.
Tabs should be used and braces should be on the same line:
```
if(foo) {

}
```
instead of
```
if(foo)
{

}
```
You can always assume the latest java version, so you can use every feature of the language without hesitations.
Add as many testcases as needed, if you add a new method. Write Javadoc for every new public class/method/constant.
Always qualify field- and method accesses with `this`:
```
public class Foo {
	private int foo;

	void a() {
		...
	}
	void b() {
		this.a();
		SomeExternalClass.method(this.foo);
	}
}
```
Encapsulate as much as possible and prefer the usage of Streams and Lambda-expressions over loops.
Make sure that each method only has one responsibility.


### Properties files for translations

These should be sorted alphabetically (`sort -o foo.properties foo.properties`)