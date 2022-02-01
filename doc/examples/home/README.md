# Scada-LTS Tutorials - Home Scenario
 - SCADA-LTS Application version: **2.6.11**
 - Scenario file: [home project](./Scada-LTS-Home.zip)
 - Scenario version: **1.0.0**
 - Updated on: **31.01.2022**
 - Created by: **Radek Jajko**

This tutorial scenario is a step-by-step introduction to Scada-LTS. Each scenario will show how to configure 
this application depending on the given use case. Every time to specific tutorial we attach the 
ZIP file with the final configuration that can be imported into the Scada-LTS application. To do that just log 
in into the Scada and open the Import/Export page. Then find an upload section and import the following file.

## Included Scada content:
- Datasource creation
- Data points creation
- Point Hierarchy
- Data point details
- Point Event Detectors
- Event Handlers
- Users
- User Profiles
- Watch Lists
- Graphical Views

## Scenario content


## Home scenario
In this scenario we will try to show how to create a basic Scada configuration from starch using only
simple Virtual Data Points. Those data points will be presented on a graphical page that takes the advantage
of the HTML component to place that points inside a Weather Station schematic graphic. User will learn 
how to handle specific event and how to use it to modify the value of another data point. 

### Requirements
Before moving to the first configuration in application we need to know the requirements and the user story how
this application will be used. In this scenario we will be using Scada-LTS to display the current state of smart 
home devices. This scenario is focused on defining a basic Scada objects like "Data Points" and presenting 
them on a "Graphical View" and the "Watch List". So to achieve that in the most simple way we will be operating 
on the "Virtual Data Sources" that generates the random data based on the provided definitions. 

Moving back to the user requirements. We will have 3 different objects in our Smart-Home: 2x Weather Station 
and one Central Heating Boiler. One weather station will be located in a living room and the 
second will be placed next to user workshop in a garage. As a house owner we will have access to every 
device but our flatmates should have a possibility only to read the current measurements of Weather Station in a 
Living Room. We should present that living room measurements on a dedicated screen.

What is more we need to take some actions if the water in the Central Heating Boiler is too high. 
The alert should be displayed and the proper message about the boiler state should be visible.
Our boiler can show us the current water temperature and pressure. It has a panel that can display
a simple message to user and 3 different working modes: "OFF", "Auto Mode", "Manual Mode". User should be able to 
change that mode from the Scada application. 

### Data source configuration

Firstly to create a such configuration we should start from the most basic element of Scada-LTS application 
that is a Data Point. But to create a Data Point we must know the type of this object. We have a lot od different 
types of DataSources and each of it can be used to define a specific data point. In our case we just want to attach
the simulated data that was randomly generated. To do that we can use a **Virtual Data Source**. So to do it we can 
move to the Data Source configuration page <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_ds.png" width="16px"/>.
Then using the select box in the top-right corner we can select a Virtual Data Source and click <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_ds_add.png" width="16px"/> add data source button. 

Then we can provide the following configuration: 

| parameter | value |
| --- | --- | 
| **name** | Weather Station - Room |
| **Export ID** | DS_WS_01 |
| **Update Period** | 1 minute |

The Export ID is a unique identifier that globally describe that object within whole application scope.
So make sure that this name is unique within your app. You can leave it as it was generated, but 
you can also provide your own naming convention for your business objects. It can be useful because sometimes
we need to know the object XID to attach it to specific component.

The update period for Virtual Data Source is a refresh time after which there is generated a new values in every 
data point that is defined within that object. Save that object and move now to the Data Point definition.

#### Data point definitions

After data source creation we can now create a Data Point within that entity. Our Weather Station device in this scenario
can handle the internal and external Temperature. It can measure the Air Pressure and the Humidity. What is more we can
get the information about the light strength. Each of this parameter should be treated as a separate Data Point object
with its own definition. To add that specific datapoint user should click on the <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_comp_add.png" width="16px"/> button.

The new section should be visible. So there is no other thing like creation of data points based on the following table:

|  | Point 1 | Point 2 | Point 3 | Point 4 | Point 5
| --- | --- | --- | --- | --- | ---
| **name** | Air Pressure | Humidity Inside | Light Sensor | Temperature Inside | Temperature Outside
| **Export ID** | DP_WS01_AIR_PRESS | DP_WS01_HUM_IN | DP_WS01_LS | DP_WS01_TEMP_IN | DP_WS01_TEMP_OUT
| **Settable** | True | True | True | True | True
| **Data type** | Numeric | Numeric | Numeric | Numeric | Numeric
| **Change type** | Brownian | Random | Random | Brownian | Brownian
| **Settings** | min=980 / max=1120 / change=10 | min=20 / max=100 | min=0 / max=1  | min=17 / max=23 / change=1 | min=-20 / max=35 / change=3
| **Start Value** | 1100 | 54 | 0 | 10 | 15

As you can see there are two different **"Change Types"**: Random and Brownian. Both are a kind of random generator 
methods but the Brownian remembers the previous value and the next one will be added to it form range that was defined
in a "change" field.


#### Copy configuration

Now we can copy our configuration to create another Weather Station. To do that we need to move back to the main 
DataSources page and click on the <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_ds_add.png" width="16px"/> button that is
placed in the "actions" section next to the "Weather Station - Room" entry.

