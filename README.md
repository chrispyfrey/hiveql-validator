# HiveQL Validator
A completely local HiveQL syntax checker based on the Apache Hive 4.0 parser.

## Requirements
- Only tested on the Java 8 JDK.
- ```$ java -version```
- Consider installing Java 8 if you run into issues with another version.
- Recommend [Homebrew](https://brew.sh/) package manager if you need to install a Java JDK on MacOS.
- The Temurin8 distribution works good for me if you are on a M1/M2 Mac.

## Installation (Unix/Unix-like)
- Download or clone this repository.
- Navigate to your new ```hiveql-validator``` folder.
- The next step is probably bad form. I am still reading up on Java packaging. The ```HiveQLValidator.java``` file just needs to be compiled with the .jar files in ```lib/``` on the Java classpath.
- Run: ```$ ./build.sh``` to compile the program and print some useful copy/paste instructions. May need to run ```$ chmod +x build.sh``` first.
- Add the ```hiveql-validator/src/``` and ```hiveql-validator/lib/*``` folders to the Java classpath as a command in your shell start script.
- ```export CLASSPATH='path/to/your/hiveql-validator/lib/*:path/to/your/hiveql-validator/src/'```
- Optionally alias the program call in your shell start script for more convenient use.
- ```alias hiveql-validator='java HiveQLValidator'```

## Usage
- Call the hiveql-validator program with an .hql file as the only argument
- ```$ java HiveQLValidator /path/to/my/hql_query.hql```
- With recommended alias
- ```$ hiveql-validator /path/to/my/hql_query.hql```

## Development Notes
- The parser does not mind comments, but leaving them in can make it hard to match other keywords that the parser will not accept.
- The following keywords are not recognized by the parser: ```SET, USE, ADD JAR```
- I kind of played whack-a-mole with like thirty HQL files from work when creating regex patterns for pre-processing HiveQL queries. Probably missed some things.
- This thing could probably be reduced way down as far as .jar files go, but I forgot how much of it's own thing Java is and need to hit the books.
