# Vue Unit testing for System Settings

Most important features are emiting, saving and restoring data received from SystemSettings components. So each of the common function must be tested (list below):

- fetchData()
- saveData()
- restoreData()
- watchForTypeChange()
- emitData()

This methods are common for all System Settings components and must return valid data. Most of the components are using Vuex and Uiv so this is importatnt to mock that objects if it is needed. Also remember about using i18n translations.
