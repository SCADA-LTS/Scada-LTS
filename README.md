# [Scada-LTS](http://scada-lts.org)

Open Source, web-based, multi-platform solution for building your own SCADA   
(Supervisory Control and Data Acquisition) system.
Code released under [the GPL license](https://github.com/SCADA-LTS/Scada-LTS/blob/develop/LICENSE).

![GitHub release (latest by date)](https://img.shields.io/github/v/release/SCADA-LTS/Scada-LTS)

[![Build Status](https://travis-ci.org/SCADA-LTS/Scada-LTS.svg?branch=develop)](https://travis-ci.org/SCADA-LTS/Scada-LTS)
![GitHub all releases](https://img.shields.io/github/downloads/SCADA-LTS/Scada-LTS/total)
[![](https://images.microbadger.com/badges/version/scadalts/scadalts.svg)](https://microbadger.com/images/scadalts/scadalts "Get your own version badge on microbadger.com")
[![](https://images.microbadger.com/badges/image/dockergb/scadalts-dev.svg)](https://microbadger.com/images/dockergb/scadalts-dev "Get your own image badge on microbadger.com")
---

<h1><a style="color:#222222;" href="https://github.com/SCADA-LTS/Scada-LTS/wiki"><img src=https://github.githubassets.com/images/modules/logos_page/GitHub-Logo.png height="20px" /> Project Documentation</a></h1> 

See our [GitHub Wiki](https://github.com/SCADA-LTS/Scada-LTS/wiki) page to take the first steps inside
Scada-LTS project, follow the latest changes and share your contribution with [our team](https://github.com/SCADA-LTS/Scada-LTS/wiki/The-Team)!
There is also a Scada-LTS users community so if you had a problem feel free to ask questions on StackOverflow or our GitHub Discussions.
For the developers we are preparing a complete code documentation that will contain all the REST API interfaces
described with example usage. To do that we use Open API specification in version 3 that is compatible with
most of the modern API tools like Swagger or Postman. Documentation file can be found in [Scada-LTS/doc](https://github.com/SCADA-LTS/Scada-LTS/tree/develop/doc/RESTAPI) directory.
To take your first steps inside Scada-LTS we suggest to get acquainted with video tutorials on YouTube or with [ScadaBR instructions](https://sourceforge.net/p/scadabr/wiki/Manual%20ScadaBR%20English%200%20Summary/).

Follow the [Release Section](https://github.com/SCADA-LTS/Scada-LTS/releases) to be up to date with the latest features and see the specific version changelogs.   
We follow the [Semantic Versioning Guidelines](http://semver.org/) to organize changes in our application.

If you need support for deployment please contact us and find more information on [our website](http://scada-lts.comhttp://scada-lts.com/#support)



## Issues and ideas
Have you found a bug? Do you have idea for a new feature? Open a new [Issue](https://github.com/SCADA-LTS/Scada-LTS/issues) on our GitHub project site!
Please follow the [issue guidelines](https://github.com/twbs/bootstrap/blob/master/CONTRIBUTING.md#using-the-issue-tracker)
and make sure that your issue is not already reported. Try to write in english and explain what do you think in clear and understandable way.


---

# Development

## Building the application
To build the application on your own environment you can use **Apache Ant** build tool.

We provide example **ant scripts** that can be used inside Scada-LTS project to perform specific operations.
Commands with their explanations are listed below:
### Examples of run tasks
| Command | Explanation |
| ---- | ---- |
| ```ant run``` | Start Scada-LTS application using Tomcat server |
| ```ant run-build``` | Build and start Scada-LTS application |
| ```ant run-build-no-ui``` | Build and start Scada-LTS application without building user interface |
| ```ant debug``` | Start Scada-LTS application in debug mode on Tomcat server |
| ```ant debug-build``` | Build and start Scada-LTS application in debug mode |

### Examples of build tasks
| Command | Explanation |
| ---- | ---- |
| ```ant build``` | Build complete Scada-LTS application |
| ```ant build-no-ui``` | Build just a core application  without Vue.js user interface |
| ```ant update-ui``` | Update running application with latest frontend application changes |
| ```ant deploy-war``` | Prepare WAR archive from existing resources<br/> _we suggest to perform **ant build** task before_ |

### Examples of test tasks
| Command | Explanation |
| ---- | ---- |
| ```ant test-junit``` | Launch Java Unit Tests |
| ```ant test-frontend-unit``` | Launch Vue.js tests |

## SeroUtils library license

Matthew Lohbihler <matthew@serotonin.ai> has granted us permission to use the SeroUtils.jar library for ScadaLTS team. ScadaLTS team can use the code seroUtils without limitation.

Fragement with the correspondence of Grzegorz Bylica and Matthew Lohbihler:
> "(...) The good news is that you may hereby and without limitation use the seroUtils code as you like. (...)"