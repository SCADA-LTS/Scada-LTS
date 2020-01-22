#!/bin/bash
hashToCheck=3a4efdf56d6309a369a39e251ce3997631d888fca7a64dfe94a13587a2953ced

function getSeroUtilsFromSourceforge() {
    curl --header 'Host: netcologne.dl.sourceforge.net' --user-agent 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0' --header 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' --header 'Accept-Language: en-GB,en;q=0.5' --referer 'https://sourceforge.net/projects/scadabr/files/Dependency/seroUtils.jar/download' --cookie '_ga=GA1.2.1965164509.1526549518; _gid=GA1.2.67519373.1526549518; __gads=ID=760cd624c839f515:T=1526549520:S=ALNI_MYLZ8YQgYn3PztCF8z30TJ_fUjVdA; _scp=1526549551658.1602999522; _scs=1526549551659.326419338' --header 'Upgrade-Insecure-Requests: 1' 'https://netcologne.dl.sourceforge.net/project/scadabr/Dependency/seroUtils.jar' --output 'seroUtils.jar'
    echo $(sha256sum seroUtils.jar | head -c 64)
}

function getSeroUtilsFromGitHub() {
    curl -LJO https://github.com/SCADA-LTS/SeroUtils/releases/download/1.0.0/seroUtils.jar > 'seroUtils.jar'
    echo $(sha256sum seroUtils.jar | head -c 64)
}

function getSeroUtilsFromLocal() {
    if [ -e './WebContent/WEB-INF/lib/seroUtils.jar' ] ; then
       echo $(sha256sum './WebContent/WEB-INF/lib/seroUtils.jar' | head -c 64)
    else
       echo -12312414
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
       true
    elif $(checkSeroUtils $(getSeroUtilsFromSourceforge)) || $(checkSeroUtils $(getSeroUtilsFromGitHub)) ;then
       mv -f seroUtils.jar ./WebContent/WEB-INF/lib/
       true
    else
       rm seroUtils.jar
       false
    fi
}

i=0;
end=3;
while : ; do
    i=$(( i + 1 ))
    [ "$i" -lt "$end" ] && ! isSeroUtils || break
done