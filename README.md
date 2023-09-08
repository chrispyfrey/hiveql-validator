# HiveQL Validator
A completely local HiveQL syntax checker based on the Apache Hive 4.0 parser.

## Requirements
- Only tested on the Java 8 JDK.
- ```$ java -version```
- Consider installing the Java 8 JDK if you run into compile issues with other version.
  - If you already have a JDK installed, you will need to change your ```JAVA_HOME``` environment variable to point at v1.8 before compiling or build your own compile command using the v1.8 ```javac``` call.
- Recommend [Homebrew](https://brew.sh/) package manager if you need to install a Java JDK on MacOS.
  - ```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"```
- The Temurin8 distribution works good for me and looks like it is replacing OpenJDK.
  - ```brew install --cask temurin8```

## Installation (Unix/Unix-like)
- Download or clone this repository.
- Navigate to your new ```hiveql-validator``` folder.
- Run: ```$ ./build.sh``` to compile the program and print some useful copy/paste instructions. May need to run ```$ chmod +x build.sh``` first to make the build script executable.
- Add the ```hiveql-validator/src/``` and ```hiveql-validator/lib/*``` folders to the Java classpath as a command in your shell start script.
  - ```export CLASSPATH='path/to/your/hiveql-validator/lib/*:path/to/your/hiveql-validator/src/'```
- Optionally alias the program call in your shell start script for more convenient use.
  - ```alias hiveql-validator='java HiveQLValidator'```

## Usage
- Call the hiveql-validator program with an .hql file or a folder containing .hql files as the only argument
  - ```$ java HiveQLValidator /path/to/my/hql_query.hql```
  - ```$ java HiveQLValidator /path/to/my/hql_folder```
- With recommended alias
  - ```$ hiveql-validator /path/to/my/hql_query.hql```
  - ```$ hiveql-validator /path/to/my/hql_folder```

## Development Notes
- The parser does not mind comments, but leaving them in can yield false positives for keyword patterns that the parser will not accept.
- The following keywords are not recognized by the 4.0 parser: ```SET, USE, ADD JAR```
- I kind of played whack-a-mole with like thirty random HQL files when creating regex patterns for pre-processing HiveQL queries. Probably missed some things.
- My next steps are to put something better together for packaging/distribution and to reduce dependencies.
