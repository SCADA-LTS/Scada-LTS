# Graphic View VueJS components.
Vue.js components is modern components for graphic view pages. It is REST based components with advanced config properties which can improve the performance and user experience of ScadaLTS.

# How to start?
Open Graphical View page in ScadaLTS. Then create a new view and add new HTML component. To create a new VueJS component just type in component name, and proprerties for this. See example below:
```
<is-alive plabel='Is Alive test' ptime-warning=6000 ptime-error=10000 ptime-refresh=1000/>
```

# List of components:
- [Chart components](./charts/README.md)
- Is Alive Component [ \<is-alive/> ]
- Simple Point Component [ \<simple-component-svg/> ]
- Advanced Point Component [ \<cmp/> ]