#!/bin/bash
hashToCheck=3a4efdf56d6309a369a39e251ce3997631d888fca7a64dfe94a13587a2953ced

function deleteIfExists() {
    if [ -e $1 ] ; then
        rm $1
    fi
}

function moveToLib() {
    mv -f $1 ./WebContent/WEB-INF/lib/
}

function calculateSha256sum() {
    echo $(sha256sum $1 | head -c 64)
}

function getSeroUtilsFromSourceforge() {
    deleteIfExists seroUtils.jar
    curl --header 'Host: netcologne.dl.sourceforge.net' --user-agent 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0' --header 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' --header 'Accept-Language: en-GB,en;q=0.5' --referer 'https://sourceforge.net/projects/scadabr/files/Dependency/seroUtils.jar/download' --cookie '_ga=GA1.2.1965164509.1526549518; _gid=GA1.2.67519373.1526549518; __gads=ID=760cd624c839f515:T=1526549520:S=ALNI_MYLZ8YQgYn3PztCF8z30TJ_fUjVdA; _scp=1526549551658.1602999522; _scs=1526549551659.326419338' --header 'Upgrade-Insecure-Requests: 1' 'https://netcologne.dl.sourceforge.net/project/scadabr/Dependency/seroUtils.jar' --output 'seroUtils.jar'
    calculateSha256sum seroUtils.jar
}

function getSeroUtilsFromGitHub() {
    deleteIfExists seroUtils.jar
    curl -L 'https://github.com/SCADA-LTS/SeroUtils/releases/download/1.0.0/seroUtils.jar' > 'seroUtils.jar'
    calculateSha256sum seroUtils.jar
}

function getSeroUtilsFromLocal() {
    if [ -e './WebContent/WEB-INF/lib/seroUtils.jar' ] ; then
       calculateSha256sum './WebContent/WEB-INF/lib/seroUtils.jar'
    else
       echo -12
    fi
}

function checkSeroUtils() {
    if [ "$hashToCheck" = "$1" ] ;then
        true
    else
        false
    fi
}

function isSeroUtils() {
    if checkSeroUtils $(getSeroUtilsFromLocal) ;then
       printf 'SeroUtils from local'
       true
    elif checkSeroUtils $(getSeroUtilsFromSourceforge) ;then
       printf 'SeroUtils from Sourceforge'
       moveToLib seroUtils.jar
       true
    elif checkSeroUtils $(getSeroUtilsFromGitHub) ;then
       printf 'SeroUtils from GitHub'
       moveToLib seroUtils.jar
       true
    else
       printf 'Correct SeroUtils file is missing'
       deleteIfExists seroUtils.jar
       false
    fi
}

i=0;
end=2;
while : ; do
    [ "$i" -lt "$end" ] && ! isSeroUtils || break
    i=$(( i + 1 ))
done