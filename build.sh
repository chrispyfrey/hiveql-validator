#!/bin/bash

CLASSPATH="`pwd`/lib/*:`pwd`/src/"
EXE_PATH="`pwd`/src/"
javac src/HiveQLValidator.java

LIGHT_GREEN='\033[1;32m'
BROWN_ORANGE='\033[0;33m'
LIGHT_CYAN='\033[1;36m'
RESET='\033[0m'

echo -e "${LIGHT_GREEN}The HiveQLValidator Java program has been compiled to ${EXE_PATH}\n"
echo -e "${LIGHT_CYAN}Add the below export command to your ~/.zshrc (Zsh) or ~/.bashrc (Bash) file to allow program execution from any file location."
echo -e "${BROWN_ORANGE}    export CLASSPATH='${CLASSPATH}'\n"
echo -e "${LIGHT_CYAN}To validate the syntax within an HQL file, run:${BROWN_ORANGE} java HiveQLValidator /path/to/my_hql_file.hql\n"
echo -e "${LIGHT_CYAN}You can also add an alias command for executing the program to your ~/.zshrc or ~/.bashrc file as shown below."
echo -e "${BROWN_ORANGE}    alias hiveql-validator='java HiveQLValidator'\n"
echo -e "${LIGHT_CYAN}Program execution with alias:${BROWN_ORANGE} hiveql-validator /path/to/my_hql_file.hql${RESET}"
