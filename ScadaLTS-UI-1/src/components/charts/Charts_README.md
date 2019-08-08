# Modern Charts Components
## August 2019 - Version 1.0.0 
ScadaLTS modern charts components it is a set of new VueJS v2.0 components designed for GraphicalView in ScadaLTS. It is based on [am4chart](https://www.amcharts.com/) library. It generates charts using JavaScript from user-side which is a new approach to charts in Scada (they were generated via server-side scripts and libraries). It is more browser load than it was before, but server application becomes lighter and gains performance. 

## Types of charts:
- __< line-chart >__ Line Series Chart
- __< step-line-chart >__ Step Line Series Chart
- __< column-chart >__ Column Based Chart
- __< pie-chart >__ Pie Chart
- __< gauge-chart >__ Gauge Chart
- __< json-custom-chart >__ Custom Chart (defined by user via JSON)

## Usage:
New charts could be added to ScadaLTS Graphical View by adding a new HTML component with specific content. Each chart has to be initialized by using this listed above Extended HTML Tags. Each of this tag take a specific properties required to set up specific chart. Chart is generated inside this tag which has default size 750x500px.
***
### Quick start:
Create simple line chart for specific [ numeric | multistate | binary ] data point. 
```
<line-chart point-id="[dataPointID]"/>
```
That's it!\
It has rendered line chart for specific point from last hour with default parameters. So if you want to monitor the state of the point from last hour it is the simplest way how to do it. This chart could be zoomed in and out using scrollbar at the bottom of the component. Values of data point in time are represented by white dots on the chart.

But it is still just a chart like this old ones... What if we really want to monitor status of this point __in real-time__? No problem just add next properties. 
***
### Live Data
```
<line-chart point-id="[dataPointID]" refresh-rate="10000"/>
```
__Now we've got live chart!__\
It is refreshed every 10s (10000 ms) and when a data point will change state to different value this new one will be added to chart and the oldest one will be deleted from out chart. Now we can monitor state of datapoint in real-time with chosen by us refresh rate. For critical data, we can monitor the status of the point with a high frequency of queries to server (more real-time data but more resource consuming) and for non-critical data we can refresh chart after a few seconds. 

But what if we want to display chart for __multiple data points?__
***
### Multiple points
Just add next data point after comma in _'point-id'_ property. 
```
<line-chart point-id="[dataPointID],[anotherDataPointID],[andNextDataPointID"]/>
```
Now we have chart for 3 data points with values from last 1 hour. This components do not have limitations for a count of points displayed on the one chart, but I hope that you have an intuition that 30 point on a single chart is not a wise move.  

Can we display __older values__ than last one hour?
***
### Specified time period
Yes! Just add a new property to our tag.
```
<line-chart point-id="[dataPointID]" live="true" refresh-rate="10000" start-date="1-day"/>
```
As you can see it's a piece of cake. Just type inside 'start-date' property, time period from which you want to see the data. You can use a every combination of numbers with specific time period __[ hour(s) | day(s) | weak(s) | month(s) ]__. (eg. '2-days', '1-week', '3-months' etc.) But it is not everything! It is dynamic calculated time from now but we can also use a specific date. If we want see data from beginning of the previous year just type in date _(eg. '2019/02/01' to see data beginning  from 1-st February 2019)_. It could be useful to limit displayed data. 

To display values from specified period just add __'end-date'__ parameter. 
```
<line-chart point-id="[dataPointID]" start-date="2019/02/01" end-date="2019/03/01"/>
```
And it still works with multiple data points. It's great! Isn't it? \
But what if I want to add a horizontal line to chart to create for example warning level, which of it is exceeded it could be dangerous? 
***
### Level range line
Ok let's consider this one:
```
<line-chart point-id="[dataPointID]" range-value="100" range-color="#FF0000" range-label="boiling"/>
```
Now we have created horizontal line for our chart which indicates boiling level for water. Thanks to that we can quickly observe that temperature of water inside tank is boiling. It is useful  even inside ScadaLTS.

Wait a moment! We decided which color this horizontal line would have. Could we do the same with chart lines? 
***
### Chart Colors
For example we have got 3 sensors. This default green colors are too similar. Can we set up a different color set for our charts. Just add this parameter: 
```
<line-chart point-id="[dpID],[dpID_2],[dpID_3]" color="#FFFC19, #0971B3, #B31212"/>
```
Now we have got defined 3 custom colors for our charts. We can give just a one color value and the rest will be retrieved from this default values. What is the most important... __USE HEXADECIMAL COLOR CODE VALUES__\
Pretty colorful Modern Charts. But we still have the same size for them... Yes, yes it also could be changed. 
***
### Chart Size
```
<step-line-chart point-id="[dpID]" width="1080" height="720"/>
```
HD Chart? Why not! Values for attributes are given in Pixels (px). That is useful when we have defined a multiple chart instances on one GraphicalView. We can easily calculate the position of the next chart. 

### Labels
```
<step-line-chart point-id="[dpID]" label="Mid season temperature"/>
```

That would be enough from the basics... It is time for more complex tasks.

***
## Usage of Column and Pie charts.
Column charts are very similar to line series charts but it do not display data in time/value series but as category/value series. Because of that this charts are designed for different usage. Default behavior of the algorithm is to display count of data type in specific time period. This chart can be applied for __every data point type!__ It can display count of Aplhanumeric states of datapoint or counted TextRendered values from Multistate datapoint. It also can display values from aggregate function of Numeric datapoints ("sum", "minimum", "maximum", "average"). 

### Differences:
In Column and Pie charts most features presented earlier are present, but few of them are not implemented.
- Multiple Data Points in one chart (__only for Numeric Datapoints__)
- (NOT) Live-update
- (NOT in PIE CHART) Level range line

But it has aggregation functions for Numeric DataPoints.
***
### Aggregation functions:
Display statistical data from specific numeric data point.
```
<column-chart point-id="1" start-date="2019/08/1" sum-type="avg" sum-time-period="day"/>
```
This line displays column chart with average value for single day from 1-st of August 2019 till today. Instead of "average" function we can also use __[ "avg" | "count" | "sum" | "min" | "max" ]__. For our statistical Graphic View we can also group data by "minute", "hour", "month", and even "year". It is very useful to monitor daily average value of temperature or maximum value of voltage in electric motor single during day. 

## Usage of Gauge chart.
Gauge chart is useful to display live values of specific datapoint. It can be defined as a component displaying a pressure in container or current voltage of generator. It is live component which send requests to server every refresh-rate time. 

### Example usage:
```
<gauge-chart point-id="[dataPointID]" min="0" max="100"/>
```
If __"refresh-rate"__ parameter is not set, it sends requests every 1 second. Min and max properties are required to define the range displayed on the gauge chart for specified point. If the point value will be exceeded hand start to show values out of defined range.

## Usage of Custom JSON Chart.

The most powerful chart, it will do everything YOU define, but it has to be compliant with am4charts interface. It is simple chart generated  by am4core.createFromConfig() method, enhanced with data loader from ScadaLTS. To create this chart you have to define "point-id", "chart-type" and "json-config". You can read [here](https://www.amcharts.com/docs/v4/concepts/json-config/#Structure_of_JSON_config) how to prepare this JSON configuration.
It is prototype chart which is not fully supported.


## Modern Chart documentation:
Available properties in one place for all chart types. All charts could be exported to external file in graphical or text way. You can export to *.png, *.jpg, *.csv, *.json files. 

Properties for all charts:
- point-id
- label
- width
- height
- color

Additional properties for Step Line, Line charts 
- start-date
- end-date
- refresh-rate
- polyline-step
- range-value
- range-color
- range-label

Additional properties for Column and Pie charts 
- start-date
- end-date
- sum-type
- sum-time-period
- range-value (Not in PieChart)
- range-color (Not in PieChart)
- range-label (Not in PieChart)

Additional properties for Gauge chart:
- min
- max
- refresh-rate

# Author

- [Radosław Jajko](https://github.com/radek2s): __rjajko@softq.pl__