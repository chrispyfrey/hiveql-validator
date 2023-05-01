# HiveQL Validator
A HiveQL syntax checker based on the Apache Hive parser.

## Requirements
- Java 8 JDK
- ```java -version```
- ```which java```

## Installation
- Download or clone this repository.
- Navigate to your new hiveql-validator folder.
- Run: ```./build.sh``` to compile the program and print some useful copy/paste instructions.
- Add the hiveql-validator/src/ and hiveql-validato/lib/* folders to your Java classpath.
- Optionally alias the program call for easier use.

## Usage
- Call the hiveql-validator program with an .hql file as the only argument
- ```java HiveQLValidator /path/to/my/hql_query.hql```
- With recommended alias
- ```hiveql-validator /path/to/my/hql_query.hql```
