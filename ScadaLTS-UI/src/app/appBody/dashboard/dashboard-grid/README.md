# ScadaLTS - Dashboard Grid Update
This is module imported from [ScdadaLTS-Dashboard project](https://github.com/SCADA-LTS/ScadaLTS-Dashbord)
This is independent module which allows user to manage Scada Components in GridView Mode. This module displays Dashboard-Grid-Component
which contain Navigator-Component and Page-Component.

## Navigator-Component
This is a component which displays created dashboard views.
Functionality:
- Open a selected view Page.
- Delete view page when user is edit mode
- Create a new view page. (Also with JSON import option)

## Page-Component
Displays a single View Page Instance which contain a varoius components.
This component uses a Frame-Component to display a single ScadaLTSItemComponent.
It is binded with Dashboard-Grid-Component (parent) and with Frame-Component (Child)
by an @Input and @Output decorators. This component recive a single active ViewPage data.
And returns every change of this page to his parent.
Functionality:
- Update a View Page
- Add a new ItemComponent
- Remove an exisiting ItemComponent (when recived event from FrameComponent)

## Frame-Component
Inside item directory. This is an component which handle the custom user-created components
It has a directive which allows to pin a custom ItemComponent. ItemComponent is the generic
component which is created. It has two variables. 'Component' and 'Data'. 'Component' is 
a Component class, 'Data' is an Object with this component data variables. This object do
do not have any limitations. 
Functionality:
- Display a custom user-created components.
- Edit selected ItemComponent
- Delete this Component (init event)

## Dialogs
Dialogs directory contains custom dialogs which are used in this module

## Classes
Classes directory contains a classes which are used in this module

## Utils
Some services required to work DashboardGridModule

# ScadaLTS - Dashboard (Adding a new custom user-creted component)
To expand the library of custom compoents import them to the node_modules by your `package.json` file.
Later find `item.service.ts` file inside /dashboard-grid/item/ directory. 
Inside this file import a custom component and include this inside ItemServie class.
- Add a new retrun condition to getDashboardItem() method.
- Add a new return statemanet inside getComponentType() method to return a your component code
- Add a new return array object instance inside getDashboardComponentList() method to return all necessary data. Like on the example in this file. 
(Remember! to add data object template in this getDashboardComponentList() method )