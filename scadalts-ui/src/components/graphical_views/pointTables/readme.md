# Simple Point Table Component

This component add a DataPoint table to Scada-LTS Graphical Views. 
Table present the latest data point values with a update time.
User can use the additional properties to customize the table and
show more details for all points. For example, the table can show 
the average value from the specific time range or display the 
total value of the specific point. This table works with the Numeric,
Multistate and Boolean data points. 

## Usage:

User can define max 10 Simple Point Tables at the one screen.
It is a Vue.js component so it is created like the AmCharts component
and other components by using HTML component that need a specific
id name and attributes to inform the renderer what to do.

```
<div id="simple-table-0" point-ids="[dataPointID]"/>
```

This table use WebSocket connection to get the latest data point values.

## Simple Point Table documentation:

Properties properties for Simple Point Table

| Atribure name | Type | Example usage |
| --- | --- | --- |
| point-ids | String | point-ids="1,32,4" |
| start-date | String | start-date="2-hours" |
| width | Number | width="1920" |
| height | Number | height="1080" |
| total | Boolean | total |
| average | Boolean | average |
| max | Boolean | max |
| min | Boolean | min |
| round | Number | round="6" |

# Author

- [Radosław Jajko](https://github.com/radek2s): **rjajko@softq.pl**
