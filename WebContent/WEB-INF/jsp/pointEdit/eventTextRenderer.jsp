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

    <tbody id="eventTextRenderer">
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.text.text"/></td>
      <td class="formField"><input id="eventTextRendererText" type="text"/></td>
    </tr>
    </tbody>
  </table>
</div>

<script type="text/javascript">
  function EventRendererEditor() {

    this.init = function() {
      $set("eventTextRendererText", "${form.eventRenderer.text}");
    };

    this.save = function(callback) {
      DataPointEditDwr.setEventTextRenderer($get("eventTextRendererText"), callback);
    };
  }
  var eventRendererEditor = new EventRendererEditor();
  dojo.addOnLoad(eventRendererEditor, "init");
</script>