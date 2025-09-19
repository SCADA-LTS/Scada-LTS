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
<%@ page import="org.scada_lts.dao.SystemSettingsDAO" %>
<%@ page import="com.serotonin.mango.Common" %>
<%@ page import="com.serotonin.mango.rt.event.AlarmLevels" %>
<%@ page import="com.serotonin.mango.rt.event.type.EventType" %>
<%@ page import="com.serotonin.mango.util.freemarker.MangoEmailContent" %>
<%@ page import="com.serotonin.mango.vo.DataPointVO" %>

<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@ include file="/WEB-INF/jsp/include/highlight.jsp" %>

<tag:page dwr="SystemSettingsDwr" onload="init">
    <!-- Vendor app styles (existing) -->
    <link href="resources/js-ui/app/css/chunk-vendors.css" rel="stylesheet" type="text/css">
    <link href="resources/js-ui/app/css/app.css" rel="stylesheet" type="text/css">

    <!-- ===== Sidebar & layout styles ===== -->
    <style>
        :root { --ss-top: 0px; }

        #ss-sidebar{
            position: absolute;
            left: 0;
            top: var(--ss-top);
            width: 260px;
            height: calc(100vh - var(--ss-top));
            overflow: auto;
            padding: 12px;
            background: var(--slts-color-secondary-lighter);
            border-right: 1px solid #ddd;
            z-index: 10;
        }

        #ss-content{
            margin-left: 260px;
            padding: 8px 12px 12px;
        }

        #ss-sections-list{ list-style:none; margin:10px 0 0; padding:0; }
        #ss-sections-list li{
            display:flex;
            align-items:center;
            gap:8px;
            padding:6px 8px;
            border-radius:6px;
            cursor:pointer;
        }
        #ss-sections-list li:hover{ background:#eee; }
        #ss-sections-list label{ cursor:pointer; user-select:none; text-align:center; }
        .ss-search input{ width:100%; }
        .ss-actions{ display:flex; gap:6px; margin-top:8px; flex-wrap:wrap; }
    </style>

    <!-- ===== Sidebar skeleton (i18n labels) ===== -->
    <div id="ss-layout">
        <aside id="ss-sidebar" aria-label="<spring:message code='systemSettings.sidebar.ariaLabel'/>">
            <div class="ss-search">
                <input id="ss-search-input"
                       type="text"
                       placeholder="<spring:message code='systemSettings.sidebar.searchPlaceholder'/>"
                       aria-label="<spring:message code='systemSettings.sidebar.searchAria'/>" />
            </div>
            <div class="ss-actions">
                <button id="ss-select-all" type="button">
                    <spring:message code="systemSettings.sidebar.selectAll" />
                </button>
                <button id="ss-clear-all" type="button">
                    <spring:message code="systemSettings.sidebar.clearAll" />
                </button>
            </div>
            <ul id="ss-sections-list"></ul>
        </aside>
        <div id="ss-content"><!-- content area offset; actual sections remain where they are --></div>
    </div>

    <!-- ===== Base JS: state, persistence, sidebar, lazy-load dispatcher (no heavy DWR here) ===== -->
    <script type="text/javascript">
			// ---- Global caches / flags (filled later by lazy inits) ----
			var SETTINGS_CACHE = null; // will hold SystemSettingsDwr.getSettings(...) result when needed
			var systemEventAlarmLevels = []; // buffers for alarm sections
			var auditEventAlarmLevels = [];

			// Track once-only init for each section (lazy)
			var SS_INIT_DONE = {
				info: false,
				sysAlarms: false,
				auditAlarms: false,
				misc: false,
				email: false,
				http: false,
				retention: false,
				amCharts: false,
				lang: false,
				customCss: false,
				cache: false
			};

			// ---- Lightweight page onload (do not fire DWR here) ----
			function init() {
				// keep this light; heavy work is deferred to lazy init
				document.addEventListener('DOMContentLoaded', function() {
					var scadaDialog = document.getElementById('scadaConfigDialog');
					if (scadaDialog) scadaDialog.style.display = 'none';
				});
			}

			// ---- Ensure settings (memoized) ----
			function ensureSettings(cb) {
				if (SETTINGS_CACHE) {
					cb(SETTINGS_CACHE);
					return;
				}
				// this is the only place that fetches all settings; it will be called from lazy inits
				SystemSettingsDwr.getSettings(function(settings) {
					SETTINGS_CACHE = settings;
					cb(settings);
				});
			}

			// ---- Sidebar model ----
			var SS = {
				//keyUser: 'ui.systemSettings.openSections', // per-user preference key (DWR optional later)
				//keyGlobal: 'SYSTEM_SETTINGS_DEFAULT_OPEN_SECTIONS', // global default (optional)
				sections: [],              // [{id,title,el}]
				currentSelection: []       // ['info','email',...]
			};

			// Collect sections from DOM by data-section attributes
			function ss_collectSections() {
				var nodes = document.querySelectorAll('[data-section]');
				SS.sections = [];
				for (var i = 0; i < nodes.length; i++) {
					var n = nodes[i];
					var id = n.getAttribute('data-section');
					var title = n.getAttribute('data-title') ||
						(n.querySelector('.smallTitle') ? n.querySelector('.smallTitle').innerText.trim() : id);
					SS.sections.push({ id: id, title: title, el: '#' + n.id });
				}
			}

			// Local persistence (backend sync can be added later)
			function ss_localGet() {
				try {
					return JSON.parse(localStorage.getItem('ss.open') || 'null');
				} catch (e) {
					return null;
				}
			}

			function ss_localSet(arr) {
				localStorage.setItem('ss.open', JSON.stringify(arr));
			}

			// Support deep-link: ?open=email,http
			function ss_parseOpenFromUrl() {
				try {
					var u = new URL(location.href);
					var p = u.searchParams.get('open');
					if (!p) return null;
					var arr = p.split(',');
					var out = [];
					for (var i = 0; i < arr.length; i++) {
						var v = (arr[i] || '').trim();
						if (v) out.push(v);
					}
					return out;
				} catch (e) {
					return null;
				}
			}

			// Apply selection (show/hide sections) + lazy init when becoming visible
			function ss_applySelection(set) {
				for (var i = 0; i < SS.sections.length; i++) {
					var s = SS.sections[i];
					var el = document.querySelector(s.el);
					if (!el) continue;
					var show = set.has(s.id);
					var wasHidden = (el.style.display === 'none');
					el.style.display = show ? '' : 'none';
					if (show && wasHidden) {
						ss_lazyInitSection(s.id);
					}
				}
				SS.currentSelection = Array.from(set);
			}

			// Persist current selection (local only for now)
			function ss_persistSelection(set) {
				var arr = Array.from(set);
				ss_localSet(arr);
			}

			// Build sidebar UI (checkbox list + search + buttons)
			function ss_buildSidebar() {
                const ul = document.getElementById('ss-sections-list');
                if (!ul) return;
                ul.innerHTML = '';

                const selected = new Set(SS.currentSelection || []);
                for (var i = 0; i < SS.sections.length; i++) {
                    var s = SS.sections[i];
                    var li = document.createElement('li');

                    var cb = document.createElement('input');
                    cb.type = 'checkbox';
                    cb.value = s.id;
                    cb.checked = selected.has(s.id);

                    // id + for = klik w tekst też działa
                    var cbId = 'ss-cb-' + s.id;
                    cb.id = cbId;

                    cb.addEventListener('change', (function(id){
                        return function(e){
                            if (e.target.checked) selected.add(id); else selected.delete(id);
                            ss_applySelection(selected);
                            ss_persistSelection(selected);
                        };
                    })(s.id));

                    var label = document.createElement('label');
                    label.textContent = s.title;
                    label.setAttribute('for', cbId);

                    li.appendChild(cb);
                    li.appendChild(label);
                    ul.appendChild(li);
                }

                // >>> delegacja klików: klik w tło li przełącza TYLKO ten wiersz
                ul.onclick = function(e){
                    const li = e.target.closest('li');
                    if (!li || !ul.contains(li)) return;
                    if (e.target.tagName === 'INPUT' || e.target.tagName === 'LABEL') return;
                    const box = li.querySelector('input[type="checkbox"]');
                    if (box) box.click();
                };

				var selAll = document.getElementById('ss-select-all');
				var clrAll = document.getElementById('ss-clear-all');

				if (selAll) selAll.onclick = function() {
					var all = new Set(SS.sections.map(function(s) {
						return s.id;
					}));
					ss_applySelection(all);
					ss_persistSelection(all);
					ss_buildSidebar();
				};
				if (clrAll) clrAll.onclick = function() {
					var none = new Set();
					ss_applySelection(none);
					ss_persistSelection(none);
					ss_buildSidebar();
				};

				var search = document.getElementById('ss-search-input');
				if (search) search.oninput = function(e) {
					var q = (e.target.value || '').toLowerCase();
					for (var i = 0; i < SS.sections.length; i++) {
						var s = SS.sections[i];
						var el = document.querySelector(s.el);
						if (!el) continue;
						var text = (el.innerText || '').toLowerCase();
						el.style.display = text.indexOf(q) !== -1 ? '' : 'none';
					}
				};
			}

			// Init layout: collect sections, compute initial selection, apply and build UI
			function ss_initLayout() {
				ss_collectSections();
				var urlSel = ss_parseOpenFromUrl();
				var local = ss_localGet();
				var def = SS.sections.map(function(s) {
					return s.id;
				});
				SS.currentSelection = urlSel || local || def;

				ss_applySelection(new Set(SS.currentSelection));
				// ensure visible ones get initialized immediately
				for (var i = 0; i < SS.currentSelection.length; i++) {
					ss_lazyInitSection(SS.currentSelection[i]);
				}
				ss_buildSidebar();
			}

			// Lazy init dispatcher – section-specific initializers will come in Part 3
			function ss_lazyInitSection(id) {
				if (id === 'info' && !SS_INIT_DONE.info) return;          // initSectionInfo();
				if (id === 'sysAlarms' && !SS_INIT_DONE.sysAlarms) return; // initSectionSystemAlarms();
				if (id === 'auditAlarms' && !SS_INIT_DONE.auditAlarms) return; // initSectionAuditAlarms();
				if (id === 'misc' && !SS_INIT_DONE.misc) return;          // initSectionMisc();
				if (id === 'email' && !SS_INIT_DONE.email) return;        // initSectionEmail();
				if (id === 'http' && !SS_INIT_DONE.http) return;          // initSectionHttp();
				if (id === 'retention' && !SS_INIT_DONE.retention) return; // initSectionRetention();
				if (id === 'amCharts' && !SS_INIT_DONE.amCharts) return;  // initSectionAmCharts();
				if (id === 'lang' && !SS_INIT_DONE.lang) return;          // initSectionLanguage();
				if (id === 'customCss' && !SS_INIT_DONE.customCss) return;// initSectionCustomCss();
				if (id === 'cache' && !SS_INIT_DONE.cache) {
					SS_INIT_DONE.cache = true;
				}
			}

            function ss_mountSectionsIntoContent() {
                var content = document.getElementById('ss-content');
                if (!content) return;

                var sections = document.querySelectorAll('div[data-section]');
                var frag = document.createDocumentFragment();
                for (var i = 0; i < sections.length; i++) {
                    var s = sections[i];
                    if (!content.contains(s)) frag.appendChild(s);
                }
                if (frag.childNodes.length) content.appendChild(frag);
            }

						(function(){
							function placeSidebar() {
								var header = document.getElementById('subHeader');

								var top = Math.max(0, Math.round(header.getBoundingClientRect().bottom)) - 1;
								document.documentElement.style.setProperty('--ss-top', top + 'px');
							}

							window.addEventListener('load', placeSidebar);
							window.addEventListener('resize', placeSidebar);
						})();

			dojo.addOnLoad(function() {
                ss_mountSectionsIntoContent();
                ss_initLayout();
			});
    </script>
    <!-- System Information -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-info"
         data-section="info"
         data-title-key="systemSettings.systemInformation">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.systemInformation" /></span>
                    <tag:help id="systemInformation" />
                </td>
                <td align="right">
                    <tag:img id="saveInfoSettingsImg" png="save" onclick="saveInfoSettings();" title="common.save" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.version" /></td>
                <td class="formField"><c:out value="<%= Common.getVersion() %>" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.instanceDescription" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>" type="text"
                           class="formMedium" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.databaseSize" /></td>
                <td class="formField">
                    <span id="databaseSize"></span>
                    <tag:img id="refreshImg" png="control_repeat_blue" onclick="dbSizeUpdate();"
                             title="common.refresh" />
                    <tag:img id="purgeNowImg" png="bin" onclick="purgeNow()" title="systemSettings.purgeNow" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.filedataSize" /></td>
                <td class="formField" id="filedataSize"></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.totalSize" /></td>
                <td class="formField" id="totalSize"></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.historyCount" /></td>
                <td class="formField" id="historyCount"></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.topPoints" /></td>
                <td class="formField" id="topPoints"></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.eventCount" /></td>
                <td class="formField" id="eventCount"></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.top.description.prefix" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION_PREFIX %>"/>" type="text"
                           class="formMedium" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.top.description" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION %>"/>" type="text"
                           class="formMedium" />
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button onclick="showScadaConfigDialog()">
                        <spring:message code="systemSettings.scadaConfTitle" />
                    </button>
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td colspan="2" id="infoMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- System Alarm Levels -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-sysAlarms"
         data-section="sysAlarms"
         data-title-key="systemSettings.systemAlarmLevels">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.systemAlarmLevels" /></span>
                    <tag:help id="systemAlarmLevels" />
                </td>
                <td align="right">
                    <tag:img id="saveSystemEventAlarmLevelsImg" png="save" onclick="saveSystemEventAlarmLevels();"
                             title="common.save" />
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

    <!-- Other Settings -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-misc"
         data-section="misc"
         data-title-key="systemSettings.otherSettings">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.otherSettings" /></span>
                    <tag:help id="otherSettings" />
                </td>
                <td align="right">
                    <tag:img id="saveMiscSettingsImg" png="save" onclick="saveMiscSettings();" title="common.save" />
                </td>
            </tr>
        </table>
        <table id="settingsMisc">
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.uiPerformance" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>" type="number"
                           class="formShort" />
                    <select id="uiPerformanceId" onchange="toUiPerformanceId()">
                        <option value=""></option>
                        <option value="1000"><spring:message code="systemSettings.uiPerformance.veryHigh" /></option>
                        <option value="2000"><spring:message code="systemSettings.uiPerformance.high" /></option>
                        <option value="5000"><spring:message code="systemSettings.uiPerformance.med" /></option>
                        <option value="10000"><spring:message code="systemSettings.uiPerformance.low" /></option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemsettings.misc.dataPointRuntimeValueSynchronized" /></td>
                <td class="formField">
                    <select id="<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>">
                        <option value="NONE"><spring:message
                                code="systemsettings.misc.dataPointRuntimeValueSynchronized.none" /></option>
                        <option value="PARTIAL"><spring:message
                                code="systemsettings.misc.dataPointRuntimeValueSynchronized.partial" /></option>
                        <option value="ALL"><spring:message
                                code="systemsettings.misc.dataPointRuntimeValueSynchronized.all" /></option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.view.forceFullScreen" /></td>
                <td class="formField">
                    <input type="checkbox" id="<c:out value="<%= SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE %>"/>" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemsettings.view.hideShortcutDisableFullScreen" /></td>
                <td class="formField">
                    <input type="checkbox"
                           id="<c:out value="<%= SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN %>"/>" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.event.pendingCacheEnabled" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED %>"/>" type="checkbox" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.event.pendingLimit" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.EVENT_PENDING_LIMIT %>"/>" type="number"
                           class="formShort" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.workitems.reporting.enabled" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>" type="checkbox"
                           onchange="workItemsReportingEnabledChange()" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemsettings.workitems.reporting.itemspersecond.enabled" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>"
                           type="checkbox" onchange="workItemsReportingItemsPerSecondEnabledChange()" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemsettings.workitems.reporting.itemspersecond.limit" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>"
                           type="number" class="formShort" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemsettings.threads.name.additional.length" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH %>"/>" type="number"
                           class="formShort" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.webresource.graphics.path" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH %>"/>" type="text"
                           class="formMediumFieldSizing" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemsettings.webresource.uploads.path" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH %>"/>" type="text"
                           class="formMediumFieldSizing" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="event.assign.enabled" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>" type="checkbox" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemsettings.reports.dataPointExtendedNameLengthLimit" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.DATA_POINT_EXTENDED_NAME_LENGTH_IN_REPORTS_LIMIT %>"/>"
                           type="number" class="formShort" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.smsDomain.defaultGateway" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.SMS_DOMAIN %>"/>" type="text"
                           class="formMediumFieldSizing" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.defaultDataPointLoggingType" /></td>
                <td class="formField">
                    <select id="<c:out value="<%= SystemSettingsDAO.DEFAULT_LOGGING_TYPE %>"/>">
                        <option value="<c:out value="<%= DataPointVO.LoggingTypes.ON_CHANGE %>"/>">
                            <spring:message code="pointEdit.logging.type.change" />
                        </option>
                        <option value="<c:out value="<%= DataPointVO.LoggingTypes.ALL %>"/>">
                            <spring:message code="pointEdit.logging.type.all" />
                        </option>
                        <option value="<c:out value="<%= DataPointVO.LoggingTypes.NONE %>"/>">
                            <spring:message code="pointEdit.logging.type.never" />
                        </option>
                        <option value="<c:out value="<%= DataPointVO.LoggingTypes.INTERVAL %>"/>">
                            <spring:message code="pointEdit.logging.type.interval" />
                        </option>
                        <option value="<c:out value="<%= DataPointVO.LoggingTypes.ON_TS_CHANGE %>"/>">
                            <spring:message code="pointEdit.logging.type.tsChange" />
                        </option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2" id="miscMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- Audit Alarm Levels -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-auditAlarms"
         data-section="auditAlarms"
         data-title-key="systemSettings.auditAlarmLevels">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.auditAlarmLevels" /></span>
                    <tag:help id="auditAlarmLevels" />
                </td>
                <td align="right">
                    <tag:img id="saveAuditEventAlarmLevelsImg" png="save" onclick="saveAuditEventAlarmLevels();"
                             title="common.save" />
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

    <!-- Email Settings -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-email"
         data-section="email"
         data-title-key="systemSettings.emailSettings">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.emailSettings" /></span>
                    <tag:help id="emailSettings" />
                </td>
                <td align="right">
                    <tag:img id="saveEmailSettingsImg" png="save" onclick="saveEmailSettings();" title="common.save" />
                    <tag:img id="sendTestEmailImg" png="email_go" onclick="sendTestEmail();"
                             title="common.sendTestEmail" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.smtpHost" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_HOST %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.smtpPort" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PORT %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.fromAddress" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_ADDRESS %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.fromName" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_FROM_NAME %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.auth" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.EMAIL_AUTHORIZATION %>"/>" type="checkbox"
                           onclick="smtpAuthChange()" />
                </td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.smtpUsername" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_USERNAME %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.smtpPassword" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_SMTP_PASSWORD %>"/>"
                                             type="password" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.tls" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.EMAIL_TLS %>"/>" type="checkbox" />
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.contentType" /></td>
                <td class="formField">
                    <select id="<c:out value="<%= SystemSettingsDAO.EMAIL_CONTENT_TYPE %>"/>">
                        <option value="<c:out value="<%= MangoEmailContent.CONTENT_TYPE_BOTH %>"/>">
                            <spring:message code="systemSettings.contentType.both" />
                        </option>
                        <option value="<c:out value="<%= MangoEmailContent.CONTENT_TYPE_HTML %>"/>">
                            <spring:message code="systemSettings.contentType.html" />
                        </option>
                        <option value="<c:out value="<%= MangoEmailContent.CONTENT_TYPE_TEXT %>"/>">
                            <spring:message code="systemSettings.contentType.text" />
                        </option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2" id="emailMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- HTTP Settings -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-http"
         data-section="http"
         data-title-key="systemSettings.httpSettings">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.httpSettings" /></span>
                    <tag:help id="httpSettings" />
                </td>
                <td align="right">
                    <tag:img id="saveHttpSettingsImg" png="save" onclick="saveHttpSettings();" title="common.save" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.useProxy" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>" type="checkbox"
                           onclick="httpUseProxyChange()" />
                </td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.proxyHost" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.proxyPort" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.proxyUsername" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>"
                                             type="text" /></td>
            </tr>
            <tr>
                <td class="formLabel"><spring:message code="systemSettings.proxyPassword" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>"
                                             type="password" /></td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <spring:message code="systemsettings.http.response.headers" />
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
  <textarea rows="5" cols="60" id="<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>"
            placeholder="<spring:message code='systemsettings.http.response.headers'/>"></textarea>
                </td>
            </tr>
            <tr>
                <td colspan="2" id="httpMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- Data Retention -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-retention"
         data-section="retention"
         data-title-key="systemSettings.dataRetentionSettings">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.dataRetentionSettings" /></span>
                    <tag:help id="dataRetentionSettings" />
                </td>
                <td align="right">
                    <tag:img id="saveDataRetentionSettingsImg" png="save" onclick="saveDataRetentionSettings();"
                             title="common.save" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.purgeEvents" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>" type="text"
                           class="formShort" />
                    <select id="<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>">
                        <tag:timePeriodOptions d="true" w="true" mon="true" y="true" />
                    </select>
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.purgeReports" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>" type="text"
                           class="formShort" />
                    <select id="<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>">
                        <tag:timePeriodOptions d="true" w="true" mon="true" y="true" />
                    </select>
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.futureDateLimit" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>" type="text"
                           class="formShort" />
                    <select id="<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>">
                        <tag:timePeriodOptions min="true" h="true" />
                    </select>
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message
                        code="systemSettings.purgePointValuesPeriodDefault" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_DEFAULT %>"/>" type="text"
                           class="formShort" />
                    <select id="<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_TYPE_DEFAULT %>"/>">
                        <tag:timePeriodOptions d="true" w="true" mon="true" y="true" />
                    </select>
                </td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.valuesLimitForPurge" /></td>
                <td class="formField">
                    <input id="<c:out value="<%= SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE %>"/>" type="number"
                           class="formMedium" />
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="button" value="<spring:message code='systemSettings.purgeData'/>"
                           onclick="checkPurgeAllData()" style="margin:5px;" />
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="button" value="<spring:message code='systemSettings.purgeNow'/>" onclick="purgeNow()"
                           style="margin:5px;" />
                </td>
            </tr>
            <tr>
                <td colspan="2" id="dataRetentionMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- amCharts -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-amCharts"
         data-section="amCharts"
         data-title-key="systemSettings.amCharts">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.amCharts" /></span>
                </td>
                <td align="right">
                    <tag:img id="saveAmChartsSettingsImg" png="save" onclick="saveAmChartsSettings();"
                             title="common.save" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.amChart.enabled" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.AGGREGATION_ENABLED %>"/>"
                                             type="checkbox" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.amChart.valuesLimit" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.AGGREGATION_VALUES_LIMIT %>"/>"
                                             type="number" class="formMedium" /></td>
            </tr>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.amChart.limitFactor" /></td>
                <td class="formField"><input id="<c:out value="<%= SystemSettingsDAO.AGGREGATION_LIMIT_FACTOR %>"/>"
                                             type="number" class="formMedium" /></td>
            </tr>
            <tr>
                <td colspan="2" id="amChartsMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- Language -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-lang"
         data-section="lang"
         data-title-key="systemSettings.languageSettings">
        <table width="100%">
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.languageSettings" /></span>
                    <tag:help id="languageSettings" />
                </td>
                <td align="right">
                    <tag:img id="saveLangSettingsImg" png="save" onclick="saveLangSettings();" title="common.save" />
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td class="formLabelRequired"><spring:message code="systemSettings.systemLanguage" /></td>
                <td class="formField">
                    <select id="<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>"></select>
                </td>
            </tr>
            <tr>
                <td colspan="2" id="langMessage" class="formError"></td>
            </tr>
        </table>
    </div>

    <!-- Custom CSS -->
    <div class="borderDiv marB marR" style="float:left"
         id="section-customCss"
         data-section="customCss"
         data-title-key="systemSettings.customCss.title">
        <table>
            <tr>
                <td>
                    <span class="smallTitle"><spring:message code="systemSettings.customCss.title" /></span>
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <button onclick="showCssDialog()">
                        <spring:message code="systemSettings.customCss.edit" />
                    </button>
                </td>
            </tr>
        </table>
    </div>

    <!-- Cache images -->
    <div class="borderDivPadded marB marR" style="float:left"
         id="section-cache"
         data-section="cache"
         data-title="Cache images">
        <table width="100%">
            <tr>
                <td><span class="smallTitle">Cache images</span></td>
            </tr>
            <tr>
                <td align="center">
                    <input type="button" value="Refresh" onClick="refreshImages()" />
                </td>
            </tr>
        </table>
    </div>
    <script type="text/javascript">
			/* ---------- Helpers specific to alarms ---------- */
			function setAlarmLevelImg(alarmLevel, imgId) {
				var img = document.getElementById(imgId);
				if (!img) return;

				var lvl = String(alarmLevel);
				// 0: NONE, 1: INFORMATION, 2: URGENT, 3: CRITICAL, 4: LIFE_SAFETY
				var map = {
					"0": "images/flag_green.png",
					"1": "images/flag_blue.png",
					"2": "images/flag_yellow.png",
					"3": "images/flag_red.png",
					"4": "images/flag_purple.png"
				};
				img.src = map[lvl] || map["0"];
				img.style.display = "";
			}

			function setEventTypeData(listId, eventTypes, alarmFunctions, alarmOptions, alarmLevelsList) {
				dwr.util.addRows(listId, eventTypes, alarmFunctions, alarmOptions);
				var eventType, etid;
				for (var i = 0; i < eventTypes.length; i++) {
					eventType = eventTypes[i];
					etid = eventType.typeId + "-" + eventType.typeRef1;
					$set("alarmLevel" + etid, eventType.alarmLevel);
					setAlarmLevelImg(eventType.alarmLevel, "alarmLevelImg" + etid);
					alarmLevelsList[alarmLevelsList.length] = { i1: eventType.typeRef1, i2: eventType.alarmLevel };
				}
			}

			function updateAlarmLevel(eventTypeId, eventId, alarmLevel) {
				setAlarmLevelImg(alarmLevel, "alarmLevelImg" + eventTypeId + "-" + eventId);
				var list = (eventTypeId == <c:out value="<%= EventType.EventSources.SYSTEM %>"/>) ? systemEventAlarmLevels : auditEventAlarmLevels;
				getElement(list, eventId, "i1")["i2"] = alarmLevel;
			}

			/* ---------- Lazy init per section ---------- */
			function initSectionInfo() {
				SS_INIT_DONE.info = true;
				ensureSettings(function(settings) {
					$set("<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>", settings.<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION_PREFIX %>"/>", settings.<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION_PREFIX %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION %>"/>", settings.<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION %>"/>);
				});
			}

			function initSectionSystemAlarms() {
				SS_INIT_DONE.sysAlarms = true;
				ensureSettings(function(settings) {
					var alarmFunctions = [
						function(et) {
							return et.description;
						},
						function(et) {
							var etid = et.typeId + "-" + et.typeRef1;
							var content = "";
							content += "<select id='alarmLevel" + etid + "' onchange='updateAlarmLevel(" + et.typeId + ", " + et.typeRef1 + ", this.value)'>";
							content += "<option value='<c:out value="<%= AlarmLevels.NONE %>"/>'><spring:message code="<%= AlarmLevels.NONE_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.INFORMATION %>"/>'><spring:message code="<%= AlarmLevels.INFORMATION_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.URGENT %>"/>'><spring:message code="<%= AlarmLevels.URGENT_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.CRITICAL %>"/>'><spring:message code="<%= AlarmLevels.CRITICAL_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.LIFE_SAFETY %>"/>'><spring:message code="<%= AlarmLevels.LIFE_SAFETY_DESCRIPTION %>"/></option>";
							content += "</select> ";
							content += "<img id='alarmLevelImg" + etid + "' src='images/flag_green.png' style='display:none'>";
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
					setEventTypeData("systemEventAlarmLevelsList", settings.systemEventTypes, alarmFunctions, alarmOptions, systemEventAlarmLevels);
				});
			}

			function initSectionAuditAlarms() {
				SS_INIT_DONE.auditAlarms = true;
				ensureSettings(function(settings) {
					var alarmFunctions = [
						function(et) {
							return et.description;
						},
						function(et) {
							var etid = et.typeId + "-" + et.typeRef1;
							var content = "<select id='alarmLevel" + etid + "' onchange='updateAlarmLevel(" + et.typeId + ", " + et.typeRef1 + ", this.value)'>";
							content += "<option value='<c:out value="<%= AlarmLevels.NONE %>"/>'><spring:message code="<%= AlarmLevels.NONE_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.INFORMATION %>"/>'><spring:message code="<%= AlarmLevels.INFORMATION_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.URGENT %>"/>'><spring:message code="<%= AlarmLevels.URGENT_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.CRITICAL %>"/>'><spring:message code="<%= AlarmLevels.CRITICAL_DESCRIPTION %>"/></option>";
							content += "<option value='<c:out value="<%= AlarmLevels.LIFE_SAFETY %>"/>'><spring:message code="<%= AlarmLevels.LIFE_SAFETY_DESCRIPTION %>"/></option>";
							content += "</select> <img id='alarmLevelImg" + etid + "' src='images/flag_green.png' style='display:none'>";
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
					setEventTypeData("auditEventAlarmLevelsList", settings.auditEventTypes, alarmFunctions, alarmOptions, auditEventAlarmLevels);
				});
			}

			function initSectionMisc() {
				SS_INIT_DONE.misc = true;
				ensureSettings(function(settings) {
					$set("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED %>"/>);
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
					$set("<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_ASSIGN_ENABLED %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.DATA_POINT_EXTENDED_NAME_LENGTH_IN_REPORTS_LIMIT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.DATA_POINT_EXTENDED_NAME_LENGTH_IN_REPORTS_LIMIT %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.SMS_DOMAIN %>"/>", settings["<c:out value="<%= SystemSettingsDAO.SMS_DOMAIN %>"/>"]);
					$set("<c:out value="<%= SystemSettingsDAO.DEFAULT_LOGGING_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.DEFAULT_LOGGING_TYPE %>"/>);

					setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>"), !settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>);
					setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>"),
						!settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/> ||
						!settings.<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>);

					var webResourceGraphicsPath = document.getElementById("<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH %>"/>");
					var webGraphicsUploadsPath = document.getElementById("<c:out value="<%= SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH %>"/>");
					var smsDomain = document.getElementById("<c:out value="<%= SystemSettingsDAO.SMS_DOMAIN %>"/>");
					if (webResourceGraphicsPath) {
						webResourceGraphicsPath.addEventListener('input', function(e) {
							sizingField(30, e.target, 250);
						});
						initSizeField(webResourceGraphicsPath);
					}
					if (webGraphicsUploadsPath) {
						webGraphicsUploadsPath.addEventListener('input', function(e) {
							sizingField(30, e.target, 250);
						});
						initSizeField(webGraphicsUploadsPath);
					}
					if (smsDomain) {
						smsDomain.addEventListener('input', function(e) {
							sizingField(30, e.target, 250);
						});
						initSizeField(smsDomain);
					}
				});
			}

			function initSectionEmail() {
				SS_INIT_DONE.email = true;
				ensureSettings(function(settings) {
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
				});
			}

			function initSectionHttp() {
				SS_INIT_DONE.http = true;
				ensureSettings(function(settings) {
					$set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_USE_PROXY %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>", settings.<c:out value="<%= SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>", unescapeHtml(JSON.stringify(settings.<c:out value="<%= SystemSettingsDAO.HTTP_RESPONSE_HEADERS %>"/>, null, 2)));
					httpUseProxyChange();
				});
			}

			function initSectionRetention() {
				SS_INIT_DONE.retention = true;
				ensureSettings(function(settings) {
					$set("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.EVENT_PURGE_PERIODS %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.REPORT_PURGE_PERIODS %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>", settings.<c:out value="<%= SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_DEFAULT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_DEFAULT %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_TYPE_DEFAULT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_TYPE_DEFAULT %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE %>"/>", settings.<c:out value="<%= SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE %>"/>);
				});
			}

			function initSectionAmCharts() {
				SS_INIT_DONE.amCharts = true;
				ensureSettings(function(settings) {
					$set("<c:out value="<%= SystemSettingsDAO.AGGREGATION_ENABLED %>"/>", settings.<c:out value="<%= SystemSettingsDAO.AGGREGATION_ENABLED %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.AGGREGATION_VALUES_LIMIT %>"/>", settings.<c:out value="<%= SystemSettingsDAO.AGGREGATION_VALUES_LIMIT %>"/>);
					$set("<c:out value="<%= SystemSettingsDAO.AGGREGATION_LIMIT_FACTOR %>"/>", settings.<c:out value="<%= SystemSettingsDAO.AGGREGATION_LIMIT_FACTOR %>"/>);
				});
			}

			function initSectionLanguage() {
				SS_INIT_DONE.lang = true;
				ensureSettings(function(settings) {
					var sel = $("<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>");
					<c:forEach items="${availableLanguages}" var="lang">
					sel.options[sel.options.length] = new Option("${lang.value}", "${lang.key}");
					</c:forEach>
					$set(sel, settings.<c:out value="<%= SystemSettingsDAO.LANGUAGE %>"/>);
				});
			}

			function initSectionCustomCss() {
				SS_INIT_DONE.customCss = true;
			}

			/* ---------- Override lazy dispatcher ---------- */
			ss_lazyInitSection = function(id) {
				if (id === 'info' && !SS_INIT_DONE.info) return initSectionInfo();
				if (id === 'sysAlarms' && !SS_INIT_DONE.sysAlarms) return initSectionSystemAlarms();
				if (id === 'auditAlarms' && !SS_INIT_DONE.auditAlarms) return initSectionAuditAlarms();
				if (id === 'misc' && !SS_INIT_DONE.misc) return initSectionMisc();
				if (id === 'email' && !SS_INIT_DONE.email) return initSectionEmail();
				if (id === 'http' && !SS_INIT_DONE.http) return initSectionHttp();
				if (id === 'retention' && !SS_INIT_DONE.retention) return initSectionRetention();
				if (id === 'amCharts' && !SS_INIT_DONE.amCharts) return initSectionAmCharts();
				if (id === 'lang' && !SS_INIT_DONE.lang) return initSectionLanguage();
				if (id === 'customCss' && !SS_INIT_DONE.customCss) return initSectionCustomCss();
				if (id === 'cache' && !SS_INIT_DONE.cache) {
					SS_INIT_DONE.cache = true;
				}
			};

			/* ---------- Actions (save/test/etc.), unchanged API contracts ---------- */
			function dbSizeUpdate() {
				$set("databaseSize", "<spring:message code='systemSettings.retrieving'/>");
				$set("filedataSize", "-");
				$set("totalSize", "-");
				$set("historyCount", "-");
				$set("topPoints", "-");
				hide("refreshImg");
				SystemSettingsDwr.getDatabaseSize(function(data) {
					$set("databaseSize", data.databaseSize);
					$set("filedataSize", data.filedataSize + " (" + data.filedataCount + " <spring:message code='systemSettings.files'/>)");
					$set("totalSize", data.totalSize);
					$set("historyCount", data.historyCount);
					show("refreshImg");
					var cnt = "";
					for (var i = 0; i < data.topPoints.length; i++) {
						cnt += "<a href='data_point_details.shtm?dpid=" + data.topPoints[i].pointId + "'>" +
							data.topPoints[i].pointName + "</a> " + data.topPoints[i].count + "<br/>";
						if (i == 3) break;
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
						setUserMessage("emailMessage", "<spring:message code='systemSettings.emailSettingsSaved'/>");
					}
				);
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
						if (result.exception) setUserMessage("emailMessage", result.exception);
						else setUserMessage("emailMessage", result.message);
					}
				);
				setUserMessage("emailMessage");
				startImageFader("sendTestEmailImg");
			}

			function saveSystemEventAlarmLevels() {
				SystemSettingsDwr.saveSystemEventAlarmLevels(systemEventAlarmLevels, function() {
					stopImageFader("saveSystemEventAlarmLevelsImg");
					setUserMessage("systemEventAlarmLevelsMessage", "<spring:message code='systemSettings.systemAlarmLevelsSaved'/>");
				});
				setUserMessage("systemEventAlarmLevelsMessage");
				startImageFader("saveSystemEventAlarmLevelsImg");
			}

			function saveAuditEventAlarmLevels() {
				SystemSettingsDwr.saveAuditEventAlarmLevels(auditEventAlarmLevels, function() {
					stopImageFader("saveAuditEventAlarmLevelsImg");
					setUserMessage("auditEventAlarmLevelsMessage", "<spring:message code='systemSettings.auditAlarmLevelsSaved'/>");
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
						if (a.messages && a.messages.length > 0) {
							setUserMessage("httpMessage", a.messages[0].contextualMessage);
						} else {
							setUserMessage("httpMessage", "<spring:message code='systemSettings.httpSaved'/>");
						}
					}
				);
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
				var enabled = $("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED %>"/>").checked;
				if (!enabled) {
					$set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>", false);
					workItemsReportingItemsPerSecondEnabledChange();
				}
				setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>"), !enabled);
			}

			function workItemsReportingItemsPerSecondEnabledChange() {
				var enabled = $("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED %>"/>").checked;
				if (!enabled) {
					$set("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>", 0);
				}
				setDisabled($("<c:out value="<%= SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT %>"/>"), !enabled);
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
					$get("<c:out value="<%= SystemSettingsDAO.DATA_POINT_EXTENDED_NAME_LENGTH_IN_REPORTS_LIMIT %>"/>"),
					$get("<c:out value="<%= SystemSettingsDAO.SMS_DOMAIN %>"/>"),
					$get("<c:out value="<%= SystemSettingsDAO.DEFAULT_LOGGING_TYPE %>"/>"),
					function(response) {
						stopImageFader("saveMiscSettingsImg");
						if (response.hasMessages) showDwrMessages(response.messages);
						else setUserMessage("miscMessage", "<spring:message code='systemSettings.miscSaved'/>");
					}
				);
				setUserMessage("miscMessage");
				hideContextualMessages("settingsMisc");
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
					$get("<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_DEFAULT %>"/>"),
					$get("<c:out value="<%= SystemSettingsDAO.PURGE_POINT_VALUES_PERIOD_TYPE_DEFAULT %>"/>"),
					$get("<c:out value="<%= SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE %>"/>"),
					function() {
						stopImageFader("saveDataRetentionSettingsImg");
						setUserMessage("dataRetentionMessage", "<spring:message code='systemSettings.dataRetentionSaved'/>");
					}
				);
				setUserMessage("dataRetentionMessage");
				startImageFader("saveDataRetentionSettingsImg");
			}

			function setUserMessage(type, msg) {
				if (msg) $set(type, msg); else $set(type, "");
			}

			function saveInfoSettings() {
				var topDescriptionPrefix = $get("<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION_PREFIX %>"/>");
				var topDescription = $get("<c:out value="<%= SystemSettingsDAO.TOP_DESCRIPTION %>"/>");
				SystemSettingsDwr.saveInfoSettings(
					"0",
					$get("<c:out value="<%= SystemSettingsDAO.INSTANCE_DESCRIPTION %>"/>"),
					topDescriptionPrefix,
					topDescription,
					function() {
						stopImageFader("saveInfoSettingsImg");
						var p = document.getElementById('top-description-prefix');
						if (p) p.innerText = topDescriptionPrefix;
						var d = document.getElementById('top-description');
						if (d) d.innerText = topDescription;
						setUserMessage("infoMessage", "<spring:message code='systemSettings.infoSaved'/>");
					}
				);
				setUserMessage("infoMessage");
				startImageFader("saveInfoSettingsImg");
			}

			function newVersionCheck() {
				SystemSettingsDwr.newVersionCheck($get("<c:out value="<%= SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL %>"/>"),
					function(result) {
						if (!result) result = "<spring:message code='systemSettings.upToDate'/>";
						alert(result);
					}
				);
			}

			function purgeNow() {
				if (confirm("<spring:message code='systemSettings.purgeDataPointStrategyConfirm'/>")) {
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
					setUserMessage("langMessage", "<spring:message code='systemSettings.langSaved'/>");
				});
				setUserMessage("langMessage");
				startImageFader("saveLangSettingsImg");
			}

			function checkPurgeAllData() {
				if (confirm("<spring:message code='systemSettings.purgeDataConfirm'/>")) {
					setUserMessage("dataRetentionMessage", "<spring:message code='systemSettings.purgeDataInProgress'/>");
					SystemSettingsDwr.purgeAllData(function(msg) {
						setUserMessage("dataRetentionMessage", msg);
						dbSizeUpdate();
					});
				}
			}

			function toUiPerformanceId() {
				var uiPerformance = $get("uiPerformanceId");
				if (!uiPerformance) uiPerformance = 1000;
				$set("<c:out value="<%= SystemSettingsDAO.UI_PERFORMANCE %>"/>", uiPerformance);
			}

			/* Cache images action (used by Cache section) */
			function refreshImages() {
				var pathArray = location.href.split('/');
				var protocol = pathArray[0];
				var host = pathArray[2];
				var appScada = pathArray[3];
				var myLocation = protocol + "//" + host + "/" + appScada + "/";
				jQuery.ajax({
					type: 'GET',
					dataType: 'text',
					url: myLocation + "api/resources/imagesRefresh",
					success: function() {
						alert("Success: the resource images have been refreshed");
					},
					error: function(_xhr, _status, errorThrown) {
						alert("Problem when refreshing assets: " + (errorThrown && errorThrown.message ? errorThrown.message : "unknown"));
					}
				});
			}

			/* amCharts save */
			function saveAmChartsSettings() {
				SystemSettingsDwr.saveAmChartsSettings(
					$get("<c:out value="<%= SystemSettingsDAO.AGGREGATION_ENABLED %>"/>"),
					$get("<c:out value="<%= SystemSettingsDAO.AGGREGATION_VALUES_LIMIT %>"/>"),
					$get("<c:out value="<%= SystemSettingsDAO.AGGREGATION_LIMIT_FACTOR %>"/>"),
					function() {
						stopImageFader("saveAmChartsSettingsImg");
						setUserMessage("amChartsMessage", "<spring:message code='systemSettings.amChartsSaved'/>");
					}
				);
				setUserMessage("amChartsMessage");
				hideContextualMessages("amChartsMessage");
				startImageFader("saveAmChartsSettingsImg");
			}
    </script>
    <style>
        /* Custom CSS editor modal */
        #css-editor-dialog {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            display: none; /* toggled by JS */
            background-color: rgba(0, 0, 0, .51);
            z-index: 2000;
            align-items: center;
            justify-content: center;
        }

        #css-editor-dialog .css-dialog-content {
            width: 650px;
            max-height: 90vh;
            background: #fff;
            color: #000;
            border-radius: 8px;
            padding: 16px;
            display: flex;
            flex-direction: column;
            gap: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, .25);
        }

        #css-editor-dialog .css-dialog-editor {
            position: relative;
            height: 520px;
            border: 1px solid #e5e5e5;
            border-radius: 6px;
            overflow: hidden;
        }

        #css-editor-dialog .hgl-editor {
            position: absolute;
            inset: 0;
            width: 100%;
            height: 100%;
            resize: none;
            border: none;
            outline: none;
            padding: 12px;
            background: transparent;
            font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
            font-size: 12px;
            line-height: 1.5;
            color: #111;
            white-space: pre;
            overflow: auto;
        }

        #css-editor-dialog .hgl-highlighting {
            position: absolute;
            inset: 0;
            margin: 0;
            pointer-events: none;
            padding: 12px;
            overflow: hidden;
            color: #222;
            background: #fafafa;
        }

        #css-editor-dialog .css-dialog-buttons {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            gap: 8px;
        }

        /* SCADA config modal (lightweight panel) */
        #scadaConfigDialog {
            display: none; /* toggled by JS */
            position: fixed;
            top: 8%;
            left: 50%;
            transform: translateX(-50%);
            background: #fff;
            color: #000;
            border: 1px solid #ccc;
            border-radius: 8px;
            width: min(920px, 92vw);
            z-index: 1900;
            box-shadow: 0 10px 30px rgba(0, 0, 0, .25);
        }

        #scadaConfigDialog .header {
            padding: 10px 12px;
            border-bottom: 1px solid #e5e5e5;
            background: #f6f6f6;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        #scadaConfigDialog .body {
            padding: 12px;
            max-height: 70vh;
            overflow: auto;
        }
    </style>

    <!-- Custom CSS Editor Dialog -->
    <div id="css-editor-dialog" role="dialog" aria-modal="true"
         aria-label="<spring:message code='systemSettings.customCss.dialog.title'/>">
        <div class="css-dialog-content">
            <div>
                <h2 class="marB">
                    <spring:message code="systemSettings.customCss.dialog.title" />
                </h2>
                <div class="note">
                    <spring:message code="systemSettings.customCss.dialog.note" />
                </div>
            </div>

            <div class="css-dialog-editor">
      <textarea
              id="cssEditor"
              class="hgl-editor"
              spellcheck="false"
              placeholder="/* Add your CSS overrides here */"
              oninput="updateCodeText(this.value, '#cssHighlightingContent');"
              onscroll="syncCodeScroll(this, '#cssHighlighting');"></textarea>
                <pre id="cssHighlighting" class="hgl-highlighting" aria-hidden="true">
          <code id="cssHighlightingContent" class="language-css"></code>
        </pre>
            </div>

            <div class="css-dialog-buttons">
                <span id="customCssMessage" class="formError" style="margin-right:auto;"></span>
                <button type="button" onclick="hideCssDialog()">
                    <spring:message code="common.close" />
                </button>
                <button type="button" onclick="saveCustomCssConfig()">
                    <spring:message code="common.save" />
                </button>
            </div>
        </div>
    </div>

    <!-- SCADA Configuration Dialog -->
    <div id="scadaConfigDialog" role="dialog" aria-modal="true"
         aria-label="<spring:message code='systemSettings.scadaConfTitle'/>">
        <div class="header">
    <span style="font-weight:600;">
      <spring:message code="systemSettings.scadaConfTitle" />
    </span>
            <button type="button" onclick="hideScadaConfigDialog()">
                <spring:message code="common.close" />
            </button>
        </div>
        <div id="scadaConfigContent" class="body">
            <!-- Filled by fetchScadaConfiguration() -->
        </div>
    </div>

    <script type="text/javascript">
			/* ===== Custom CSS editor: fetch/save & dialog control ===== */
			var customCssUrl = './api/customcss/';

			function showCssDialog() {
				var dialog = document.getElementById('css-editor-dialog');
				if (!dialog) return;
				dialog.style.display = 'flex';
				initCustomCssData();
			}

			function hideCssDialog() {
				var dialog = document.getElementById('css-editor-dialog');
				if (!dialog) return;
				dialog.style.display = 'none';
			}

			function initCustomCssData() {
				fetchCustomCssConfig().then(function(val) {
					try {
						var res = JSON.parse(val);
						var content = res && typeof res.content === 'string' ? res.content : '';
						var editor = document.getElementById('cssEditor');
						editor.value = content;
						updateCodeTextEscaped(content, '#cssHighlightingContent');
						// Keep scroll in sync after programmatic set:
						syncCodeScroll(editor, '#cssHighlighting');
					} catch (e) {
						setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
					}
				}).catch(function() {
					setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
				});
			}

			function fetchCustomCssConfig() {
				return new Promise(function(resolve, reject) {
					var req = new XMLHttpRequest();
					req.open('GET', customCssUrl, true);
					req.onload = function() {
						if (req.status === 200) resolve(req.responseText);
						else reject(req.status);
					};
					req.onerror = function() {
						reject(req.status);
					};
					req.send(null);
				});
			}

			function saveCustomCssConfig() {
				var req = new XMLHttpRequest();
				req.open('POST', customCssUrl, true);
				req.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
				setUserMessage("customCssMessage"); // clear

				req.onload = function() {
					if (req.status === 200) {
						setUserMessage("customCssMessage", "<spring:message code='systemSettings.customCssSaved'/>");
					} else if (req.status === 400) {
						try {
							var errors = JSON.parse(req.responseText);
							if (errors && errors.length > 0) {
								setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
							} else {
								setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
							}
						} catch (_e) {
							setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
						}
					} else {
						setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
					}
				};
				req.onerror = function() {
					setUserMessage("customCssMessage", "<spring:message code='systemSettings.invalidCustomCss'/>");
				};

				var cssContent = document.getElementById('cssEditor').value;
				req.send(JSON.stringify({ content: cssContent }));
			}

			/* ===== SCADA configuration dialog ===== */
			function showScadaConfigDialog() {
				var dialog = document.getElementById('scadaConfigDialog');
				if (!dialog) return;
				dialog.style.display = 'block';
				fetchScadaConfiguration();
			}

			function hideScadaConfigDialog() {
				var dialog = document.getElementById('scadaConfigDialog');
				if (!dialog) return;
				dialog.style.display = 'none';
			}

			function fetchScadaConfiguration() {
				var body = document.getElementById('scadaConfigContent');
				if (!body) return;
				body.innerHTML = "<em><spring:message code='systemSettings.retrieving'/></em>";

				SystemSettingsDwr.getScadaConfig(function(response) {
					try {
						var config = JSON.parse(response);
						var html = "<table style='border-collapse:collapse;table-layout:auto;width:100%'>" +
							"<thead><tr style='background:#f3f3f3'>" +
							"<th style='padding:6px;text-align:left;border:1px solid #ddd;white-space:nowrap;'>" +
							"Parameter</th>" +
							"<th style='padding:6px;text-align:left;border:1px solid #ddd;white-space:nowrap;'>" +
							"Value</th>" +
							"</tr></thead><tbody>";
						for (var key in config) {
							if (!config.hasOwnProperty(key)) continue;
							html += "<tr>" +
								"<td style='padding:6px;border:1px solid #eee;white-space:nowrap;'>" + key + "</td>" +
								"<td style='padding:6px;border:1px solid #eee;white-space:nowrap;'>" + config[key] + "</td>" +
								"</tr>";
						}
						html += "</tbody></table>";
						body.innerHTML = html;
					} catch (e) {
						body.innerHTML = "<span class='formError'>Error parsing configuration data.</span>";
					}
				});
			}
    </script>

    <tag:newPageNotification href="./app.shtm#/system-settings" ref="systemSettingsNotification" />

</tag:page>