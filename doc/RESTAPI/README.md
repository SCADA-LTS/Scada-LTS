# Scada-LTS REST API Documentation
[This yaml file](./ScadaLTS_API_OAS3.yaml) contains Open API Specification version 3 for Scada-LTS project.
Every existing REST interface has been described in this file with example request messages and responses.
It has been prepared to provide the latest API for the Scada-LTS developers. It may be useful when
someone wants to create a mobile app to use or monitor Scada application. For our teem this documentation
is base document that allow us to create the new Scada-LTS UI based on the Vue.js framework that fetch data
from application via REST requests.

## Usage
The simplest way to read that documentation is to copy the content of that _*.yaml file_ and paste the text
to the [SwaggerEditor Online Application](https://editor.swagger.io/). Because of that this file has been written
with OAS3 standard there is also a possibility to import that REST API documentation to the [Postman application](https://www.postman.com/). 
When the operations are imported now developer is able to generate a specific request based on the provided definitions.

## Developing
For __Us - the Developers__ it is a important task to keep this documentation up-to-date. So if some REST methods
are not working write to us and we will try to fix the bugs.