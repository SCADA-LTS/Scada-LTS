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
                       swal({
                         text: swal_message,
                         buttons: {
                           cancel: true,
                           confirm: "Confirm",
                           roll: {
                             text: "Do a barrell roll!",
                             value: "roll",
                           },
                         },
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