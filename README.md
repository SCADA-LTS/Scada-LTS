# [Scada-LTS](http://scada-lts.org)

[![Build Status](https://img.shields.io/travis/grzesiekb/json.svg?style=flat-square)](https://travis-ci.org/sdtabilit/Scada-LTS)
[![GPL-2.0](https://img.shields.io/npm/l/gb-json.svg?style=flat-square)](https://github.com/sdtabilit/Scada-LTS/blob/master-sdtabilit/LICENSE)

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

Here is one of the several start options:

* [Download the latest release](https://github.com/sdtabilit/Scada-LTS/releases/download/v0.0.6-test-modbus.3.0.2/ScadaBR.war).
* Install tomcat7: `apt-get install tomcat7`.
* Install mysql: `apt-get install mysql-server`.
* Login to mysql: `mysql -u [user] -p`.
* Create db:`create dababase scadalts;`.
* Copy ScadaBR.war to dir tomacat webapps: `cp ScadaBR.war /var/lib/tomcat7/webapps/`.
* Change config file env.properties set connection to db: `vim /var/lib/tomcat7/webapps/ScadaBR/WEB-INF/classes/env.properties`.
* Restart tomcat7: `/etc/init.d/tomcat7 restart`


### What's included

App ScadaBR.war 

## Bugs and feature requests

Have a bug or a feature request? Please first read the [issue guidelines](https://github.com/twbs/bootstrap/blob/master/CONTRIBUTING.md#using-the-issue-tracker) and search for existing and closed issues. If your problem or idea is not addressed yet, [please open a new issue](https://github.com/sdtabilit/Scada-LTS/issues/new).

## Documentation

Scada-LTS documentation, will be included in this repo in the root directory.


## Contributing

In the process of making.

## Community

* Chat with fellow scada-lts in IRC. On the `irc.freenode.net` server, in the `##scada-lts` channel.


## Versioning

For transparency into our release cycle and in striving to maintain backward compatibility, Scada-LTS is maintained under [the Semantic Versioning guidelines](http://semver.org/). Sometimes we screw up, but we'll adhere to those rules whenever possible.

See [the Releases section of our GitHub project](https://github.com/grzesiekb/Scada-LTS/releases) for changelogs for each release version of Scada-LTS. 

## Creators 

Code base on [ScadaBR](https://sourceforge.net/projects/scadabr/?source=directory)

**Grzesiek Bylica**

* <https://github.com/grzesiekb>

**Mateusz Kaproń**

* <https://github.com/matkapron>

**Diego Rodrigues Ferreira**

* <https://github.com/katesclau>

**Jerzy Piejko**

* <https://github.com/JerzyPiejko>

**Marcin Gołda**

* <https://github.com/marcingolda>



## Copyright and license

Code released under [the GPL license](https://github.com/sdtabilit/Scada-LTS/blob/master-sdtabilit/LICENSE). 
