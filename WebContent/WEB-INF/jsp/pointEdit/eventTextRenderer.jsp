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
      <span class="smallTitle"><spring:message code="pointEdit.event.text.props"/></span>
      <tag:help id="eventTextRenderers"/>
    </td></tr>

    <tr>
      <td class="formLabelRequired"><spring:message code="pointEdit.text.type"/></td>
      <td class="formField">
        <sst:select id="eventTextRendererSelect" onchange="eventTextRendererEditor.change();"
                    value="${form.eventTextRenderer.typeName}">
          <c:forEach items="${eventTextRenderers}" var="etrdef">
            <sst:option value="${etrdef.name}"><spring:message code="${etrdef.nameKey}"/></sst:option>
          </c:forEach>
        </sst:select>
      </td>
    </tr>

    <tbody id="eventTextRendererBinary" style="display:none;">
    <tr>
      <td class="formLabelRequired"><spring:message code="pointEdit.text.zero"/></td>
      <td class="formField">
        <table cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top"><input class="formLong" id="eventTextRendererBinaryZero" type="text" value="<c:catch var="exception"><c:out value="${form.eventTextRenderer.zeroLabel}" /></c:catch>"/></td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td class="formLabelRequired"><spring:message code="pointEdit.text.one"/></td>
      <td class="formField">
        <table cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top"><input class="formLong" id="eventTextRendererBinaryOne" type="text" value="<c:catch var="exception"><c:out value="${form.eventTextRenderer.oneLabel}" /></c:catch>"/></td>
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
            <th><spring:message code="pointEdit.text.key"/></th>
            <th><spring:message code="pointEdit.text.text"/></th>
            <td></td>
          </tr>
          <tr>
            <td valign="top"><input type="text" id="eventTextRendererMultistateKey" value="" class="formVeryShort"/></td>
            <td valign="top"><input class="formLong" type="text" id="eventTextRendererMultistateText"/></td>
            <td valign="top">
              <tag:img png="add" title="common.add" onclick="return eventTextRendererEditor.addMultistateEventValue();"/>
            </td>
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
      <td colspan="2">
        <table>
          <tr>
            <th><spring:message code="pointEdit.text.from"/></th>
            <th><spring:message code="pointEdit.text.to"/></th>
            <th><spring:message code="pointEdit.text.text"/></th>
            <td></td>
          </tr>
          <tr>
            <td valign="top"><input type="text" id="eventTextRendererRangeFrom" value="" class="formVeryShort"/></td>
            <td valign="top"><input type="text" id="eventTextRendererRangeTo" value="" class="formVeryShort"/></td>
            <td valign="top"><input class="formLong" type="text" id="eventTextRendererRangeText" value=""/></td>
            <td valign="top">
              <tag:img png="add" title="common.add" onclick="return eventTextRendererEditor.addRangeEventValue();"/>
            </td>
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

      // Figure out which fields to populate with data.
      <c:choose>
          <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererBinary"}'>
          </c:when>
          <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererMultistate"}'>
              <c:forEach items="${form.eventTextRenderer.multistateEventValues}" var="msValue">
                eventTextRendererEditor.addMultistateEventValue("${msValue.key}", "<c:out value="${msValue.text}"/>");
              </c:forEach>
          </c:when>
          <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererNone"}'>
          </c:when>
          <c:when test='${form.eventTextRenderer.typeName == "eventTextRendererRange"}'>
              <c:forEach items="${form.eventTextRenderer.rangeEventValues}" var="rgValue">
                eventTextRendererEditor.addRangeEventValue("${rgValue.from}", "${rgValue.to}", "<c:out value="${rgValue.text}"/>");
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
        DataPointEditDwr.setBinaryEventTextRenderer($get("eventTextRendererBinaryZero"), $get("eventTextRendererBinaryOne"), callback);
      else if (typeName == "eventTextRendererMultistate") {
        for(let i = 0; i < multistateEventValues.length; i++) {
            multistateEventValues[i].text = unescapeHtml(multistateEventValues[i].text);
        }
        DataPointEditDwr.setMultistateEventRenderer(multistateEventValues, callback);
      } else if (typeName == "eventTextRendererNone")
        DataPointEditDwr.setNoneEventRenderer(callback);
      else if (typeName == "eventTextRendererRange") {
        for(let i = 0; i < rangeEventValues.length; i++) {
            rangeEventValues[i].text = unescapeHtml(rangeEventValues[i].text);
        }
        DataPointEditDwr.setRangeEventRenderer(rangeEventValues, callback);
      } else
        callback();
    };

    //
    // List objects
    this.MultistateEventValue = function() {
      this.key;
      this.text;
    };

    this.RangeEventValue = function() {
      this.from;
      this.to;
      this.text;
    };

    //
    // Multistate list manipulation
    this.addMultistateEventValue = function(theKey, text) {
      if (!theKey)
        theKey = $get("eventTextRendererMultistateKey");
      var theNumericKey = parseInt(theKey);
      if (isNaN(theNumericKey)) {
        alert("<spring:message code="pointEdit.text.errorParsingKey"/>");
        return false;
      }
      for (var i=multistateEventValues.length-1; i>=0; i--) {
        if (multistateEventValues[i].key == theNumericKey) {
          alert("<spring:message code="pointEdit.text.listContainsKey"/> "+ theNumericKey);
          return false;
        }
      }

      var theValue = new this.MultistateEventValue();
      theValue.key = theNumericKey;
      if (text)
        theValue.text = text;
      else {
        theValue.text = escapeHtml($get("eventTextRendererMultistateText"));
      }
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
          return "<span>"+ data.text +"</span>";
        },
        function(data) {
          return "<a href='#' onclick='return eventTextRendererEditor.removeMultistateEventValue("+ data.key +
                  ");'><img src='images/bullet_delete.png' width='16' height='16' border='0' "+
                  "title='<spring:message code="common.delete"/>'/><\/a>";
        }
      ], null);
    };

    //
    // Range list manipulation
    this.addRangeEventValue = function(theFrom, theTo, text) {
      if (!theFrom)
        theFrom = parseFloat($get("eventTextRendererRangeFrom"));
      if (isNaN(theFrom)) {
        alert("<spring:message code="pointEdit.text.errorParsingFrom"/>");
        return false;
      }

      if (!theTo)
        theTo = parseFloat($get("eventTextRendererRangeTo"));
      if (isNaN(theTo)) {
        alert("<spring:message code="pointEdit.text.errorParsingTo"/>");
        return false;
      }

      if (isNaN(theTo >= theFrom)) {
        alert("<spring:message code="pointEdit.text.toGreaterThanFrom"/>");
        return false;
      }

      for (var i=0; i<rangeEventValues.length; i++) {
        if (rangeEventValues[i].from == theFrom && rangeEventValues[i].to == theTo) {
          alert("<spring:message code="pointEdit.text.listContainsRange"/> "+ theFrom +" - "+ theTo);
          return false;
        }
      }

      var theValue = new this.RangeEventValue();
      theValue.from = theFrom;
      theValue.to = theTo;
      if (text)
        theValue.text = text;
      else {
        theValue.text = escapeHtml($get("eventTextRendererRangeText"));
      }
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
          return "<span>"+ data.text +"</span>";
        },
        function(data) {
          return "<a href='#' onclick='return eventTextRendererEditor.removeRangeEventValue("+
                  data.from +","+ data.to +");'><img src='images/bullet_delete.png' width='16' "+
                  "height='16' border='0' title='<spring:message code="common.delete"/>'/><\/a>";
        }
      ], null);
    };
    this.getRangeEventValues = function(){
        return rangeEventValues;
    }
  }
  var eventTextRendererEditor = new EventTextRendererEditor();
  dojo.addOnLoad(eventTextRendererEditor, "init");
</script>