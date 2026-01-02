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
<link href="resources/jQuery/plugins/chosen/chosen.min.css" rel="stylesheet" type="text/css"/>	
<script type="text/javascript" src="resources/jQuery/plugins/chosen/chosen.jquery.min.js"></script>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<div id="settingsEditorPopup" style="display:none;left:0px;top:0px;" class="windowDiv">
  <div>
  <table cellpadding="0" cellspacing="0"><tr><td>
    <table width="100%">
      <tr>
        <td>
          <tag:img png="plugin_edit" title="viewEdit.settings.editor" style="display:inline;"/>
          <span class="copyTitle" id="settingsComponentName"></span>
        </td>
        <td align="right">
          <tag:img png="save" onclick="settingsEditor.save()" title="common.save" style="display:inline;"/>&nbsp;
          <tag:img png="cross" onclick="settingsEditor.close()" title="common.close" style="display:inline;"/>
        </td>
      </tr>
    </table>
    <table>
      <tr>
        <td class="formLabelRequired"><spring:message code="viewEdit.settings.point"/></td>
        <td class="formField"><select id="settingsPointList" onchange="settingsEditor.pointSelectChanged(this.value)"></select></td>
      </tr>
      <tr>
        <td class="formLabel"><spring:message code="viewEdit.settings.nameOverride"/></td>
        <td class="formField"><input id="settingsPointName" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><spring:message code="viewEdit.settings.settableOverride"/></td>
        <td class="formField"><input id="settingsSettable" type="checkbox"/></td>
      </tr>
      <tr>
        <td class="formLabel"><spring:message code="viewEdit.settings.background"/></td>
        <td class="formField"><input id="settingsBkgdColor" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><spring:message code="viewEdit.settings.displayControls"/></td>
        <td class="formField"><input id="settingsControls" type="checkbox"/></td>
      </tr>
      <tr>
        <td class="formLabel"><spring:message code="viewEdit.position.x"/></td>
        <td class="formField"><input id="settingsPositionX" type="number"/></td></td>
      </tr>
      <tr>
        <td class="formLabel"><spring:message code="viewEdit.position.y"/></td>
        <td class="formField"><input id="settingsPositionY" type="number"/></td></td>
      </tr>
    </table>
  </td></tr></table>
  
  <script type="text/javascript">
    var viewId = mango.longPoll.pollRequest.viewId;
    // Script requires
    //  - Drag and Drop library for locating objects and positioning the window.
    //  - DWR utils for using $() prototype.
    //  - common.js
    function SettingsEditor() {
        this.componentId = null;
        this.pointList = [];

        if(this.dataPointsSelect) {
            this.dataPointsSelect.clear();
        }

        let ref = {}
        ref.excludePointsArray = [];
        ref.limit = 500;
        ref.selectHtmlId = "settingsPointList";
        ref.placeholderTextSingle = "<spring:message code='chosen.selector.selectPoint'/>";
        ref.pointsArray = [];
        ref.dataTypes = [];
        ref.altKey = "id";
        ref.altValue = "name";
        ref.widthPx = "400px";

        this.dataPointsSelect = new DataPointsSelect(ref);
        
        this.open = function(compId) {
            document.getElementById("settingsEditorPopup").firstElementChild.setAttribute("id", "settings" + compId);
            settingsEditor.componentId = compId;
            
            ViewDwr.getViewComponentRes(compId, viewId, function(response) {
            	let comp = response.data.comp;
                $set("settingsComponentName", comp.displayName);
                
                // Update the point list
                settingsEditor.setPointList(response.data.pointList);
                settingsEditor.setDataTypes(comp.supportedDataTypes);

                // Update the data in the form.
                $set("settingsPointList", comp.dataPointId);
                $set("settingsPointName", comp.nameOverride);
                $set("settingsSettable", comp.settableOverride);
                $set("settingsBkgdColor", comp.bkgdColorOverride);
                $set("settingsControls", comp.displayControls);                
                $set("settingsPositionX", comp.x);
                $set("settingsPositionY", comp.y);

                settingsEditor.pointSelectChanged(comp.dataPointId);

                positionEditor(compId, "settingsEditorPopup");
                show("settingsEditorPopup");
            });
        };
        
        this.close = function() {
            hide("settingsEditorPopup");
            hideContextualMessages("settingsEditorPopup");
        };
        
        this.save = function() {
            hideContextualMessages("settingsEditorPopup");
            let posX = $get("settingsPositionX");
            let posY = $get("settingsPositionY");
            [posX, posY] = validateComponentPosition(posX, posY);
            updatePointPosition(settingsEditor.componentId,
              posX, posY, "settingsPositionX", "settingsPositionY"
            );
            ViewDwr.setPointComponentSettings(settingsEditor.componentId, this.dataPointsSelect.getPointId(),
                    $get("settingsPointName"), $get("settingsSettable"), $get("settingsBkgdColor"),
                    $get("settingsControls"), posX, posY, viewId, function(response) {
                if (response.hasMessages) {
                    showDwrMessages(response.messages);
                }
                else {
                    settingsEditor.close();
                    MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
                }
            });
        };
        
        this.setPointList = function(pointList) {
            this.dataPointsSelect.setPointsArray(pointList);
        };

        this.setDataTypes = function(dataTypes) {
            if(dataTypes) {
                this.dataPointsSelect.setDataTypes(dataTypes);
            }
        };
        
        this.pointSelectChanged = function(dataPointId) {
            if(dataPointId > 0) {
                var point = this.dataPointsSelect.getPoint(dataPointId);
                if (!point || !point.settable) {
                    $set("settingsSettable", false);
                    $("settingsSettable").disabled = true;
                } else {
                    $("settingsSettable").disabled = false;
                }

                if(point) {
                    this.dataPointsSelect.setPointsArray([point]);
                    this.dataPointsSelect.setPointId(point.id);
                }
            } else {
                this.dataPointsSelect.setPointsArray([]);
            }

        };
    }
    var settingsEditor = new SettingsEditor();
  </script>
  </div>
</div>