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
<%@page import="org.scada_lts.workdomain.event.EventExporter"%>
<%@page import="com.serotonin.mango.Common"%>
<%@page import="com.serotonin.mango.rt.event.AlarmLevels"%>
<%@page import="com.serotonin.mango.rt.event.type.EventType"%>
<%@page import="com.serotonin.mango.util.freemarker.MangoEmailContent"%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>


<tag:page dwr="SystemSettingsDwr" onload="init">
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

            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_HOST %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_HOST %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PORT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PORT %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_VIRTUAL %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_VIRTUAL %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_USERNAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_USERNAME %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PASSWORD %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PASSWORD %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_EX_NAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_EX_NAME %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_Q_NAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_Q_NAME %>"/>);
            alarmTypeChange();
            if( "<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>" == 2 ||  "<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>" == 3) {
                testExportConnection();
            }

            
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>);
            httpUseProxyChange();
            
            $set("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>);
         	           
            $set("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>);

            $set("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>);
            $set("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>);
            
         // DBH [2018-09-12]: Init the data purge CRON field with the value stored into the database
            $set("<c:out value="<%= SystemSettingsDAO.DATA_PURGE_CRON %>"/>", settings.<c:out value="<%= SystemSettingsDAO.DATA_PURGE_CRON %>"/>);
         
            $set("<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>", settings.<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>);
            
            var sel = $("<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>");
            <c:forEach items="${availableLanguages}" var="lang">
              sel.options[sel.options.length] = new Option("${lang.value}", "${lang.key}");
            </c:forEach>
            $set(sel, settings.<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>);
        });
    
    	SystemSettingsDwr.checkTypeDB(function(msg){
        	
        	if (msg == "derby") {
        		document.getElementById('radioDerby').checked = true;
			}
        	
        	if (msg == "mysql") {
        		document.getElementById('radioMysql').checked = true;
			}
        });
    
    
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

    function alarmTypeChange() {
        var aType = $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>");
        if (aType == 1) {
            hide("alarmExportFields");
        } else {
            show("alarmExportFields");
        }
    }

    function saveAlarmExportSettings() {
        SystemSettingsDwr.saveAlarmExportSettings(
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_HOST %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PORT %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_VIRTUAL %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_USERNAME %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PASSWORD %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_EX_NAME %>"/>"),
            $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_Q_NAME %>"/>"),
            function() {
                stopImageFader("saveAlarmExportSettingsImg");
                setUserMessage("saveAlarmExportSettingsMessage", "Saved Export Settings");
        });
        setUserMessage("saveAlarmExportSettingsMessage");
        startImageFader("saveAlarmExportSettingsImg");
    }

    function testExportConnection() {
        SystemSettingsDwr.testExportConnection(
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_HOST %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PORT %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_VIRTUAL %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_USERNAME %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PASSWORD %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_EX_NAME %>"/>"),
                $get("<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_Q_NAME %>"/>"),
                function(msg) {
                    var exportConnectionIcon = document.getElementById('testExportSettingsImg');
                    if (msg == "Connected") {
                        exportConnectionIcon.src = "images/database_go.png";
                    } else {
                        exportConnectionIcon.src = "images/database_stop.png";
                        alert("<fmt:message key="systemSettings.reServer"/>" + " Event Export Settings - RabbitMQ broker " + msg);
                    }

            });

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
                function() {
                    stopImageFader("saveHttpSettingsImg");
                    setUserMessage("httpMessage", "<fmt:message key="systemSettings.httpSaved"/>");
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
    
    function saveMiscSettings() {
   		if (checkCronFormat()) {   		
	        SystemSettingsDwr.saveMiscSettings(
	                $get("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>"),
	                $get("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>"),
	                $get("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>"),
	                $get("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>"),
	                $get("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>"),
	                1,
	                $get("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>"),
	                $get("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>"),
	                $get("<c:out value="<%= SystemSettingsDAO.DATA_PURGE_CRON %>"/>"),
	                function() {
	                    stopImageFader("saveMiscSettingsImg");
	                    setUserMessage("miscMessage", "<fmt:message key="systemSettings.miscSaved"/>");
	                });
	        setUserMessage("miscMessage");
	        startImageFader("saveMiscSettingsImg");
   		} else {
   			stopImageFader("saveMiscSettingsImg");
   			setUserMessage("miscMessage", "<fmt:message key="systemSettings.miscCronFormatError"/>");
   		}
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
        reportsCount = document.getElementById("reportPurgePeriods").value;
        reportsType = document.getElementById("reportPurgePeriodType").options[document.getElementById("reportPurgePeriodType").selectedIndex].text;
        eventsCount = document.getElementById("eventPurgePeriods").value;
        eventsType = document.getElementById("eventPurgePeriodType").options[document.getElementById("eventPurgePeriodType").selectedIndex].text;
        confirmMsg = "This will purge: " +
        "\n-all events before " + eventsCount + " " + eventsType +
        "\n-all reports before " + reportsCount + " " + reportsType +
        "\n-all data points values according to point properties" +
        "\n Are you sure?";
        if (confirm(confirmMsg)) {
            SystemSettingsDwr.purgeNow(function() {
                stopImageFader("purgeNowImg");
                dbSizeUpdate();
            });
            startImageFader("purgeNowImg");
        }
    }

    function purgeEvents() {
        count = document.getElementById("eventPurgePeriods").value;
        dateTypes = document.getElementById("eventPurgePeriodType");
        type = dateTypes.options[dateTypes.selectedIndex].text;
        if (confirm("This will purge all events before " + count + " " + type +". Are you sure?")) {
            SystemSettingsDwr.purgeEvents(function() {
                stopImageFader("purgeNowImg");
                dbSizeUpdate();
            });
            startImageFader("purgeNowImg");
        }
    }

    function purgeReports() {
        count = document.getElementById("reportPurgePeriods").value;
        dateTypes = document.getElementById("reportPurgePeriodType");
        type = dateTypes.options[dateTypes.selectedIndex].text;
        if (confirm("This will purge all reports before " + count + " " + type +". Are you sure?")) {
                SystemSettingsDwr.purgeReports(function() {
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
            setUserMessage("miscMessage", "<fmt:message key="systemSettings.purgeDataInProgress"/>");
            SystemSettingsDwr.purgeAllData(function(msg) {
                setUserMessage("miscMessage", msg);
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
            
    function checkCronFormat() {
    	var cronString = $get("<c:out value="<%= SystemSettingsDAO.DATA_PURGE_CRON %>"/>");
    	var cronRegEx = RegExp("^\\s*($|#|\\w+\\s*=|(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|\/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|\/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|\/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|\/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[01]?\\d|2[0-3])(?:(?:-|\/|\\,)(?:[01]?\\d|2[0-3]))?(?:,(?:[01]?\\d|2[0-3])(?:(?:-|\/|\\,)(?:[01]?\\d|2[0-3]))?)*)\\s+(\\?|\\*|(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|\/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?(?:,(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|\/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?)*)\\s+(\\?|\\*|(?:[1-9]|1[012])(?:(?:-|\/|\\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\\?|\\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\\s+(\\?|\\*|(?:[0-6])(?:(?:-|\/|\\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\/|\\,|#)(?:[0-6]))?(?:L)?)*|\\?|\\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\\s)+(\\?|\\*|(?:|\\d{4})(?:(?:-|\/|\\,)(?:|\\d{4}))?(?:,(?:|\\d{4})(?:(?:-|\/|\\,)(?:|\\d{4}))?)*))$");
    	var isCorrect = cronRegEx.test(cronString);
    	return isCorrect;
    }
    
    
    
  </script>
  
  <div class="borderDiv marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.systemInformation"/></span>
          <tag:help id="systemInformation"/>
        </td>
        <td align="right">
          <tag:img id="saveInfoSettingsImg" png="save" onclick="saveInfoSettings();" title="common.save"/>
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
        <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>" type="text"/></td>
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
  
  <div class="borderDiv marB marR" style="float:left">
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
  
  <div class="borderDiv marB marR" style="float:left">
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

  <div class="borderDiv marB marR" style="float:left">
    <table width="100%">
      <tr>
        <td>
          <span class="smallTitle"><fmt:message key="systemSettings.eventExport"/></span>
          <tag:help id="systemEventExport"/>
        </td>
        <td align="right">
          <tag:img id="testExportSettingsImg" png="database_go" onclick="testExportConnection();" title="Test Connection"/>
          <tag:img id="saveAlarmExportSettingsImg" png="save" onclick="saveAlarmExportSettings();" title="common.save"/>
        </td>
        <td colspan="2" id="saveAlarmExportSettingsMessage" class="formError"></td>
      </tr>
    </table>
    <table>
      <tbody id="alarmExportSettings"></tbody>
      <tr>
        <td>
            <select id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_TYPE %>"/>"  onchange="alarmTypeChange()">
                <option value="<c:out value="<%= EventExporter.DEFAULT %>"/>"><fmt:message key="systemSettings.eventExport.eType.default"/></option>
                <option value="<c:out value="<%= EventExporter.RABBIT_MQ %>"/>"><fmt:message key="systemSettings.eventExport.eType.rabbitMq"/></option>
                <option value="<c:out value="<%= EventExporter.SCADA_AND_RABBBIT %>"/>"><fmt:message key="systemSettings.eventExport.eType.all"/></option>
            </select>
        </td>
      </tr>
      <tbody id="alarmExportFields" style="display:none;">
        <tr>
          <td><fmt:message key="systemSettings.eventExport.host"/></td>
          <td><input type="text" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_HOST %>"/>"></input></td>
        </tr>
        <tr>
          <td><fmt:message key="systemSettings.eventExport.port"/></td>
          <td><input type="number" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PORT %>"/>"></input></td>
        </tr>
        <tr>
          <td><fmt:message key="systemSettings.eventExport.virtualHost"/></td>
          <td><input type="text" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_VIRTUAL %>"/>"></input></td>
        </tr>
        <tr>
          <td><fmt:message key="systemSettings.eventExport.username"/></td>
          <td><input type="text" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_USERNAME %>"/>"></input></td>
        </tr>
        <tr>
          <td><fmt:message key="systemSettings.eventExport.password"/></td>
          <td><input type="password" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_PASSWORD %>"/>"></input></td>
        </tr>
        <tr>
          <td><fmt:message key="systemSettings.eventExport.exchangeName"/></td>
          <td><input type="text" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_EX_NAME %>"/>"></input></td>
        </tr>
        <tr>
          <td><fmt:message key="systemSettings.eventExport.queueName"/></td>
          <td><input type="text" id="<c:out value="<%= SystemSettingsDAO.ALARM_EXPORT_Q_NAME %>"/>"></input></td>
        </tr>
      </tbody>
    </table>
  </div>

  <div class="borderDiv marB marR" style="float:left">
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
  <div class="borderDiv marB marR" style="clear:left;float:left">
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
  
  <div class="borderDiv marB marR" style="float:left">
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
        <td colspan="2" id="httpMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
  <div class="borderDiv marB marR" style="float:left">
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
          <select id="<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>">
            <option value="2000"><fmt:message key="systemSettings.uiPerformance.high"/></option>
            <option value="5000"><fmt:message key="systemSettings.uiPerformance.med"/></option>
            <option value="10000"><fmt:message key="systemSettings.uiPerformance.low"/></option>
          </select>
        </td>
      </tr>
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
        <td>
            <input type="button" value="Purge now" onclick="purgeEvents()"/>
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
        <td>
          <input type="button" value="Purge now" onclick="purgeReports()"/>
        </td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <input type="button" value="<fmt:message key="systemSettings.purgeData"/>" onclick="checkPurgeAllData()"/>
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
        <td class="formLabelRequired"><fmt:message key="systemSettings.purgeCron"/></td>
        <td class="formField">
          <input id="<c:out value="<%= SystemSettingsDAO.DATA_PURGE_CRON %>"/>" type="text" />
        </td>
      </tr>
      <tr>
      <tr>
        <td colspan="2" id="miscMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  
   <div class="borderDiv marB marR" style="float:left">
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
<%--
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
   --%>   
      <tr>
        <td colspan="2" id="httpMessage" class="formError"></td>
      </tr>
    </table>
  </div>
  <div class="borderDiv marB marR" style="float:left">
       <table width="100%">
          <tr>
             <td>
               <span class="smallTitle">Cache images</span>
             </td>
          </tr>
          <tr>
             <td>
               <button onClick="refreshImages()">Refresh</button>
             </td>
          </tr>
       </table>
  </div>

  <div class="borderDiv marB marR" style="clear:left;float:left">
    <table>
    <tr>
      <td>ScadaLTS: #Major.#Minor.#Release.#Build </td>
    </tr>
    </table>
  </div>

  <div class="" style="float:left; color:white">
  #branchName
  </div>
  
  
</tag:page>