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

     <link href="resources/node_modules/sweetalert2/dist/sweetalert2.min.css" rel="stylesheet" type="text/css">
     <script type="text/javascript" src="resources/node_modules/sweetalert2/dist/sweetalert2.min.js"></script>

     <style type="text/css">

         #swal2-title {
            font-size: 16px;
         }
         .scada-swal2 {
            color: blue !important;
            text-size: 12em;
         }
         .swal-content {
            font-size: 12px;
         }
         .scada-swal-ul {
            text-align: left;
            font-weight: bold;
            list-style-type: none;
            font-size: 12px;
            margin-left: 0px;
            padding: 0;
         }
         .scada-swal-ul2 {
            text-align: left;
            list-style-type: none;
            font-weight: normal;
            margin-left: 10px;
            padding: 0;
         }
         .scada-swal-content {
            font-family: Verdana, Arial, Helvetica, sans-serif;
            font-size: 14px;
         }
         .swal2-cancel {
           font-size: 0.8em;
         }
         .swal2-actions {
           font-size: 0.8em;
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
          <%@ include file="/WEB-INF/jsp/pointEdit/eventTextRenderer.jsp"%>
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

                   function checkGetAlertError() {
                     return jQuery("#checkGetAlertError").prop('checked');
                   }
                   function checkType(pointDataTypeIdFromNewConfiguration) {
                     let existingDataTypeId = dataTypeId;
                     return existingDataTypeId == pointDataTypeIdFromNewConfiguration;
                   }

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

                       jQuery("[name='intervalLoggingPeriod']").val(prop.intervalLoggingPeriod);

                       // Interval logging period type
                       jQuery("[name='intervalLoggingPeriodType']").val(prop.intervalLoggingPeriodType);

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

                       jQuery("#purgeStrategy").val(prop.purgeStrategy);
                       jQuery("#purgeNowStrategy").val(prop.purgeStrategy);
                       jQuery("#purgeValuesLimit").val(prop.purgeValuesLimit);

                   }

                   function setConfig(properties) {
                      jQuery('[name="engineeringUnits"]').val(properties.engineeringUnits);

                      showAndSetLoginType(properties);

                       var currentEventTextRenderer = $("eventTextRendererSelect").value;

                       dojo.html.hide(
                           $(currentEventTextRenderer)
                       );

                       jQuery("#eventTextRendererSelect").val(properties.eventTextRenderer.def.name);

                       currentEventTextRenderer = $("eventTextRendererSelect").value;

                       dojo.html.show(
                           $(currentEventTextRenderer)
                       );
                       if (properties.eventTextRenderer.def.name == "eventTextRendererBinary") {
                           jQuery("#eventTextRendererBinaryZero").val(properties.eventTextRenderer.zeroLabel);
                           jQuery("#eventTextRendererBinaryOne").val(properties.eventTextRenderer.oneLabel);
                       }

                       if (properties.eventTextRenderer.def.name == "eventTextRendererMultistate") {

                           if (checkGetAlertError()) {
                               try {
                                   var alert_old = alert;
                                   alert = function (message) {
                                       console.log(message);
                                   }
                                   for (var multistate in properties.eventTextRenderer.multistateEventValues) {
                                       eventTextRendererEditor.addMultistateEventValue(
                                           String( properties.eventTextRenderer.multistateEventValues[multistate].key ),
                                           String( properties.eventTextRenderer.multistateEventValues[multistate].text ));
                                   }
                               } catch (err) {
                                   console.log(err);
                               } finally {
                                   alert = alert_old;
                               }
                           } else {
                               for (var multistate in properties.eventTextRenderer.multistateEventValues) {
                                   eventTextRendererEditor.addMultistateEventValue(
                                       String( properties.eventTextRenderer.multistateEventValues[multistate].key ),
                                       String( properties.eventTextRenderer.multistateEventValues[multistate].text ));
                               }
                           }
                       }

                       if (properties.eventTextRenderer.def.name == "eventTextRendererRange") {

                           if (checkGetAlertError()) {
                               let thisEventRangeValues = this.eventTextRendererEditor.getRangeEventValues();
                               try {
                                   var alert_old = alert;
                                   alert = function (message) {
                                       console.log(message);
                                   }
                                   for (var range in properties.eventTextRenderer.rangeEventValues) {
                                       for (let i = 0; i < thisEventRangeValues.length; i++){
                                            if(
                                                parseFloat(thisEventRangeValues[i].from) === parseFloat(properties.eventTextRenderer.rangeEventValues[range].from) &&
                                                parseFloat(thisEventRangeValues[i].to) === parseFloat(properties.eventTextRenderer.rangeEventValues[range].to))
                                           {
                                               eventTextRendererEditor.removeRangeEventValue(parseFloat(properties.eventTextRenderer.rangeEventValues[range].from),
                                                   parseFloat(properties.eventTextRenderer.rangeEventValues[range].to))
                                           }
                                       }
                                       eventTextRendererEditor.addRangeEventValue(
                                           String( properties.eventTextRenderer.rangeEventValues[range].from ),
                                           String( properties.eventTextRenderer.rangeEventValues[range].to ),
                                           String( properties.eventTextRenderer.rangeEventValues[range].text ));
                                   }
                               } catch( err ) {
                                   console.log(err);
                               } finally {
                                   alert = alert_old;
                               }

                           } else {
                               for (var range in properties.eventTextRenderer.rangeEventValues) {
                                   eventTextRendererEditor.addRangeEventValue(
                                       String( properties.eventTextRenderer.rangeEventValues[range].from ),
                                       String( properties.eventTextRenderer.rangeEventValues[range].to ),
                                       String( properties.eventTextRenderer.rangeEventValues[range].text ));
                               }
                           }

                       }

                       // text renderer

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

                            if (checkGetAlertError()) {
                               try {
                                    var alert_old = alert;
                                    alert = function (message) {
                                        console.log(message);
                                    }
                                    for (var multistate in properties.textRenderer.multistateValues) {
                                        textRendererEditor.addMultistateValue(
                                            String( properties.textRenderer.multistateValues[multistate].key ),
                                            String( properties.textRenderer.multistateValues[multistate].text ),
                                            String( properties.textRenderer.multistateValues[multistate].colour ));
                                    }
                               } catch (err) {
                                    console.log(err);
                               } finally {
                                    alert = alert_old;
                               }
                            } else {
                               for (var multistate in properties.textRenderer.multistateValues) {
                                    textRendererEditor.addMultistateValue(
                                        String( properties.textRenderer.multistateValues[multistate].key ),
                                        String( properties.textRenderer.multistateValues[multistate].text ),
                                        String( properties.textRenderer.multistateValues[multistate].colour ));
                               }
                            }
                      }

                      if (properties.def.name == "textRendererAnalog") {
                        jQuery("#textRendererAnalogFormat").val(properties.textRenderer.format);
                        jQuery("#textRendererAnalogSuffix").val(properties.textRenderer.metaText);
                      }

                      if (properties.def.name == "textRendererRange") {
                        jQuery("#textRendererRangeFormat").val(properties.textRenderer.format);

                        if (checkGetAlertError()) {
                            try {
                                let thisRangeValues = this.textRendererEditor.getRangeValues();
                                var alert_old = alert;
                                alert = function (message) {
                                    console.log(message);
                                }
                                for (var range in properties.textRenderer.rangeValues) {
                                    for (let i = 0; i < thisRangeValues.length; i++) {
                                        if (
                                            parseFloat(thisRangeValues[i].from) === parseFloat(properties.textRenderer.rangeValues[range].from) &&
                                            parseFloat(thisRangeValues[i].to) === parseFloat(properties.textRenderer.rangeValues[range].to))
                                        {
                                            textRendererEditor.removeRangeValue(parseFloat(properties.textRenderer.rangeValues[range].from), parseFloat(properties.textRenderer.rangeValues[range].to));
                                        }
                                    }
                                    textRendererEditor.addRangeValue(
                                        String( properties.textRenderer.rangeValues[range].from ),
                                        String( properties.textRenderer.rangeValues[range].to ),
                                        String( properties.textRenderer.rangeValues[range].text ),
                                        String( properties.textRenderer.rangeValues[range].colour ));
                                }
                            } catch( err ) {
                                console.log(err);
                            } finally {
                                alert = alert_old;
                            }

                        } else {
                            for (var range in properties.textRenderer.rangeValues) {
                               textRendererEditor.addRangeValue(
                                    String( properties.textRenderer.rangeValues[range].from ),
                                    String( properties.textRenderer.rangeValues[range].to ),
                                    String( properties.textRenderer.rangeValues[range].text ),
                                    String( properties.textRenderer.rangeValues[range].colour ));
                            }
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

                  function confirmText(properties) {

                      let result = "";

                      let htmlLogginProperties = "";

                      if (properties.loggingType == 1) {
                          // When point value changes 1
                          htmlLogginProperties = ""
                          + "<li>Logging type: " + arrDictLoggingType[properties.loggingType] + "</li>"
                          + "<li>Tolerance: " + properties.tolerance + "</li>"
                      } else if (
                        (properties.loggingType == 2) ||
                        (properties.loggingType == 3) ||
                        (properties.loggingType == 5) ) {

                        // All data 2
                        // Do not log 3
                        // When point timestamp changes 5

                        htmlLogginProperties = ""
                        + "<li>Logging type: " + arrDictLoggingType[properties.loggingType] + "</li>";

                      } else if (properties.loggingType == 4) {

                        // Interval 4
                        htmlLogginProperties = ""
                        + "<li>Logging type: " + arrDictLoggingType[properties.loggingType] + "</li>"
                        + "<li>Interval logging period: Every:" + properties.intervalLoggingPeriod + " " + arrDictIntervalLoggingPeriod[properties.intervalLoggingPeriodType] + "</li>"
                      }

                      htmlLogginProperties = htmlLogginProperties + ""
                      + "<li>Discard extreme values: " + properties.discardExtremeValues
                      + "<ul class='scada-swal-ul2'>"
                      + "<li>low:" + properties.discardLowLimit + "</li>"
                      + "<li>high:" + properties.discardHighLimit + "</li></ul></li>"

                      let eventTextRenderer = "";

                      if (properties.eventTextRenderer.def.name == "eventTextRendererBinary") {
                          eventTextRenderer = ""
                              + "<li>Event text renderer properties: Binary"
                              + "<ul class='scada-swal-ul2'>"
                              + "<li>zero: " + properties.eventTextRenderer.zeroLabel + "</li>"
                              + "<li>one: " +  properties.eventTextRenderer.oneLabel + "</li></ul></li>";
                      }

                      if (properties.eventTextRenderer.def.name == "eventTextRendererMultistate") {

                          eventTextRenderer = ""
                              + "<li>Event text renderer properties: Multistate "
                              + "<ul class='scada-swal-ul2'>";

                          for (var multistate in properties.eventTextRenderer.multistateEventValues) {
                              eventTextRenderer = eventTextRenderer + "<li>key: " + properties.eventTextRenderer.multistateEventValues[multistate].key
                                  + " text: " + properties.eventTextRenderer.multistateEventValues[multistate].text + "</li>";
                          }

                          eventTextRenderer = eventTextRenderer + "</ul></li>";
                      }

                      if (properties.eventTextRenderer.def.name == "eventTextRendererRange") {

                          eventTextRenderer = ""
                              + "<li>Text renderer properties: Range "
                              + "<ul class='scada-swal-ul2'>";

                          for (var range in properties.eventTextRenderer.rangeEventValues) {

                              eventTextRenderer = eventTextRenderer + "<li>from: " + properties.eventTextRenderer.rangeEventValues[range].from
                                  + " to: " + properties.eventTextRenderer.rangeEventValues[range].to
                                  + " text: " + properties.eventTextRenderer.rangeEventValues[range].text + "</li>";

                          }

                          eventTextRenderer = eventTextRenderer + "</ul></li>";
                      }

                      let textRenderer = "";

                      if (properties.def.name == "textRendererBinary") {
                        textRenderer = ""
                        + "<li>Text renderer properties: Binary"
                        + "<ul class='scada-swal-ul2'>"
                        + "<li>zero: " + properties.textRenderer.zeroLabel + " color:" + properties.textRenderer.zeroColour + "</li>"
                        + "<li>one: " +  properties.textRenderer.oneLabel +  " color:" + properties.textRenderer.oneColour + "</li></ul></li>";
                      }

                      if (properties.def.name == "textRendererPlain") {
                        textRenderer = ""
                        + "<li>Text renderer properties: Plain"
                        + "<ul class='scada-swal-ul2'>"
                        + "<li>Suffix: " + properties.textRenderer.suffix + "</li></ul></li>";
                      }

                      if (properties.def.name == "textRendererMultistate") {

                        textRenderer = ""
                        + "<li>Text renderer properties: Multistate "
                        + "<ul class='scada-swal-ul2'>";

                        for (var multistate in properties.textRenderer.multistateValues) {
                            textRenderer = textRenderer + "<li>key: " + properties.textRenderer.multistateValues[multistate].key
                            + " text: " + properties.textRenderer.multistateValues[multistate].text
                            + " color: " + properties.textRenderer.multistateValues[multistate].colour + "</li>";
                        }

                        textRenderer = textRenderer + "</ul></li>";
                      }

                      if (properties.def.name == "textRendererAnalog") {
                        textRenderer =  ""
                        + "<li>Text renderer properties: Analog "
                        + "<ul class='scada-swal-ul2'>"
                        + "<li> Format: " + properties.textRenderer.format + "</li>"
                        + "<li> Suffix: " + properties.textRenderer.metaText + "</li></ul></li>";
                      }

                      if (properties.def.name == "textRendererRange") {

                        textRenderer = ""
                        + "<li>Text renderer properties: Range "
                        + "<ul class='scada-swal-ul2'>"
                        + "<li> Format: " + properties.textRenderer.format + "</li>";

                        for (var range in properties.textRenderer.rangeValues) {

                            textRenderer = textRenderer + "<li>from: " + properties.textRenderer.rangeValues[range].from
                            + " to: " + properties.textRenderer.rangeValues[range].to
                            + " text: " + properties.textRenderer.rangeValues[range].text
                            + " color: " + properties.textRenderer.rangeValues[range].colour + "</li>";

                        }

                        textRenderer = textRenderer + "</ul></li>";
                      }

                      if (properties.def.name == "textRendererTime") {
                        textRenderer = ""
                            + "<li>Text renderer properties: Time <ul class='scada-swal-ul2'>"
                            + "<li>Format: " + properties.textRenderer.format + "</li>"
                            + "<li>Exponent: " + properties.textRenderer.conversionExponent + "</li></ul></li>";
                      }

                      let chartRenderer = "";

                      if (properties.chartRenderer == null) {
                        chartRenderer = ""
                            + "<li>Chart renderer properties: None</li>";

                      } else  if (properties.chartRenderer.def.name == "chartRendererImage") {

                        chartRenderer = ""
                        + "<li>Chart renderer properties: Images<ul class='scada-swal-ul2'>"
                        + "<li>Time period:" + properties.chartRenderer.numberOfPeriods + " " + arrDictChartRendererImageTimePeriod[properties.chartRenderer.timePeriod] + "</li></ul></li>";

                      } else if (properties.chartRenderer.def.name == "chartRendererStats") {

                        chartRenderer = ""
                        + "<li>Chart renderer properties: Statistics<ul class='scada-swal-ul2'>"
                        + "<li>Time period:" + properties.chartRenderer.numberOfPeriods + " " + arrDictChartRendererImageTimePeriod[properties.chartRenderer.timePeriod] + "</li>"
                        + "<li>Include sum:" + properties.chartRenderer.includeSum + "</li></ul></li>";

                      } else if (properties.chartRenderer.def.name == "chartRendererTable") {

                        chartRenderer = ""
                        + "<li>Chart renderer properties: Table<ul class='scada-swal-ul2'>"
                        + "<li>Limit:" + properties.chartRenderer.limit + "</li></ul></li>";
                      }

                      let textConfirmButton = "";

                      let bCheckedType = checkType(properties.dataTypeId);

                      let namePointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].text;

                      result = "<div class='gb-content'> "
                        + "<p><strong>basing on</strong> <i>" + namePointConfigurationToBaseOnExistingPoint + "</i></p>"
                        + "<ul class='scada-swal-ul'> "
                        + "<li>Engineering units: " + arrDictEnginneringUnits[properties.engineeringUnits] + "</li>"
                        + htmlLogginProperties
                        + "<li>Purge After: " + properties.purgePeriod + " " + arrDictPurge[properties.purgeType] + "</li>"
                        + "<li>Default cache size: " + properties.defaultCacheSize + "</li>"
                        + eventTextRenderer
                        + textRenderer
                        + chartRenderer
                        + "</ul></div>";

                      return result;
                  }

                  function baseOnExistingPointWithOutHint() {
                    let idPointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].value;

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

                                                                    let bCheckedType = checkType(properties.dataTypeId);

                                                                    let htmlConfirmText = "";
                                                                    let htmlTitle = "";

                                                                    if (bCheckedType) {
                                                                        htmlTitle = "Apply new properties"
                                                                        htmlConfirmText = confirmText(properties);

                                                                      } else {
                                                                        htmlTitle = "Invalid point data type";
                                                                        htmlConfirmText = "It is possible to apply point properties basing only on the same type of points";
                                                                      }

                           					        	            swal({
                                                                      title: htmlTitle,
                                                                      // icon: 'warning',
                                                                      html: htmlConfirmText,
                                                                      showCloseButton: true,
                                                                      showCancelButton: true,
                                                                      showConfirmButton: bCheckedType,
                                                                      focusConfirm: bCheckedType,
                                                                      confirmButtonText:
                                                                        '<i class="fa fa-thumbs-up"></i> Accept',
                                                                      confirmButtonAriaLabel: 'Thumbs up, great!',
                                                                      cancelButtonText:
                                                                      '<i class="fa fa-thumbs-down">Cancel</i>',
                                                                      cancelButtonAriaLabel: 'Thumbs down',
                                                                      customClass: "scada-swal2"
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

        </td>
      </tr>
    </table>

    <%@ include file="/WEB-INF/jsp/pointEdit/buttons.jsp" %>
  </form>
        <table width="100%" cellpadding="0" cellspacing="0">
        <tr>
            <td>
                <div class="borderDiv marB marR" style="margin:20px; padding:10px; border-color:blue; max-width: 800px;">
                    <table width="100%" cellpadding="0" cellspacing="0">
                        <tr><td colspan="4">
                            <span class="smallTitle"> <fmt:message key="pointEdit.basing_on.title"/></span>
                        </td></tr>

                        <tr>
                            <td class="formLabelRequired"><fmt:message key="pointEdit.basing_on.select"/></td>
                            <td colspan="2" class="formField">
                                <select id="selected_base_on_existing_point_chooser">
                                    <c:forEach items="${userPoints}" var="point">
                                        <sst:option value="${point.id}">${point.extendedName}</sst:option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input id="baseOnExistingPointBtn" type="button" value="<fmt:message key="pointEdit.basing_on.apply"/>" onclick="baseOnExistingPoint()">
                            </td>
                            <td colspan="3">
                                <input type="checkbox" id="checkGetAlertError" value="true" checked>
                                <label for="checkGetAlertError"><fmt:message key="pointEdit.basing_on.warning_on"/></label>
                            </td>
                        </tr>
                    </table>
                    <p>Note: changes require saving</p>
                </div>
          </td>
        </tr>
        <tr>
          <td>
            <%@ include file="/WEB-INF/jsp/pointEdit/valuePurge.jsp" %>
          </td>
        </tr>
        </table>

</tag:page>