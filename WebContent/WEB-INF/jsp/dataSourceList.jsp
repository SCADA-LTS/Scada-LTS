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
<tag:page dwr="DataSourceListDwr" onload="init">
  <script>
    function init() {
        DataSourceListDwr.init(function(response) {
            if (response.data.types) {
                dwr.util.addOptions("dataSourceTypes", response.data.types, "key", "value");
                show("dataSourceTypesContent");
            }
        });
    }
    
    function toggleDataSource(dataSourceId) {
        var imgNode = $("dsImg"+ dataSourceId);
        if (!hasImageFader(imgNode)) {
            DataSourceListDwr.toggleDataSource(dataSourceId, function(result) {
                updateStatusImg($("dsImg"+ result.id), result.enabled, true);
            });
            startImageFader(imgNode);
        }
    }
    
    function toggleDataPoint(dataPointId) {
        var imgNode = $("dpImg"+ dataPointId);
        if (!hasImageFader(imgNode)) {
            DataSourceListDwr.toggleDataPoint(dataPointId, function(result) {
                updateStatusImg($("dpImg"+ result.data.id), result.data.enabled, false);
            });
            startImageFader(imgNode);
        }
    }
    
    function updateStatusImg(imgNode, enabled, ds) {
        stopImageFader(imgNode);
        if (ds)
            setDataSourceStatusImg(enabled, imgNode);
        else
            setDataPointStatusImg(enabled, imgNode);
    }
    
    function deleteDataSource(dataSourceId) {
        if (confirm("<fmt:message key="dsList.dsDeleteConfirm"/>")) {
            startImageFader("deleteDataSourceImg"+ dataSourceId);
            DataSourceListDwr.deleteDataSource(dataSourceId, function(dataSourceId) {
                stopImageFader("deleteDataSourceImg"+ dataSourceId);
                // Delete the data source row
                var row = $("dataSourceRow"+ dataSourceId);
                row.parentNode.removeChild(row);
                // Delete the points section.
                row = $("points"+ dataSourceId);
                row.parentNode.removeChild(row);
            });
        }
    }
    
    function copyDataSource(fromDataSourceId) {
        startImageFader("copyDataSourceImg"+ fromDataSourceId);
        DataSourceListDwr.copyDataSource(fromDataSourceId, function(toDataSourceId) {
            window.location = "data_source_edit.shtm?dsid="+ toDataSourceId;
        });
    }
    
    function deleteDataPoint(pointId) {
        if (confirm("<fmt:message key="dsList.pointDeleteConfirm"/>")) {
            DataSourceListDwr.deleteDataPoint(pointId, function(pointId) {
                var row = $("pointRow"+ pointId);
                row.parentNode.removeChild(row);
            });
        }
    }
    
    function addDataSource() {
        window.location = "data_source_edit.shtm?typeId="+ $get("dataSourceTypes");
    }
  </script>
  
  <table cellspacing="0" cellpadding="0">
    <tr>
      <td>
        <tag:img png="icon_ds" title="dsList.dataSources"/>
        <span class="smallTitle"><fmt:message key="dsList.dataSources"/></span>
        <tag:help id="dataSourceList"/>
      </td>
      <td align="right" id="dataSourceTypesContent" style="display:none">
        <select id="dataSourceTypes"></select>
        <tag:img png="icon_ds_add" title="common.add" onclick="addDataSource()"/>
      </td>
    </tr>
    
    <tr>
      <td colspan="2">
        <table cellspacing="0" cellpadding="0" border="0">
          <tr>
            <td colspan="2">
              <table cellspacing="1" cellpadding="0" border="0">
                <tr class="rowHeader">
                  <td><sst:listSort labelKey="dsList.name" field="name" paging="${paging}"/></td>
                  <td><sst:listSort labelKey="dsList.type" field="type" paging="${paging}"/></td>
                  <td><sst:listSort labelKey="dsList.connection" field="conn" paging="${paging}"/></td>
                  <td><sst:listSort labelKey="dsList.status" field="enabled" paging="${paging}"/></td>
                  <td></td>
                </tr>
                
                <c:set var="hideText"><fmt:message key="dsList.hide"/></c:set>
                <c:set var="showText"><fmt:message key="dsList.show"/></c:set>
                <c:forEach items="${paging.data}" var="listParent" begin="${paging.offset}" end="${paging.endIndex}">
                  <tr class="row" id="dataSourceRow${listParent.parent.id}">
                    <td><b>${listParent.parent.name}</b></td>
                    <td><fmt:message key="${listParent.parent.type.key}"/></td>
                    <td><sst:i18n message="${listParent.parent.connectionDescription}"/></td>
                    <td align="center">
                      <c:choose>
                        <c:when test="${listParent.parent.enabled}">
                          <tag:img png="database_go" title="common.enabledToggle" id="dsImg${listParent.parent.id}"
                                  onclick="toggleDataSource(${listParent.parent.id})"/>
                        </c:when>
                        <c:otherwise>
                          <tag:img png="database_stop" title="common.disabledToggle" id="dsImg${listParent.parent.id}"
                                  onclick="toggleDataSource(${listParent.parent.id})"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <a href="data_source_edit.shtm?dsid=${listParent.parent.id}"><tag:img png="icon_ds_edit"
                              title="common.edit"/></a>
                      <tag:img png="arrow_out" title="dsList.show" onclick="togglePanelVisibility2(this, 'points${listParent.parent.id}', '${hideText}', '${showText}');"/>
                      <tag:img png="icon_ds_delete" title="common.delete" id="deleteDataSourceImg${listParent.parent.id}" 
                              onclick="deleteDataSource(${listParent.parent.id})"/>
                      <tag:img png="icon_ds_add" title="common.copy" id="copyDataSourceImg${listParent.parent.id}" 
                              onclick="copyDataSource(${listParent.parent.id})"/>
                    </td>
                  </tr>
                  
                  <tr id="points${listParent.parent.id}" class="rowAlt2" style="display:none">
                    <td colspan="5" style="padding:0px 0px 0px 30px;background-color:#FFFFFF;">
                      <table cellspacing="1" cellpadding="0" border="0">
                        <tr class="rowHeader">
                          <td><fmt:message key="dsList.pointName"/></td>
                          <td><fmt:message key="dsList.description"/></td>
                          <td><fmt:message key="dsList.status"/></td>
                          <td></td>
                        </tr>
                        <c:forEach items="${listParent.list}" var="point">
                          <tr id="pointRow${point.id}">
                            <td>${point.name}</td>
                            <td><sst:i18n message="${point.dataTypeMessage}"/> / <sst:i18n message="${point.configurationDescription}"/></td>
                            <td align="center">
                              <c:choose>
                                <c:when test="${point.enabled}">
                                  <tag:img png="brick_go" title="common.enabledToggle" id="dpImg${point.id}"
                                          onclick="toggleDataPoint(${point.id})"/>
                                </c:when>
                                <c:otherwise>
                                  <tag:img png="brick_stop" title="common.disabledToggle" id="dpImg${point.id}"
                                          onclick="toggleDataPoint(${point.id})"/>
                                </c:otherwise>
                              </c:choose>
                            </td>
                            <td><tag:img png="icon_comp" title="events.pointDetails"
                                    onclick="window.location='data_point_details.shtm?dpid=${point.id}'"/></td>
                            <td><tag:img png="icon_comp_edit" title="pointEdit.props.props"
                                    onclick="window.location='data_point_edit.shtm?dpid=${point.id}'"/></td>
                          </tr>
                        </c:forEach>
                      </table>
                    </td>
                  </tr>
                </c:forEach>
              </table>
            </td>
          </tr>
          <tr>
            <td><sst:pageNumber paging="${paging}" pageLabelKey="pagination.page" ofLabelKey="pagination.of"
                    rowsLabelKey="pagination.rows" noRowsLabelKey="pagination.noRows"/></td>
            <td align="right"><sst:pagination paging="${paging}" previousLabelKey="pagination.previous"
                    nextLabelKey="pagination.next"/></td>
          </tr>
        </table>  
      </td>
    </tr>
  </table>
</tag:page>