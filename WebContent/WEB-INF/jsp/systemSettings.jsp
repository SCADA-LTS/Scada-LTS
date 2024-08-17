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
<%@page import="org.scada_lts.dao.SystemSettingsDAO"%>
<%@page import="org.scada_lts.utils.SystemSettingsUtils"%>
<%@page import="com.serotonin.mango.Common"%>
<%@page import="com.serotonin.mango.rt.event.AlarmLevels"%>
<%@page import="com.serotonin.mango.rt.event.type.EventType"%>
<%@page import="com.serotonin.mango.util.freemarker.MangoEmailContent"%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@ include file="/WEB-INF/jsp/include/highlight.jsp" %>


<tag:page dwr="SystemSettingsDwr" onload="init">
<link href="resources/js-ui/app/css/chunk-vendors.css" rel="stylesheet" type="text/css">
<link href="resources/js-ui/app/css/app.css" rel="stylesheet" type="text/css">

  <script type="text/javascript">
    var systemEventAlarmLevels = new Array();
    var auditEventAlarmLevels = new Array();
    
    function init() {
        SystemSettingsDwr.getSettings(function(settings) {
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_HOST %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_HOST %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PORT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PORT %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_ADDRESS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_ADDRESS %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_NAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_NAME %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_TLS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_TLS %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EMAIL_CONTENT_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EMAIL_CONTENT_TYPE %>"/>);
            smtpAuthChange();
            
            var alarmFunctions = [
                function(et) { return et.description; },
                function(et) {
                    var etid = et.typeId +"-"+ et.typeRef1;
                    var content = "<select id='alarmLevel"+ etid +"' ";
                    content += "onchange='updateAlarmLevel("+ et.typeId +", "+ et.typeRef1 +", this.value)'>";
                    content += "<option value='<c:out value="<%= AlarmLevels.NONE %>"/>'><fmt:message key="<%= AlarmLevels.NONE_DESCRIPTION %>"/></option>";
                    content += "<option value='<c:out value="<%= AlarmLevels.INFORMATION %>"/>'><fmt:message key="<%= AlarmLevels.INFORMATION_DESCRIPTION %>"/></option>";
                    content += "<option value='<c:out value="<%= AlarmLevels.URGENT %>"/>'><fmt:message key="<%= AlarmLevels.URGENT_DESCRIPTION %>"/></option>";
                    content += "<option value='<c:out value="<%= AlarmLevels.CRITICAL %>"/>'><fmt:message key="<%= AlarmLevels.CRITICAL_DESCRIPTION %>"/></option>";
                    content += "<option value='<c:out value="<%= AlarmLevels.LIFE_SAFETY %>"/>'><fmt:message key="<%= AlarmLevels.LIFE_SAFETY_DESCRIPTION %>"/></option>";
                    content += "</select> ";
                    content += "<img id='alarmLevelImg"+ etid +"' src='images/flag_green.png' style='display:none'>";
                    return content;
                }
            ];
            var alarmOptions = {
                cellCreator: function(options) {
                    var td = document.createElement("td");
                    td.className = (options.cellNum == 0 ? "formLabelRequired" : "formField");
                    return td;
                }
            };
            setEventTypeData("systemEventAlarmLevelsList", settings.systemEventTypes, alarmFunctions, alarmOptions,
                    systemEventAlarmLevels);
            setEventTypeData("auditEventAlarmLevelsList", settings.auditEventTypes, alarmFunctions, alarmOptions,
                    auditEventAlarmLevels);
            
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>", JSON.stringify(settings.<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>, null, 2));
            httpUseProxyChange();
            
            $set("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>", settings.<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN %>"/>", settings.<c:out value="<%= SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_LIMIT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_LIMIT %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH %>"/>", settings.<c:out value="<%= SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH %>"/>", settings.<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH %>"/>", settings.<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH %>"/>);

            setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>"), !settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>);
            setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>"), !settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/> || !settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>);
            var sel = $("<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>");
            <c:forEach items="${availableLanguages}" var="lang">
              sel.options[sel.options.length] = new Option("${lang.value}", "${lang.key}");
            </c:forEach>
            $set(sel, settings.<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>);
        });

<%--
    	SystemSettingsDwr.checkTypeDB(function(msg){
        	
        	if (msg == "derby") {
        		document.getElementById('radioDerby').checked = true;
			}
        	
        	if (msg == "mysql") {
        		document.getElementById('radioMysql').checked = true;
			}
        });
--%>
    
    }
    
    function setEventTypeData(listId, eventTypes, alarmFunctions, alarmOptions, alarmLevelsList) {
        dwr.util.addRows(listId, eventTypes, alarmFunctions, alarmOptions);
        
        var eventType, etid;
        for (var i=0; i<eventTypes.length; i++) {
            eventType = eventTypes[i];
            etid = eventType.typeId +"-"+ eventType.typeRef1;
            $set("alarmLevel"+ etid, eventType.alarmLevel);
            setAlarmLevelImg(eventType.alarmLevel, "alarmLevelImg"+ etid);
            alarmLevelsList[alarmLevelsList.length] = { i1: eventType.typeRef1, i2: eventType.alarmLevel };
        }
    }
    
    function dbSizeUpdate() {
        $set("databaseSize", "<fmt:message key="systemSettings.retrieving"/>");
        $set("filedataSize", "-");
        $set("totalSize", "-");
        $set("historyCount", "-");
        $set("topPoints", "-");
        $set("eventCount", "-");
        hide("refreshImg");
        SystemSettingsDwr.getDatabaseSize(function(data) {
            $set("databaseSize", data.databaseSize);
            $set("filedataSize", data.filedataSize +" ("+ data.filedataCount +" <fmt:message key="systemSettings.files"/>)");
            $set("totalSize", data.totalSize);
            $set("historyCount", data.historyCount);
            show("refreshImg");
            
            var cnt = "";
            for (var i=0; i<data.topPoints.length; i++) {
                cnt += "<a href='data_point_details.shtm?dpid="+ data.topPoints[i].pointId +"'>"+
                        data.topPoints[i].pointName +"</a> "+ data.topPoints[i].count +"<br/>";
                if (i == 3)
                    break;
            }
            $set("topPoints", cnt);
            $set("eventCount", data.eventCount);
        });
    }
    
    function saveEmailSettings() {
        SystemSettingsDwr.saveEmailSettings(
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_HOST %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PORT %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_ADDRESS %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_NAME %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_TLS %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.EMAIL_CONTENT_TYPE %>"/>"),
            function() {
                stopImageFader("saveEmailSettingsImg");
                setUserMessage("emailMessage", "<fmt:message key="systemSettings.emailSettingsSaved"/>");
            });
        setUserMessage("emailMessage");
        startImageFader("saveEmailSettingsImg");
    }
    
    function sendTestEmail() {
        SystemSettingsDwr.sendTestEmail(
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_HOST %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PORT %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_ADDRESS %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_NAME %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_TLS %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EMAIL_CONTENT_TYPE %>"/>"),
                function(result) {
                    stopImageFader("sendTestEmailImg");
                    if (result.exception)
                        setUserMessage("emailMessage", result.exception);
                    else
                        setUserMessage("emailMessage", result.message);
                });
        setUserMessage("emailMessage");
        startImageFader("sendTestEmailImg");
    }
    
    function updateAlarmLevel(eventTypeId, eventId, alarmLevel) {
        setAlarmLevelImg(alarmLevel, "alarmLevelImg"+ eventTypeId +"-"+ eventId);
        var list;
        if (eventTypeId == <c:out value="<%= EventType.EventSources.SYSTEM %>"/>)
            list = systemEventAlarmLevels;
        else
            list = auditEventAlarmLevels;
        getElement(list, eventId, "i1")["i2"] = alarmLevel;
    }
    
    function saveSystemEventAlarmLevels() {
        SystemSettingsDwr.saveSystemEventAlarmLevels(systemEventAlarmLevels, function() {
                stopImageFader("saveSystemEventAlarmLevelsImg");
                setUserMessage("systemEventAlarmLevelsMessage", "<fmt:message key="systemSettings.systemAlarmLevelsSaved"/>");
        });
        setUserMessage("systemEventAlarmLevelsMessage");
        startImageFader("saveSystemEventAlarmLevelsImg");
    }
    
    function saveAuditEventAlarmLevels() {
        SystemSettingsDwr.saveAuditEventAlarmLevels(auditEventAlarmLevels, function() {
                stopImageFader("saveAuditEventAlarmLevelsImg");
                setUserMessage("auditEventAlarmLevelsMessage", "<fmt:message key="systemSettings.auditAlarmLevelsSaved"/>");
        });
        setUserMessage("auditEventAlarmLevelsMessage");
        startImageFader("saveAuditEventAlarmLevelsImg");
    }
    
    function smtpAuthChange() {
        var auth = $("<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>").checked;
        setDisabled($("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>"), !auth);
        setDisabled($("<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>"), !auth);
    }
    
    function saveHttpSettings() {
        SystemSettingsDwr.saveHttpSettings(
                $get("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>"),
                function(a) {
                    stopImageFader("saveHttpSettingsImg");
                    if(a.messages && a.messages.length > 0) {
                        setUserMessage("httpMessage", a.messages[0].contextualMessage);
                    } else {
                        setUserMessage("httpMessage", "<fmt:message key="systemSettings.httpSaved"/>");
                    }
                });
        setUserMessage("httpMessage");
        startImageFader("saveHttpSettingsImg");
    }
    
    function httpUseProxyChange() {
        var proxy = $("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>").checked;
        setDisabled($("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>"), !proxy);
        setDisabled($("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>"), !proxy);
        setDisabled($("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>"), !proxy);
        setDisabled($("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>"), !proxy);
    }

    function workItemsReportingEnabledChange() {
        var workItemsReportingEnabled = $("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>").checked;
        if(!workItemsReportingEnabled) {
            $set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>", false);
            workItemsReportingItemsPerSecondEnabledChange();
        }
        setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>"), !workItemsReportingEnabled);
    }

    function workItemsReportingItemsPerSecondEnabledChange() {
        var workItemsReportingItemsPerSecondEnabled = $("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>").checked;
        if(!workItemsReportingItemsPerSecondEnabled) {
            $set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>", 0);
        }
        setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>"), !workItemsReportingItemsPerSecondEnabled);
    }
    
    function saveMiscSettings() {
        SystemSettingsDwr.saveMiscSettings(
                $get("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_LIMIT %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>"),
                function(response) {
                    stopImageFader("saveMiscSettingsImg");
                    if (response.hasMessages)
                        setUserMessage("miscMessage", response.messages[0].contextualMessage);
                    else {
                        setUserMessage("miscMessage", "<fmt:message key="systemSettings.miscSaved"/>");
                    }
                });
        setUserMessage("miscMessage");
        startImageFader("saveMiscSettingsImg");
    }

    function saveDataRetentionSettings() {
            SystemSettingsDwr.saveDataRetentionSettings(
                    $get("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>"),
                    $get("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>"),
                    $get("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>"),
                    $get("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>"),
                    1,
                    $get("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>"),
                    $get("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>"),
                    function() {
                        stopImageFader("saveDataRetentionSettingsImg");
                        setUserMessage("dataRetentionMessage", "<fmt:message key="systemSettings.dataRetentionSaved"/>");
                    });
            setUserMessage("dataRetentionMessage");
            startImageFader("saveDataRetentionSettingsImg");
        }
    
    function setUserMessage(type, msg) {
        if (msg)
            $set(type, msg);
        else
            $set(type, "");
    }
    
    function saveInfoSettings() {
        SystemSettingsDwr.saveInfoSettings("0",
                //$get("<c:out value="<%= SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>"),
                function() {
                    stopImageFader("saveInfoSettingsImg");
                    setUserMessage("infoMessage", "<fmt:message key="systemSettings.infoSaved"/>");
                });
        setUserMessage("infoMessage");
        startImageFader("saveInfoSettingsImg");
    }
    
    function newVersionCheck() {
        SystemSettingsDwr.newVersionCheck($get("<c:out value="<%= SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL %>"/>"),
                function(result) {
                    if (!result)
                        result = "<fmt:message key="systemSettings.upToDate"/>";
                    alert(result);
                }
        );
    }
    
    function purgeNow() {
        if (confirm("<fmt:message key="systemSettings.purgeDataPointStrategyConfirm"/>")) {
            SystemSettingsDwr.purgeNow(function() {
                stopImageFader("purgeNowImg");
                dbSizeUpdate();
            });
            startImageFader("purgeNowImg");
        }
    }
    
    function saveLangSettings() {
        SystemSettingsDwr.saveLanguageSettings($get("<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>"), function() {
            stopImageFader("saveLangSettingsImg");
            setUserMessage("langMessage", "<fmt:message key="systemSettings.langSaved"/>");
        });
        setUserMessage("langMessage");
        startImageFader("saveLangSettingsImg");
    }
    
    function checkPurgeAllData() {
        if (confirm("<fmt:message key="systemSettings.purgeDataConfirm"/>")) {
            setUserMessage("dataRetentionMessage", "<fmt:message key="systemSettings.purgeDataInProgress"/>");
            SystemSettingsDwr.purgeAllData(function(msg) {
                setUserMessage("dataRetentionMessage", msg);
                dbSizeUpdate();
            });
        }
    }
    
    function saveConfigDB() {
		verifyTypeDB();    	
    }

    function verifyTypeDB(){
    	SystemSettingsDwr.checkTypeDB(function(msg){
    		if (msg == "derby") {
    			
    			if(document.getElementById('radioDerby').checked) {
    			}
    			
    			if(document.getElementById('radioMysql').checked) {
    				useMysqlDB();
    			}
    			
    		}

    		if (msg == "mysql"){
    			
    			if(document.getElementById('radioDerby').checked) {
    				useDerbyDB();
    			}
    			
    			if(document.getElementById('radioMysql').checked) {
    			}
    			
    		}

    		if (msg == "mssql") {
    		}
    		
    	});	
    }
    
    function useDerbyDB() {
    	SystemSettingsDwr.useDerbyDB();
		SystemSettingsDwr.getAppServer(function(msg){
    		alert("<fmt:message key="systemSettings.reServer"/>" + " - " + msg);
		});
		
    }
    
    function useMysqlDB() {
    	SystemSettingsDwr.useMysqlDB();
    	SystemSettingsDwr.getAppServer(function(msg){
    		alert("<fmt:message key="systemSettings.reServer"/>" + " - " + msg);
		});
    }
    
    function useMssqlDB() {
    	SystemSettingsDwr.useMssqlDB();
    	SystemSettingsDwr.getAppServer(function(msg){
    		alert("<fmt:message key="systemSettings.reServer"/>" + " - " + msg);
		});
    }
    
    function dbBackup() {
    	alert("Not implemented !");
    }

    function refreshImages() {

        var pathArray = location.href.split( '/' );
        var protocol = pathArray[0];
        var host = pathArray[2];
        var appScada = pathArray[3];
        var myLocation;
        if (!myLocation) {
     	   myLocation = protocol + "//" + host + "/" + appScada + "/";
        }

        jQuery.ajax({
            type: 'GET',
            dataType: 'text',
            url:myLocation+"/api/resources/imagesRefresh",
            success: function(msg){
                alert("Success: the resource images has been refreshed");
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("Problem when refreshing assets:"+errorThrown.message);
            }
        });

    }

    /* CUSTOM CSS JAVASCRIPT */
    let customCssUrl = `./api/customcss/`;

    function showCssDialog() {
      let dialog = document.getElementById('css-editor-dialog');
      dialog.style.display = 'flex';
      initCustomCssData();
    }

    function hideCssDialog() {
      let dialog = document.getElementById('css-editor-dialog');
      dialog.style.display = 'none';
    }

    function saveCssSettings() {
      hideCssDialog();
      saveCustomCssConfig();
    }

    function initCustomCssData() {
      fetchCustomCssConfig().then((val) => {
        let res = JSON.parse(val);
        document.getElementById('cssEditor').innerHTML = res.content;
        updateCodeTextEscaped(res.content, '#cssHighlightingContent');
      });
    }

    function fetchCustomCssConfig() {
      return new Promise((resolve, reject) => {
        let req = new XMLHttpRequest();
        req.open('GET', customCssUrl, true);
        req.onload = () => {
          if (req.status === 200) {
            resolve(req.response);
          } else {
            reject(req.status);
          }
        };
        req.onerror = () => {
          reject(req.status);
        }
        req.send(null);
      });
    }

    function saveCustomCssConfig() {
      return new Promise((resolve, reject) => {
        let req = new XMLHttpRequest();
        req.open('POST', customCssUrl, true);
        req.setRequestHeader('Content-type', 'application/text');
        req.onload = () => {
          if (req.status === 200) {
            resolve(req.responseText);
          } else {
            reject(req.status);
          }
        };
        req.onerror = () => {
          reject(req.status);
        }
        req.send(document.getElementById('cssEditor').value);
      });
    }

    function toUiPerformanceId() {
        var uiPerformance = $get("uiPerformanceId");
        if(!uiPerformance) {
            uiPerformance = 1000;
        }
        $set("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>", uiPerformance);
    }
  </script>
  
  <div class="borderDivPadded marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.systemInformation"/></span>
          <tag:help id="systemInformation"/>
        </td>
      </tr>
    </table>
    <table>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.version"/></td>
        <td class="formField"><c:out value="<%= Common.getVersion() %>"/></td>
      </tr>
      <%-- 
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.notify"/></td>
        <td class="formField" valign="top">
          <select id="<c:out value="<%= SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL %>"/>">
            <option value="<c:out value="<%= SystemSettingsDAO.NOTIFICATION_LEVEL_STABLE %>"/>"><fmt:message key="systemSettings.notifyStable"/></option>
            <option value="<c:out value="<%= SystemSettingsDAO.NOTIFICATION_LEVEL_RC %>"/>"><fmt:message key="systemSettings.notifyRC"/></option>
            <option value="<c:out value="<%= SystemSettingsDAO.NOTIFICATION_LEVEL_BETA %>"/>"><fmt:message key="systemSettings.notifyBeta"/></option>
          </select>
          <tag:img png="accept" title="systemSettings.checkNow" onclick="newVersionCheck()"/>
        </td>
      </tr>
      --%>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.instanceDescription"/></td>
        <td align="center"><input type="button" value="<fmt:message key="systemSettings.setInNewUI"/>" onClick="location.href='app.shtm#/system-settings#system-info-settings'"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.databaseSize"/></td>
        <td class="formField">
          <span id="databaseSize"></span>
          <tag:img id="refreshImg" png="control_repeat_blue" onclick="dbSizeUpdate();" title="common.refresh"/>
          <tag:img id="purgeNowImg" png="bin" onclick="purgeNow()" title="systemSettings.purgeNow"/>
        </td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.filedataSize"/></td>
        <td class="formField" id="filedataSize"></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.totalSize"/></td>
        <td class="formField" id="totalSize"></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.historyCount"/></td>
        <td class="formField" id="historyCount"></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.topPoints"/></td>
        <td class="formField" id="topPoints"></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.eventCount"/></td>
        <td class="formField" id="eventCount"></td>
      </tr>
      <tr>
        <td colspan="2" id="infoMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
  <div class="borderDivPadded marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.systemAlarmLevels"/></span>
          <tag:help id="systemAlarmLevels"/>
        </td>
        <td align="right">
          <tag:img id="saveSystemEventAlarmLevelsImg" png="save" onclick="saveSystemEventAlarmLevels();"
                  title="common.save"/>
        </td>
      </tr>
    </table>
    <table>
      <tbody id="systemEventAlarmLevelsList"></tbody>
      <tr>
        <td colspan="2" id="systemEventAlarmLevelsMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
  <div class="borderDivPadded marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.auditAlarmLevels"/></span>
          <tag:help id="auditAlarmLevels"/>
        </td>
        <td align="right">
          <tag:img id="saveAuditEventAlarmLevelsImg" png="save" onclick="saveAuditEventAlarmLevels();"
                  title="common.save"/>
        </td>
      </tr>
    </table>
    <table>
      <tbody id="auditEventAlarmLevelsList"></tbody>
      <tr>
        <td colspan="2" id="auditEventAlarmLevelsMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
  <div class="borderDivPadded marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.languageSettings"/></span>
          <tag:help id="languageSettings"/>
        </td>
        <td align="right">
          <tag:img id="saveLangSettingsImg" png="save" onclick="saveLangSettings();" title="common.save"/>
        </td>
      </tr>
    </table>

    <table>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.systemLanguage"/></td>
        <td class="formField">
          <select id="<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>"></select>
        </td>
      </tr>
      <tr>
        <td colspan="2" id="langMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  <div class="borderDivPadded marB marR" style="clear:left;float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.emailSettings"/></span>
          <tag:help id="emailSettings"/>
        </td>
        <td align="right">
          <tag:img id="saveEmailSettingsImg" png="save" onclick="saveEmailSettings();" title="common.save"/>
          <tag:img id="sendTestEmailImg" png="email_go" onclick="sendTestEmail();" title="common.sendTestEmail"/>
        </td>
      </tr>
    </table>
    <table>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.smtpHost"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_HOST %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.smtpPort"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PORT %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.fromAddress"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_ADDRESS %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.fromName"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_NAME %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.auth"/></td>
        <td class="formField">
          <input id="<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>" type="checkbox" onclick="smtpAuthChange()"/>
        </td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.smtpUsername"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.smtpPassword"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>" type="password"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.tls"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_TLS %>"/>" type="checkbox"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.contentType"/></td>
        <td class="formField">
          <select id="<c:out value="<%= SystemSettingsDAO.EMAIL_CONTENT_TYPE %>"/>">
            <option value="<c:out value="<%= MangoEmailContent.CONTENT_TYPE_BOTH %>"/>"><fmt:message key="systemSettings.contentType.both"/></option>
            <option value="<c:out value="<%= MangoEmailContent.CONTENT_TYPE_HTML %>"/>"><fmt:message key="systemSettings.contentType.html"/></option>
            <option value="<c:out value="<%= MangoEmailContent.CONTENT_TYPE_TEXT %>"/>"><fmt:message key="systemSettings.contentType.text"/></option>
          </select>
        </td>
      </tr>
      
      <tr>
        <td colspan="2" id="emailMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
  <div class="borderDivPadded marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.httpSettings"/></span>
          <tag:help id="httpSettings"/>
        </td>
        <td align="right">
          <tag:img id="saveHttpSettingsImg" png="save" onclick="saveHttpSettings();" title="common.save"/>
        </td>
      </tr>
    </table>
    <table>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.useProxy"/></td>
        <td class="formField">
          <input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>" type="checkbox"
                  onclick="httpUseProxyChange()"/>
        </td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.proxyHost"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.proxyPort"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.proxyUsername"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>" type="text"/></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="systemSettings.proxyPassword"/></td>
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>" type="password"/></td>
      </tr>
      <tr>
        <td colspan="2" align="center"><fmt:message key="systemsettings.http.response.headers"/></td>
      </tr>
      <tr>
        <td colspan="2" align="center"><textarea placeholder="<fmt:message key="systemsettings.http.response.headers"/>" rows="5" cols="60" id="<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>"></textarea></td>
      </tr>
      <tr>
        <td colspan="2" id="httpMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
  <div class="borderDivPadded marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.dataRetentionSettings"/></span>
          <tag:help id="dataRetentionSettings"/>
        </td>
        <td align="right">
          <tag:img id="saveDataRetentionSettingsImg" png="save" onclick="saveDataRetentionSettings();" title="common.save"/>
        </td>
      </tr>
    </table>
    <table>
      <%--
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.groveLogging"/></td>
        <td class="formField"><input type="checkbox" id="<c:out value="<%= SystemSettingsDAO.GROVE_LOGGING %>"/>"/></td>
      </tr>
      --%>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.purgeEvents"/></td>
        <td class="formField">
          <input id="<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>" type="text" class="formShort"/>
          <select id="<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>">
            <tag:timePeriodOptions d="true" w="true" mon="true" y="true"/>
          </select>
        </td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.purgeReports"/></td>
        <td class="formField">
          <input id="<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>" type="text" class="formShort"/>
          <select id="<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>">
            <tag:timePeriodOptions d="true" w="true" mon="true" y="true"/>
          </select>
        </td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.futureDateLimit"/></td>
        <td class="formField">
          <input id="<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>" type="text" class="formShort"/>
          <select id="<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>">
            <tag:timePeriodOptions min="true" h="true"/>
          </select>
        </td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.valuesLimitForPurge"/></td>
        <td><input type="button" value="<fmt:message key="systemSettings.setInNewUI"/>" onClick="location.href='app.shtm#/system-settings#data-retention-settings'"/></td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <input type="button" value="<fmt:message key="systemSettings.purgeData"/>" onclick="checkPurgeAllData()" style="margin: 5px;"/>
        </td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <input type="button" value="<fmt:message key="systemSettings.purgeNow"/>" onclick="purgeNow()" style="margin: 5px;"/>
        </td>
      </tr>
      <tr>
        <td colspan="2" id="dataRetentionMessage" class="formError"></td>
      </tr>
    </table>
  </div>

    <div class="borderDivPadded marB marR" style="float:left">
      <table width="100%">
        <tr>
          <td>
            <span class="smallTitle"><fmt:message key="systemSettings.otherSettings"/></span>
            <tag:help id="otherSettings"/>
          </td>
          <td align="right">
            <tag:img id="saveMiscSettingsImg" png="save" onclick="saveMiscSettings();" title="common.save"/>
          </td>
        </tr>
      </table>
      <table>
        <tr>
          <td class="formLabelRequired"><fmt:message key="systemSettings.uiPerformance"/></td>
          <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>" type="number" class="formShort"/>
            <select id="uiPerformanceId" onchange="toUiPerformanceId()">
              <option value=""></option>
              <option value="1000"><fmt:message key="systemSettings.uiPerformance.veryHigh"/></option>
              <option value="2000"><fmt:message key="systemSettings.uiPerformance.high"/></option>
              <option value="5000"><fmt:message key="systemSettings.uiPerformance.med"/></option>
              <option value="10000"><fmt:message key="systemSettings.uiPerformance.low"/></option>
            </select>
          </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.misc.dataPointRuntimeValueSynchronized"/></td>
         <td class="formField">
           <select id="<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>">
             <option value="NONE"><fmt:message key="systemsettings.misc.dataPointRuntimeValueSynchronized.none"/></option>
             <option value="PARTIAL"><fmt:message key="systemsettings.misc.dataPointRuntimeValueSynchronized.partial"/></option>
             <option value="ALL"><fmt:message key="systemsettings.misc.dataPointRuntimeValueSynchronized.all"/></option>
           </select>
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.view.forceFullScreen"/></td>
         <td class="formField">
           <input type="checkbox" id="<c:out value="<%= SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE %>"/>" />
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.view.hideShortcutDisableFullScreen"/></td>
         <td class="formField">
           <input type="checkbox" id="<c:out value="<%= SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN %>"/>" />
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.event.pendingCacheEnabled"/></td>
         <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED %>"/>" type="checkbox" />
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.event.pendingLimit"/></td>
         <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_LIMIT %>"/>" type="number" class="formShort"/>
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.workitems.reporting.enabled"/></td>
         <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>" type="checkbox" onchange="workItemsReportingEnabledChange()"/>
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.workitems.reporting.itemspersecond.enabled"/></td>
         <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>" type="checkbox" onchange="workItemsReportingItemsPerSecondEnabledChange()"/>
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.workitems.reporting.itemspersecond.limit"/></td>
         <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>" type="number" class="formShort"/>
         </td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="systemsettings.threads.name.additional.length"/></td>
         <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH %>"/>" type="number" class="formShort"/>
         </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="systemsettings.webresource.graphics.path"/></td>
          <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH %>"/>" type="text" class="formShort" style="width: 300px;"/>
          </td>
          <td colspan="2" id="uploadsPathMessage" class="formError"></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="systemsettings.webresource.uploads.path"/></td>
          <td class="formField">
            <input id="<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH %>"/>" type="text" class="formShort" style="width: 300px;"/>
          </td>
          <td colspan="2" id="graphicsPathMessage" class="formError"></td>
        </tr>
        <tr>
         <td class="formLabelRequired"><fmt:message key="event.assign.enabled"/></td>
          <td class="formField">
           <input id="<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>" type="checkbox" />
          </td>
        </tr>
        <tr>
          <td colspan="2" id="miscMessage" class="formError"></td>
        </tr>
      </table>
    </div>
  
