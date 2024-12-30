This library is not for public usage, but you can download it if you feel curious or just find it useful.

The author does not give any warranties.

# The plan

## 1) Plug the library in.
```xml
<dependency>
	<groupId>io.github.vladimirpianykh</groupId>
	<artifactId>bpa4j</artifactId>
	<version>1.0.0</version>
</dependency>
```

## 2) Use `_init` snippet in the `Main` class and `_start` snippet inside of the `main` method.

## 3) Fill in the application.
* Add all necessary users.
* Create editable objects required with `_editable` snippet.
* Register groups.
* Add new screens (`Feature`s) by calling `registerList()`, `registerBoard()`, etc on classes in the `com.futurefactory.defaults.features` package.
* 
## 4) Create and fill in the `helppath.cfg` file.
Call `Navigator.init()` and add all the mechanics implemented to the helppath.cfg list.
See the `HelpView` documentation for details.

# Troubleshooting

## Jar file does not connect
- Do not forget about `dependencies` block.
