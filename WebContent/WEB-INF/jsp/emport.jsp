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
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>

<tag:page dwr="EmportDwr" onload="init">
	<script type="text/javascript">
    function init() {
        setDisabled("cancelBtn", true);
        importUpdate();
        dataPointsCheckboxChanged();
        $set("includePointValues",true);
        $set("includeUploadsFolder",true);
    }
    
    function doExportJson() {
        setDisabled("exportJsonBtn", true);
        EmportDwr.createExportData($get("prettyIndent"), $get("graphicalViews"), $get("eventHandlers"),
                $get("dataSources"), $get("dataPoints"), $get("scheduledEvents"), $get("compoundEventDetectors"),
                $get("pointLinks"), $get("users"), $get("pointHierarchy"), $get("mailingLists"), $get("publishers"),
                $get("watchLists"), $get("maintenanceEvents"),$get("scripts"),$get("pointValues"),$get("pointValuesMax"),
                $get("systemSettings"), $get("usersProfiles"), $get("reports"), function(data) {
            $set("emportData", data);
            setDisabled("exportJsonBtn", false);
        });
    }

    function doExport() {
      projectName = $get("projectName");

      if(projectName == null || projectName.trim().length == 0) {
    	  messages = [createValidationMessage("projectName","<spring:message code="emport.invalidProjectName"/>")];
  		  showDwrMessages(messages);
    	  return;
      }

      projectDescription = $get("projectDescription");
      includePointValues = $get("includePointValues");
      pointValuesMaxZip = $get("pointValuesMaxZip");
      includeUploadsFolder = $get("includeUploadsFolder");
      includeGraphicsFolder = $get("includeGraphicsFolder");

	  parameters =   "?projectName="+projectName
	  				+"&includePointValues="+includePointValues
	  				+"&includeUploadsFolder="+includeUploadsFolder
	  				+"&includeGraphicsFolder="+includeGraphicsFolder
	  				+"&projectDescription="+projectDescription
	  				+"&pointValuesMaxZip="+pointValuesMaxZip;

      w = window.open("export_project.htm"+parameters, "_blank");
    }

    function doImport() {
        setDisabled("importJsonBtn", true);
        hideGenericMessages("importMessages");
        $set("alternateMessage", "<spring:message code="emport.importProgress"/>");
        EmportDwr.importData($get("emportData"), function(response) {
            if (response.data.importStarted)
                importUpdate();
            else if (response.hasMessages) {
                showDwrMessages(response.messages, $("importMessages"));
                $set("alternateMessage");
                setDisabled("importJsonBtn", false);
            }
            else {
                $set("alternateMessage", "<spring:message code="emport.noMessages"/>");
                setDisabled("importJsonBtn", false);
            }
        });
    }

    function importUpdate() {
        EmportDwr.importUpdate(function(response) {
            if (response.data.noImport)
                // no op
                return;

            $set("alternateMessage", "<spring:message code="emport.importProgress"/>");
            setDisabled("importJsonBtn", true);
            setDisabled("cancelBtn", false);

            showDwrMessages(response.messages, $("importMessages"));

            if (response.data.cancelled || response.data.complete) {
                setDisabled("importJsonBtn", false);
                setDisabled("cancelBtn", true);

                if (response.data.cancelled)
                    $set("alternateMessage", "<spring:message code="emport.importCancelled"/>");
                else
                    $set("alternateMessage", "<spring:message code="emport.importComplete"/>");
            }
            else
                setTimeout(importUpdate, 1000);
        });
    }

    function changingProject() {
        EmportDwr.importUpdate(function(response) {
            if (response.data.noImport)
                // no op
                return;

            $set("alternateMessage", "<spring:message code="emport.importProgress"/>");
            setDisabled("importJsonBtn", true);
            setDisabled("cancelBtn", false);

            showDwrMessages(response.messages, $("changingProjectMessages"));

            if (response.data.cancelled || response.data.complete) {
                setDisabled("importJsonBtn", false);
                setDisabled("cancelBtn", true);

                if (response.data.cancelled)
                    $set("alternateMessage", "<spring:message code="emport.importCancelled"/>");
                else
                    $set("alternateMessage", "<spring:message code="emport.importComplete"/>");
            }
            else
                setTimeout(changingProject, 1000);
        });
    }

    function importCancel() {
        EmportDwr.importCancel();
    }

    function selectAll(checked) {
        $set("graphicalViews", checked);
        $set("eventHandlers", checked);
        $set("dataSources", checked);
        $set("dataPoints", checked);
        $set("pointValues", checked);
        $set("scheduledEvents", checked);
        $set("compoundEventDetectors", checked);
        $set("pointLinks", checked);
        $set("users", checked);
        $set("pointHierarchy", checked);
        $set("mailingLists", checked);
        $set("publishers", checked);
        $set("watchLists", checked);
        $set("maintenanceEvents", checked);
        $set("scripts", checked);
        dataPointsCheckboxChanged();
        $set("pointValues", checked);
        $set("systemSettings", checked);
        $set("usersProfiles", checked);
        $set("reports", checked);
    }

    function dataPointsCheckboxChanged() {
		if($get("dataPoints")) {
			$("pointValues").disabled = false;
		} else {
			$("pointValues").disabled = true;
			$set("pointValues", false);
		}
    }


    function checkFileToImport() {
    	let fileName = $get("importFile");
    	let ext = fileName.substring(fileName.length-3,fileName.length);
    	ext = ext.toLowerCase();

    	if(ext != 'zip') {
			messages = [createValidationMessage("importFile","<spring:message code="emport.invalidFile" arguments="!"/>")];
    		showDwrMessages(messages);
        	return false;
    	}

    	changingProject();
    	return true;
    }

  </script>

	<div class="borderDiv marR marB" style="float: left;">
		<table width="100%">
			<tr>
				<td colspan="2"><span class="smallTitle"><fmt:message
							key="emport.export" />
				</span> <tag:help id="emport" /></td>
			</tr>
			<tr>
				<td class="formLabel"><b><spring:message code="emport.select" />
				</b><br /> <a href="#" onclick="selectAll(true); return false"><spring:message
							code="emport.selectAll" />
				</a> | <a href="#" onclick="selectAll(false); return false"><spring:message
							code="emport.unselectAll" />
				</a></td>
				<td></td>
			</tr>
			<tr>
				<td class="formField" style="padding-left: 50px;"><input
					type="checkbox" id="watchLists" /> <label for="watchLists"><fmt:message
							key="header.watchLists" />
				</label><br /> <input type="checkbox" id="graphicalViews" /> <label
					for="graphicalViews"><spring:message code="header.views" />
				</label><br /> <input type="checkbox" id="eventHandlers" /> <label
					for="eventHandlers"><spring:message code="header.eventHandlers" />
				</label><br /> <input type="checkbox" id="dataSources" /> <label
					for="dataSources"><spring:message code="header.dataSources" />
				</label><br /> <input type="checkbox" id="dataPoints"
					onchange="dataPointsCheckboxChanged();" /> <label for="dataPoints"><fmt:message
							key="emport.dataPoints" />
				</label><br /> - <input type="checkbox" id="pointValues" /> <label
					for="pointValues"><spring:message code="emport.pointValues" />
				</label><br /> <input type="checkbox" id="scheduledEvents" /> <label
					for="scheduledEvents"><fmt:message
							key="header.scheduledEvents" />
				</label><br /> <input type="checkbox" id="compoundEventDetectors" /> <label
					for="compoundEventDetectors"><fmt:message
							key="header.compoundEvents" />
				</label><br /> <input type="checkbox" id="pointLinks" /> <label
                    for="pointLinks"><spring:message code="header.pointLinks" />
                </label><br />
                </td>
				<td><input type="checkbox" id="users" /> <label for="users"><fmt:message
							key="header.users" />
				</label><br /> <input type="checkbox" id="pointHierarchy" /> <label
					for="pointHierarchy"><fmt:message
							key="header.pointHierarchy" />
				</label><br /> <input type="checkbox" id="mailingLists" /> <label
					for="mailingLists"><spring:message code="header.mailingLists" />
				</label><br /> <input type="checkbox" id="publishers" /> <label
					for="publishers"><spring:message code="header.publishers" />
				</label><br /> <input type="checkbox" id="maintenanceEvents" /> <label
					for="maintenanceEvents"><fmt:message
							key="header.maintenanceEvents" />
				</label><br /> <input type="checkbox" id="scripts" /> <label for="scripts"><fmt:message
							key="header.scripts" />
				</label><br /> <input type="checkbox" id="systemSettings" /> <label
					for="scripts"><spring:message code="header.systemSettings" />
				</label><br />
				<input type="checkbox" id="usersProfiles" /> <label
					for="usersProfiles"><spring:message code="header.usersProfiles" />
				</label>
				<br />
				<input type="checkbox" id="reports" /> <label
                    for="reports"><spring:message code="header.reports" />
                </label>
                <br />
				</td>
				<!--          <input type="checkbox" id="reports"/> <label for="reports"><spring:message code="header.reports"/></label><br/>-->
				<!--          <input type="checkbox" id="systemSettings"/> <label for="systemSettings"><spring:message code="header.systemSettings"/></label><br/>-->
				<!--          <input type="checkbox" id="imageSets"/> <label for="imageSets"><spring:message code="header.imageSets"/></label><br/>-->
				<!--          <input type="checkbox" id="dynamicImages"/> <label for="dynamicImages"><spring:message code="header.dynamicImages"/></label><br/>-->
			</tr>
			<tr>
				<td class="formLabelRequired"><spring:message code="emport.indent" />
				</td>
				<td><input type="text" id="prettyIndent" value="3"
					class="formVeryShort" />
				</td>
			</tr>
			<tr>
				<td class="formLabelRequired"><fmt:message
						key="emport.pointValuesMax" />
				</td>
				<td><select id="pointValuesMax">
						<option value="100" />100
						</option>
						<option value="500" />500
						</option>
						<option value="1000" />1000
						</option>
						<option value="10000" />10000
						</option>
						<option value="100000" />100000
						</option>
						<option value="1000000" />1000000
						</option>
				</select></td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input id="exportJsonBtn"
					type="button" value="<spring:message code="emport.exportJson"/>"
					onclick="doExportJson()" /></td>
			</tr>

			<tr>
				<td colspan="2" align="center">
				<p></p>
				<div class="borderDiv marB marR" style="float: none;border-bottom-width: 0;border-left-width:0;border-right-width:0;">
		<p></p>
		<table width="100%">
			<tr>
				<td><span class="smallTitle"><fmt:message
							key="emport.import" />
				</span>
				</td>
			</tr>
			<tr>
				<td><spring:message code="emport.importInstruction" /> <input
					id="importJsonBtn" type="button"
					value="<spring:message code="emport.import"/>" onclick="doImport()" />
					<input id="cancelBtn" type="button"
					value="<spring:message code="common.cancel"/>" onclick="importCancel()"
					disabled="disabled" /></td>
			</tr>
			<tbody id="importMessages"></tbody>
			<tr>
				<td id="alternateMessage"></td>
			</tr>
		</table>
	</div>

				</td>
			</tr>

		</table>
	</div>

	<div class="borderDiv marB marR" style="float: left;">
		<table width="100%">
			<tr>
				<td><span class="smallTitle"><fmt:message
							key="emport.exportProjectTitle" />
				</span> <tag:help id="exportProject" /></td>
			</tr>

			<tr>
				<td><label for="projectName"><fmt:message
							key="emport.projectName" />
				</label> <input type="text" id="projectName" value="${instanceDescriptionHeader}" /><br />
				</td>

			</tr>
			<tr>
				<td><label for="projectDescription"><fmt:message
							key="emport.projectDescription" />
				</label><br /> <textarea id="projectDescription" rows="4" cols="50"></textarea>
				</td>
			</tr>
			<tr>
				<td><input type="checkbox" id="includePointValues" /> <label
					for="includePointValues"><fmt:message
							key="emport.includePointValues" />
				</label><br /> <input type="checkbox" id="includeUploadsFolder" /> <label
					for="includeUploadsFolder"><fmt:message
							key="emport.uploadsFolder" />
				</label><br /> <input type="checkbox" id="includeGraphicsFolder" /> <label
					for="includeGraphicsFolder"><fmt:message
							key="emport.graphicsFolder" />
				</label><br /></td>

			</tr>

			<tr>
				<td><label for="pointValuesMaxZip"><fmt:message
							key="emport.pointValuesMax" />
				</label> <select id="pointValuesMaxZip">
						<option value="100" />100
						</option>
						<option value="500" />500
						</option>
						<option value="1000" />1000
						</option>
						<option value="10000" />10000
						</option>
						<option value="100000" />100000
						</option>
						<option value="1000000" />1000000
						</option>
				</select></td>
			</tr>
			<tr>
				<td><spring:message code="emport.exportProjectInstruction" /> <input
					id="exportBtn" type="button"
					value="<spring:message code="emport.exportProject"/>"
					onclick="doExport()" /></td>
			</tr>
			<tbody></tbody>
			<tr>
				<td></td>
			</tr>

			<tr>
				<td colspan="2" align="center">
				<p></p>
				<div class="borderDiv marB marR" style="float: none;border-bottom-width: 0;border-left-width:0;border-right-width:0;">
				<p></p>
			<form method="POST" action="import_project.htm"
			enctype="multipart/form-data" onsubmit="return checkFileToImport();">
			<table width="100%">
				<tr>
					<td><span class="smallTitle"><fmt:message
								key="emport.importProjectTitle" />
					</span> <tag:help id="importProject" /></td>
				</tr>

				<tr>
					<td><spring:message code="emport.importProjectInstruction" /></td>
				</tr>

				<tr>
					<td><input type="file" name="importFile" id="importFile" /> <input
						id="importBtn" type="submit"
						value="<spring:message code="emport.send"/>" /></td>
				</tr>

				<tr>
					<td></td>
				</tr>
			</table>
		</form>

	</div>


				</td>
			</tr>


		</table>
	</div>

	<div style="clear: both;">
		<span class="formLabelRequired"><spring:message code="emport.data" />
		</span><br />
		<textarea rows="40" cols="150" id="emportData"></textarea>
	</div>
</tag:page>