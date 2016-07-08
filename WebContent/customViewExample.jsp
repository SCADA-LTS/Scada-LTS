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
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@ taglib prefix="view" tagdir="/WEB-INF/tags/view" %>
<html>
<head>
  <link href="resources/common.css" type="text/css" rel="stylesheet"/>
  
  <%--
  <!--
    Change the username attribute below to the user under whose permissions this page will run.
  -->
  <view:init username="changeMe"/>
  --%>  
</head>
<body>
<h1>Custom View Example</h1>

<p>
  The active components of this example have been commented out. To reactivate, remove the &lt;%-- --%&gt; style 
  comment tags (but not what is between them!) and replace the view:init username and all xid attributes with valid
  values.
</p>
<p>
  <b>You will receive errors if you do not provide valid attribute values!</b> Read the error messages for clues about
  what needs to be fixed. You can remove these instructions when you are done.
</p>
<p>
  You can create as many custom views as you want. Simply create a new JSP file in the main directory (where the 
  index.jsp is).
</p>

<%--
<h3>A simple table of some static values</h3>
<table>
  <tr>
    <!--
      A staticPoint tag is used to display the present value of a single point. This value is included in the HTML
      directly, and is not updated via AJAX.
        - xid: the point to use (required)
        - disabledValue: the value to display if the point is disabled (defaults to "")
        - raw: whether the value should be fully rendered according to the text renderer, or should be provided in its
               raw form. For example, a raw numeric is does not include a suffix, a raw multistate is its integer value,
               and a raw binary is 0 or 1. (Defaults to false).
    -->
    <td align="right" style="padding-right:20px">A point value:</td>
    <td><view:staticPoint xid="changeMe" disabledValue="(disabled)"/></td>
  </tr>
  <tr>
    <td align="right" style="padding-right:20px">A raw point value:</td>
    <td><view:staticPoint xid="changeMe" raw="true"/></td>
  </tr>
  <tr>
    <td align="right" style="padding-right:20px">A binary point value:</td>
    <td><view:staticPoint xid="changeMe"/></td>
  </tr>
</table>
  
<h3>A simple table of some values</h3>
<table>
  <tr>
    <!--
      A simplePoint tag is used to display the value of a single point.
        - xid: the point to use (required)
        - disabledValue: the value to display if the point is disabled (defaults to "")
        - raw: whether the value should be fully rendered according to the text renderer, or should be provided in its
               raw form. For example, a raw numeric is does not include a suffix, a raw multistate is its integer value,
               and a raw binary is 0 or 1. (Defaults to false).
    -->
    <td align="right" style="padding-right:20px">A point value:</td>
    <td><view:simplePoint xid="changeMe" disabledValue="(disabled)"/></td>
  </tr>
  <tr>
    <td align="right" style="padding-right:20px">A raw point value:</td>
    <td><view:simplePoint xid="changeMe" raw="true"/></td>
  </tr>
  <tr>
    <td align="right" style="padding-right:20px">A binary point value:</td>
    <td><view:simplePoint xid="changeMeBinary"/></td>
  </tr>
</table>

<p>A scripted point. Depending on the value of the binary source point, one of two images is shown.</p>
<img id="scripted" src="images/hourglass.png"/>

<!--
  A scriptPoint tag accepts similar attributes to the simplePoint, with the addition of one:
    - time: whether to include a time parameter in the script. The script is only called when either the value or the
            time has changed, so care must be taken if the time attribute is set to "true". For example, if the 
            timestamp of the point changes but the value does not, the "value" parameter in the script will be null.
            (Defaults to false.)
            
  The content of the scriptPoint tag is the script that will be called when the value and/or time change. For 
  convenience, the function declaration of "function(value, time)" is made elsewhere.
  
  Note that the value is always received as a string. Use parseInt() or parseFloat() to convert to numeric. 
-->
<view:scriptPoint xid="changeMeBinary" raw="true">
  if (value == "0") {
      $("scripted").src = "graphics/BlinkingLights/light_red.gif";
      // Set the global variable to false. This value is used in the toggle link.
      binaryValue = false;
  }
  else {
      $("scripted").src = "graphics/BlinkingLights/light_green.gif";
      // Set the global variable to true. This value is used in the toggle link.
      binaryValue = true;
  }
</view:scriptPoint>
<br/>
<br/>

<!--
  To set the value of any point (to which the user in the init tag has permission to change), simply call the setPoint
  function: setPoint(xid, value, callback)
    - xid: the point to set (required)
    - value: the value to set (required)
    - callback: a function to call following the point set attempt (optional)
-->
<a href="#" onclick="setPoint('changeMeBinary', !binaryValue); return false;">Click here</a> to toggle the image above.
<br/>
<br/>

<!--
  Display charts with the chart tag. Include points in the chart with the chartPoint child tag. Most attributes should 
  require no explanation.
    - color: this attribute can be an HTML-style hex, or any color name in the HTML specification.
-->
<h3>A chart</h3>
<view:chart duration="1" durationType="hours" width="800" height="300">
  <view:chartPoint xid="changeMe1" color="#2468AC"/>
  <view:chartPoint xid="changeMe2" color="#CA8642"/>
  <view:chartPoint xid="changeMe3" color="dodgerblue"/>
  <view:chartPoint xid="changeMe4" color="red"/>
</view:chart>
--%>
</body>
</html>