<%--
   <div class="borderDivPadded marB marR" style="float:left">
    <table align="center" "100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.dbConfiguration"/></span>
          <tag:help id="dbConfiguration"/>
        </td>
        <td align="right">
          <tag:img id="saveConfigurationDB" png="save" onclick="saveConfigDB();" title="common.save"/>
        </td>
      </tr>
      </table>
      <table align="center">
      <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.dbConfiguration.Derby"/></td>
        <td class="formField">
          <input id="radioDerby" name="db" type="radio"/>
        </td>
      </tr>
      
       <tr>
        <td class="formLabelRequired"><fmt:message key="systemSettings.dbConfiguration.Mysql"/></td>
        <td class="formField">
          <input id="radioMysql" name="db" type="radio"/>
        </td>
      </tr>
       <tr>
        <td class="formLabel"><fmt:message key="systemSettings.dbConfiguration.Mssql"/></td>
        <td class="formField">
          <input id="radioMssql" name="db" type="radio" disabled/>
        </td>
      </tr>
       
       <tr>
        <td colspan="2" align="center">
          <input type="button" value="<fmt:message key="systemSettings.dbBackup"/>" onclick="dbBackup()"/>
        </td>
      </tr>
      <tr>
        <td colspan="2" id="httpMessage" class="formError"></td>
      </tr>
    </table>
  </div>
