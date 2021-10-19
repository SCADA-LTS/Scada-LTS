# Auto Manual Component

Reforged component for the Auto Manual (called CMP) based on the Grzesiek's origin vue.js component.

This component has the same inputs like the classic CMP so it can be used as a replacement for it without reconfiguration. That was the main requiremnet to keep the 
interfaces but improve the stability of that component. Now it is a module build from four simple files that are responsible for renderig a specific part of it. To test it you just need to replace the id inside HTML component in your Graphical View from:
```html
<div id="app-cmp-0" plabel="Example" .../>
```
to app-cmp**2**-0 like on this example:
```html
<div id="app-cmp2-0" plabel="Example" .../>
```
Additionaly it has additional property like `pwidth` that can be used to set the width of the component: 
```html
<div id="app-cmp2-0" pwidth="400" .../>
```

## Available Properties

| Property | Type | Description | Example |
| --- | --- | --- | --- |
| pconfig | Object | Configuration of the component | *(to long to show it here)* |
| plabel | String | Label of the component | plabel="Example label" |
| pxIdViewAndIdCmp | String | Unique ID for the CMP | pxIdViewAndIdCmp="10" |
| pzeroState | String | Label for state zero | pzeroState="Disabled" |
| ptimeRefresh | Number | Time between requests | pTimeRefresh="5000" |
| pwidth | Number | Width of the component in pixels | pwidth="1000" |
| prequestTimeout | Number | Change the request timeout after each request is cancelled | prequestTimeout="10000" |
| phideControls | Boolean | Hide State History and state change buttons | pHideControls="true" |
| pdebugRequest | Boolean | Debugging messages in the console | pdebugRequest="true" |

## Example Configuration
Example configuration was provided by @grzesiekb and it is a Project Configuration that
could be imported in the Import/Export section of the Scada-LTS application. The [file](../../../../../doc/cmp-config/cmp-config-prj.zip) is available in `Scada-LTS/doc/cmp-config`. To check how does it work, change only the id property in the Graphical Views. 