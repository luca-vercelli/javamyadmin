#!/bin/bash

#npm install -g sass
#npm install -g yarn
#cd src/main/javascript && npm install

mkdir -p target/classes
for FILE in src/main/po/*.po ; do
    lang=$(basename $FILE .po)
	msgfmt --java2 -d target/classes/ -r org.javamyadmin -l $lang $FILE
done

#mvn package
