<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/.
--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<tag:page dwr="DataPointEditDwr">

     <link href="resources/app/bower_components/sweetalert2/dist/sweetalert2.min.css" rel="stylesheet" type="text/css">
     <script type="text/javascript" src="resources/app/bower_components/sweetalert2/dist/sweetalert2.min.js"></script>

     <style type="text/css">
         .swal2-icon {
            zoom: 0.4;
         }
         #swal2-title {
            font-size: 1em;
         }
         .gbtest {
            color: blue !important;
            text-decoration: bold;
            text-size: 1em;
         }
         .swal-content {
            font-size: 0.8em;
          }
         .gbul {
            text-align: left;
            list-style-type: none;
            margin: 0;
            padding: 0;
         }
     </style>


  <%@ include file="/WEB-INF/jsp/pointEdit/pointName.jsp" %>
  
  <form action="" method="post">
    <input type="hidden" id="taskName" name="asdf" value=""/>
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top">
          <%@ include file="/WEB-INF/jsp/pointEdit/pointProperties.jsp" %>
          <%@ include file="/WEB-INF/jsp/pointEdit/loggingProperties.jsp" %>
          <%@ include file="/WEB-INF/jsp/pointEdit/valuePurge.jsp" %>
          <%@ include file="/WEB-INF/jsp/pointEdit/textRenderer.jsp" %>
          <%@ include file="/WEB-INF/jsp/pointEdit/chartRenderer.jsp" %>
        </td>
        <td valign="top">
          <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectors.jsp" %>
        </td>
      </tr>
      <tr>
        <td>
            <script>
                   function showEditChartRender(charRenerer) {
                      jQuery("#chartRendererNone").hide();
                      jQuery("#chartRendererTable").hide();
                      jQuery("#chartRendererImage").hide();
                      jQuery("#chartRendererStats").hide();
                      var selectorChartRenderer = "#" + charRenerer;
                      jQuery(selectorChartRenderer).show();
                   };


                   function showAndSetLoginType(prop) {

                       jQuery("#loggingType").val(prop.loggingType);

                       // Interval
                       if (prop.loggingType == 4) {
                         jQuery("#intervalLoggingSection").show();
                       } else {
                         jQuery("#intervalLoggingSection").hide();
                       }

                       //When point value changes
                       if (prop.loggingType == 1) {
                         jQuery("#tolerance").removeAttr('disabled');
                       } else {
                         jQuery("#tolerance").attr('disabled','disabled');
                       }

                       // Discard Extreme values
                       if (prop.discardExtremeValues) {
                         jQuery("#discardLowLimit").removeAttr('disabled');
                         jQuery("#discardHighLimit").removeAttr('disabled');
                       } else {
                         jQuery("#discardLowLimit").attr('disabled','disabled');
                         jQuery("#discardHighLimit").attr('disabled','disabled');
                       }

                       // tolerance ok
                       jQuery("#tolerance").val(prop.tolerance);

                       // Interval logging period
                       jQuery("#intervalLoggingPeriod").val(prop.intervalLoggingPeriod);

                       // Interval logging period type
                       jQuery("#intervalLoggingPeriodType").val(prop.intervalLoggingPeriodType);

                       // Value type
                       jQuery("#intervalLoggingType").val(prop.intervalLoggingType);

                       // discard extreme values
                       if (prop.discardExtremeValues) {
                          jQuery("#discardExtremeValues").attr('checked','checked')
                       } else {
                          jQuery("#discardExtremeValues").removeAttr('checked');
                       }

                       jQuery("#discardExtremeValues").val(prop.discardExtremeValues);

                       // discard low limit
                       jQuery("#discardLowLimit").val(prop.discardLowLimit);

                       // discard high limit
                       jQuery("#discardHighLimit").val(prop.discardHighLimit);

                       // set state of form base on loggingType

                       jQuery("#purgePeriod").val(prop.purgePeriod);
                       jQuery("#purgeType").val(prop.purgeType);
                       jQuery("#defaultCacheSize").val(prop.defaultCacheSize);

                   }

                   function setConfig(properties) {
                      jQuery("#engineeringUnits").val(properties.engineeringUnits);

                      showAndSetLoginType(properties);

                      var currentTextRenderer = $("textRendererSelect").value;

                      dojo.html.hide(
                        $(currentTextRenderer)
                      );

                      jQuery("#textRendererSelect").val(properties.def.name);

                      currentTextRenderer = $("textRendererSelect").value;

                      dojo.html.show(
                        $(currentTextRenderer)
                      );

                      if (properties.def.name == "textRendererBinary") {
                        jQuery("#textRendererBinaryZero").val(properties.textRenderer.zeroLabel);
                        dojo.widget.byId("textRendererBinaryZeroColour").selectedColour = properties.textRenderer.zeroColour;
                        jQuery("#textRendererBinaryZero").css('color', properties.textRenderer.zeroColour);
                        jQuery("#textRendererBinaryOne").val(properties.textRenderer.oneLabel);
                        jQuery("#textRendererBinaryOne").css('color', properties.textRenderer.oneColour);
                        dojo.widget.byId("textRendererBinaryOneColour").selectedColour = properties.textRenderer.oneColour;
                      }

                      if (properties.def.name == "textRendererPlain") {
                        jQuery("#textRendererPlainSuffix").val(properties.textRenderer.suffix);
                      }

                      if (properties.def.name == "textRendererMultistate") {
                        for (var multistate in properties.textRenderer.multistateValues) {
                            textRendererEditor.addMultistateValue(
                                properties.textRenderer.multistateValues[multistate].key,
                                properties.textRenderer.multistateValues[multistate].text,
                                properties.textRenderer.multistateValues[multistate].colour);
                        }
                      }

                      if (properties.def.name == "textRendererAnalog") {
                        jQuery("#textRendererAnalogFormat").val(properties.textRenderer.format);
                        jQuery("#textRendererAnalogSuffix").val(properties.textRenderer.metaText);
                      }

                      if (properties.def.name == "textRendererRange") {
                        jQuery("#textRendererRangeFormat").val(properties.textRenderer.format);

                        for (var range in properties.textRenderer.rangeValues) {
                            textRendererEditor.addRangeValue(
                                properties.textRenderer.rangeValues[range].from,
                                properties.textRenderer.rangeValues[range].to,
                                properties.textRenderer.rangeValues[range].text,
                                properties.textRenderer.rangeValues[range].color);
                        }
                      }

                      if (properties.def.name == "textRendererTime") {
                        jQuery("#textRendererTimeFormat").val(properties.textRenderer.format);
                        jQuery("#textRendererTimeConversionExponent").val(properties.textRenderer.conversionExponent);
                      }

                      if (properties.chartRenderer == null) {
                        jQuery("#chartRendererSelect").val("chartRendererNone");
                        showEditChartRender("chartRendererNone");
                      } else  if (properties.chartRenderer.def.name == "chartRendererImage") {
                        jQuery("#chartRendererSelect").val("chartRendererImage");
                        showEditChartRender("chartRendererImage");
                        jQuery("#chartRendererImageNumberOfPeriods").val(properties.chartRenderer.numberOfPeriods);
                        jQuery("#chartRendererImageTimePeriod").val(properties.chartRenderer.timePeriod);

                      } else if (properties.chartRenderer.def.name == "chartRendererStats") {

                        jQuery("#chartRendererSelect").val("chartRendererStats");
                        showEditChartRender("chartRendererStats");
                        jQuery("#chartRendererStatsNumberOfPeriods").val(properties.chartRenderer.numberOfPeriods);
                        jQuery("#chartRendererStatsTimePeriod").val(properties.chartRenderer.timePeriod);
                        jQuery("#chartRendererStatsIncludeSum").attr('checked', properties.chartRenderer.includeSum);

                      } else if (properties.chartRenderer.def.name == "chartRendererTable") {
                        jQuery("#chartRendererSelect").val("chartRendererTable");
                        showEditChartRender("chartRendererTable");
                        jQuery("#chartRendererTableLimit").val(properties.chartRenderer.limit);
                      }
                  }
                  // const
                  var pathArray = location.href.split( '/' );
                  var protocol = pathArray[0];
                  var host = pathArray[2];
                  var appScada = pathArray[3];
                  var myLocation;
                  if (!myLocation) {
                    myLocation = protocol + "//" + host + "/" + appScada + "/";
                   }

                   var arrDictLoggingType = ["", "When point value changes", "All data", "Do not log", "Interval", "When point timestamp changes"];

                   var arrDictPurge = ["","","","","day(s)", "week(s)", "month(s)", "year(s)"];

                   var arrDictChartRendererImageTimePeriod = ["","","minute(s)", "hour(s)", "day(s)", "week(s)", "month(s)"];
                   var arrDictIntervalLoggingPeriod = ["","second(s)","minute(s)", "hour(s)", "day(s)", "week(s)", "month(s)"];

                  var arrDictEnginneringUnits = [
                                                                  "square meters", /* 0 */
                                                                  "square feet",
                                                                  "milliamperes",
                                                                  "amperes",
                                                                  "ohms",
                                                                  "volts",
                                                                  "kilovolts",
                                                                  "megavolts",
                                                                  "volt amperes",
                                                                  "kilovolt amperes",
                                                                  "megavolt amperes", /* 10 */
                                                                  "volt amperes reactive",
                                                                  "kilovolt amperes reactive",
                                                                  "megavolt amperes reactive",
                                                                  "degrees phase",
                                                                  "power factor",
                                                                  "joules",
                                                                  "kilojoules",
                                                                  "watt hours",
                                                                  "kilowatt hours",
                                                                  "btus",  /* 20 */
                                                                  "therms",
                                                                  "ton hours",
                                                                  "joules per kilogram dry air",
                                                                  "btus per pound dry air",
                                                                  "cycles per hour",
                                                                  "cycles per minute",
                                                                  "hertz",
                                                                  "grams of water per kilogram dry air",
                                                                  "percent relative humidity",
                                                                  "millimeters", /* 30 */
                                                                  "meters",
                                                                  "inches",
                                                                  "feet",
                                                                  "watts per square foot",
                                                                  "watts per square meter",
                                                                  "lumens",
                                                                  "luxes",
                                                                  "foot candles",
                                                                  "kilograms",
                                                                  "pounds mass", /* 40 */
                                                                  "tons",
                                                                  "kilograms per second",
                                                                  "kilograms per minute",
                                                                  "kilograms per hour",
                                                                  "pounds mass per minute",
                                                                  "pounds mass per hour",
                                                                  "watts",
                                                                  "kilowatts",
                                                                  "megawatts",
                                                                  "btus per hour", /* 50 */
                                                                  "horsepower",
                                                                  "tons refrigeration",
                                                                  "pascals",
                                                                  "kilopascals",
                                                                  "bars",
                                                                  "pounds force per square inch",
                                                                  "centimeters of water",
                                                                  "inches of water",
                                                                  "millimeters of mercury",
                                                                  "centimeters of mercury", /* 60 */
                                                                  "inches of mercury",
                                                                  "degrees celsius",
                                                                  "degrees kelvin",
                                                                  "degrees fahrenheit",
                                                                  "degree days celsius",
                                                                  "degree days fahrenheit",
                                                                  "years",
                                                                  "months",
                                                                  "weeks",
                                                                  "days", /* 70 */
                                                                  "hours",
                                                                  "minutes",
                                                                  "seconds",
                                                                  "meters per second",
                                                                  "",
                                                                  "feet per second",
                                                                  "feet per minute",
                                                                  "miles per hour",
                                                                  "cubic feet",
                                                                  "cubic meters", /* 80 */
                                                                  "imperial gallons",
                                                                  "liters",
                                                                  "us gallons",
                                                                  "cubic feet per minute",
                                                                  "cubic meters per second",
                                                                  "imperial gallons per minute",
                                                                  "liters per second",
                                                                  "liters per minute",
                                                                  "us gallons per minute",
                                                                  "degrees angular", /* 90 */
                                                                  "degrees celsius per hour",
                                                                  "degrees celsius per minute",
                                                                  "degrees fahrenheit per hour",
                                                                  "degrees fahrenheit per minute",
                                                                  "no units",
                                                                  "parts per million",
                                                                  "parts per billion",
                                                                  "percent",
                                                                  "percent per second",
                                                                  "per minute", /* 100 */
                                                                  "per second",
                                                                  "psi per degree fahrenheit",
                                                                  "radians",
                                                                  "revolutions per minute",
                                                                  "currency 1",
                                                                  "currency 2",
                                                                  "currency 3",
                                                                  "currency 4",
                                                                  "currency 5",
                                                                  "currency 6", /* 110 */
                                                                  "currency 7",
                                                                  "currency 8",
                                                                  "currency 9",
                                                                  "currency 10",
                                                                  "square inches",
                                                                  "square centimeters",
                                                                  "btus per pound",
                                                                  "centimeters",
                                                                  "pounds mass per second",
                                                                  "delta degrees fahrenheit", /* 120 */
                                                                  "delta degrees kelvin",
                                                                  "kilohms",
                                                                  "megohms",
                                                                  "millivolts",
                                                                  "kilojoules per kilogram",
                                                                  "megajoules",
                                                                  "joules per degree kelvin",
                                                                  "joules per kilogram degree kelvin",
                                                                  "kilohertz",
                                                                  "megahertz", /* 130 */
                                                                  "per hour",
                                                                  "milliwatts",
                                                                  "hectopascals",
                                                                  "millibars",
                                                                  "cubic meters per hour",
                                                                  "liters per hour",
                                                                  "kilowatt hours per square meter",
                                                                  "kilowatt hours per square foot",
                                                                  "megajoules per square meter",
                                                                  "megajoules per square foot", /* 140 */
                                                                  "watts per square meter degree kelvin",
                                                                  "cubic feet per second",
                                                                  "percent obscuration per foot",
                                                                  "percent obscuration per meter",
                                                                  "milliohms",
                                                                  "megawatt hours",
                                                                  "kilo btus",
                                                                  "mega btus",
                                                                  "kilojoules per kilogram dry air",
                                                                  "megajoules per kilogram dry air", /* 150 */
                                                                  "kilojoules per degree kelvin",
                                                                  "megajoules per degree kelvin",
                                                                  "newton",
                                                                  "grams per second",
                                                                  "grams per minute",
                                                                  "tons per hour",
                                                                  "kilo btus per hour",
                                                                  "hundredths seconds",
                                                                  "milliseconds",
                                                                  "newton meters", /* 160 */
                                                                  "millimeters per second",
                                                                  "millimeters per minute",
                                                                  ">meters per minute",
                                                                  "meters per hour",
                                                                  "cubic meters per minute",
                                                                  "meters per second per second",
                                                                  "amperes per meter",
                                                                  "amperes per square meter",
                                                                  "ampere square meters",
                                                                  "farads", /* 170 */
                                                                  "henrys",
                                                                  "ohm meters",
                                                                  "siemens",
                                                                  "siemens per meter",
                                                                  "teslas",
                                                                  "volts per degree kelvin",
                                                                  "volts per meter",
                                                                  "webers",
                                                                  "candelas",
                                                                  "candelas per square meter", /* 180 */
                                                                  "degrees kelvin per hour",
                                                                  "degrees kelvin per minute",
                                                                  "joule seconds",
                                                                  "radians per second",
                                                                  "square meters perNewton",
                                                                  "kilograms per cubic meter",
                                                                  "newton seconds",
                                                                  "newtons per meter",
                                                                  "watts per meter per degree kelvin",
                                                                  "", /* 190 */
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "",
                                                                  "" /* 200 */
                  ];


                  function baseOnExistingPointWithOutHint() {
                    var idPointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].value;
                    var namePointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].text;

                    jQuery.ajax({
                            type: "GET",
                            dataType: "json",
                            url:myLocation+"/api/point_properties/getPropertiesBaseOnId/"+idPointConfigurationToBaseOnExistingPoint,
                                                           success: function(properties){
                                                                setConfig(properties);
                                                           },
                                                           error: function(XMLHttpRequest, textStatus, errorThrown) {
                                                                swal({
                                                                    text: "Problem when get properties:"+errorThrown.message,
                                                                        buttons: {
                                                                            cancel: true
                                                                        },
                                                                    });
                                                                }
                    });

                  }

                  function baseOnExistingPoint(){
                       var idPointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].value;
                       var namePointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].text;

                       jQuery.ajax({
                            type: "GET",
                            dataType: "json",
                            url:myLocation+"/api/point_properties/getPropertiesBaseOnId/"+idPointConfigurationToBaseOnExistingPoint,
                           					        	   success: function(properties){

                           					        	            let htmlLogginProperties = "";

                           					        	            if (properties.loggingType == 1) {

                           					        	              // When point value changes 1
                           					        	              htmlLogginProperties = ""
                           					        	                + "<li><b>Logging type:</b> " + arrDictLoggingType[properties.loggingType] + "</li>"
                                                                        + "<li><b>Tolerance:</b> " + properties.tolerance + "</li>"

                           					        	            } else if (
                           					        	                (properties.loggingType == 2) ||
                           					        	                (properties.loggingType == 3) ||
                           					        	                (properties.loggingType == 5) ) {

                           					        	              // All data 2
                           					        	              // Do not log 3
                           					        	              // When point timestamp changes 5

                           					        	              htmlLogginProperties = ""
                           					        	                + "<li><b>Logging type:</b> " + arrDictLoggingType[properties.loggingType] + "</li>"

                           					        	            } else if (properties.loggingType == 4) {

                           					        	              // Interval 4

                                                                      htmlLogginProperties = ""
                                                                        + "<li><b>Logging type:</b> " + arrDictLoggingType[properties.loggingType] + "</li>"
                                                                        + "<li><b>Interval logging period:</b> Every:" + properties.intervalLoggingPeriod + " " + arrDictIntervalLoggingPeriod[properties.intervalLoggingPeriodType] + "</li>"
                           					        	            }

                                                                    htmlLogginProperties = ""
                                                                       + "<li><b>Discard extreme values:</b> " + properties.discardExtremeValues + " low:" + properties.discardLowLimit + " high:" + properties.discardHighLimit + "</li>"

                                                                    let textRenderer = "";

                                                                    if (properties.def.name == "textRendererBinary") {
                                                                        textRenderer = ""
                                                                          + "<li><b>Text renderer properties:</b>  Binary </br>"
                                                                          + "zero: " + properties.textRenderer.zeroLabel + " color:" + properties.textRenderer.zeroColour + "</br>"
                                                                          + "one: " +  properties.textRenderer.oneLabel +  " color:" + properties.textRenderer.oneColour + "</li>";
                                                                    }

                                                                    if (properties.def.name == "textRendererPlain") {
                                                                        textRenderer = ""
                                                                          + "<li><b>Text renderer properties:</b>  Plain </br>"
                                                                          + "Suffix: " + properties.textRenderer.suffix + "</li>";
                                                                    }

                                                                    if (properties.def.name == "textRendererMultistate") {
                                                                       textRenderer = ""
                                                                          + "<li><b>Text renderer properties:</b>  Multistate </br>";

                                                                      for (var multistate in properties.textRenderer.multistateValues) {
                                                                        textRenderer = textRenderer + "key: " + properties.textRenderer.multistateValues[multistate].key
                                                                           + " text: " + properties.textRenderer.multistateValues[multistate].text
                                                                           + " color: " + properties.textRenderer.multistateValues[multistate].colour;
                                                                      }

                                                                      textRenderer =+ "</li>";
                                                                     }

                                                                     if (properties.def.name == "textRendererAnalog") {
                                                                        textRenderer =  ""
                                                                          + "<li><b>Text renderer properties:</b>  Analog </br>"
                                                                          + " Format: " + properties.textRenderer.format + "</br>"
                                                                          + " Suffix: " + properties.textRenderer.metaText + "</li>";
                                                                     }

                                                                     if (properties.def.name == "textRendererRange") {

                                                                        textRenderer = ""
                                                                            + "<li><b>Text renderer properties:</b>  Range </br>"
                                                                            + " Format: " + properties.textRenderer.format + "</br>";

                                                                        for (var range in properties.textRenderer.rangeValues) {

                                                                            textRenderer = textRenderer + "from: " + properties.textRenderer.rangeValues[range].from
                                                                               + " to: " + properties.textRenderer.rangeValues[range].to
                                                                               + " text: " + properties.textRenderer.rangeValues[range].text
                                                                               + " color: " + properties.textRenderer.rangeValues[range].color;

                                                                        }
                                                                     }

                                                                      if (properties.def.name == "textRendererTime") {
                                                                            textRenderer = ""
                                                                               + "<li><b>Text renderer properties:</b>  Time </br>"
                                                                               + " Format: " + properties.textRenderer.format + "</br>"
                                                                               + " Exponent: " + properties.textRenderer.conversionExponent + "</li>";
                                                                      }

                                                                      let chartRenderer = "";

                                                                      if (properties.chartRenderer == null) {
                                                                            chartRenderer = ""
                                                                                + "<li><b>Chart renderer properties:</b>  None</li>";

                                                                       } else  if (properties.chartRenderer.def.name == "chartRendererImage") {
                                                                            chartRenderer = ""
                                                                                + "<li><b>Chart renderer properties:</b>  Images</br>"
                                                                                + "Time period:" + properties.chartRenderer.numberOfPeriods + " " + arrDictChartRendererImageTimePeriod[properties.chartRenderer.timePeriod] + "</li>";

                                                                       } else if (properties.chartRenderer.def.name == "chartRendererStats") {

                                                                            chartRenderer = ""
                                                                                + "<li><b>Chart renderer properties:</b>  Statistics</br>"
                                                                                + "Time period:" + properties.chartRenderer.numberOfPeriods + " " + arrDictChartRendererImageTimePeriod[properties.chartRenderer.timePeriod] + "</br>"
                                                                                + "Include sum:" + properties.chartRenderer.includeSum + "</li>";

                                                                       } else if (properties.chartRenderer.def.name == "chartRendererTable") {

                                                                            chartRenderer = ""
                                                                                + "<li><b>Chart renderer properties:</b>  Table</br>"
                                                                                + "Limit:" + properties.chartRenderer.limit + "</li>";
                                                                       }

                           					        	            swal({
                                                                      title: '<i>New</i> <u>configuration point</u>',
                                                                      type: 'warning',
                                                                      html: "<div class='font-size:8px'> "
                                                                        + "<p><b>base on</b> " + namePointConfigurationToBaseOnExistingPoint + "</p>"
                                                                        + "<b>Point properties:</b> "
                                                                        + "<ul class='gbul'> "
                                                                        + "<li><b>Engineering units:</b> " + arrDictEnginneringUnits[properties.engineeringUnits] + "</li>"
                                                                        + htmlLogginProperties
                                                                        + "<li><b>Purge After:</b> " + properties.purgePeriod + " " + arrDictPurge[properties.purgeType] + "</li>"
                                                                        + "<li><b>Default cache size:</b> " + properties.defaultCacheSize + "</li>"
                                                                        + textRenderer
                                                                        + chartRenderer
                                                                        + "</ul></div>",
                                                                      showCloseButton: true,
                                                                      showCancelButton: true,
                                                                      focusConfirm: true,
                                                                      confirmButtonText:
                                                                        '<i class="fa fa-thumbs-up"></i> Accept',
                                                                      confirmButtonAriaLabel: 'Thumbs up, great!',
                                                                      cancelButtonText:
                                                                      '<i class="fa fa-thumbs-down">Cancel</i>',
                                                                      cancelButtonAriaLabel: 'Thumbs down',
                                                                      customClass: "gbtest"
                                                                    }).then(function(isConfirm) {
                                                                        if (isConfirm.value) {
                                                                            setConfig(properties);
                                                                        } else {
                                                                            console.log(isConfirm);
                                                                        }
                                                                     });

                           					        	   },
                           					        	   error: function(XMLHttpRequest, textStatus, errorThrown) {
                           					        		   swal({
                                                                  text: "Problem when get properties:"+errorThrown.message,
                                                                  buttons: {
                                                                    cancel: true
                                                                   },
                                                                });
                           					        	   }
                           					        	});
                  }
            </script>

            <div class="borderDiv marB marR">
              <table>
                <tr><td colspan="4">
                  <span class="smallTitle">Configuration base on existing point</span>
                </td></tr>

                <tr>
                  <td class="formLabelRequired">Select point:</td>
                  <td colspan="2" class="formField">
                    <select id="selected_base_on_existing_point_chooser">
                                   <c:forEach items="${userPoints}" var="point">
                                     <sst:option value="${point.id}">${point.extendedName}</sst:option>
                                   </c:forEach>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">
                    <input id="baseOnExistingPointBtn" type="button" value="Set configuration base on existing point (from hints of changes)" onclick="baseOnExistingPoint()">
                  </td>
                </tr>
                <tr>
                  <td colspan="4">
                      <input id="baseOnExistingPointBtnWithOutHint" type="button" value="Set configuration base on existing point (no hint of change)" onclick="baseOnExistingPointWithOutHint()">
                  </td>
                </tr>
              </table>
              <p>Note: changes require saving</p>
            </div>

        </td>
      </tr>
    </table>

    <%@ include file="/WEB-INF/jsp/pointEdit/buttons.jsp" %>
  </form>
</tag:page>