#!/bin/bash

curl --header 'Host: liquidtelecom.dl.sourceforge.net' --user-agent 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0' --header 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' --header 'Accept-Language: en-GB,en;q=0.5' --referer 'https://sourceforge.net/projects/scadabr/files/Dependency/seroUtils.jar/download' --cookie '_ga=GA1.2.254292041.1560931069; _gid=GA1.2.254292041.1560931069; __gads=ID=1fb94423dd43c1ae:T=1558967390:S=ALNI_MbQSvQdMr1dBrQg2F2Stxa-s-V1XA;' --header 'Upgrade-Insecure-Requests: 1' 'https://liquidtelecom.dl.sourceforge.net/project/scadabr/Dependency/seroUtils.jar' --output 'seroUtils.jar'

mv seroUtils.jar ./WebContent/WEB-INF/lib/