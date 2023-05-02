# HiveQL Validator
A HiveQL syntax checker based on the Apache Hive parser.

## Requirements
- Only tested on the Java 8 JDK.
- ```java -version```
- Consider installing Java 8 if you run into issues with another version.
- Recommend [Homebrew](https://brew.sh/) package manager if you need to install a Java JDK.
- The Temurin8 distribution works good for me if you are on a M1/M2 MBP.

## Installation
- Download or clone this repository.
- Navigate to your new hiveql-validator folder.
- The next step is probably bad form if not outright dangerous for the user. I am still reading up on Java packaging. The HiveQLValidator.java file just needs to be compiled with the .jar files in lib/ on the Java classpath.
- Run: ```./build.sh``` to compile the program and print some useful copy/paste instructions.
- Add the hiveql-validator/src/ and hiveql-validato/lib/* folders to your Java classpath. Instructions are printed to console.
- Optionally alias the program call for more convenient use. Instructions are printed to console.

## Usage
- Call the hiveql-validator program with an .hql file as the only argument
- ```java HiveQLValidator /path/to/my/hql_query.hql```
- With recommended alias
- ```hiveql-validator /path/to/my/hql_query.hql```
