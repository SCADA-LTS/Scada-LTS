#!/bin/bash -x

sha256() {
    curl -LJO https://github.com/SCADA-LTS/SeroUtils/releases/download/1.0.0/seroUtils.jar
    curl -o seroUtils.json https://api.github.com/repos/SCADA-LTS/SeroUtils/releases
    online_sha256=$(grep -oP '(?<="body": ")[^"]*' seroUtils.json)
    local_sha256="$(cat seroUtils.jar | sha256sum | head -c 64)"
}

i=0
while : ; do
    sha256
    i=$(( i + 1 ))
    [ "$i" -lt 3 ] && [ ! -e seroUtils.jar ] && [ "$online_sha256" != "$local_sha256" ] || break
done