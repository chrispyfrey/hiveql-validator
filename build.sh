#!/bin/bash

CLASSPATH="`pwd`/lib/*:`pwd`/src/"
EXE_PATH="`pwd`/src/"
javac src/HiveQLValidator.java

echo -e "The HiveQLValidator Java program has been compiled to $EXE_PATH\n"

echo "Add the below export command to your ~/.zshrc (Zsh) or ~/.bashrc (Bash) file to allow program execution from any file location."
echo -e "    export CLASSPATH='$CLASSPATH'\n"

echo -e "To validate the syntax within an HQL file, run: java HiveQLValidator my_hql_file.hql\n"

echo "You can also add an alias command for executing the program to your ~/.zshrc or ~/.bashrc file like below:"
echo -e "    alias hiveql-validator='java HiveQLValidator'\n"
echo "Example with alias: hiveql-validator my_hql_file.hql"
