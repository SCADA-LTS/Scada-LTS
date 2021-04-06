# Scada-LTS - Vue User Interface

Vue.js subproject for modern Views and Components

## Description

User Interface subproject to display ScadaLTS content.

## Usage

- To start a developmnent server type inside this directory:  
  `npm run-script serve`
- To run unit test type:  
  `npm run-script test:unit`
- To run E2E cypress tests type:  
  `npm run-script test:e2e`
- To run Cypress testing framework type:  
  `npm run-script cypress`
- To build the fornt-end application type:  
  `npm run-script prettier && npm run-script build`  
   _This code runs prettier and then builds the application to dist directory_

## Cypress Testing

Before starting the Cypress Tests make sure that you are using blank Database to ensure the TestingSuite the clear Scada-LTS application otherwise more tests may fail.

[Here](./cypress/README.md) you can find Tutorial presenting how to create Cypress Tests for Scada-LTS.

## Creators

Developers from Scada-LTS project:

- @grzesiekb
- @radek2s
