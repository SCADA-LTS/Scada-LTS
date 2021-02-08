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
<div class="borderDiv marB marR">
  <table>
    <tr><td colspan="3">
      <span class="smallTitle"><fmt:message key="pointEdit.event.text.props"/></span>
      <tag:help id="eventTextRenderers"/>
    </td></tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.text.type"/></td>
      <td class="formField">
        <sst:select id="eventTextRendererSelect" onchange="eventTextRendererEditor.change();"
                    value="${form.eventTextRenderer.typeName}">
          <c:forEach items="${eventTextRenderers}" var="etrdef">
            <sst:option value="${etrdef.name}"><fmt:message key="${etrdef.nameKey}"/></sst:option>
          </c:forEach>
        </sst:select>
      </td>
    </tr>

    <tbody id="eventTextRendererBinary" style="display:none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.text.zero"/></td>
      <td class="formField">
        <table cellspacing="0" cellpadding="0">
          <tr>
            <td class="formLabel"><fmt:message key="pointEdit.text.short"/></td>
            <td valign="top"><input id="eventTextRendererBinaryZeroShort" type="text"/></td>
            <td width="10"></td>
            <td valign="top" rowspan="2" align="center">
              <div dojoType="ColorPalette" palette="3x4" id="eventTextRendererBinaryZeroColour"></div>
              <a href="#" onclick="eventTextRendererEditor.handlerBinaryZeroColour(null); return false;">(<fmt:message key="pointEdit.text.default"/>)</a>
            </td>
          </tr>
          <tr>
            <td class="formLabel"><fmt:message key="pointEdit.text.long"/></td>
            <td valign="top"><input class="formLong" id="eventTextRendererBinaryZeroLong" type="text"/></td>
            <td width="10"></td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.text.one"/></td>
      <td class="formField">
        <table cellspacing="0" cellpadding="0">
          <tr>
            <td class="formLabel"><fmt:message key="pointEdit.text.short"/></td>
            <td valign="top"><input id="eventTextRendererBinaryOneShort" type="text"/></td>
            <td width="10"></td>
            <td valign="top" rowspan="2" align="center">
              <div dojoType="ColorPalette" palette="3x4" id="eventTextRendererBinaryOneColour"></div>
              <a href="#" onclick="eventTextRendererEditor.handlerBinaryOneColour(null); return false;">(<fmt:message key="pointEdit.text.default"/>)</a>
            </td>
          </tr>
          <tr>
            <td class="formLabel"><fmt:message key="pointEdit.text.long"/></td>
            <td valign="top"><input class="formLong" id="eventTextRendererBinaryOneLong" type="text"/></td>
            <td width="10"></td>
          </tr>
        </table>
      </td>
    </tr>
    </tbody>
    <tbody id="eventTextRendererMultistate" style="display:none;">
    <tr>
      <td colspan="2">
        <table>
          <tr>
            <th><fmt:message key="pointEdit.text.key"/></th>
            <th colspan="2"><fmt:message key="pointEdit.text.text"/></th>
            <th><fmt:message key="pointEdit.text.colour"/></th>
            <td></td>
          </tr>
          <tr>
            <td valign="top"><input type="text" id="eventTextRendererMultistateKey" value="" class="formVeryShort"/></td>
            <td class="formLabel"><fmt:message key="pointEdit.text.short"/></td>
            <td valign="top"><input type="text" id="eventTextRendererMultistateTextShort"/></td>
            <td valign="top" rowspan="2" align="center">
              <div dojoType="ColorPalette" palette="3x4" id="eventTextRendererMultistateColour"></div>
              <a href="#" onclick="eventTextRendererEditor.handlerMultistateColour(null); return false;">(<fmt:message key="pointEdit.text.default"/>)</a>
            </td>
            <td valign="top">
              <tag:img png="add" title="common.add" onclick="return eventTextRendererEditor.addMultistateEventValue();"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="formLabel"><fmt:message key="pointEdit.text.long"/></td>
            <td valign="top"><input class="formLong" type="text" id="eventTextRendererMultistateTextLong"/></td>
          </tr>
          <tbody id="eventTextRendererMultistateTable"></tbody>
        </table>
      </td>
    </tr>
    </tbody>
    <tbody id="eventTextRendererNone" style="display:none;">
    </tbody>
    <tbody id="eventTextRendererRange" style="display:none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.text.format"/></td>
      <td class="formField"><input id="eventTextRendererRangeFormat" type="text"/></td>
    </tr>
    <tr>
      <td colspan="2">
        <table>
          <tr>
            <th><fmt:message key="pointEdit.text.from"/></th>
            <th><fmt:message key="pointEdit.text.to"/></th>
            <th colspan="2"><fmt:message key="pointEdit.text.text"/></th>
            <th><fmt:message key="pointEdit.text.colour"/></th>
            <td></td>
          </tr>
          <tr>
            <td valign="top"><input type="text" id="eventTextRendererRangeFrom" value="" class="formVeryShort"/></td>
            <td valign="top"><input type="text" id="eventTextRendererRangeTo" value="" class="formVeryShort"/></td>
            <td class="formLabel"><fmt:message key="pointEdit.text.short"/></td>
            <td valign="top"><input type="text" id="eventTextRendererRangeTextShort" value=""/></td>
            <td valign="top" rowspan="2" align="center">
              <div dojoType="ColorPalette" palette="3x4" id="eventTextRendererRangeColour"></div>
              <a href="#" onclick="eventTextRendererEditor.handlerRangeColour(null); return false;">(<fmt:message key="pointEdit.text.default"/>)</a>
            </td>
            <td valign="top">
              <tag:img png="add" title="common.add" onclick="return eventTextRendererEditor.addRangeEventValue();"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td></td>
            <td class="formLabel"><fmt:message key="pointEdit.text.long"/></td>
            <td valign="top"><input class="formLong" type="text" id="eventTextRendererRangeTextLong" value=""/></td>
          </tr>
          <tbody id="eventTextRendererRangeTable"></tbody>
        </table>
      </td>
    </tr>
    </tbody>
  </table>
