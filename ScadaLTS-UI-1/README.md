# ScadaLTS UI Elements (Vue.JS )

VueJS componets for ScadaLTS. Subproject to impove the user experience in ScadaLTS. It it based on simple Vue components which are included into base application. 

# Project structure:
\+ assets \
\+ components\
\- + graphicViewComponents (custom widgets for ScadaLTS Graphic View)\
\- + userInterfaceComponents (widgets for ScadaLTS-UI *.jsp pages) 

# How it works?
Using new VueJS components user can create new modern and advanced views for his projects. It can for example display current state of point, display live charts for specific points or even display statistics for specific data point in time period. To start just see [GraphicViewComponents](./src/components/graphicViewComponents/README.md) section in _src/components/graphicViewComponents_. 

## Build Setup

``` bash
# install dependencies
npm install

# build for production with minification
npm run-script build
```

## Developed by:

- [Grzegorz Bylica](https://github.com/grzesiekb)
- [Radosław Jajko ](https://github.com/radek2s)

#### Created:
- August 2019 
