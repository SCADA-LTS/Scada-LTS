#! /bin/bash

perl ../../buildscripts/jslink.pl -pre cat -i program.js -l lib.js -o out.js 
java -jar ../../buildscripts/lib/custom_rhino.jar -e "load('out.js'); load('program.js');"
