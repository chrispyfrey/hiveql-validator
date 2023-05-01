#!/bin/bash

for HQL_FILE in ../hql/*; do
    java HiveQLValidator $HQL_FILE
done
