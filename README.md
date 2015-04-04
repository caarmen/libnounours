Nounours
========

This library is the Java implementation of Nounours used in the Android "Noonoors" apps
(https://play.google.com/store/apps/details?id=ca.rmen.nounours)

This project contains the library and a Swing implementation.

To build the library, run this from the root of the project:
```
./gradlew clean jar
```

The jar will be found here:
```
./libnounours/build/libs/libnounours-1.0.jar
```


To build the Swing implementation, run this from the root of the project:
```
./gradlew clean build
```

The jar will be found here:
```
./swingnours/build/libs/swingnours-1.0-standalone.jar
```

To run the Swing app, the working directory must contain the resource files. So:

```
cd swingnours/src/main/resources/themes/nounours/
java -jar ../../../../../build/libs/swingnours-1.0-standalone.jar
```


Note
====
This code was written 2009 and has not been maintained since. The only changes
made recently (April 2015) were to gradlize the project, update copyright
headers with the license information, and to create the license and readme files.
There is an applet class in the Swing project, but this project currently does
not build a jar file that can be used with the applet html file. 

