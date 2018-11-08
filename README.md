# [Scada-LTS](http://scada-lts.org)

[![Build Status](https://img.shields.io/travis/grzesiekb/json.svg?style=flat-square)](https://travis-ci.org/sdtabilit/Scada-LTS)
[![GPL-2.0](https://img.shields.io/npm/l/gb-json.svg?style=flat-square)](https://github.com/sdtabilit/Scada-LTS/blob/master-sdtabilit/LICENSE)
[![](https://images.microbadger.com/badges/version/dockergb/scadalts-dev.svg)](https://microbadger.com/images/dockergb/scadalts-dev "Get your own version badge on microbadger.com")
[![](https://images.microbadger.com/badges/image/dockergb/scadalts-dev.svg)](https://microbadger.com/images/dockergb/scadalts-dev "Get your own image badge on microbadger.com")
[![Waffle.io - Columns and their card count](https://badge.waffle.io/SCADA-LTS/User-Management.svg?columns=all)](https://waffle.io/SCADA-LTS/User-Management)

Scada-LTS is an Open Source, web-based, multi-platform solution for building your own SCADA (Supervisory Control and Data Acquisiton) system.


## Table of contents

* [Quick start](#quick-start)
* [Bugs and feature requests](#bugs-and-feature-requests)
* [Documentation](#documentation)
* [Contributing](#contributing)
* [Community](#community)
* [Versioning](#versioning)
* [Creators](#creators)
* [Copyright and license](#copyright-and-license)


## Quick start

Here is two of the several start options:

### To Run ScadaLTS on Docker:
* Download and install Docker from: https://www.docker.com/
* Download Docker Toolbox from: https://kitematic.com/
* Run Docker and Docker Toolbox.
* When the download is completed in the Docker Toolbox run Docker CLI (bottom-left corner).
* In the Docker CLI type in: `docker pull scadalts/scadalts` to download repository.
* Type in: `docker images` to check if repository is created. It should show "scadalts/scadalts" on the list.
* To run Scada on Docker type in: 
``docker run -it -e DOCKER_HOST_IP=`docker-machine ip` -p 81:8080 scadalts/scadalts /root/start.sh``.
* Type in: `Get-NetIPAddress` and find IPAdrress for "DockerNAT" InterfaceAlias (You need a new CLI window to do it).
* Type in docker IP adrress in the browser with port 81 and `/ScadaLTS/` sufix. Example:
`http://10.0.75.2:81/ScadaLTS/`

### Second option:
* [Download the latest release](https://github.com/sdtabilit/Scada-LTS/releases/download/v0.0.6-test-modbus.3.0.2/ScadaBR.war).
* Install tomcat7: `apt-get install tomcat7`.
* Install mysql: `apt-get install mysql-server`.
* Login to mysql: `mysql -u [user] -p`.
* Create db:`create dababase scadalts;`.
* Copy ScadaBR.war to dir tomacat webapps: `cp ScadaBR.war /var/lib/tomcat7/webapps/`.
    Optionally you can build ScadaBR.war. For this you have instruction how do it in section "Building from sources",which is below.
* Restart tomcat7 to generate ScadaLTS folder: `/etc/init.d/tomcat7 restart`
* Change config file env.properties set connection to db: `vim /var/lib/tomcat7/webapps/ScadaBR/WEB-INF/classes/env.properties`.
* Restart tomcat7: `/etc/init.d/tomcat7 restart`

### Building from sources

1.Please checkout sources from git from master branch by command
    `git clone https://github.com/SCADA-LTS/Scada-LTS.git`

After that You can open the project on Intellij Community Edition

2.Build project by Ant

Please set up environment variable CATALINA_HOME in your system.
Example for linux system:
`CATALINA_HOME='home/yourusername/path_to_tomcat_directory'`

We have build.xml which is used to build scadalts.war file.

Below are ant tasks which we run step by step
- `ant clean`,
- `ant build`,
- `ant war`

3.Deploy

For deploy war file to tomcat we use `ant clean_and_tomcat_deploy` task on Windows and Linux.

4.Running and debugging application

After deploying you can run tomcat by supplying either `ant run-debug` on windows or `ant run-debug-linux` on Linux

## Bugs and feature requests

Have a bug or a feature request? Please first read the [issue guidelines](https://github.com/twbs/bootstrap/blob/master/CONTRIBUTING.md#using-the-issue-tracker) and search for existing and closed issues. If your problem or idea is not addressed yet, [please open a new issue](https://github.com/sdtabilit/Scada-LTS/issues/new).

## Documentation

Scada-LTS documentation, will be included in this repo in the root directory.


## Contributing

In the process of making.

## Community

* ScadaLTS tag is finally available on Stack Overflow. Feel free to ask questions - http://stackoverflow.com/questions/tagged/scadalts.

## Tests

[scada-lts.testquality.com](https://scada-lts.testquality.com)

## Project Encoding

Default project encoding is set to UTF-8.


## Versioning

For transparency into our release cycle and in striving to maintain backward compatibility, Scada-LTS is maintained under [the Semantic Versioning guidelines](http://semver.org/). Sometimes we screw up, but we'll adhere to those rules whenever possible.

See [the Releases section of our GitHub project](https://github.com/grzesiekb/Scada-LTS/releases) for changelogs for each release version of Scada-LTS. 

## Creators 

Code base on [ScadaBR](https://sourceforge.net/projects/scadabr/?source=directory)

**Michał Rokitiański**

* <https://github.com/SoftQ>

**Grzesiek Bylica**

* <https://github.com/grzesiekb>

**Arkadiusz Parafiniuk**

* <https://github.com/ArkadiuszParafiniuk>

**Konrad Daniek**

* <https://github.com/wisyr>

**Jarosław Morzyniec**

* <https://github.com/Morzyniec> Testing

**Mateusz Kaproń**

* <https://github.com/matkapron>

**Diego Rodrigues Ferreira**

* <https://github.com/katesclau>

**Jerzy Piejko**

* <https://github.com/JerzyPiejko>

**Radosław Jajko**

* <https://github.com/radek2s>

**Marcin Gołda**

* <https://github.com/marcingolda>

**Tomasz Skrobol**

* <https://github.com/TomashHawk>

## Support

If you need support for deployment please contact us and find more information on [our website](http://scada-lts.comhttp://scada-lts.com/#support)


## Copyright and license

Code released under [the GPL license](https://github.com/sdtabilit/Scada-LTS/blob/master-sdtabilit/LICENSE). 
