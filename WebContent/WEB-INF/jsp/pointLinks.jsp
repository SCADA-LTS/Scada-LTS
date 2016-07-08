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
<%@page import="com.serotonin.mango.Common"%>
<%@page import="com.serotonin.mango.vo.link.PointLinkVO"%>
<c:set var="NEW_ID"><%= Common.NEW_ID %></c:set>

<tag:page dwr="PointLinksDwr" onload="init">
  <script type="text/javascript">
    var sourcePoints;
    var editingPointLink;
    
    function init() {
        PointLinksDwr.init(function(response) {
            sourcePoints = response.sourcePoints;
            
            // Add points to source and target selects
            dwr.util.addOptions("sourcePointId", response.sourcePoints, "key", "value");
            dwr.util.addOptions("targetPointId", response.targetPoints, "key", "value");
            
            // Create the list of existing links
            for (var i=0; i<response.pointLinks.length; i++) {
                appendPointLink(response.pointLinks[i].id);
                updatePointLink(response.pointLinks[i]);
            }
            
            <c:if test="${!empty param.plid}">
              showPointLink(${param.plid});
            </c:if>
        });
    }
    
    function showPointLink(plId) {
        if (editingPointLink)
            stopImageFader($("pl"+ editingPointLink.id +"Img"));
        PointLinksDwr.getPointLink(plId, function(pl) {
            if (!editingPointLink)
                show("pointLinkDetails");
            editingPointLink = pl;
            
            $set("xid", pl.xid);
            $set("sourcePointId", pl.sourcePointId);
            $set("targetPointId", pl.targetPointId);
            $set("script", pl.script);
            $set("event", pl.event);
            $set("disabled", pl.disabled);
            
            setUserMessage();
        });
        startImageFader($("pl"+ plId +"Img"));
        display("deletePointLinkImg", plId != ${NEW_ID});
    }
    
    function savePointLink() {
        setUserMessage();
        hideContextualMessages("pointLinkDetails")
        PointLinksDwr.savePointLink(editingPointLink.id, $get("xid"), $get("sourcePointId"), $get("targetPointId"),
                $get("script"), $get("event"), $get("disabled"), function(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                if (editingPointLink.id == ${NEW_ID}) {
                    stopImageFader($("pl"+ editingPointLink.id +"Img"));
                    editingPointLink.id = response.data.plId;
                    appendPointLink(editingPointLink.id);
                    startImageFader($("pl"+ editingPointLink.id +"Img"));
                    setUserMessage("<fmt:message key="pointLinks.pointLinkAdded"/>");
                    show($("deletePointLinkImg"));
                }
                else
                    setUserMessage("<fmt:message key="pointLinks.pointLinkSaved"/>");
                PointLinksDwr.getPointLink(editingPointLink.id, updatePointLink);
            }
        });
    }
    
    function setUserMessage(message) {
        if (message) {
            show("userMessage");
            $set("userMessage", message);
        }
        else
            hide("userMessage");
    }
    
    function appendPointLink(plId) {
        createFromTemplate("pl_TEMPLATE_", plId, "pointLinksTable");
    }
    
    function updatePointLink(pl) {
        $set("pl"+ pl.id +"Name", getPointName(pl.sourcePointId) +' <tag:img png="bullet_go"/> '+ getPointName(pl.targetPointId));
        setPointLinkImg(pl.disabled, $("pl"+ pl.id +"Img"));
    }
    
    function getPointName(pointId) {
        for (var i=0; i<sourcePoints.length; i++) {
            if (sourcePoints[i].key == pointId)
                return sourcePoints[i].value;
        }
        return null;
    }
    
    function deletePointLink() {
        PointLinksDwr.deletePointLink(editingPointLink.id, function() {
            stopImageFader($("pl"+ editingPointLink.id +"Img"));
            $("pointLinksTable").removeChild($("pl"+ editingPointLink.id));
            hide("pointLinkDetails");
            editingPointLink = null;
        });
    }
    
    function validateScript() {
        PointLinksDwr.validateScript($get("script"), $get("sourcePointId"), $get("targetPointId"),
                function(response) {
            showDwrMessages(response.messages);
        });
    }
  </script>
  
  <table>
    <tr>
      <td valign="top">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="pointLinks.pointLinks"/></span>
                <tag:help id="pointLinks"/>
              </td>
              <td align="right"><tag:img png="link_add" title="common.add" onclick="showPointLink(${NEW_ID})"
                      id="pl${NEW_ID}Img"/></td>
            </tr>
          </table>
          <table id="pointLinksTable">
            <tbody id="pl_TEMPLATE_" onclick="showPointLink(getMangoId(this))" class="ptr" style="display:none">
              <tr>
                <td><tag:img id="pl_TEMPLATE_Img" png="link" title="pointLinks.pointLink"/></td>
                <td class="link" id="pl_TEMPLATE_Name"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
      
      <td valign="top" style="display:none;" id="pointLinkDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td><span class="smallTitle"><fmt:message key="pointLinks.details"/></span></td>
              <td align="right">
                <tag:img png="save" onclick="savePointLink();" title="common.save"/>
                <tag:img id="deletePointLinkImg" png="delete" onclick="deletePointLink();" title="common.delete"/>
              </td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
              <td class="formField"><input type="text" id="xid"/></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="pointLinks.source"/></td>
              <td class="formField"><select id="sourcePointId"></select></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="pointLinks.target"/></td>
              <td class="formField"><select id="targetPointId"></select></td>
            </tr>
            
            <tr>
              <td class="formLabel">
                <fmt:message key="pointLinks.script"/>
                <tag:img png="accept" onclick="validateScript();" title="pointLinks.validate"/>
              </td>
              <td class="formField">
                <textarea id="script" rows="10" cols="50"/></textarea>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="pointLinks.event"/></td>
              <td class="formField">
                <select id="event">
                  <option value="<c:out value="<%= PointLinkVO.EVENT_CHANGE %>"/>"><fmt:message key="pointLinks.event.change"/></option>
                  <option value="<c:out value="<%= PointLinkVO.EVENT_UPDATE %>"/>"><fmt:message key="pointLinks.event.update"/></option>
                </select>
              </td>
            </tr>
            
            <tr>
              <td class="formLabel"><fmt:message key="common.disabled"/></td>
              <td class="formField"><input type="checkbox" id="disabled"/></td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td colspan="2" id="userMessage" class="formError" style="display:none;"></td>
            </tr>
          </table>
        </div>
      </td>
    </tr>
  </table>
</tag:page>