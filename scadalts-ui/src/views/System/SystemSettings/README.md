# System Settings Vue Component

Vesrion: 1.1.0  
Creator: Radoslaw Jajko [rjajko@softq.pl](mailto:rjajko@softq.pl)

System Settings component use REST API to configure Scada-LTS parameters. All available settings are displayed in the SystemSettings section "mainbar" as individual components with common interface. All changes done by user are summarized before final save. From each section changes can be individually restored or saved.

There is a sidebar on the right that display Application Info such as Runtime, database version, application version or latest available Scada-LTS release.

Only admin user can open this page.

## How to create a new section?

Create new vue file with naming convention `[name]Component.vue`. Each component can share the common interface from the main SystemSettings page. There are few methods that has to be implemented to be compatible with the main page actions.

### Interface methods

To inheritance the behaviour of the main component it is necessary to implement descripted below methods.

```js
/**
 * Fetch Data from remote server
 * and initialize the _*Store variable
 * to hold the unchanged object values.
 */
async fetchData() {}

/**
 * Save Data
 * It is a function that is invoked from the parent
 * comonent. It is responsible for saving object
 * values to the server and to the view.
 */
saveData()

/**
 * Restore data
 * When user tries to revert his changes.
 * Invoked from parent component.
 * Simply download data again and set your
 * edit_flag to false
 */
restoreData()

/**
 * Function that is triggered whenever user
 * change any value in this section.
 * When action is performed notify parent component.
 */
async watchDataChagne()

/**
 * Notify parent component
 * Using special common interface. This method
 * emit a new event called "chagned" that
 * send to parent object with configuration changes
 *
 * It is REQUIRED to set the same name for
 * "component" parameter as it is declared in the
 * parent component attribute "ref=name"!
 *
 * This interface structure is common for all
 * settings sections.
 */
emitData()

/**
 * Sumarize Data Chagnes
 * To display a summary what has been chagned it
 * is necessary to prepare this data.
 * Find all changed parameters and give them
 * valid label to be displayed.
 */
sumarizeDataChanges()
```

**Remember to add required methods and variables to VuexStore!** It is necessary to be able to reset to default values.

Briefly described emitted data:

```js
// ** emited data structure **
// component value must be the same
// as the reference in parent component
data = {
	component: String,
	title: String,
	changed: Boolean,
	data: {
		label: String,
		originalData: Object,
		changedData: Object,
	},
};
```

### Parent component HTML

Inside a parent component (index.vue file) you should imported this new component and append it somewhere inside SystemSettings section. Add a new line that create this new component and create the reference (`ref=""`) to this element and handle the `@changed` event by `componentChanged()` method. Example is presented below.

```html
<NewSettingsComponent ref="newSettingsComponent" @changed="componentChanged">
</NewSettingsComponent>
```

Now your component is bonded with the main page. Index page will automatically detect new component and will be able to save or restore changes easily due to implement methods.
