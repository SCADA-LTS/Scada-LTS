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
<%@page import="com.serotonin.mango.vo.event.ScheduledEventVO"%>
<%@page import="org.joda.time.DateTimeConstants"%>
<%@page import="br.org.scadabr.rt.scripting.context.*"%>
<c:set var="NEW_ID"><%= Common.NEW_ID %></c:set>

<tag:page dwr="ScriptsDwr,DataSourceEditDwr" onload="init">


  <script type="text/javascript">

  	var myLocation = getAppLocation();
    var urlGetDataPoints = "api/datapoint/getAll";
    function executeScript() {

        let payload = {}
        payload.id = -1;
        payload.script = $get("script");

        let toSave = pointsContext.convertToSave();
        payload.pointsOnContext = convertPointsOnContext(toSave);
        payload.datapointContext = objectsContextArray[0] ? objectsContextArray[0].value : "";
        payload.datasourceContext = objectsContextArray[1] ? objectsContextArray[1].value : "";

        jQuery.ajax({
            url: myLocation+"api/scripts/execute-test",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(payload),
            success: function() {
              setUserMessage("<spring:message code="script.execute.success"/> ")
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(XMLHttpRequest);
                console.log(errorThrown);
                setUserMessage("<spring:message code="script.execute.error"/> "+XMLHttpRequest.responseText);
            }
        });
    };

    var pointsContext;
    var objectsContextArray = new Array();

    function init() {
        ScriptsDwr.getScripts(initCB);
        createContextualMessageNode("contextContainer", "context");

        document.getElementById("loader").style.display = "none";
                    document.body.style.overflow="visible";
    }

    function convertPointsOnContext(toSave) {
        let pointsOnContext = [];
        for(let i = 0; i < toSave.length; i++) {
            let entry = toSave[i];
            let dataPoint = pointsContext.getContextPointById(entry.key);
            if(dataPoint) {
                let dataPointXid = dataPoint.xid ? dataPoint.xid : dataPoint.pointXid;
                let varName = entry.value;
                let object = {
                    dataPointXid: dataPointXid,
                    varName: varName
                };
                pointsOnContext[pointsOnContext.length] = object;
            } else {
                throw new Error('dataPoint is undefined!');
            }
        }
        return pointsOnContext;
    }

    function initCB(scripts) {
    	for (var i=0; i<scripts.length; i++) {
            appendScript(scripts[i].id);
            updateScript(scripts[i]);
        }
    }

    function appendScript(seId) {
        updateFromTemplate("se_TEMPLATE_", seId, "scriptsTable");
    }

    function updateScript(se) {
        $("se"+ se.id +"Name").innerHTML = escapeHtml(se.name);
        //setScheduledEventImg(se.disabled, $("se"+ se.id +"Img"));
    }

    var editingScript;
    function showScript(seId) {
    	if (editingScript)
            stopImageFader($("se"+ editingScript.id +"Img"));
        hideContextualMessages("scriptDetails");

        ScriptsDwr.getScript(seId, function(response) {
        	 if (!editingScript)
                 show($("scriptDetails"));

            editingScript = response.data.script;
            setValueInNode('xid', editingScript.xid);
            setValueInNode('name', editingScript.name);
            setValueInNode('script', editingScript.script);

            pointsContext = new ScriptPointsContext(new DataPointsSelect({
                selectHtmlId: "allPointsList",
                placeholderTextSingle: "<spring:message code='chosen.selector.selectPoint'/>",
                excludePointsArray: editingScript.pointsOnContext,
                pointsArray: response.data.dataPoints
            }));

            clearObjectsTable();
		 	for (var i=0; i<editingScript.objectsOnContext.length; i++)
		 		 objectsContextArray.push({key: editingScript.objectsOnContext[i].key, value: editingScript.objectsOnContext[i].value});

	        writeObjectsContextArray();
	        setUserMessage();
        });

		startImageFader($("se"+ seId +"Img"));

        if (seId == ${NEW_ID}) {
        	hide($("deleteScriptImg"));
        	hide($("executeScriptImg"));
        }
        else {
        	 show($("deleteScriptImg"));
        	 show($("executeScriptImg"));
        	 jQuery('#executeScriptImg').click(function() {
        		 executeScript();
        	 });
        }
    }

    function saveScript() {
        ScriptsDwr.saveScript(editingScript.id,$get("xid"), $get("name"),
                $get("script"),pointsContext.convertToSave(),objectsContextArray,
                function(response) {
		        	if (response.hasMessages)
		                showDwrMessages(response.messages);
		            else {
		                hideContextualMessages("scriptDetails");
		                if (editingScript.id == ${NEW_ID}) {
		                    stopImageFader($("se"+ editingScript.id +"Img"));
		                    editingScript.id = response.data.seId;
		                    appendScript(editingScript.id);
		                    startImageFader($("se"+ editingScript.id +"Img"));
		                    show($("deleteScriptImg"));
		                    show($("executeScriptImg"));
		                    jQuery('#executeScriptImg').click(function() {
		                    	executeScript();
		                	});
		                }
		                setUserMessage("<spring:message code="scripts.saved"/>");
		                ScriptsDwr.getScripts(function(scripts) {
		                    init(scripts);
		                });
		            }
        		}
        );
    }

    function deleteScript() {
    	ScriptsDwr.deleteScript(editingScript.id, function() {
            stopImageFader($("se"+ editingScript.id +"Img"));
            $("scriptsTable").removeChild($("se"+ editingScript.id));
            hide($("scriptDetails"));
            editingScript = null;
        });
    }

    function validateScript() {
        //alert('Not implemented!');
        //("pointProperties");
        //DataSourceEditDwr.validateScript($get("script"), createContextArray(), $get("dataTypeId"), validateScriptCB);
    }

    function validateScriptCB(response) {
        showDwrMessages(response.messages);
    }

    function clearObjectsTable() {
    	objectsContextArray.length = 0;
        table = $("objectsContextTable");
		for(i = 1; i < table.rows.length; i++) {
			id = table.rows[i].cells[0].title;
			$(id+"ObjectVarName").disabled = false;
			$(id+"ObjectVarName").value = "val_"+id;
			$(id+"ObjectAdd").checked = false;
		}

    }

    function addObjectToContext(objectId, checked) {
        objectNameField = $(objectId+"ObjectVarName");
        if(checked) {
			varName = ""+objectNameField.value;
			if(varName == null || varName.trim().length == 0){
				alert('<spring:message code="scripts.objectsContext.invalidVar"/>');
				$(objectId+"ObjectAdd").checked = false;
				return;
			}

        	objectNameField.disabled = true;
        	objectsContextArray.push({"key":objectId, "value": varName });
        } else {
        	objectNameField.disabled = false;
        	 for (var i=objectsContextArray.length-1; i>=0; i--) {
                 if (objectsContextArray[i].key == objectId)
                	 objectsContextArray.splice(i, 1);
             }
        }
    }

    function writeObjectsContextArray() {
    	for (var i= 0; i < objectsContextArray.length; i++) {
        	o = objectsContextArray[i];
    		objectNameField = $(o.key+"ObjectVarName");
    		objectNameField.disabled = true;
    		objectNameField.value = o.value;
    		$(o.key+"ObjectAdd").checked = true;
        }
    }

    function setUserMessage(message) {
        if (message) {
            show($("userMessage"));
            $("userMessage").innerHTML = message;
        }
        else
            hide($("userMessage"));
    }
  </script>

  <style>body{ overflow:hidden;}</style>

  <div id="loader" style="background-color:rgba(0, 0, 0, 0.7); height: 100%; position:absolute; width:100%;">
  	<div style="color:#ffffff; font-size:30px; text-align:center; margin-top:40vh;"><img src="images/ajax-loader.gif" style="height:20px;"/> LOADING... </div>
  </div>

  <table id="scriptsList" class="subPageHeader">
    <tr>
      <td valign="top">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><spring:message code="scripts.title"/></span>
                <tag:help id="scripts"/>
              </td>
              <td align="right"><tag:img png="report_add" title="scripts.addSe"
                      onclick="showScript(${NEW_ID})" id="se${NEW_ID}Img"/></td>
            </tr>
          </table>
          <table id="scriptsTable">
            <tbody id="se_TEMPLATE_" onclick="showScript(getMangoId(this))" class="ptr" style="display:none">
              <tr>
                <td><tag:img id="se_TEMPLATE_Img" png="report" title="scripts.se"/></td>
                <td class="link" id="se_TEMPLATE_Name"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>

      <td valign="top" style="display:none; position: auto;" id="scriptDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td><span class="smallTitle"><spring:message code="scripts.seDetails"/></span></td>
              <td align="right">
                <%-- <tag:img id="executeScriptImg" png="exclamation" title="common.run"/> --%>
                <tag:img png="save" onclick="saveScript();" title="common.save"/>
                <tag:img id="deleteScriptImg" png="delete" onclick="deleteScript();" title="common.delete"/>
              </td>
            </tr>
          </table>

          <table>
          	<tr>
	            <td class="formLabelRequired"><spring:message code="dsEdit.points.name"/></td>
	            <td class="formField"><input type="text" id="name"/></td>
          	</tr>

            <tr>
              <td class="formLabelRequired"><spring:message code="common.xid"/></td>
              <td class="formField"><input type="text" id="xid"/></td>
            </tr>

            <tr>
			    <td class="formLabelRequired"><spring:message code="scripts.pointsContext"/></td>
			    <td class="formField">
			      <select id="allPointsList"></select>
			      <tag:img png="add" onclick="pointsContext.addPointToContext();" title="common.add"/>

			      <table cellspacing="1" id="contextContainer">
			        <tbody id="contextTableEmpty" style="display:none;">
			          <tr><th colspan="4"><spring:message code="dsEdit.meta.noPoints"/></th></tr>
			        </tbody>
			        <tbody id="contextTableHeaders" style="display:none;">
			          <tr class="smRowHeader">
			            <td><spring:message code="dsEdit.meta.pointName"/></td>
			            <td><spring:message code="pointHierarchySLTS.xid"/></td>
			            <td><spring:message code="dsEdit.pointDataType"/></td>
			            <td><spring:message code="dsEdit.meta.var"/></td>
			            <td></td>
			          </tr>
			        </tbody>
			        <tbody id="contextTable"></tbody>
			      </table>
			    </td>
			</tr>

			<tr>
			    <td class="formLabelRequired"><spring:message code="scripts.objectsContext"/></td>
			    <td class="formField">

			      <table cellspacing="1" id="objectsContextTable">
				      <tbody id="objectsContextTable">
				        		<tr class="smRowHeader">
									<td> <spring:message code="scripts.objectsContext.name"/> </td>
									<td> <spring:message code="scripts.objectsContext.var"/> </td>
									<td> <spring:message code="scripts.objectsContext.add"/> </td>
									<td> &nbsp; </td>
								</tr>
				        		<c:forEach var="object" items="<%=br.org.scadabr.rt.scripting.context.ScriptContextObject.Type.values()%>">
								<tr style="width: 100%;" class="smRow">
									<td title="${object.id}" style="display: none;"> </td>
									<td> <spring:message code="${object.key}"/>  </td>
									<td> <input id="${object.id}ObjectVarName" type="text" value="var_${object.id}"/> </td>
									<td> <input id="${object.id}ObjectAdd" type="checkbox" onchange="addObjectToContext(${object.id}, this.checked)"/> </td>
									<td> <tag:help id="${object.help}"/> </td>
								</tr>
				        		</c:forEach>
				       	</tbody>
			      </table>
			    </td>
			</tr>

            <tr>

    			<td class="formLabelRequired">
      				<spring:message code="dsEdit.meta.script"/>

      				<tag:img id="executeScriptImg" png="cog_go" title="common.run"/>
      				<%-- <tag:img png="accept" onclick="validateScript();" title="dsEdit.meta.validate"/> --%>
    			</td>

    			<td class="formField"><textarea id="script" rows="10" cols="50"/></textarea></td>
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
    <tag:newPageNotification href="./app.shtm#/scripts" ref="scriptsNotification"/>
  </table>
</tag:page>
