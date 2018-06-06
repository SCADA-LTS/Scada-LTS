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

  	var pathArray = location.href.split( '/' );
  	var protocol = pathArray[0];
  	var host = pathArray[2];
  	var port = location.port;
   	var appScada = pathArray[3];
  	var url = protocol + '//' + host;
  	var myLocation;
  	if (!myLocation) {
   		myLocation = location.protocol + "//" + location.host + "/" + appScada + "/";
  	}
    var urlGetDataPoints = "/api/datapoint/getAll";
    function executeScript(){
    	var xid = jQuery("#xid");
    	// saveScript() nie zdarzy zapisac !!!
    	jQuery.ajax({
    		url: myLocation+"/script/execute/"+xid[0].value,
    		type:"POST",
    		success: function(){
              setUserMessage("<fmt:message key="script.execute.success"/> ")
        	},
        	error: function(XMLHttpRequest, textStatus, errorThrown) {
        		console.log(textStatus);
        		console.log(XMLHttpRequest);
        		setUserMessage("<fmt:message key="script.execute.error"/> "+XMLHttpRequest.responseText);
        	}
    	});
    };

    var pointsArray = new Array();
    var contextArray = new Array();
    var objectsContextArray = new Array();

    function init() {
        ScriptsDwr.getScripts(initCB);
        getPointsCB();

        jQuery("#allPointsList").chosen({
       		allow_single_deselect: true,
			placeholder_text_single: " ",
			search_contains: true,
			width: "400px"
		});
    }

    function getPointsCB()
    {
        jQuery.ajax({
            type: "GET",
        	dataType: "text",
        	url:myLocation+urlGetDataPoints,
        	success: function(points){
            points = JSON.parse(points);
            for(i = 0; i < points.length; i++) {
                point = points[i];
                pointsArray[i] = {
                        "id":point.id,
                        "name":point.name,
                        "type":1
                        };
            }
            document.getElementById("loader").style.display = "none";
            document.body.style.overflow="visible";
        	},
        	error: function(XMLHttpRequest, textStatus, errorThrown) {
        	  console.log(textStatus);
        	}
        });
    }

    function initCB(scripts) {
    	for (var i=0; i<scripts.length; i++) {
            appendScript(scripts[i].id);
            updateScript(scripts[i]);
        }
    }

    function appendScript(seId) {
        createFromTemplate("se_TEMPLATE_", seId, "scriptsTable");
    }

    function updateScript(se) {
        $("se"+ se.id +"Name").innerHTML = se.name;
        //setScheduledEventImg(se.disabled, $("se"+ se.id +"Img"));
    }

    var editingScript;
    function showScript(seId) {
    	if (editingScript)
            stopImageFader($("se"+ editingScript.id +"Img"));
        hideContextualMessages("scriptDetails");

        ScriptsDwr.getScript(seId, function(s) {
        	 if (!editingScript)
                 show($("scriptDetails"));

            editingScript = s;
            $set("xid", s.xid);
            $set("name", s.name);
            $set("script", s.script);

            contextArray.length = 0;
            for (var i=0; i<s.pointsOnContext.length; i++)
                addToContextArray(s.pointsOnContext[i].key, s.pointsOnContext[i].value);
            writeContextArray();

            clearObjectsTable();
		 	for (var i=0; i<s.objectsOnContext.length; i++)
		 		 objectsContextArray.push({key: s.objectsOnContext[i].key, value: s.objectsOnContext[i].value});

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
                $get("script"),createContextArray(),objectsContextArray,
                function(response) {
		        	if (response.hasMessages)
		                showDwrMessages(response.messages);
		            else {
		                if (editingScript.id == ${NEW_ID}) {
		                    stopImageFader($("se"+ editingScript.id +"Img"));
		                    editingScript.id = response.data.seId;
		                    appendScript(editingScript.id);
		                    startImageFader($("se"+ editingScript.id +"Img"));
		                    show($("deleteScriptImg"));
		                    show($("executeScriptImg"));
		                    jQuery('#executeScriptImg').click(function() {
		                    	executeScriptImg();
		                	});
		                }
		                setUserMessage("<fmt:message key="scripts.saved"/>");
		                ScriptsDwr.getScript(editingScript.id, updateScript);
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

    function addPointToContext() {
        var pointId = $get("allPointsList");
        addToContextArray(pointId, "p"+ pointId);
        writeContextArray();
    }

    function addToContextArray(pointId, scriptVarName) {
        var data = getElement(pointsArray, pointId);
        if (data) {
            // Missing names imply that the point was deleted, so ignore.
            contextArray[contextArray.length] = {
                pointId : pointId,
                pointName : data.name,
                pointType : data.type,
                scriptVarName : scriptVarName
            };
        }
    }

    function removeFromContextArray(pointId) {
        for (var i=contextArray.length-1; i>=0; i--) {
            if (contextArray[i].pointId == pointId)
                contextArray.splice(i, 1);
        }
        writeContextArray();
    }

    function writeContextArray() {
        dwr.util.removeAllRows("contextTable");
        if (contextArray.length == 0) {
            show($("contextTableEmpty"));
            hide($("contextTableHeaders"));
        }
        else {
            hide($("contextTableEmpty"));
            show($("contextTableHeaders"));
            dwr.util.addRows("contextTable", contextArray,
                [
                    function(data) { return data.pointName; },
                    function(data) { return data.pointType; },
                    function(data) {
                            return "<input type='text' value='"+ data.scriptVarName +"' class='formShort' "+
                                    "onblur='updateScriptVarName("+ data.pointId +", this.value)'/>";
                    },
                    function(data) {
                            return "<img src='images/bullet_delete.png' class='ptr' "+
                                    "onclick='removeFromContextArray("+ data.pointId +")'/>";
                    }
                ],
                {
                    rowCreator:function(options) {
                        var tr = document.createElement("tr");
                        tr.className = "smRow"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                        return tr;
                    }
                });
        }
        updatePointsList();
    }

    function updatePointsList() {
        dwr.util.removeAllOptions("allPointsList");
        var availPoints = new Array();
        for (var i=0; i<pointsArray.length; i++) {
            var found = false;
            for (var j=0; j<contextArray.length; j++) {
                if (contextArray[j].pointId == pointsArray[i].id) {
                    found = true;
                    break;
                }
            }
            if (!found)
                availPoints[availPoints.length] = pointsArray[i];
        }
        dwr.util.addOptions("allPointsList", availPoints, "id", "name");
        jQuery("#allPointsList").trigger('chosen:updated');
    }

    function updateScriptVarName(pointId, scriptVarName) {
        for (var i=contextArray.length-1; i>=0; i--) {
            if (contextArray[i].pointId == pointId)
                contextArray[i].scriptVarName = scriptVarName;
        }
    }

    function createContextArray() {
        var context = new Array();
        for (var i=0; i<contextArray.length; i++) {
            context[context.length] = {
                key : contextArray[i].pointId,
                value : contextArray[i].scriptVarName
            };
        }
        return context;
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
				alert('<fmt:message key="scripts.objectsContext.invalidVar"/>');
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

    jQuery(document).ready(function(){
    	(function($) {
			loadjscssfile("resources/jQuery/plugins/chosen/chosen.min.css","css");
			loadjscssfile("resources/jQuery/plugins/chosen/chosen.jquery.min.js","js");
    	})(jQuery);
    });


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
                <span class="smallTitle"><fmt:message key="scripts.title"/></span>
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

      <td valign="top" style="display:none;" id="scriptDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td><span class="smallTitle"><fmt:message key="scripts.seDetails"/></span></td>
              <td align="right">
                <%-- <tag:img id="executeScriptImg" png="exclamation" title="common.run"/> --%>
                <tag:img png="save" onclick="saveScript();" title="common.save"/>
                <tag:img id="deleteScriptImg" png="delete" onclick="deleteScript();" title="common.delete"/>
              </td>
            </tr>
          </table>

          <table>
          	<tr>
	            <td class="formLabelRequired"><fmt:message key="dsEdit.points.name"/></td>
	            <td class="formField"><input type="text" id="name"/></td>
          	</tr>

            <tr>
              <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
              <td class="formField"><input type="text" id="xid"/></td>
            </tr>

            <tr>
			    <td class="formLabelRequired"><fmt:message key="scripts.pointsContext"/></td>
			    <td class="formField">
			      <select id="allPointsList"></select>
			      <tag:img png="add" onclick="addPointToContext();" title="common.add"/>

			      <table cellspacing="1" id="contextContainer">
			        <tbody id="contextTableEmpty" style="display:none;">
			          <tr><th colspan="4"><fmt:message key="dsEdit.meta.noPoints"/></th></tr>
			        </tbody>
			        <tbody id="contextTableHeaders" style="display:none;">
			          <tr class="smRowHeader">
			            <td><fmt:message key="dsEdit.meta.pointName"/></td>
			            <td><fmt:message key="dsEdit.pointDataType"/></td>
			            <td><fmt:message key="dsEdit.meta.var"/></td>
			            <td></td>
			          </tr>
			        </tbody>
			        <tbody id="contextTable"></tbody>
			      </table>
			    </td>
			</tr>

			<tr>
			    <td class="formLabelRequired"><fmt:message key="scripts.objectsContext"/></td>
			    <td class="formField">

			      <table cellspacing="1" id="objectsContextTable">
				      <tbody id="objectsContextTable">
				        		<tr class="smRowHeader">
									<td> <fmt:message key="scripts.objectsContext.name"/> </td>
									<td> <fmt:message key="scripts.objectsContext.var"/> </td>
									<td> <fmt:message key="scripts.objectsContext.add"/> </td>
									<td> &nbsp; </td>
								</tr>
				        		<c:forEach var="object" items="<%=br.org.scadabr.rt.scripting.context.ScriptContextObject.Type.values()%>">
								<tr style="width: 100%;" class="smRow">
									<td title="${object.id}" style="display: none;"> </td>
									<td> <fmt:message key="${object.key}"/>  </td>
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
      				<fmt:message key="dsEdit.meta.script"/>

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
  </table>
</tag:page>
