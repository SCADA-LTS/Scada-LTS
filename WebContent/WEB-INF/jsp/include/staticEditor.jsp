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
<div id="staticEditorPopup" style="display:none;left:0px;top:0px;" class="windowDiv">
  <div>
  <table cellpadding="0" cellspacing="0"><tr><td>
    <table width="100%">
      <tr>
        <td>
          <tag:img png="html" title="viewEdit.static.editor" style="display:inline;"/>
        </td>
        <td align="right">
          <tag:img png="save" onclick="staticEditor.save()" title="common.save" style="display:inline;"/>&nbsp;
          <tag:img png="cross" onclick="staticEditor.close()" title="common.close" style="display:inline;"/>
        </td>
      </tr>
    </table>
    <table>
      <tr>
        <td class="formLabel"><fmt:message key="viewEdit.position.x"/></td>
        <td class="formField"><input id="staticPositionX" type="number" default="0" min="0"/></td></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="viewEdit.position.y"/></td>
        <td class="formField"><input id="staticPositionY" type="number" default="0" min="0"/></td></td>
      </tr>
    </table>
    <table id="htmlEditor">
      <tr>
        <td class="formField"><textarea id="staticPointContent" rows="10" cols="50"></textarea></td>
      </tr>
    </table>
    <table id="linkEditor">
      <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.text"/></td>
          <td class="formField"><input id="linkText" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.link"/></td>
          <td class="formField"><input id="linkLink" type="text"/></td>
        </tr>
        <tr>
        <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.views"/></td>
        <td class="formField"><select id="viewsList" onchange="staticEditor.viewSelectChanged(this.value)"></select></td>
      </tr>
    </table>
    <table id="scriptButtonEditor">
      	<tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.text"/></td>
          <td class="formField"><input id="scriptButtonText" type="text"/></td>
        </tr>
        <tr>
        	<td class="formLabelRequired"><fmt:message key="eventHandlers.type.script"/></td>
        	<td class="formField"><select id="scriptsList"></select></td>
      	</tr>
    </table>

    <table id="chartComparatorEditor">
      	<tr>
          <td class="formLabelRequired"><fmt:message key="graphic.chartWidth"/></td>
          <td class="formField"><input id="chartComparatorWidth" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="graphic.chartHeight"/></td>
          <td class="formField"><input id="chartComparatorHeight" type="text"/></td>
        </tr>
    </table>

     <table id="flexEditor">
      	<tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.width"/></td>
          <td class="formField"><input id="flexWidth" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.height"/></td>
          <td class="formField"><input id="flexHeight" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.projectDefined"/></td>
          <td class="formField"><input id="flexProjectDefined" type="checkbox"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.projectsSource"/></td>
          <td class="formField"><input  id="flexProjectsSource" type="text"/></td>
        </tr>
		<tr>
        	<td class="formLabelRequired"><fmt:message key="viewEdit.graphic.project"/></td>
        	<td class="formField"><select id="flexProjectsList"></select></td>
      	</tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="viewEdit.graphic.runtimeMode"/></td>
          <td class="formField"><input id="flexRuntimeMode" type="checkbox"/></td>
        </tr>
    </table>
  </td></tr></table>

  <script type="text/javascript">
    var viewId = mango.longPoll.pollRequest.viewId;
    function StaticEditor() {
        this.componentId = null;
        this.component = null;

        this.open = function(compId) {
            document.getElementById("staticEditorPopup").firstElementChild.setAttribute("id", "static" + compId);

            hide('htmlEditor');
            hide('linkEditor');
            hide('scriptButtonEditor');
            hide('chartComparatorEditor');
            hide('flexEditor');

            staticEditor.componentId = compId;
            ViewDwr.getViewComponent(compId, viewId, function(comp) {
                // Update the data in the form.
                staticEditor.component = comp;

                var pDim = getNodeBounds($("c"+ compId));
            $set("staticPositionX", pDim.x);
            $set("staticPositionY", pDim.y);

				if(comp.defName == 'html') {
					$set("staticPointContent", comp.content);
					show('htmlEditor');
		            hide('linkEditor');
		            hide('scriptButtonEditor');
		            hide('flexEditor');

		            show("staticEditorPopup");

				} else if(comp.defName == 'link'){
					ViewDwr.getViews(function(views) {
						staticEditor.updateViewsList(views);
		            });
					$set("staticPointContent", comp.content);
					$set("linkText", comp.text);
					$set("linkLink", comp.link);
					hide('htmlEditor');
					hide('scriptButtonEditor');
					hide('flexEditor');

		            show('linkEditor');
		            show("staticEditorPopup");

				} else if(comp.defName == 'scriptButton'){
					ViewDwr.getScripts(function(scripts) {
						staticEditor.updateScriptsList(scripts);
						$set("scriptsList", comp.scriptXid);
		            });
					$set("staticPointContent", comp.content);
					$set("scriptButtonText", comp.text);
					hide('htmlEditor');
					hide('linkEditor');
					hide('flexEditor');

		            show('scriptButtonEditor');
		            show("staticEditorPopup");

				} else if(comp.defName == 'chartComparator'){

					$set("chartComparatorWidth", comp.width);
					$set("chartComparatorHeight", comp.height);

		            show('chartComparatorEditor');
		            show("staticEditorPopup");

				}else if(comp.defName == 'flex'){

					ViewDwr.getFlexProjects(function(flexProjects) {
						staticEditor.updateFlexProjectsList(flexProjects);
						$set("flexProjectsList", comp.projectId);
		            });

					$set("staticPointContent", comp.content);
					$set("flexWidth", comp.width);
					$set("flexHeight", comp.height);
					$set("flexProjectDefined", comp.projectDefined);
					$set("flexProjectsSource", comp.projectSource);
					$set("flexRuntimeMode", comp.runtimeMode);

					hide('htmlEditor');
					hide('linkEditor');
					hide('scriptButtonEditor');

		            show('flexEditor');
		            show("staticEditorPopup");

				}
                    positionEditor(compId, "staticEditorPopup");

            });
        };

        this.close = function() {
            hide("staticEditorPopup");
        };

        this.save = function() {
          let posX = Number($get("staticPositionX").trim());
          let posY = Number($get("staticPositionY").trim());
          [posX, posY] = validateComponentPosition(posX,posY);

          switch(staticEditor.component.defName) {
            case 'html':
              ViewDwr.saveHtmlComponent(staticEditor.componentId,
                $get("staticPointContent"),
                posX, posY, viewId,
                function() {
	                staticEditor.close();
	                updateHtmlComponentContent("c"+ staticEditor.componentId, $get("staticPointContent"));
	              }
              );
              break;
            case 'link':
              ViewDwr.saveLinkComponent(staticEditor.componentId,
                $get("linkText"), $get("linkLink"),
                posX, posY, viewId,
                function(response) {
	                if (response.hasMessages)
			        	    showDwrMessages(response.messages);
					        else {
						        staticEditor.close();
		                tempContent = "<a> " +$get("linkText") +"</a>";
		                updateHtmlComponentContent("c"+ staticEditor.componentId, tempContent);
					        }
	              }
              );
              break;
            case 'scriptButton':
              ViewDwr.saveScriptButtonComponent(staticEditor.componentId,
                $get("scriptButtonText"), $get("scriptsList"),
                posX, posY, viewId,
                function(response) {
					        if (response.hasMessages)
			        	    showDwrMessages(response.messages);
					        else {
						        staticEditor.close();
		                tempContent = "<button> " +$get("scriptButtonText") +"</button>";
		                updateHtmlComponentContent("c"+ staticEditor.componentId, tempContent);
					        }
	            });
              break;
            case 'chartComparator':
              ViewDwr.saveChartComparatorComponent(staticEditor.componentId,
                $get("chartComparatorWidth"), $get("chartComparatorHeight"),
                posX, posY, viewId,
                function(response) {
						      if (response.hasMessages)
				        	  showDwrMessages(response.messages);
						      else {
							      staticEditor.close();
			              tempContent =
				              "<div style='background-color: silver; border: 1px solid red; width: "+ ($get("chartComparatorWidth")*2) +"px; height: "+$get("chartComparatorHeight") +"px;'> <b> <fmt:message key='viewEdit.graphic.saveToLoad'/> </b> </div>";
			              componentId = "c"+ staticEditor.componentId;
			              updateHtmlComponentContent(componentId, tempContent);
			                //$(componentId).style.top=  "0px";
			                //$(componentId).style.left=  "0px";
			                //updateViewComponentLocation(componentId);

			                //resizeViewBackground($get("flexWidth"), $get("flexHeight"));
						      }
	            });
              break;
            case 'flex':
              ViewDwr.saveFlexComponent(staticEditor.componentId,
                $get("flexWidth"), $get("flexHeight"),
						    $get("flexProjectDefined"),$get("flexProjectsSource"),
                $get("flexProjectsList"),$get("flexRuntimeMode"),
                posX, posY, viewId,
					      function(response) {
						      if (response.hasMessages)
				        	  showDwrMessages(response.messages);
						      else {
							      staticEditor.close();
			              tempContent =
				              "<div style='background-color: silver; border: 1px solid red; width: "+ $get("flexWidth") +"px; height: "+$get("flexHeight") +"px;'> <b> <fmt:message key='viewEdit.graphic.saveToLoad'/> </b> </div>";
			              componentId = "c"+ staticEditor.componentId;
			              updateHtmlComponentContent(componentId, tempContent);
			                //$(componentId).style.top=  "0px";
			                //$(componentId).style.left=  "0px";
			                //updateViewComponentLocation(componentId);

			                //resizeViewBackground($get("flexWidth"), $get("flexHeight"));
						      }
	            });
              break;
            default:
              console.error("Not found component!")
          }
          updatePointPosition(staticEditor.componentId,
            posX, posY, "staticPositionX", "staticPositionY"
          );
        };

        this.updateViewsList = function(views) {
            dwr.util.removeAllOptions("viewsList");
            var sel = $("viewsList");
            sel.options[0] = new Option("", 0);
            for (var i=0; i<views.length; i++) {
                sel.options[i+1] = new Option(views[i].value, views[i].key);
            }
        };

        this.updateScriptsList = function(scripts) {
            dwr.util.removeAllOptions("scriptsList");
            var sel = $("scriptsList");
            for (var i=0; i<scripts.length; i++) {
                sel.options[i] = new Option(scripts[i].name, scripts[i].xid);
            }
        };

        this.updateFlexProjectsList = function(flexProjects) {
            dwr.util.removeAllOptions("flexProjectsList");
            var sel = $("flexProjectsList");
            for (var i=0; i<flexProjects.length; i++) {
                sel.options[i] = new Option(flexProjects[i].name, flexProjects[i].id);
            }
        };

        this.viewSelectChanged = function(value) {
        	$set("linkLink","");
            if(value!=0) {
            	link = "views.shtm?viewId="+value;
                $set("linkLink",link);
            }
        };
    }

    function getAbsolutePath() {
        var loc = window.location;
        var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
        return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
    }

    var staticEditor = new StaticEditor();
  </script>
  </div>
</div>