</div>

<script type="text/javascript">
  dojo.require("dojo.widget.ColorPalette");

  function EventTextRendererEditor() {
    var currentEventTextRenderer;
    var multistateEventValues = new Array();
    var rangeEventValues = new Array();

    this.init = function() {
      // Colour handler events
      dojo.event.connect(dojo.widget.byId("eventTextRendererRangeColour"), 'onColorSelect', this,
              'handlerRangeColour');
      dojo.event.connect(dojo.widget.byId("eventTextRendererMultistateColour"), 'onColorSelect', this,
              'handlerMultistateColour');
      dojo.event.connect(dojo.widget.byId("eventTextRendererBinaryZeroColour"), 'onColorSelect', this,
              'handlerBinaryZeroColour');
      dojo.event.connect(dojo.widget.byId("eventTextRendererBinaryOneColour"), 'onColorSelect', this,
              'handlerBinaryOneColour');

      // Figure out which fields to populate with data.
      <c:choose>
      <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererBinary"}'>
      $set("eventTextRendererBinaryZeroShort", "${form.eventTextRenderer.zeroShortLabel}");
      $set("eventTextRendererBinaryZeroLong", "${form.eventTextRenderer.zeroLongLabel}");
      eventTextRendererEditor.handlerBinaryZeroColour("${form.eventTextRenderer.zeroColour}");
      $set("eventTextRendererBinaryOneShort", "${form.eventTextRenderer.oneShortLabel}");
      $set("eventTextRendererBinaryOneLong", "${form.eventTextRenderer.oneLongLabel}");
      eventTextRendererEditor.handlerBinaryOneColour("${form.eventTextRenderer.oneColour}");
      </c:when>
      <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererMultistate"}'>
      <c:forEach items="${form.eventTextRenderer.multistateEventValues}" var="msValue">
      eventTextRendererEditor.addMultistateEventValue("${msValue.key}", "${msValue.shortText}", "${msValue.longText}", "${msValue.colour}");
      </c:forEach>
      </c:when>
      <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererNone"}'>
      </c:when>
      <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererRange"}'>
      $set("textRendererRangeFormat", "${form.eventTextRenderer.format}");
      <c:forEach items="${form.eventTextRenderer.rangeEventValues}" var="rgValue">
      eventTextRendererEditor.addRangeEventValue("${rgValue.from}", "${rgValue.to}", "${rgValue.shortText}", "${rgValue.longText}",
              "${rgValue.colour}");
      </c:forEach>
      </c:when>
      <c:otherwise>
      dojo.debug("Unknown text renderer: ${form.eventTextRenderer.typeName}");
      </c:otherwise>
      </c:choose>

      eventTextRendererEditor.change();
    }

    this.change = function() {
      if (currentEventTextRenderer)
        dojo.html.hide($(currentEventTextRenderer));
      currentEventTextRenderer = $("eventTextRendererSelect").value
      dojo.html.show($(currentEventTextRenderer));
    };

    this.save = function(callback) {
      var typeName = $get("eventTextRendererSelect");
      if (typeName == "eventTextRendererBinary")
        DataPointEditDwr.setBinaryEventTextRenderer($get("eventTextRendererBinaryZeroShort"), $get("eventTextRendererBinaryZeroLong"),
                dojo.widget.byId("eventTextRendererBinaryZeroColour").selectedColour, $get("eventTextRendererBinaryOneShort"),
                $get("eventTextRendererBinaryOneLong"), dojo.widget.byId("eventTextRendererBinaryOneColour").selectedColour, callback);
      else if (typeName == "eventTextRendererMultistate")
        DataPointEditDwr.setMultistateEventRenderer(multistateEventValues, callback);
      else if (typeName == "eventTextRendererNone")
        DataPointEditDwr.setNoneEventRenderer(callback);
      else if (typeName == "eventTextRendererRange")
        DataPointEditDwr.setRangeEventRenderer($get("eventTextRendererRangeFormat"), rangeEventValues, callback);
      else
        callback();
    };

    //
    // List objects
    this.MultistateEventValue = function() {
      this.key;
      this.shortText;
      this.longText;
      this.colour;
    };

    this.RangeEventValue = function() {
      this.from;
      this.to;
      this.shortText;
      this.longText;
      this.colour;
    };

    //
    // Multistate list manipulation
    this.addMultistateEventValue = function(theKey, shortText, longText, colour) {
      if (!theKey)
        theKey = $get("eventTextRendererMultistateKey");
      var theNumericKey = parseInt(theKey);
      if (isNaN(theNumericKey)) {
        alert("<fmt:message key="pointEdit.text.errorParsingKey"/>");
        return false;
      }
      for (var i=multistateEventValues.length-1; i>=0; i--) {
        if (multistateEventValues[i].key == theNumericKey) {
          alert("<fmt:message key="pointEdit.text.listContainsKey"/> "+ theNumericKey);
          return false;
        }
      }

      var theValue = new this.MultistateEventValue();
      theValue.key = theNumericKey;
      if (shortText)
        theValue.shortText = shortText;
      else
        theValue.shortText = $get("eventTextRendererMultistateTextShort");
      if (longText)
        theValue.longText = longText;
      else
        theValue.longText = $get("eventTextRendererMultistateTextLong");
      if (colour)
        theValue.colour = colour;
      else
        theValue.colour = dojo.widget.byId("eventTextRendererMultistateColour").selectedColour;
      multistateEventValues[multistateEventValues.length] = theValue;
      this.sortMultistateEventValues();
      this.refreshMultistateEventList();
      $set("eventTextRendererMultistateKey", theNumericKey+1);

      return false;
    };

    this.removeMultistateEventValue = function(theValue) {
      for (var i=multistateEventValues.length-1; i>=0; i--) {
        if (multistateEventValues[i].key == theValue)
          multistateEventValues.splice(i, 1);
      }
      this.refreshMultistateEventList();
      return false;
    };

    this.sortMultistateEventValues = function() {
      multistateEventValues.sort( function(a,b) { return a.key-b.key; } );
    };

    this.refreshMultistateEventList = function() {
      dwr.util.removeAllRows("eventTextRendererMultistateTable");
      dwr.util.addRows("eventTextRendererMultistateTable", multistateEventValues, [
        function(data) { return data.key; },
        function(data) {
          if (data.colour)
            return "<span style='color:"+ data.colour +"'>"+ data.shortText +"</span>";
          return data.shortText;
        },
        function(data) {
          if (data.colour)
            return "<span style='color:"+ data.colour +"'>"+ data.longText +"</span>";
          return data.longText;
        },
        function(data) {
          return "<a href='#' onclick='return eventTextRendererEditor.removeMultistateEventValue("+ data.key +
                  ");'><img src='images/bullet_delete.png' width='16' height='16' border='0' "+
                  "title='<fmt:message key="common.delete"/>'/><\/a>";
        }
      ], null);
    };

    //
    // Range list manipulation
    this.addRangeEventValue = function(theFrom, theTo, shortText, longText, colour) {
      if (!theFrom)
        theFrom = parseFloat($get("eventTextRendererRangeFrom"));
      if (isNaN(theFrom)) {
        alert("<fmt:message key="pointEdit.text.errorParsingFrom"/>");
        return false;
      }

      if (!theTo)
        theTo = parseFloat($get("eventTextRendererRangeTo"));
      if (isNaN(theTo)) {
        alert("<fmt:message key="pointEdit.text.errorParsingTo"/>");
        return false;
      }

      if (isNaN(theTo >= theFrom)) {
        alert("<fmt:message key="pointEdit.text.toGreaterThanFrom"/>");
        return false;
      }

      for (var i=0; i<rangeEventValues.length; i++) {
        if (rangeEventValues[i].from == theFrom && rangeEventValues[i].to == theTo) {
          alert("<fmt:message key="pointEdit.text.listContainsRange"/> "+ theFrom +" - "+ theTo);
          return false;
        }
      }

      var theValue = new this.RangeEventValue();
      theValue.from = theFrom;
      theValue.to = theTo;
      if (shortText)
        theValue.shortText = shortText;
      else
        theValue.shortText = $get("eventTextRendererRangeTextShort");
      if (longText)
        theValue.longText = longText;
      else
        theValue.longText = $get("eventTextRendererRangeTextLong");
      if (colour)
        theValue.colour = colour;
      else
        theValue.colour = dojo.widget.byId("eventTextRendererRangeColour").selectedColour;
      rangeEventValues[rangeEventValues.length] = theValue;
      this.sortRangeEventValues();
      this.refreshRangeList();
      $set("eventTextRendererRangeFrom", theTo);
      $set("eventTextRendererRangeTo", theTo + (theTo - theFrom));
      return false;
    };

    this.removeRangeEventValue = function(theFrom, theTo) {
      for (var i=rangeEventValues.length-1; i>=0; i--) {
        if (rangeEventValues[i].from == theFrom && rangeEventValues[i].to == theTo)
          rangeEventValues.splice(i, 1);
      }
      this.refreshRangeList();
      return false;
    };

    this.sortRangeEventValues = function() {
      rangeEventValues.sort( function(a,b) {
        if (a.from == b.from)
          return a.to-b.to;
        return a.from-b.from;
      });
    };

    this.refreshRangeList = function() {
      dwr.util.removeAllRows("eventTextRendererRangeTable");
      dwr.util.addRows("eventTextRendererRangeTable", rangeEventValues, [
        function(data) { return data.from; },
        function(data) { return data.to; },
        function(data) {
          if (data.colour)
            return "<span style='color:"+ data.colour +"'>"+ data.shortText +"</span>";
          return data.shortText;
        },
        function(data) {
          if (data.colour)
            return "<span style='color:"+ data.colour +"'>"+ data.longText +"</span>";
          return data.longText;
        },
        function(data) {
          return "<a href='#' onclick='return eventTextRendererEditor.removeRangeEventValue("+
                  data.from +","+ data.to +");'><img src='images/bullet_delete.png' width='16' "+
                  "height='16' border='0' title='<fmt:message key="common.delete"/>'/><\/a>";
        }
      ], null);
    };

    //
    // Color handling
    this.handlerRangeColour = function(colour) {
      dojo.widget.byId("eventTextRendererRangeColour").selectedColour = colour;
      $("eventTextRendererRangeTextShort").style.color = colour;
      $("eventTextRendererRangeTextLong").style.color = colour;
    };
    this.handlerMultistateColour = function(colour) {
      dojo.widget.byId("eventTextRendererMultistateColour").selectedColour = colour;
      $("eventTextRendererMultistateTextShort").style.color = colour;
      $("eventTextRendererMultistateTextLong").style.color = colour;
    };
    this.handlerBinaryZeroColour = function(colour) {
      dojo.widget.byId("eventTextRendererBinaryZeroColour").selectedColour = colour;
      $("eventTextRendererBinaryZeroShort").style.color = colour;
      $("eventTextRendererBinaryZeroLong").style.color = colour;
    };
    this.handlerBinaryOneColour = function(colour) {
      dojo.widget.byId("eventTextRendererBinaryOneColour").selectedColour = colour;
      $("eventTextRendererBinaryOneShort").style.color = colour;
      $("eventTextRendererBinaryOneLong").style.color = colour;
    };
  }
  var eventTextRendererEditor = new EventTextRendererEditor();
  dojo.addOnLoad(eventTextRendererEditor, "init");
</script>