Now the copy of data source has been created but the DataSource name and Export ID parameters are not correctly 
generated. To we have to fix that and replace the autogenerated ExportIDs with our pattern.

|  | Point 1 | Point 2 | Point 3 | Point 4 | Point 5
| --- | --- | --- | --- | --- | ---
| **name** | Air Pressure | Humidity Inside | Light Sensor | Temperature Inside | Temperature Outside
| **Export ID** | DP_WS02_AIR_PRESS | DP_WS02_HUM_IN | DP_WS02_LS | DP_WS02_TEMP_IN | DP_WS02_TEMP_OUT
| **Settable** | True | True | True | True | True
| **Data type** | Numeric | Numeric | Numeric | Numeric | Numeric
| **Change type** | Brownian | Random | Random | Brownian | Brownian
| **Settings** | min=980 / max=1120 / change=10 | min=20 / max=100 | min=0 / max=1  | min=17 / max=23 / change=1 | min=-20 / max=35 / change=3
| **Start Value** | 1100 | 54 | 0 | 10 | 15


#### Central heating data source

Final step is a creation of the third Data Source that will be representing our Central Heating Boiler device. 
We should repeat the previous steps with following configuration:

| parameter | value |
| --- | --- | 
| **name** | Central Heating |
| **Export ID** | DS_CH |
| **Update Period** | 10 seconds |

And then we can create a following data points:

|  | Point 1 | Point 2 | Point 3 | Point 4 | Point 5
| --- | --- | --- | --- | --- | ---
| **name** | Boiler Alert | Boiler Enabled | Boiler Mode | Boiler Pressure | Boiler Temperature
| **Export ID** | DS_CH_B_ALERT | DS_CH_B_STATE | DS_CH_B_MODE | DS_CH_B_PRESS | DS_CH_B_TEMP
| **Settable** | True | True | True | True | True
| **Data type** | Alphanumeric | Binary | Multistate | Numeric | Numeric
| **Change type** | No change | No change | No change | Brownian | Brownian
| **Settings** | - | - | - | min=0.5 / max=1.5 / change=0.3 | min=15 / max=150 / change=10
| **Start Value** | 'N/A' | 0 | 0 | 1 | 50

### Data Point properties

Now when we have data point definitions we can set up the rendering properties. To do that we
must open the specific data point details page. We can open it from two places. The first one is the 
watch list object, but we need to add datapoint to this list to move to the properties.
Second place is our "Data Sources page" <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_ds.png" width="16px"/>. 
There we have to expand the specific Data Source using <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/arrow_out.png" width="16px"/> arrows icon. 
Now we can see the DataPoint list with their description, status and action buttons. Using this
glass icon <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_comp.png" width="16px"/>
we can move to the DataPoint details. On that page user can find a details about this datapoint with
the value history table and chart and also with some additional statistics. Only from this place we can
move to the Data Point Properties page. To do that we must click on the <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/icon_comp_edit.png" width="16px"/> edit
button. Now we are on the proper page.

We will change the "Text renderer properties" to round the received value to one or two decimal
places depending on the datapoint. We should add also additional suffix to our measurements 
to know what kind of units we are using there. Try to provide a following configuration:

| Point | Type | Format | Suffix | 
| --- | --- | --- | --- |
| WS Air Pressure | Analog | #,##0.0 | [hPa] |
| WS Humidity | Analog | 0.0 | % |
| WS Temperature Inside | Analog | 0.00 | [°C] |
| WS Temperature Outside | Analog | 0.00 | [°C] |
| CH Boiler Enabled | Binary | Zero=Not working One=Working | - |
| CH Boiler Mode | Multistate | 0=Off 1=Auto Mode 2=Manual Mode | - |
| CH Boiler Temperature | Range | 0-30=Cold 31-60=Warm 61-100=Hot 101-200=Boiling! | Cold=blue Warm=green Hot=yellow Boiling=red |

Data points from Weather Station are simple "Analog Text Renderers" that just round the raw value
and display it with the specific suffix. Boiler data points are a bit different. We want to inform
user about the state in which it is working instead of displaying the raw numeric values. So for 
boiler mode that is a multistate point we can assign the following statements that key equal to 0
should be treated as "Off" state of this device. If this point reports the state equal to 1 we should
treat that as an "Auto Mode". And the last state is the "Manual Mode". It is very intuitive 
but this example was prepared to show the possibilities and the features of Scada.

### Data Point organization

Most of the configuration has been done. But we still need to create an objects that will
present the current values of selected data points. Before we do that we can organize our 
data points into separate directories to make our project clear. 

We should move to the "Point Hierarchy" page using this <img height="16px" src="https://github.com/SCADA-LTS/Scada-LTS/raw/develop/WebContent/images/folder_brick.png" width="16px"/> 
button on the navigation bar. Now in this page we see all DataPoint that we have just created in our 
application. Using the **"plus"** button we can create a new directory where we can just drag-and-drop
a specific Data Point. During this tutorial we will create a following structure:

- My Home
  - Heating
    - [Boiler Data Points] 
  - Devices
    - Weather Station 1
      - [Weather Station 1 points] 
    - Weather Station 2
      - [Weather Station 2 points]

This structure will be visible on the WatchList. 

### Creation of Watch List


### Creation of Graphical View

### Creation of users and user permissions

### Adding event triggers

### Adding event handlers