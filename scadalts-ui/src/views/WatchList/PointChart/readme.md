# Modern Watch List View

Created by _Radek Jajko_ [rjajko@softq.pl](mail:rjajko@softq.pl)  
Updated for Scada-LTS version 2.4.0.

Modern Wach list is a new View for ScadaLTS working next to the classic WatchList page.
It base on WachList data and genereate a Modern Charts using AmChart4 library. Modern Watch List Chart is a new vue.js component that works only on watchlist page. You can find settings button to modify chart behaviour and apperance.

## Functionality:

This widget can display following charts.

### Live chart

Render real-time data based on the selected WatchList.

Select time range to be displayed on the chart from last hour or even from the whole month!
Remember only about huge amount of data - the more data to rednder the more performance it requires. When a new data has been saved to datapoint user will be able to see that value after a few seconds on the chart.

### Static chart

Display data from specific WatchList time range.

Usefull function when user define mulitpe watch lists with monthly summarization. User easly select specific range using intuitive datepicker component.

### Compare chart

Compare two specific datapoints on one chart.

Using that mode user will be able to select datapoint values from specific time period and
then compare this points to another datapoint from different time range. Time range is selected by datepicker component as it was in Static Chart.

**CONFIGURATION**  
To configure "Compare Chart" correctly you must change the X-Axis of the 2-nd datapoint to 2-nd axis and make sure that Aggregation function is disabled (_"All Data" button selected_).

**TROUBLESHOOTING**  
When changing from different chart types (eg. from "Compare" to "Static") remember to change
all datapoint series X-Axis to 1-st Axis. In case of blank chart in this Compare mode make sure that aggegation is disabled or try to assign the X-Axes again.

## Chart series configuration:

Since Scada-LTS version 2.4.0 ModernWatchList Chart has seperate configuration for each datapoint series. Each one can be configured individually. Example configurable elements are descripted below:

- **Name** - Display name of that series (it do not change the DP name it is only a label)
- **Series Type** - _Line_ or _Step Line_ series line type.
- **Y-Axis** - Type of Y-Axis (eg. Logarithmic, Binary...).
- **X-Axis** - Required when comparing two datapoints
- **Stroke color** - Color of the series line.
- **Stroke width** - Width of the series line.
- **Stroke tension** - "Smooth" of the line (values from 0-1)
- **Fill color** - Color below the series line.
- **Fill opacity** - Opacity for color below the series line.
- **Bullets** - Display or Hide

## Version change

### v2.1.0
- Vuetifization of the interface
- Using Day.js library to manage the time operations
- More responsibe Chart Settings
- Chart is loaded using WatchList ID instead of WatchList name

### v2.0.0

- Rewritten from strach vue.js component dedicated for WatchList instead of embedding default "Scada-LTS modern chart component".
- Improved performance due to aggregation function
- More customization functions (fill, width, color and more)
- Multiple line series types on one chart
- Comparing two datapoint on a single chart
- Reloading chart while changing the active watchlist.

### v1.0.0

- Created Modern Watch List Chart component.

## Notice

This is completly independed component. If you want to create a ModernChart on View page see [ModerChart Documentation](../amcharts/readme.md)
