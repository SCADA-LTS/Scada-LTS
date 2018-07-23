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

<tag:page dwr="UsersDwr" onload="init">
  <script type="text/javascript">
    var userId = ${sessionUser.id};
    var editingUserId;
    var dataSources;
    var adminUser;
    
    function init() {
    	
    	UsersDwr.getInitData(function(data) {
            if (data.admin) {
            	show("usersProfilesListTable");
                adminUser = true;
            
            	
                show("userList");
                show("usernameRow");
                show("administrationRow");
                show("disabledRow");
                show("deleteImg");
                show("sendTestEmailImg");
                
            	
                var i, j;
                for (i=0; i<data.users.length; i++) {
                    appendUser(data.users[i].id);
                    updateUser(data.users[i]);
                }

                var usersProfileHtml = "", userProfileId, userProfileName;
                usersProfileHtml += "<option value=" + <c:out value="<%= Common.NEW_ID %>"/> + ">"+ "<fmt:message key="userProfiles.none"/>" + "</option>";
                
                for (k=0; k<data.usersProfiles.length; k++) {
                	userProfileId = data.usersProfiles[k].id;
                	userProfileName = data.usersProfiles[k].name;
                	usersProfileHtml += "<option value=" + userProfileId + ">"+ userProfileName + "</option>";
                }
                $("usersProfilesList").innerHTML = usersProfileHtml;
                
            	
                var dshtml = "", id, dp;
                dataSources = data.dataSources;
                for (i=0; i<dataSources.length; i++) {
                    id = "ds"+ dataSources[i].id;
                    dshtml += '<input type="checkbox" id="'+ id +'" onclick="dataSourceChange(this)">';
                    dshtml += '<label for="'+ id +'"> '+ dataSources[i].name +'</label><br/>';
                    dshtml += '<div style="margin-left:25px;" id="dsps'+ dataSources[i].id +'">';
                    if (dataSources[i].points.length > 0) {
                        dshtml +=   '<table cellspacing="0" cellpadding="1">';
                        for (j=0; j<dataSources[i].points.length; j++) {
                            dp = dataSources[i].points[j];
                            dshtml += '<tr>';
                            dshtml +=   '<td class="formLabelRequired">'+ dp.name +'</td>';
                            dshtml +=   '<td>';
                            dshtml +=     '<input type="radio" name="dp'+ dp.id +'" id="dp'+ dp.id +'/0" value="0">';
                            dshtml +=             '<label for="dp'+ dp.id +'/0"><fmt:message key="common.access.none"/></label> ';
                            dshtml +=     '<input type="radio" name="dp'+ dp.id +'" id="dp'+ dp.id +'/1" value="1">';
                            dshtml +=             '<label for="dp'+ dp.id +'/1"><fmt:message key="common.access.read"/></label> ';
                            if (dp.settable) {
                                dshtml +=     '<input type="radio" name="dp'+ dp.id +'" id="dp'+ dp.id +'/2" value="2">';
                                dshtml +=             '<label for="dp'+ dp.id +'/2"><fmt:message key="common.access.set"/></label>';
                            }
                            dshtml +=   '</td>';
                            dshtml += '</tr>';
                        }
                        dshtml +=   '</table>';
                    }
                    dshtml += '</div>';
                }
                $("dataSourceList").innerHTML = dshtml;
            }
            else {
            	
                // Not an admin user.
                adminUser = false;
                editingUserId = data.user.id;
                showUserCB(data.user);
                hide("usersProfilesListTable");
            }
        });
    }
    
    function showUser(userId, hideMsg) {
    	
    	if (hideMsg)
            setUserMessage();
    	
    	
        if (editingUserId)
            stopImageFader($("u"+ editingUserId +"Img"));
        editingUserId = userId;
        UsersDwr.getUser(userId, showUserCB);
        startImageFader($("u"+ editingUserId +"Img"));
    	
    }
    
    function showUserCB(user) {
    	
        show($("userDetails"));
        $set("username", user.username);
        $set("password", user.password);
        $set("email", user.email);
        $set("phone", user.phone);
        $set("administrator", user.admin);
        $set("disabled", user.disabled);
        $set("receiveAlarmEmails", user.receiveAlarmEmails);
        $set("receiveOwnAuditEvents", user.receiveOwnAuditEvents);

    	
        if(user.id != <c:out value="<%= Common.NEW_ID %>"/>) {
        	 $set("usersProfilesList", user.userProfile);
        	 //console.log("User profile: " + user.userProfile);
        }

    	
        if (adminUser) {
            // Turn off all data sources and set all data points to 'none'.
            var i, j, dscb, dp;
            for (i=0; i<dataSources.length; i++) {
                dscb = $("ds"+ dataSources[i].id);
                dscb.checked = false;
                dataSourceChange(dscb);
                for (j=0; j<dataSources[i].points.length; j++)
                    $set("dp"+ dataSources[i].points[j].id, "0");
            }
            
            // Turn on the data sources to which the user has permission.
            for (i=0; i<user.dataSourcePermissions.length; i++) {
                dscb = $("ds"+ user.dataSourcePermissions[i]);
                dscb.checked = true;
                dataSourceChange(dscb);
            }
            
            // Update the data point permissions.
            for (i=0; i<user.dataPointPermissions.length; i++)
                $set("dp"+ user.dataPointPermissions[i].dataPointId, user.dataPointPermissions[i].permission);
            stopImageFader($("u"+ user.id +"Img"));
        	
        }

        //setUserMessage();
    	
        updateUserImg();
    }

    function setDataSourcesNone() {
        var i;
        for (i=0; i<dataSources.length; i++) {
            dscb = $("ds"+ dataSources[i].id);
            dscb.checked = false;
            dataSourceChange(dscb);
            for (j=0; j<dataSources[i].points.length; j++)
                $set("dp"+ dataSources[i].points[j].id, "0");
        }
    }

    function hideDataSources() {
    	hide("dataSources");
    }

    function showDataSources() {
    	show("dataSources");
    }
    
    function checkProfile(){
    	var profile = document.getElementById("usersProfilesList");
    	if(profile.options[profile.selectedIndex].value > 0){
    		hideDataSources();  
    	}else{
    		//setDataSourcesNone();
    		showDataSources();
    	}
    }
    
    function saveUser() {
 		
		startImageFader($("saveImg"));
    	
		setUserMessage();
        if (adminUser) {
    		startImageFader($("u"+ editingUserId +"Img"));
           // Create the list of allowed data sources and data point permissions.
            var i, j;
            var dsPermis = new Array();
            var dpPermis = new Array();
            var dpval;
            
            // If a profile is select, reset other permissions on DSs.
            if($get("usersProfileList") == <c:out value="<%= Common.NEW_ID %>"/>)
            {
        		setDataSourcesNone();
            }
            
            for (i=0; i<dataSources.length; i++) {
                if ($("ds"+ dataSources[i].id).checked)
                    dsPermis[dsPermis.length] = dataSources[i].id;
                else {
                    for (j=0; j<dataSources[i].points.length; j++) {
                        dpval = $get("dp"+ dataSources[i].points[j].id);
                        if (dpval == "1" || dpval == "2")
                            dpPermis[dpPermis.length] = {dataPointId: dataSources[i].points[j].id, permission: dpval};
                    }
                }
            }
            
            
            UsersDwr.saveUserAdmin(editingUserId, $get("username"), $get("password"), $get("email"), $get("phone"), 
                    $get("administrator"), $get("disabled"), $get("receiveAlarmEmails"), $get("receiveOwnAuditEvents"),
                    dsPermis, dpPermis, $get("usersProfilesList"), saveUserCB);
        }
        else
            UsersDwr.saveUser(editingUserId, $get("password"), $get("email"), $get("phone"),
                    $get("receiveAlarmEmails"), $get("receiveOwnAuditEvents"), $get("usersProfilesList"), saveUserCB);
     
    }
    
    function saveUserCB(response) {
        stopImageFader($("saveImg"));
        if (response.hasMessages)
            showDwrMessages(response.messages, "genericMessages");
        else if (!adminUser)
            setUserMessage("<fmt:message key="users.dataSaved"/>");
        else {
            if (editingUserId == <c:out value="<%= Common.NEW_ID %>"/>) {
                stopImageFader($("u"+ editingUserId +"Img"));
                editingUserId = response.data.userId;
                appendUser(editingUserId);
                startImageFader($("u"+ editingUserId +"Img"));
                setUserMessage("<fmt:message key="users.added"/>");
                UsersDwr.getUser(editingUserId, updateUser);
	            showUser(editingUserId, false);
            }
            else {
	                setUserMessage("<fmt:message key="users.saved"/>");
		            UsersDwr.getUser(editingUserId, updateUser);
		            showUser(editingUserId, false);
                }
            
        }
    }
    
    function sendTestEmail() {
        UsersDwr.sendTestEmail($get("email"), $get("username"), function(result) {
            stopImageFader($("sendTestEmailImg"));
            if (result.exception)
                setUserMessage(result.exception);
            else
                setUserMessage(result.message);
        });
        startImageFader($("sendTestEmailImg"));
    }
    
    function setUserMessage(message) {
        if (message){
            $set("userMessage", message);
        //console.log("show message - " + message);
        }
        else {
            $set("userMessage");
            //console.log("hide messages");
            hideContextualMessages("userDetails");
            hideGenericMessages("genericMessages");
        }
    }
    
    function appendUser(userId) {
        createFromTemplate("u_TEMPLATE_", userId, "usersTable");
    }
    
    function updateUser(user) {
        $("u"+ user.id +"Username").innerHTML = user.username;
        setUserImg(user.admin, user.disabled, $("u"+ user.id +"Img"));
    }
    
    function updateUserImg() {
    	
        var admin = $get("administrator");
        if (adminUser) {
            if (admin) {
                	$("usersProfilesList").disabled = true;
                	
            }
            else {
	                $("usersProfilesList").disabled = false;
	            	
            }
            checkProfile();	
        	
        }
        setUserImg(admin, $get("disabled"), $("userImg"));
    	
    }
    
    function dataSourceChange(dscb) {
        display("dsps"+ dscb.id.substring(2), !dscb.checked);
    }
    
    function deleteUser() {
        if (confirm("<sst:i18n key="users.deleteConfirm" escapeDQuotes="true"/>")) {
        	var userId = editingUserId;
            startImageFader("deleteImg");
            UsersDwr.deleteUser(userId, function(response) {
                stopImageFader("deleteImg");
                
                if (response.hasMessages)
                	setUserMessage(response.messages[0].genericMessage);
                else {
                    stopImageFader("u"+ userId +"Img");
                    $("usersTable").removeChild($("u"+ userId));
                    hide("userDetails");
                    editingUserId = null;
                }
            });
        }
    }
  </script>
  
  <table class="subPageHeader">
    <tr>
      <td valign="top" id="userList" style="display:none;">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="users.title"/></span>
                <tag:help id="userAdministration"/>
              </td>
              <td align="right"><tag:img png="user_add" onclick="showUser(${applicationScope['constants.Common.NEW_ID']}, false)"
                      title="users.add" id="u${applicationScope['constants.Common.NEW_ID']}Img"/></td>
            </tr>
          </table>
          <table id="usersTable">
            <tbody id="u_TEMPLATE_" onclick="showUser(getMangoId(this), true)" class="ptr" style="display:none;"><tr>
              <td><tag:img id="u_TEMPLATE_Img" png="user_green" title="users.user"/></td>
              <td class="link" id="u_TEMPLATE_Username"></td>
            </tr></tbody>
          </table>
        </div>
      </td>
      
      <td valign="top" style="display:none;" id="userDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><tag:img id="userImg" png="user_green" title="users.user"/>
                <fmt:message key="users.details"/></span>
              </td>
              <td align="right">
                <tag:img id="saveImg" png="save" onclick="saveUser();" title="common.save"/>
                <tag:img id="deleteImg" png="delete" onclick="deleteUser();" title="common.delete" style="display:none;"/>
                <tag:img id="sendTestEmailImg" png="email_go" onclick="sendTestEmail();" title="common.sendTestEmail"
                        style="display:none;"/>
              </td>
            </tr>
          </table>
          
          <table><tbody id="genericMessages"></tbody></table>
          
          <table>
            <tr>
              <td colspan="2" id="userMessage" class="formError"></td>
            </tr>
            <tr id="usernameRow" style="display:none;">
              <td class="formLabelRequired"><fmt:message key="users.username"/></td>
              <td class="formField"><input id="username" type="text"/></td>
            </tr>
            <tr>
              <td class="formLabelRequired"><fmt:message key="users.newPassword"/></td>
              <td class="formField"><input id="password" type="text"/></td>
            </tr>
            <tr>
              <td class="formLabelRequired"><fmt:message key="users.email"/></td>
              <td class="formField"><input id="email" type="text" class="formLong"/></td>
            </tr>
            <tr>
              <td class="formLabel"><fmt:message key="users.phone"/></td>
              <td class="formField"><input id="phone" type="text"/></td>
            </tr>
            <tr id="administrationRow" style="display:none;">
              <td class="formLabelRequired"><fmt:message key="common.administrator"/></td>
              <td class="formField"><input id="administrator" type="checkbox" onclick="updateUserImg();"/></td>
            </tr>
            <tr id="disabledRow" style="display:none;">
              <td class="formLabelRequired"><fmt:message key="common.disabled"/></td>
              <td class="formField"><input id="disabled" type="checkbox" onclick="updateUserImg();"/></td>
            </tr>
            <tr>
              <td class="formLabelRequired"><fmt:message key="users.receiveAlarmEmails"/></td>
              <td class="formField"><select id="receiveAlarmEmails"><tag:alarmLevelOptions/></select></td>
            </tr>
            <tr>
              <td class="formLabelRequired"><fmt:message key="users.receiveOwnAuditEvents"/></td>
              <td class="formField"><input id="receiveOwnAuditEvents" type="checkbox"/></td>
            </tr>
            <tbody id="usersProfilesListTable" style="display:none;">
            <tr>
              <td class="formLabel"><fmt:message key="userProfiles.selectName"/></td>
              <td class="formField"><select id="usersProfilesList" onchange="checkProfile()">
              </select></td>
            </tr>
            </div>
            
            <tbody id="dataSources" style="display:none;">
              <tr><td class="horzSeparator" colspan="2"></td></tr>
              <tr id="dataSources">
                <td class="formLabelRequired"><fmt:message key="users.dataSources"/></td>
                <td class="formField" id="dataSourceList"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
    </tr>
  </table>
</tag:page>