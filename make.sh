#!/bin/bash

npm install -g sass
npm install -g yarn
cd src/main/javascript && npm install

for FILE in src/main/po/*.po ; do
    lang=$(basename $FILE .po)
	mkdir -p src/main/resources/i18n/$lang
	#msgcat instead of msgfmt
    msgcat -p -o src/main/resources/i18n/$lang/phpmyadmin.properties $FILE  
done

mvn package