--%>

  <div class="borderDivPadded marB marR" style="float:left">
       <table width="100%">
          <tr>
             <td>
               <span class="smallTitle">Cache images</span>
             </td>
          </tr>
          <tr>
             <td align="center">
               <input type="button" value="Refresh" onClick="refreshImages()"/>
             </td>
          </tr>
       </table>
  </div>

    <div class="borderDivPadded marB marR" style="clear:left;float:left">
         <table align="center" "100%">
           <tr>
             <td>
               <span class="smallTitle"><fmt:message key="systemSettings.newUI"/></span>
               <tag:help id="newUISettings"/>
             </td>
          </tr>
       </table>
       <table>
                  <tr>
                    <td class="formLabelRequired"><fmt:message key="systemSettings.smsDomain"/></td>
                    <td colspan="2" align="center"><input type="button" value="<fmt:message key="systemSettings.setInNewUI"/>" onClick="location.href='app.shtm#/system-settings#sms-domain-settings'"/></td>
                  </tr>
                  <tr>
                    <td class="formLabelRequired"><fmt:message key="systemSettings.amCharts"/></td>
                    <td colspan="2" align="center"><input type="button" value="<fmt:message key="systemSettings.setInNewUI"/>" onClick="location.href='app.shtm#/system-settings#aggregation-settings'"/></td>
                  </tr>
                  <tr>
                    <td class="formLabelRequired"><fmt:message key="systemSettings.defaultDataPointLoggingType"/></td>
                    <td colspan="2" align="center"><input type="button" value="<fmt:message key="systemSettings.setInNewUI"/>" onClick="location.href='app.shtm#/system-settings#default-logging-type-settings'"/></td>
                  </tr>
                  <tr>
                    <td class="formLabelRequired"><fmt:message key="systemSettings.environmentSettings"/></td>
                    <td colspan="2" align="center"><input type="button" value="<fmt:message key="systemSettings.setInNewUI"/>" onClick="location.href='app.shtm#/system-settings#scada-configuration'"/></td>
                  </tr>
                  <tr>
                    <td colspan="2" id="httpMessage" class="formError"></td>
                  </tr>
                </table>
  </div>

  <div class="borderDiv marB marR" style="float: left;">
        <table>
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="systemSettings.customCss.title"/></span>
              </td>
            </tr>
        </table>
        <table>
          <tr>
            <td>
              <button onclick="showCssDialog()"><fmt:message key="systemSettings.customCss.edit"/></button>
            </td>
          </tr>
        </table>
      </div>

  <div class="" style="float:left; color:white">
  #branchName

  <div id="css-editor-dialog">
        <div class="css-dialog-content">
          <div>
            <h2><fmt:message key="systemSettings.customCss.dialog.title"/></h2>
          </div>
          <div class="note">
            <fmt:message key="systemSettings.customCss.dialog.note"/>
          </div>
          <div class="css-dialog-editor">
            <textarea
              placeholder="Enter Code Here"
              id="cssEditor"
              class="hgl-editor"
              spellcheck="false"
              oninput="updateCodeTextEscaped(this.value, '#cssHighlightingContent');"
              onscroll="syncCodeScroll(this, '#cssHighlightingContent');">
            </textarea>
            <pre id="cssHighlighting" class="hgl-highlighting" aria-hidden="true">
              <code id="cssHighlightingContent" class="language-css">
              </code>
            </pre>
          </div>
          <div class="css-dialog-buttons">
          <table>
            <tr>
              <td>
                <button onclick="hideCssDialog()"><fmt:message key="common.cancel"/></button>
              </td>
              <td>
                <button onclick="saveCssSettings()"><fmt:message key="common.save"/></button>
              </td>
            </tr>
          </table>
          </div>
        </div>
      </div>


      <style>
      #css-editor-dialog {
        position: fixed;
        top: 0;
        left: 0;
        background-color: #00000082;
        width: 100%;
        height: 100%;
        display: none;
        flex-direction: column;
        justify-content: center;
        align-items: center;
      }
      .css-dialog-content {
        width: 650px;
        height: 90%;
        background-color: white;
        color: black;
        display: flex;
        flex-direction: column;
        border-radius: 5px;
        padding: 15px;
      }
      .css-dialog-buttons > table {
        float: right;
      }
      .css-dialog-buttons  td > button {
        margin: 0 10px;
        padding: 3px;
      }
      .css-dialog-editor {
        position: relative;
        height: 520px;
      }

      </style>
  
  
</tag:page>
<tag:newPageNotification href="./app.shtm#/system-settings" ref="systemSettingsNotification"/>