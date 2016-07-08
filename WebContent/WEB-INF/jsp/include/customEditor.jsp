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
<div id="customEditorPopup" style="display:none;left:0px;top:0px;" class="windowDiv">
  <table cellpadding="0" cellspacing="0"><tr><td>
    <table width="100%">
      <tr>
        <td>
          <tag:img png="graphic" title="viewEdit.graphic.editor" style="display:inline;"/>
          <span class="copyTitle" id="graphicsComponentName"></span>
        </td>
        <td align="right">
          <tag:img png="save" onclick="customEditor.save()" title="common.save" style="display:inline;"/>&nbsp;
          <tag:img png="cross" onclick="customEditor.close()" title="common.close" style="display:inline;"/>
        </td>
      </tr>
    </table>
    
    <table>
      <tbody id="customEditor_alarmlist" style="display:none;">
      	<tr>
          <td class="formLabel"><fmt:message key="common.alarmLevel"/></td>
          <td class="formField"><select id="customEditorAlarmListMinAlarmLevel"><tag:alarmLevelOptions allOption="true"/></select></td>
        </tr>
      	<tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.maxListSize"/></td>
          <td class="formField"><input id="customEditorAlarmListMaxListSize" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.width"/></td>
          <td class="formField"><input id="customEditorAlarmListWidth" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="viewEdit.graphic.hideIdColumn"/></td>
          <td class="formField"><input id="customEditorAlarmListIdColumn" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="viewEdit.graphic.hideAlarmLevelColumn"/></td>
          <td class="formField"><input id="customEditorAlarmListAlarmLevelColumn" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="viewEdit.graphic.hideTimestampColumn"/></td>
          <td class="formField"><input id="customEditorAlarmListTimestampColumn" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="viewEdit.graphic.hideInactivityColumn"/></td>
          <td class="formField"><input id="customEditorAlarmListInactivityColumn" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="viewEdit.graphic.hideAckColumn"/></td>
          <td class="formField"><input id="customEditorAlarmListAckColumn" type="checkbox"/></td>
        </tr>
      </tbody>
      
      <tbody id="customEditor_button" style="display:none;">
        
      </tbody>
      
    </table>
  </td></tr></table>
  
  <script type="text/javascript">
    function CustomEditor() {
        this.componentId = null;
        this.typeName = null;
        
        this.open = function(compId) {
            customEditor.componentId = compId;
            
            // Set the renderers for the data type of this point view.
            ViewDwr.getViewComponent(compId, customEditor.setViewComponent);
            
            positionEditor(compId, "customEditorPopup");
        };
        
        this.setViewComponent = function(comp) {
            customEditor.typeName = comp.typeName;
            
            // Update the data in the form.
            if (comp.typeName == "alarmlist") {
            	$set("customEditorAlarmListMinAlarmLevel",comp.minAlarmLevel);
                $set("customEditorAlarmListMaxListSize",comp.maxListSize);
                $set("customEditorAlarmListWidth",comp.width);
                $set("customEditorAlarmListIdColumn",comp.hideIdColumn);
                $set("customEditorAlarmListAlarmLevelColumn",comp.hideAlarmLevelColumn);
                $set("customEditorAlarmListTimestampColumn",comp.hideTimestampColumn);
                $set("customEditorAlarmListInactivityColumn",comp.hideInactivityColumn);
                $set("customEditorAlarmListAckColumn",comp.hideAckColumn);
                
            } else if(comp.typeName == "yourCustomComponent") {
                
            }
            show("customEditor_"+ comp.typeName);
            show("customEditorPopup");
        };
    
        this.close = function() {
            hide("customEditorPopup");
            //hideContextualMessages("customEditorPopup");
            if (customEditor.typeName)
                hide("customEditor_"+ customEditor.typeName);
        };
        
        this.save = function() {
            //hideContextualMessages("graphicRendererEditorPopup");
            if (customEditor.typeName == "alarmlist")
            	ViewDwr.saveAlarmListComponent(customEditor.componentId,
                    	$get("customEditorAlarmListMinAlarmLevel"), $get("customEditorAlarmListMaxListSize"),
                        $get("customEditorAlarmListWidth"),$get("customEditorAlarmListIdColumn"),
                        $get("customEditorAlarmListAlarmLevelColumn"),$get("customEditorAlarmListTimestampColumn"),
                        $get("customEditorAlarmListInactivityColumn"),$get("customEditorAlarmListAckColumn"), 
                        customEditor.saveCB);
            else if (customEditor.typeName == "yourCustomComponent")
            	alert('save your custom component component!');
            
        };
        
        this.saveCB = function(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                customEditor.close();
                MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
            }
        };
        
    }
    var customEditor = new CustomEditor();
  </script>
</div>