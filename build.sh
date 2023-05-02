#!/bin/bash

CLASSPATH="`pwd`/lib/*:`pwd`/src/"
EXE_PATH="`pwd`/src/"
javac src/HiveQLValidator.java

BOLD_PURPLE='\033[1;34m'
BOLD_GREEN='\033[1;32m'
BOLD_CYAN='\033[1;36m'
BROWN_ORANGE='\033[0;33m'
RESET='\033[0m'

echo -e "${BOLD_GREEN}✅ The HiveQLValidator Java program has been successfully compiled in ${EXE_PATH}\n"
echo -e "${BOLD_CYAN}🗒  Add the below export command to your ~/.zshrc (Zsh) or ~/.bashrc (Bash) file to allow program execution from any file location."
echo -e "${BROWN_ORANGE}📁    export CLASSPATH='${CLASSPATH}'\n"
echo -e "${BOLD_CYAN}🗒  Raw program execution:${BROWN_ORANGE} java HiveQLValidator /path/to/my_hql_file.hql\n"
echo -e "${BOLD_PURPLE}💡 Add an alias command to your ~/.zshrc (Zsh) or ~/.bashrc (Bash) file for simpler program execution as shown below."
echo -e "${BROWN_ORANGE}📁    alias hiveql-validator='java HiveQLValidator'\n"
echo -e "${BOLD_PURPLE}💡 Program execution with alias:${BROWN_ORANGE} hiveql-validator /path/to/my_hql_file.hql${RESET}"
