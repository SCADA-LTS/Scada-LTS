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
        </br>
        <div id="base_on_existing_point_chooser">
             <select id="selected_base_on_existing_point_chooser">
               <c:forEach items="${userPoints}" var="point">
                 <sst:option value="${point.id}">${point.extendedName}</sst:option>
               </c:forEach>
             </select>
         </div>
            <script>
                  function baseOnExistingPoint(){
                       var idPointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].value;
                       var namePointConfigurationToBaseOnExistingPoint = jQuery('#selected_base_on_existing_point_chooser').find(":selected")[0].text;
                       var swal_message = namePointConfigurationToBaseOnExistingPoint + "</br> id:"+idPointConfigurationToBaseOnExistingPoint;

                       var pathArray = location.href.split( '/' );
                       var protocol = pathArray[0];
                       var host = pathArray[2];
                       var appScada = pathArray[3];
                       var myLocation;
                       if (!myLocation) {
                        myLocation = protocol + "//" + host + "/" + appScada + "/";
                       }

                       var arrDictLoggingType = ["When point value changes", "All data", "Do not log", "Interval", "When point timestamp changes"];

                       var arrDictPurge = ["","","","","day(s)", "week(s)", "month(s)", "year(s)"];

                       jQuery.ajax({
                            type: "GET",
                            dataType: "json",
                            url:myLocation+"/api/point_properties/getPropertiesBaseOnId/"+idPointConfigurationToBaseOnExistingPoint,
                           					        	   success: function(properties){
                           					        	            swal({
                                                                      html: swal_message
                                                                                 + "</br> Logging type:"+arrDictLoggingType[properties.loggingType]
                                                                                 + "</br> Purge After:"+properties.intervalLoggingPeriod+" "+ arrDictPurge[properties.purgeType]
                                                                                 + "</br> Default cache size: " + properties.defaultCacheSize
                                                                                 + "</br> Type key: " + properties.typeKey
                                                                                 + "</br> Def: " + properties.def.name
                                                                                 + "</br>",
                                                                      buttons: {
                                                                        cancel: true,
                                                                        confirm: "Confirm",
                                                                        icon: "warning",
                                                                        buttons: true,
                                                                        dangerMode: true,
                                                                       },
                                                                     }).then(function(isConfirm) {
                                                                         if (isConfirm) {
                                                                           alert(JSON.stringify(properties));
                                                                           jQuery("#loggingType").val(properties.loggingType);
                                                                           jQuery("#purgePeriod").val(properties.intervalLoggingPeriod);
                                                                           jQuery("#purgeType").val(properties.purgeType);
                                                                           jQuery("#defaultCacheSize").val(properties.defaultCacheSize);

                                                                           var currentTextRenderer = $("textRendererSelect").value;

                                                                           console.log(currentTextRenderer);

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
                                                                                        properties.textRenderer.multistateValues[multistate].colour
                                                                                   );
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
                                                                                        properties.textRenderer.rangeValues[range].color
                                                                                    );
                                                                                }
                                                                           }


                                                                         } else {
                                                                           alert("cancel");
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
            </br>
            <div id="baseOnTemplate">
                <input id="baseOnExistingPointBtn" type="button" value="Base on existing point" onclick="baseOnExistingPoint()" class="">
            </div>
            </br>
            </br>

        </td>
      </tr>
    </table>

    <%@ include file="/WEB-INF/jsp/pointEdit/buttons.jsp" %>
  </form>
</tag:page>