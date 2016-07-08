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
<tag:page dwr="PointHierarchyDwr" onload="init">
  <script type="text/javascript">
    var selectedFolderNode;
    
    function init() {
        PointHierarchyDwr.getPointHierarchy(initCB);
        var tree = dojo.widget.manager.getWidgetById('tree');
        dojo.event.topic.subscribe("tree/titleClick", new TreeClickHandler(), 'handle');
        setErrorMessage();
    }
    
    var TreeClickHandler = function() {
        this.handle = function(message) {
            setErrorMessage();
            var widget = message.source;
            if (widget.isFolder) {
                selectedFolderNode = widget;
                $set("folderName", widget.object.name);
                show("folderEditDiv");
            }
            else
                hide("folderEditDiv");
        }
    }
    
    function initCB(rootFolder) {
        var tree = dojo.widget.manager.getWidgetById('tree');
        var i;
        
        for (i=0; i<rootFolder.subfolders.length; i++)
            addFolder(rootFolder.subfolders[i], tree);
        
        for (i=0; i<rootFolder.points.length; i++)
            addPoint(rootFolder.points[i], tree);
        
        hide("loadingImg");
        show("treeDiv");
    }
    
    function addFolder(folder, parent) {
        var i;
        var folderNode = dojo.widget.createWidget("TreeNode", {
                title: "<img src='images/folder_brick.png'/> "+ folder.name,
                isFolder: "true",
                object: folder
        });
        parent.addChild(folderNode);
        
        if (folder.subfolders) {
            for (i=0; i<folder.subfolders.length; i++)
                addFolder(folder.subfolders[i], folderNode);
        }
        
        if (folder.points) {
            for (i=0; i<folder.points.length; i++)
                addPoint(folder.points[i], folderNode);
        }
        
        folder.subfolders = null;
        folder.points = null;
    }
    
    function addPoint(point, parent) {
        var pointNode = dojo.widget.createWidget("TreeNode", {
                title: "<img src='images/icon_comp.png'/> "+ point.value,
                object: point
        });
        parent.addChild(pointNode);
    }
    
    function newFolder() {
        setErrorMessage();
        var folder = {
            id: <c:out value="<%= Common.NEW_ID %>"/>,
            name: "<fmt:message key="pointHierarchy.defaultName"/>"
        };
        var tree = dojo.widget.manager.getWidgetById('tree');
        addFolder(folder, tree);
    }
    
    function save() {
    	startImageFader("saveHierImg");
        setErrorMessage();
        hide("folderEditDiv");
        var tree = dojo.widget.manager.getWidgetById('tree');
        var rootFolder = { id: 0, name: "root", subfolders: new Array(), points: new Array() };
        gatherTreeData(tree, rootFolder);
        PointHierarchyDwr.savePointHierarchy(rootFolder, saveCB);
    }
    
    function saveCB(rootFolder) {
    	stopImageFader("saveHierImg");
        setErrorMessage("<fmt:message key="pointHierarchy.saved"/>");
        
        var tree = dojo.widget.manager.getWidgetById('tree');
        while (tree.children.length > 0)
            tree.removeNode(tree.children[0]);
        
        initCB(rootFolder);
    }
    
    function gatherTreeData(treeNode, folder) {
        for (var i=0; i<treeNode.children.length; i++) {
            if (treeNode.children[i].isFolder) {
                var subfolder = treeNode.children[i].object;
                folder.subfolders[folder.subfolders.length] = subfolder;
                subfolder.subfolders = new Array();
                subfolder.points = new Array();
                gatherTreeData(treeNode.children[i], subfolder);
            }
            else
                folder.points[folder.points.length] = treeNode.children[i].object;
        }
    }
    
    function deleteFolder() {
        setErrorMessage();
        if (selectedFolderNode.children.length > 0) {
            if (!confirm("<fmt:message key="pointHierarchy.deleteConfirm"/>"))
                return;
        }
        
        while (selectedFolderNode.children.length > 0) {
            var child = selectedFolderNode.children[0];
            selectedFolderNode.removeNode(child);
            selectedFolderNode.parent.addChild(child);
        }
        
        selectedFolderNode.parent.removeNode(selectedFolderNode);
        hide("folderEditDiv");
    }
    
    function saveFolder() {
        setErrorMessage();
        var name = $get("folderName");
        if (!name || name == "")
            alert("<fmt:message key="pointHierarchy.noName"/>");
        else {
          selectedFolderNode.object.name = name;
          selectedFolderNode.titleNode.innerHTML =
                  "<img src='images/folder_brick.png'/> "+ selectedFolderNode.object.name;
        }
    }
    
    function setErrorMessage(message) {
        if (!message)
            hide("errorMessage");
        else {
            $("errorMessage").innerHTML = message;
            show("errorMessage");
        }
    }
  </script>
  
  <table>
    <tr>
      <td valign="top">
        <div class="borderDivPadded">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="pointHierarchy.hierarchy"/></span>
                <tag:help id="pointHierarchy"/>
              </td>
              <td align="right">
                <tag:img png="folder_add" title="common.add" onclick="newFolder()"/>
                <tag:img id="saveHierImg" png="save" title="common.save" onclick="save()"/>
              </td>
            </tr>
            <tr><td class="formError" id="errorMessage"></td></tr>
          </table>
        
          <tag:img png="hourglass" id="loadingImg"/>
          <div id="treeDiv" style="display:none;">
            <div dojoType="Tree" DNDMode="between" toggle="wipe" DNDAcceptTypes="tree" widgetId="tree"></div>
          </div>
        </div>
      </td>
      
      <td valign="top">
        <div id="folderEditDiv" class="borderDivPadded" style="display:none;">
          <table width="100%">
            <tr>
              <td class="smallTitle"><fmt:message key="pointHierarchy.details"/></td>
              <td align="right">
                <tag:img id="deleteImg" png="delete" title="common.delete" onclick="deleteFolder();"/>
                <tag:img id="saveImg" png="save" title="common.save" onclick="saveFolder();"/>
              </td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td class="formLabelRequired"><fmt:message key="pointHierarchy.name"/></td>
              <td class="formField"><input id="folderName" type="text"/></td>
            </tr>
          </table>
        </div>
      </td>
    </tr>
  </table>
</tag:page>