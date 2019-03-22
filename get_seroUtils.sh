#!/bin/bash

curl --header 'Host: liquidtelecom.dl.sourceforge.net' --user-agent 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0' --header 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' --header 'Accept-Language: en-GB,en;q=0.5' --referer 'https://sourceforge.net/projects/scadabr/files/Dependency/seroUtils.jar/download' --cookie '_ga=GA1.2.1965164509.1526549518; _gid=GA1.2.67519373.1526549518; __gads=ID=760cd624c839f515:T=1526549520:S=ALNI_MYLZ8YQgYn3PztCF8z30TJ_fUjVdA; _scp=1526549551658.1602999522; _scs=1526549551659.326419338' --header 'Upgrade-Insecure-Requests: 1' 'https://liquidtelecom.dl.sourceforge.net/project/scadabr/Dependency/seroUtils.jar' --output 'seroUtils.jar'

mv seroUtils.jar ./WebContent/WEB-INF/lib/