#!/bin/bash

find ../../src/java/* -type f -name \*java | 
    sed -e "s/^\.\.\/\.\.\/src\/java\///" |
    sed -e "s/\.java//" | 
    sed -e "s/\//./g" > classlist

cvs diff -u classlist > changes  
