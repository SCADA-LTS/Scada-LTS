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
<tag:page dwr="PublisherListDwr" onload="init">
  <script type="text/javascript">
    function init() {
        PublisherListDwr.init(function(response) {
            dwr.util.addOptions("publisherTypes", response.data.types, "key", "message");
            updatePublisherList(response.data.publishers);
        });
    }
    
    function addPublisher() {
        window.location = "publisher_edit.shtm?typeId="+ $get("publisherTypes");
    }
    
    function updatePublisherList(publishers) {
        dwr.util.removeAllRows("publisherList");
        dwr.util.addRows("publisherList", publishers,
            [
                function(p) { return "<b>"+ p.name +"</b>"; },
                function(p) { return p.typeMessage; },
                function(p) { return p.configDescription; },
                function(p) {
                    if (p.enabled)
                        return '<img src="images/transmit_go.png" title="<fmt:message key="common.enabledToggle"/>" '+
                            'class="ptr" onclick="togglePublisher('+ p.id +')" id="pImg'+ p.id +'"/>';
                    return '<img src="images/transmit_stop.png" title="<fmt:message key="common.disabledToggle"/>" '+
                        'class="ptr" onclick="togglePublisher('+ p.id +')" id="pImg'+ p.id +'"/>';
                },
                function(p) {
                    return '<a href="publisher_edit.shtm?pid='+ p.id +'"><img src="images/transmit_edit.png" '+
                        'border="0" title="<fmt:message key="common.edit"/>"/></a> '+
                        '<img src="images/transmit_delete.png" title="<fmt:message key="common.delete"/>" id="deleteImg'+ p.id +'" '+
                        'class="ptr" onclick="deletePublisher('+ p.id +')"/>';
                }
            ],
            {
                rowCreator: function(options) {
                    var tr = document.createElement("tr");
                    tr.id = "publisherRow"+ options.rowData.id;
                    tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                    return tr;
                },
                cellCreator: function(options) {
                    var td = document.createElement("td");
                    if (options.cellNum == 3)
                        td.align = "center";
                    return td;
                }
            });
        display("noPublishers", publishers.length == 0);
    }
    
    function togglePublisher(id) {
        var imgNode = $("pImg"+ id);
        if (!hasImageFader(imgNode)) {
            PublisherListDwr.togglePublisher(id, function(result) {
                updateStatusImg($("pImg"+ result.data.id), result.data.enabled);
            });
            startImageFader(imgNode);
        }
    }
    
    
    function deletePublisher(id) {
        if (confirm("<fmt:message key="publisherList.deleteConfirm"/>")) {
            startImageFader("deleteImg"+ id);
            PublisherListDwr.deletePublisher(id, function(publisherId) {
                stopImageFader("deleteImg"+ publisherId);
                var row = $("publisherRow"+ publisherId);
                row.parentNode.removeChild(row);
            });
        }
    }
    
    function updateStatusImg(imgNode, enabled) {
        stopImageFader(imgNode);
        setPublisherStatusImg(enabled, imgNode);
    }
  </script>
  
  <table cellspacing="0" cellpadding="0">
    <tr>
      <td>
        <tag:img png="transmit" title="publisherList.publishers"/>
        <span class="smallTitle"><fmt:message key="publisherList.publishers"/></span>
        <tag:help id="publisherList"/>
      </td>
      <td align="right">
        <select id="publisherTypes"></select>
        <tag:img png="transmit_add" title="common.add" onclick="addPublisher()"/>
      </td>
    </tr>
    
    <tr>
      <td colspan="2">
        <table cellspacing="1" cellpadding="0" border="0">
          <tr class="rowHeader">
            <td><fmt:message key="publisherList.name"/></td>
            <td><fmt:message key="publisherList.type"/></td>
            <td><fmt:message key="publisherList.config"/></td>
            <td><fmt:message key="publisherList.status"/></td>
            <td></td>
          </tr>
          <tbody id="noPublishers" style="display:none"><tr><td colspan="5"><fmt:message key="publisherList.noRows"/></td></tr></tbody>
          <tbody id="publisherList"></tbody>
        </table>
      </td>
    </tr>
  </table>
</tag:page>