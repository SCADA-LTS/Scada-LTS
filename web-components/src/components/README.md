# Graphic View VueJS components.
Vue.js components is modern components for graphic view pages. It is REST based components with advanced config properties which can improve the performance and user experience of ScadaLTS.

# How to start?
Open Graphical View page in ScadaLTS. Then create a new view and add new HTML component. To create a new VueJS component just type in component name, and proprerties for this. See example below:
```
<is-alive plabel='Is Alive test' ptime-warning=6000 ptime-error=10000 ptime-refresh=1000/>
```

# List of components:
- [Chart components](./charts/README.md)
- [Synoptic Panel Components](./synoptic-panel/README.md) (only for SLTS Synoptic Panel menu!)
- Is Alive Component [ \<is-alive/> ]
- Simple Point Component [ \<simple-component-svg/> ]
- Advanced Point Component [ \<cmp/> ]

# Structuring components
Basic structure has several rules.  Vue’s style guide multi-word rule tells us not to use a single word to name a component, that’s why we’re prefixing some with “App”. Everything is a component but it has to be categorized.

### UI Components:
This are reusable components across the whole application. They communicate just by using properties and events. They are not holding application logic. For example it could be a AppButton.

### Layout Components: 
Components that are used just one in while application. For example footer, header or navigator. Components that are only used once per page should begin with the prefix “The”, to denote that there can be only one. For example for a navbar or a footer you should use “TheNavbar.vue” or “TheFooter.vue”.

### Domain Components:
Components that are reusable in different pages, that is why they are not located with views components. Organized in separate folders - one per domain. Example: _charts/lineChart.vue_
Child components should include their parent name as a prefix. For example if you would like a “Photo” component used in the “UserCard” you will name it “UserCardPhoto”. It’s for better readability since files in a folder are usually order alphabetically.

### Views:
Views are usually composed by more than one component and sections. That components that exist only in that View should be placed inside this View directory. Main view page can be renamed to __index.vue__ _(e.g. src/views/LoginView/index.vue)_ Using that structure we are able to expand that View for further components without making a complex refactoring.