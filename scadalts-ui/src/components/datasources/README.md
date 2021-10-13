# Data Source Components Vue.js

Each Data Source module should contian a index.vue file that will be used to render the main component. Data Source edit page should be described in config.vue file. There you should place all config that is required to create the data source.
Next one list.vue is additional info to be displayed on the data point list below data source details. More importan is point.vue file that contains the logic of the data point creation and modification. Each data source should have its own folder and that components should be placed there. Inside DataSourcesMixin.js file you should import that components and also extend the "createInitialDataPoint()" method to return a new JavaScript class of Default Data Point Configuration. What is more you should import the DataSource ID into the DataSource definitions in Vuex Store related to DataSources. 