# Modern Watch List View
Created by _Radek Jajko_ [rjajko@softq.pl](mail:rjajko@softq.pl)  
version 2.0.0

Modern Wach list is a new View for ScadaLTS prepared next to old classic WatchList page. It base on WachList 
data and genereate a Modern Charts using AmChart4 library.

Modern Watch List Chart is a new vue.js component that works only on watchlist page. You can find settings button to modify chart behaviour and apperance. 

## Example usage:
It is as simple as user expect. Just add data to watch list then set a name to that watch list. Setup your chart settings from choosing one type [Live, Static, Compare] and configure required fields.

### Live data
While setting up the Live Chart specify the time period since data should be displayed and refresh rate.
### Static data
Choose speific date range to be displayed on the chart. 
### Compare data
Choose two datapoints and assign to them specific time ranges.
Then open more settings and asign second datapoint to second X-axis.

## Configure
When you click on "more option (cog)" button you will see more options to be defined like chart colour or fill opacity. You can change the display name of the datapoint. There is also a button to change from LineSeries data to StepLineSeries. What is more you can customize the specific series color and fill completly including stroke tension (to be more curved). 

## Version change

### v2.0.0
- Rewritten from strach vue.js component dedicated for WatchList instead of embedding default "Scada-LTS modern chart component".
- Improved performance due to aggregation function
- More customization functions (fill, width, color and more)
- Multiple line series types on one chart
- Comparing two datapoint on a single chart
- Reloading chart while changing the active watchlist.
### v1.0.0
- Created Modern Watch List Chart component. 