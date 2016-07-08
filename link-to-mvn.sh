#!/bin/sh

SCADA_BR_SRC=`pwd`;

SCADA_BR=`pwd`/../scada-br-maven
mkdir $SCADA_BR

DEST=$SCADA_BR/src/main

mkdir $SCADA_BR/src
mkdir $SCADA_BR/src/main
mkdir $DEST/webapp
mkdir $DEST/webapp/WEB-INF
mkdir $DEST/resources

ln -s $SCADA_BR_SRC/db $DEST/webapp/WEB-INF/db

ln -s $SCADA_BR_SRC/pom.xml  $SCADA_BR/pom.xml 
ln -s $SCADA_BR_SRC/WebContent/soundmanager2.swf  $DEST/webapp/ 
ln -s $SCADA_BR_SRC/WebContent/index.jsp $DEST/webapp/
ln -s $SCADA_BR_SRC/WebContent/audio $DEST/webapp/
ln -s $SCADA_BR_SRC/WebContent/exception $DEST/webapp/
ln -s $SCADA_BR_SRC/WebContent/graphics $DEST/webapp/
ln -s $SCADA_BR_SRC/WebContent/images $DEST/webapp/
ln -s $SCADA_BR_SRC/WebContent/resources $DEST/webapp/
ln -s $SCADA_BR_SRC/WebContent/uploads $DEST/webapp/ 

ln -s $SCADA_BR_SRC/WebContent/WEB-INF/web.xml $DEST/webapp/WEB-INF/ 
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/applicationContext.xml $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/dwr.xml $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/springDispatcher-servlet.xml $DEST/webapp/WEB-INF/

ln -s $SCADA_BR_SRC/WebContent/WEB-INF/dox $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/ftl $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/jsp $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/scripts $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/snippet $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/tld $DEST/webapp/WEB-INF/
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/tags $DEST/webapp/WEB-INF/

ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/changeSnippetMap.properties $DEST/resources 
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/chartSnippetMap.properties $DEST/resources
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/i18n.properties $DEST/resources
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/messages_de.properties $DEST/resources
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/messages_en.properties $DEST/resources
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/messages.properties $DEST/resources
ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/setPointSnippetMap.properties $DEST/resources

#Do use own ones
#ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/env.properties $DEST/resources
#ln -s $SCADA_BR_SRC/WebContent/WEB-INF/classes/log4j.xml $DEST/resources


cd $DEST/

ln -s $SCADA_BR_SRC/src java

