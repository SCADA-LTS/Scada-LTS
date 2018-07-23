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

<tag:page dwr="UsersProfilesDwr" onload="init">
  <script type="text/javascript">
    var userId = ${sessionUser.id};
    var editingUserProfileId;
    var dataSources;
    var watchlists;
    var views;
    var adminUser;
    
    function init() {
        UsersProfilesDwr.getInitData(function(data) {
           adminUser = true;
       
           show("userProfileList");
           show("userProfileNameRow");
           show("dataSources");
           show("watchlists");
           show("views");
           
           var i, j;
           for (i=0; i<data.profiles.length; i++) {
               appendUserProfile(data.profiles[i].id);
               updateUserProfile(data.profiles[i]);
           }
           
           var dshtml = "", id, dp;
           dataSources = data.dataSources;
           if (dataSources != null){
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
           }
           $("dataSourceList").innerHTML = dshtml;

           var wlhtml = "";
           watchlists = data.watchlists;
           if (watchlists != null){
	           for (i=0; i<watchlists.length; i++) {
	        	   if(watchlists[i].name == '<fmt:message key="common.newName"/>') // skip unnamed lists
	        		   continue;
	        	   
	        	   id = watchlists[i].id;
	               wlhtml += '<label for="wllist'+ id +'"> '+ watchlists[i].name +'</label><br/>';
	               wlhtml += '<div style="margin-left:25px;" id="wldiv'+ id +'">';
	                   wlhtml +=   '<table cellspacing="0" cellpadding="1">';
	                       wlhtml += '<tr>';
	                       wlhtml +=   '<td>';
	                       wlhtml +=     '<input type="radio" name="wl'+ id +'" id="wl'+ id +'/0" value="0" checked>';
	                       wlhtml +=             '<label for="wl'+ id +'"><fmt:message key="common.access.none"/></label> ';
	                       wlhtml +=     '<input type="radio" name="wl'+ id +'" id="wl'+ id +'/1" value="1">';
	                       wlhtml +=             '<label for="wl'+ id +'"><fmt:message key="common.access.read"/></label> ';
	                       wlhtml +=     '<input type="radio" name="wl'+ id +'" id="wl'+ id +'/2" value="2">';
	                       wlhtml +=             '<label for="wl'+ id +'"><fmt:message key="common.access.set"/></label>';
	                       wlhtml +=   '</td>';
	                       wlhtml += '</tr>';
	                   wlhtml +=   '</table>';
	               wlhtml += '</div>';
	           }
           }
           $("watchlistsList").innerHTML = wlhtml;

           var vwhtml = "";
           views = data.views;
           if (views != null){
	           for (i=0; i<views.length; i++) {
	        	   id = views[i].id;
	               vwhtml += '<label for="vvwist'+ id +'"> '+ views[i].name +'</label><br/>';
	               vwhtml += '<div style="margin-left:25px;" id="vwdiv'+ id +'">';
	                   vwhtml +=   '<table cellspacing="0" cellpadding="1">';
	                       vwhtml += '<tr>';
	                       vwhtml +=   '<td>';
	                       vwhtml +=     '<input type="radio" name="vw'+ id +'" id="vw'+ id +'/0" value="0" checked>';
	                       vwhtml +=             '<label for="vw'+ id +'"><fmt:message key="common.access.none"/></label> ';
	                       vwhtml +=     '<input type="radio" name="vw'+ id +'" id="vw'+ id +'/1" value="1">';
	                       vwhtml +=             '<label for="vw'+ id +'"><fmt:message key="common.access.read"/></label> ';
	                       vwhtml +=     '<input type="radio" name="vw'+ id +'" id="vw'+ id +'/2" value="2">';
	                       vwhtml +=             '<label for="vw'+ id +'"><fmt:message key="common.access.set"/></label>';
	                       vwhtml +=   '</td>';
	                       vwhtml += '</tr>';
	                   vwhtml +=   '</table>';
	               vwhtml += '</div>';
	           }
           }
           $("viewsList").innerHTML = vwhtml;

           
        });
    }
    
    function showUserProfile(userProfileId) {
        if (editingUserProfileId)
            stopImageFader($("u"+ editingUserProfileId +"Img"));
        editingUserProfileId = userProfileId;
        UsersProfilesDwr.getUserProfile(userProfileId, showUserProfileCB);
        startImageFader($("u"+ editingUserProfileId +"Img"));
    }
    
    function showUserProfileCB(userProfile) {
        show($("deleteButton"));
        show($("userProfileDetails"));
        $set("userProfileName", userProfile.name);

        if (dataSources != null){
	        var i, j, dscb, dp;
	        for (i=0; i<dataSources.length; i++) {
	            dscb = $("ds"+ dataSources[i].id);
	            dscb.checked = false;
	            dataSourceChange(dscb);
	            for (j=0; j<dataSources[i].points.length; j++)
	                $set("dp"+ dataSources[i].points[j].id, "0");
	        }
	        
	        // Turn on the data sources to which the user has permission.
	        for (i=0; i<userProfile.dataSourcePermissions.length; i++) {
	            dscb = $("ds"+ userProfile.dataSourcePermissions[i]);
	            dscb.checked = true;
	            dataSourceChange(dscb);
	        }
	        
	        // Update the data point permissions.
	        for (i=0; i<userProfile.dataPointPermissions.length; i++)
	            $set("dp"+ userProfile.dataPointPermissions[i].dataPointId, userProfile.dataPointPermissions[i].permission);
        }

     	// Update the watchlist permissions.
        for (i=0; i<userProfile.watchlistPermissions.length; i++)
            $set("wl"+ userProfile.watchlistPermissions[i].id, userProfile.watchlistPermissions[i].permission);

     	// Update the view permissions.
      	for (i=0; i<userProfile.viewPermissions.length; i++)
            $set("vw"+ userProfile.viewPermissions[i].id, userProfile.viewPermissions[i].permission);
        
        
        setUserProfileMessage();
        updateUserProfileImg();
    }
    
    function saveUserProfile() {
		startImageFader($("saveButton"));
    	
    	setUserProfileMessage();
        // Create the list of allowed data sources and data point permissions.
        var i, j;
        var dsPermis = new Array();
        var dpPermis = new Array();
        var dpval;
        if (dataSources != null){
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
        }

      //populate watchlist permissions paremeters
        var wlPermis = new Array();
        var wlval;
        if (watchlists != null){
	      	for (i=0; i<watchlists.length; i++) {
	 			wlval = $get("wl"+ watchlists[i].id);
	              
		          if (wlval == "1" || wlval == "2") {
		        	  wlPermis.push({id: watchlists[i].id, permission: parseInt(wlval)});
		          }
	        }
        }

        //populate view permissions paremeters
        var vwPermis = new Array();
        var vwval;
        if (views != null){
	        for (i=0; i<views.length; i++) {
	 			vwval = $get("vw"+ views[i].id);
	              
		          if (vwval == "1" || vwval == "2") {
		        	  vwPermis.push({id: views[i].id, permission: parseInt(vwval)});
		          }
	        }
        }
        
        UsersProfilesDwr.saveUserAdmin(editingUserProfileId, $get("userProfileName"), dsPermis, dpPermis, wlPermis, vwPermis, saveUserProfileCB);
    }
    
    function saveUserProfileCB(response) {
		stopImageFader($("saveButton"));
        if (response.hasMessages)
            showDwrMessages(response.messages, "genericMessages");
        else if (!adminUser)
            setUserProfileMessage("<fmt:message key="userProfiles.saved"/>");
        else {
            if (editingUserProfileId == <c:out value="<%= Common.NEW_ID %>"/>) 
            {
                stopImageFader($("u"+ editingUserProfileId +"Img"));
                editingUserProfileId = response.data.userProfileId;
                appendUserProfile(editingUserProfileId);
                startImageFader($("u"+ editingUserProfileId +"Img"));
                setUserProfileMessage("<fmt:message key="userProfiles.added"/>");
            } else {
                setUserProfileMessage("<fmt:message key="userProfiles.saved"/>");
                stopImageFader($("u"+ editingUserProfileId +"Img"));
        	}
            UsersProfilesDwr.getUserProfile(editingUserProfileId, updateUserProfile)
        }
    }
    
    function setUserProfileMessage(message) {
        if (message)
            $set("userProfileMessage", message);
        else {
            $set("userProfileMessage");
            hideContextualMessages("userProfileDetails");
            hideGenericMessages("genericMessages");
        }
    }
    
    function appendUserProfile(userProfileId) {
        createFromTemplate("u_TEMPLATE_", userProfileId, "userProfileTable");
    }
    
    function updateUserProfile(userProfile) {
        $("u"+ userProfile.id +"UserProfileName").innerHTML = userProfile.name;
        setUserImg(true, userProfile.disabled, $("u"+ userProfile.id +"Img"));
        console.log("u"+ editingUserProfileId +"Img")
    }
    
    function updateUserProfileImg() {
        setUserImg(true, $get("disabled"), $("userImg"));
    }
    
    function dataSourceChange(dscb) {
        display("dsps"+ dscb.id.substring(2), !dscb.checked);
    }
    
    function deleteUserProfile() {
        if (confirm("<sst:i18n key="userProfiles.deleteConfirm" escapeDQuotes="true"/>")) {
        	var profileId = editingUserProfileId;
            startImageFader("deleteButton");
            UsersProfilesDwr.deleteUsersProfile(profileId, function(response) {
                stopImageFader("deleteButton");
                
                if (response.hasMessages) {
                	alert(response.messages[0].genericMessage);
          		}
                stopImageFader("u"+ profileId +"Img");
                $("userProfileTable").removeChild($("u"+ profileId));
                hide("userProfileDetails");
                editingUserProfileId = null;
              
            });
        }
    }
  </script>
  
  <table class="subPageHeader">
    <tr>
      <td valign="top" id="userProfileList" style="display:none;">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="userProfiles.title"/></span>
              </td>
              <td align="right"><tag:img png="user_add" onclick="showUserProfile(${applicationScope['constants.Common.NEW_ID']})"
                      title="userProfiles.add" id="u${applicationScope['constants.Common.NEW_ID']}Img"/></td>
            </tr>
          </table>
          <table id="userProfileTable">
            <tbody id="u_TEMPLATE_" onclick="showUserProfile(getMangoId(this))" class="ptr" style="display:none;"><tr>
              <td><tag:img id="u_TEMPLATE_Img" png="user_green" title="userProfile.user"/></td>
              <td class="link" id="u_TEMPLATE_UserProfileName"></td>
            </tr></tbody>
          </table>
        </div>
      </td>
      
      <td valign="top" style="display:none;" id="userProfileDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><tag:img id="userImg" png="user_green" title="userProfile.user"/>
                <fmt:message key="userProfiles.details"/></span>
              </td>
              <td align="right">
                <tag:img png="save" id="saveButton" onclick="saveUserProfile();" title="common.save"/>
                <tag:img png="delete" id="deleteButton" onclick="deleteUserProfile();" title="common.delete" style="display:none;"/>
              </td>
            </tr>
          </table>
          
          <table><tbody id="genericMessages"></tbody></table>
          
          <table>
            <tr>
              <td colspan="2" id="userProfileMessage" class="formError"></td>
            </tr>
            <tr id="userProfileNameRow" style="display:none;">
              <td class="formLabelRequired"><fmt:message key="userProfiles.name"/></td>
              <td class="formField"><input id="userProfileName" type="text"/></td>
            </tr>
            <tbody id="dataSources" style="display:none;">
              <tr><td class="horzSeparator" colspan="2"></td></tr>
              <tr id="dataSources">
                <td class="formLabelRequired"><fmt:message key="userProfiles.dataSources"/></td>
                <td class="formField" id="dataSourceList"></td>
              </tr>
              <tr id="watchlists">
                <td class="formLabelRequired"><fmt:message key="header.watchLists"/></td>
                <td class="formField" id="watchlistsList"></td>
              </tr>
              <tr id="views">
                <td class="formLabelRequired"><fmt:message key="views.title"/></td>
                <td class="formField" id="viewsList"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
    </tr>
  </table>
</tag:page>