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
<%@page import="com.serotonin.mango.view.component.SimpleCompoundComponent"%>
<div id="compoundEditorPopup" style="display:none;left:0px;top:0px;" class="windowDiv">
  <table cellpadding="0" cellspacing="0"><tr><td>
    <table width="100%">
      <tr>
        <td>
          <tag:img png="plugin_edit" title="viewEdit.compound.editor" style="display:inline;"/>
          <span class="copyTitle" id="compoundComponentName"></span>
        </td>
        <td align="right">
          <tag:img png="save" onclick="compoundEditor.save()" title="common.save" style="display:inline;"/>&nbsp;
          <tag:img png="cross" onclick="compoundEditor.close()" title="common.close" style="display:inline;"/>
        </td>
      </tr>
    </table>
    <table>
      <tr>
        <td class="formLabelRequired"><fmt:message key="viewEdit.compound.name"/></td>
        <td class="formField"><input id="compoundName" type="text"/></td>
      </tr>
      <tbody id="simpleCompoundAttrs">
        <tr>
          <td class="formLabel"><fmt:message key="viewEdit.compound.backgroundColour"/></td>
          <td class="formField"><input id="compoundBackgroundColour" type="text"/></td>
        </tr>
      </tbody>
      <tbody id="imageChartAttrs">
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.compound.width"/></td>
          <td class="formField"><input id="imageChartWidth" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.compound.height"/></td>
          <td class="formField"><input id="imageChartHeight" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.compound.duration"/></td>
          <td class="formField">
            <input type="text" id="imageChartDurationPeriods" class="formShort"/>
            <select id="imageChartDurationType">
              <tag:timePeriodOptions sst="false" s="true" min="true" h="true" d="true" w="true" mon="true" y="true"/>
            </select>
          </td>
        </tr>
      </tbody>
      <tbody id="pointLists"></tbody>
    </table>
  </td></tr></table>
  
  <script type="text/javascript">
    function CompoundEditor() {
        this.component = null;
        this.pointList = [];
        
        this.open = function(compId) {
            ViewDwr.getViewComponent(compId, function(comp) {
                compoundEditor.component = comp;
                $set("compoundComponentName", comp.displayName);
                
                // Update the point lists
                compoundEditor.updatePointLists();
                
                // Update the data in the form.
                $set("compoundName", comp.name);
                
                if (comp.defName == "simpleCompound") {
                    $set("compoundBackgroundColour", comp.backgroundColour);
                    show("simpleCompoundAttrs");
                }
                else
                    hide("simpleCompoundAttrs");
                
                if (comp.defName == "imageChart") {
                    $set("imageChartWidth", comp.width);
                    $set("imageChartHeight", comp.height);
                    $set("imageChartDurationType", comp.durationType);
                    $set("imageChartDurationPeriods", comp.durationPeriods);
                    show("imageChartAttrs");
                }
                else
                    hide("imageChartAttrs");
                
                show("compoundEditorPopup");
            });
            
            positionEditor(compId, "compoundEditorPopup");
        };
        
        this.close = function() {
            hide("compoundEditorPopup");
            hideContextualMessages("compoundEditorPopup");
        };
        
        this.save = function() {
            hideContextualMessages("compoundEditorPopup");
            
            // Gather the point settings
            var pointChildren = compoundEditor.getPointChildren();
            var childPointIds = new Array();
            var sel;
            for (var i=0; i<pointChildren.length; i++)
                childPointIds.push({key: pointChildren[i].id, value: $get("compoundPointSelect"+ pointChildren[i].id)});
            
            if (compoundEditor.component.defName == "simpleCompound")
                ViewDwr.saveSimpleCompoundComponent(compoundEditor.component.id, $get("compoundName"),
                        $get("compoundBackgroundColour"), childPointIds, compoundEditor.saveCB);
            else if (compoundEditor.component.defName == "imageChart")
                ViewDwr.saveImageChartComponent(compoundEditor.component.id, $get("compoundName"),
                        $get("imageChartWidth"), $get("imageChartHeight"), $get("imageChartDurationType"),
                        $get("imageChartDurationPeriods"), childPointIds, compoundEditor.saveCB);
            else
                ViewDwr.saveCompoundComponent(compoundEditor.component.id, $get("compoundName"), childPointIds,
                        compoundEditor.saveCB);
        };
        
        this.saveCB = function(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                if (compoundEditor.component.defName == "simpleCompound")
                    $("c"+ compoundEditor.component.id +"Info").style.background = $get("compoundBackgroundColour");
                
                compoundEditor.close();
                MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
            }
        };
        
        this.setPointList = function(pointList) {
            compoundEditor.pointList = pointList;
        };
        
        this.updatePointLists = function() {
            var pointChildren = compoundEditor.getPointChildren();
            
            // Create the select controls
            dwr.util.removeAllRows("pointLists");
            dwr.util.addRows("pointLists", pointChildren,
                [
                    function(data) { return data.description; },
                    function(data) { return '<select id="compoundPointSelect'+ data.id +'"></select>'; }
                ],
                {
                    cellCreator: function(options) {
                        var td = document.createElement("td");
                        if (options.cellNum == 0) {
                            if (compoundEditor.component.defName == "simpleCompound" &&
                                    options.rowData.id == "<%= SimpleCompoundComponent.LEAD_POINT %>")
                                td.className = "formLabelRequired";
                            else
                                td.className = "formLabel";
                        }
                        else if (options.cellNum == 1)
                            td.className = "formField";
                        return td;
                    }
                }
            );
            
            // Add options to the controls.
            var sel, p;
            for (var i=0; i<pointChildren.length; i++) {
                sel = $("compoundPointSelect"+ pointChildren[i].id);
                sel.options[0] = new Option("", 0);
                for (p=0; p<compoundEditor.pointList.length; p++) {
                    if (contains(pointChildren[i].dataTypes, compoundEditor.pointList[p].dataType))
                        sel.options[sel.options.length] = new Option(settingsEditor.pointList[p].name,
                                settingsEditor.pointList[p].id);
                }
                
                // Set the control default value.
                $set(sel, pointChildren[i].viewComponent.dataPointId);
            }
        };
        
        this.getPointChildren = function() {
            var pointChildren = new Array();
            for (var i=0; i<compoundEditor.component.childComponents.length; i++) {
                if (compoundEditor.component.childComponents[i].viewComponent.pointComponent)
                    pointChildren.push(compoundEditor.component.childComponents[i]);
            }
            return pointChildren;
        };
    }
    var compoundEditor = new CompoundEditor();
  </script>
</div>