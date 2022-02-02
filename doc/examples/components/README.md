# Scada-LTS Tutorials - Components Scenario
- SCADA-LTS Application version: **2.6.12**
- Scenario file: [component project](./Scada-LTS-Components.zip)
- Level: **intermediate**
- Scenario version: **1.0.0**
- Updated on: **02.01.2022**
- Created by: **Radek Jajko**

This tutorial scenario is a step-by-step introduction to Scada-LTS. Each scenario will show how to configure
this application depending on the given use case. Every time to specific tutorial we attach the
ZIP file with the final configuration that can be imported into the Scada-LTS application. To do that just log
in into the Scada and open the Import/Export page. Then find an upload section and import the following file.

## Included Scada content:
- Custom Vue.js components in Graphical View
- Data Points definitions

## Scenario content


## Components scenario
In this scenario we will just introduce user to the Custom Components concept. Where to look for available
components and how to use them. 

### Prepared environment
In this scenario user will receive ready to use project with preconfigured Graphical Views. 
Each view will present just one custom Vue.js component. To use this custom component user must
define a specific HTML graphical view component with `div` element and valid `id=` argument. 
List of all available components is in our [Wiki site](https://github.com/SCADA-LTS/Scada-LTS/wiki/Vue.js-Graphical-View-components).
So to present the expected result we are presenting this example project.   

### Component usage

To start using those custom components, user must add a new HTML component into a Graphical View.
And using just a plain HTML tag syntax he can configure that components and their properties. 
Some components may work without earlier configuration like for example the `isAlive` component that 
just need a following statement to be added into this HTML component: 
```html
<div id="app-isalive2"/>
```

Each component has different creation `id` so user have to get acquainted with the components [Documentation Manual](https://github.com/SCADA-LTS/Scada-LTS/wiki/Vue.js-Graphical-View-components)
that is available on our project Wiki page. Most of the components has their own dedicated 
approach how to configure them, but on this page we are trying to add references to the specific 
manuals to keep up-to date with the latest changes. When we are adding a new component that is ready to use
we will update that page.

That is the general rule how to use those components. 


### Component examples

#### IsAlive

**Goal**: Check the application conditions  

This component can be used for checking the health of the Scada-LTS graphic view. When this screen
is running all the time user might not see that he lost the connection with Scada server or
the application has stopped responding. So this component is sending a heart-beat check to the server
to make sure that the application **isAlive**. 

User can add some additional configuration to check data points state and based on it decide 
if he wants to treat this state as application failure or not. By default, all data point checks are 
treated as additional information state, and when the error occurred it is reported as warning.

#### CMP

**Goal**: Display and Control compound Data Points objects   

For example if user wants to describe a motor engine that has multiple datapoint inside 
that must be working together.  Using this component we can display the current motor state
based on the provided check conditions. What is more user can use this component to change the values of multiple data points using
the controls that he can define in its configuration.

This is a very complex component but there is a lot of flexibility to adopt it into client 
needs. 

#### Charts

**Goal**: Show point values on a flexible chart component.

User can add this component to view to present the specific data point historical values. It can
be updated in real-time or can be used to present only specific time period. Using the "chart-range-X" id user
can render an advanced chart with time period selector.

All charts require at least one data point with more than one value. If there is no value the chart will 
not appear.

### Summary

Configuration of this Custom Components may be tricky but give user additional tools that he can use 
to extend the Graphical View screen. 