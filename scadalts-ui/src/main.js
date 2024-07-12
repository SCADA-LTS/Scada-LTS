import 'bootstrap/dist/css/bootstrap.min.css';

import Vue from 'vue';
import App from './apps/App.vue';
import router from './router/index';
import store from './store';

import VueCookie from 'vue-cookie';
import VueLogger from 'vuejs-logger';
import VueDayjs from 'vue-dayjs-plugin';

import IsAlive from './components/graphical_views/IsAlive';
import Watchdog from './components/graphical_views/watchdog';
import CMP from './components/graphical_views/cmp/CMP';
import AutoManual from './components/graphical_views/cmp2/AutoManual'
import AutoManual3 from './components/graphical_views/cmp3/AutoManual3'
import SimpleComponentSVG from './components/graphical_views/SimpleComponentSVG';
import ExportImportPointHierarchy from './components/point_hierarchy/ExportImportPointHierarchy';
import SleepAndReactivationDS from './components/forms/SleepAndReactivationDS';
import VueLodash from 'vue-lodash';

import LineChartComponent from './components/amcharts/LineChartComponent';
import RangeChartComponent from './components/amcharts/RangeChartComponent';
import TableComponent from './components/graphical_views/pointTables/SimplePointTable.vue'

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { library } from '@fortawesome/fontawesome-svg-core';
import {
	faCoffee,
	faTimes,
	faBars,
	faBell,
	faFileMedicalAlt,
	faInfo,
	faListAlt,
	faCogs,
} from '@fortawesome/free-solid-svg-icons';
import i18n from './i18n';
import LiveAlarms from './components/graphical_views/AlarmsComponent';
import RefreshView from '@/components/graphical_views/RefreshView';
import SMSDomain from '@/components/forms/SMSDomain';
import vuetify from './plugins/vuetify';
import 'roboto-fontface/css/roboto/roboto-fontface.css';
import '@mdi/font/css/materialdesignicons.css';
import * as uiv from 'uiv';
import svgJS from './plugins/svg';

library.add(
	faCoffee,
	faTimes,
	faBars,
	faBell,
	faFileMedicalAlt,
	faInfo,
	faListAlt,
	faCogs
);

Vue.component('font-awesome-icon', FontAwesomeIcon);

const isProduction = process.env.NODE_ENV === 'production';

const options = {
	isEnabled: true,
	logLevel: isProduction ? 'error' : 'debug',
	stringifyArguments: false,
	showLogLevel: true,
	showMethodName: true,
	separator: '|',
	showConsoleColors: true,
};

Vue.use(VueLogger, options);

const optionsLodash = { name: 'lodash' };

Vue.use(VueLodash, optionsLodash);

Vue.use(VueCookie);
Vue.use(VueDayjs);

Vue.use(svgJS);

Vue.config.devtools = true;

if (window.location.href.indexOf('app.shtm') != -1) {
    console.log('app.shtm');
    new Vue({
        router,
        store,
        i18n,
        vuetify,
        render: (h) => h(App),
    }).$mount('#app');
}

Vue.use(uiv);

if (window.document.getElementById('app-isalive') != undefined) {
	console.log('app-isalive');
	const isAliveDom = document.getElementById('app-isalive');
	new Vue({
		store,
		render: (h) =>
			h(IsAlive, {
				props: {
					plabel: isAliveDom.getAttribute('plabel'),
					ptimeWarning: isAliveDom.getAttribute('ptime-warning'),
					ptimeError: isAliveDom.getAttribute('ptime-error'),
					ptimeRefresh: isAliveDom.getAttribute('ptime-refresh'),
					pTextWarning: isAliveDom.getAttribute('ptext-warning'),
					feedbackUrl: isAliveDom.getAttribute('feedback-url'),
				},
			}),
	}).$mount('#app-isalive');
}

const watchdogId = 'app-isalive2';
if (window.document.getElementById(watchdogId) != undefined) {
	console.log(watchdogId);
	const watchdogEl = document.getElementById(watchdogId);
	new Vue({
		store,
		i18n,
		vuetify,
		render: (h) =>
			h(Watchdog, {
				props: {
					name: watchdogEl.getAttribute('name'),
					interval: watchdogEl.getAttribute('interval') !== null ? Number(watchdogEl.getAttribute('interval')) : 10000,
					wdHost: watchdogEl.getAttribute('wd-host'),
					wdPort: watchdogEl.getAttribute('wd-port') !== null ? Number(watchdogEl.getAttribute('wd-port')) : null,
					wdMessage: watchdogEl.getAttribute('wd-message'),
					dpValidation: watchdogEl.getAttribute('dp-validation') !== null ? JSON.parse(watchdogEl.getAttribute('dp-validation')) : null,
					dpBreak: watchdogEl.getAttribute('dp-break') !== null,
					dpWarnAsFail: watchdogEl.getAttribute('dp-warn-as-fail') !== null,
				},
			}),
	}).$mount(`#${watchdogId}`);
}

for (let i = 0; i < 20; i++) {
	const cmpId = `app-cmp-${i}`;
	if (window.document.getElementById(cmpId) != undefined) {
        console.log('cmpId: ' + cmpId);
		new Vue({
			vuetify,
			render: (h) =>
				h(CMP, {
					store,
					props: {
						pLabel: window.document.getElementById(cmpId).getAttribute('plabel'),
						pTimeRefresh: window.document
							.getElementById(cmpId)
							.getAttribute('ptimeRefresh'),
						pConfig: window.document.getElementById(cmpId).getAttribute('pconfig'),
						pxIdViewAndIdCmp: window.document
							.getElementById(cmpId)
							.getAttribute('pxIdViewAndIdCmp'),
					},
				}),
		}).$mount('#' + cmpId);
	}
}

for (let i = 0; i < 10; i++) {
	const cmpId = `app-cmp2-${i}`;
	const el = window.document.getElementById(cmpId);
	if (el != undefined) {
		console.log('cmpId: ' + cmpId);
		new Vue({
			store,
			i18n,
			vuetify,
			render: (h) =>
				h(AutoManual, {
					props: {
						pConfig: JSON.parse(el.getAttribute('pconfig')),
						pLabel: el.getAttribute('plabel'),
						pTimeRefresh: el.getAttribute('ptimeRefresh') !== null ? el.getAttribute('ptimeRefresh') : 10000,
						pxIdViewAndIdCmp: el.getAttribute('pxIdViewAndIdCmp'),
						pZeroState: el.getAttribute('pzeroState') !== null ? el.getAttribute('pzeroState') : 'Auto',
						pWidth: el.getAttribute('pwidth') !== null ? el.getAttribute('pwidth') : 140,
						pRequestTimeout: el.getAttribute('prequestTimeout') !== null ? el.getAttribute('prequestTimeout') : 5000,
						pHideControls: el.getAttribute('phideControls') !== null,
						pDebugRequest: el.getAttribute('pdebugRequest') !== null,
					},
				})
		}).$mount('#' + cmpId);
	}
}

for (let i = 0; i < 10; i++) {
	const cmpId = `app-cmp3-${i}`;
	const el = window.document.getElementById(cmpId);
	if (el != undefined) {
		console.log('cmpId: ' + cmpId);
		new Vue({
			store,
			i18n,
			vuetify,
			render: (h) =>
				h(AutoManual3, {
					props: {
						pConfig: JSON.parse(el.getAttribute('pconfig')),
						pLabel: el.getAttribute('plabel'),
						pTimeRefresh: el.getAttribute('ptimeRefresh') !== null ? el.getAttribute('ptimeRefresh') : 10000,
						pxIdViewAndIdCmp: el.getAttribute('pxIdViewAndIdCmp'),
						pZeroState: el.getAttribute('pzeroState') !== null ? el.getAttribute('pzeroState') : 'Auto',
						pWidth: el.getAttribute('pwidth') !== null ? el.getAttribute('pwidth') : 140,
						pRequestTimeout: el.getAttribute('prequestTimeout') !== null ? el.getAttribute('prequestTimeout') : 5000,
						pHideControls: el.getAttribute('phideControls') !== null,
						pDebugRequest: el.getAttribute('pdebugRequest') !== null,
					},
				})
		}).$mount('#' + cmpId);
	}
}




if (window.document.getElementById('simple-component-svg') != undefined) {
	console.log('simple-component-svg');
	new Vue({
		render: (h) =>
			h(SimpleComponentSVG, {
				props: {
					pxidPoint: window.document
						.getElementById('simple-component-svg')
						.getAttribute('pxidPoint'),
					ptimeRefresh: window.document
						.getElementById('simple-component-svg')
						.getAttribute('ptimeRefresh'),
					plabel: window.document
						.getElementById('simple-component-svg')
						.getAttribute('plabel'),
					pvalue: window.document
						.getElementById('simple-component-svg')
						.getAttribute('pvalue'),
				},
			}),
	}).$mount('#simple-component-svg');
}

if (window.document.getElementById('sleep-reactivation-ds') != undefined) {
	console.log('sleep-reactivation-ds');
	new Vue({
		render: (h) => h(SleepAndReactivationDS),
	}).$mount('#sleep-reactivation-ds');
}

if (window.document.getElementById('sms-domain') != undefined) {
	console.log('sms-domain');
	new Vue({
		vuetify,
		render: (h) => h(SMSDomain),
	}).$mount('#sms-domain');
}

if (window.document.getElementById('export-import-ph') != undefined) {
	console.log('export-import-ph');
	new Vue({
		render: (h) => h(ExportImportPointHierarchy),
	}).$mount('#export-import-ph');
}

for (let x = 0; x < 10; x++) {
	const chartId = `chart-line-${x}`;
	const el = window.document.getElementById(chartId);
	if (el != undefined) {
		console.log('chartId: ' + chartId);
		new Vue({
			render: (h) =>
				h(LineChartComponent, {
					props: {
						pointIds: el.getAttribute('point-ids'),
						useXid: el.getAttribute('use-xid') !== null,
						separateAxis: el.getAttribute('separate-axes') !== null,
						stepLine: el.getAttribute('step-line') !== null,
						startDate: el.getAttribute('start-date'),
						endDate: el.getAttribute('end-date'),
						refreshRate: el.getAttribute('refresh-rate'),
						width: el.getAttribute('width') !== null ? el.getAttribute('width') : '500',
						height: el.getAttribute('height') !== null ? el.getAttribute('height') : '400',
						color: el.getAttribute('color'),
						strokeWidth: Number(el.getAttribute('stroke-width')),
						aggregation: Number(el.getAttribute('aggregation')),
						showScrollbar: el.getAttribute('show-scrollbar') !== null,
						showLegend: el.getAttribute('show-legned') !== null,
						showBullets: el.getAttribute('show-bullets') !== null,
						showExportMenu: el.getAttribute('show-export-menu') !== null,
						smoothLine: Number(el.getAttribute('smooth-line')),
						serverValuesLimit: Number(el.getAttribute('server-values-limit')),
						serverLimitFactor: Number(el.getAttribute('server-limit-factor')),
						webSocketEnabled: el.getAttribute('web-socket-enabled') !== null,
						showControls: el.getAttribute('show-controls') !== null,
					},
				}),
		}).$mount(`#${chartId}`);
	}
}

for (let x = 0; x < 10; x++) {
	const chartId = `chart-range-${x}`;
	const el = window.document.getElementById(chartId);
	if (el != undefined) {
		console.log('chartId: ' + chartId);
		new Vue({
			store,
			vuetify,
			render: (h) =>
				h(RangeChartComponent, {
					props: {
						chartId: x,
						pointIds: el.getAttribute('point-ids'),
						useXid: el.getAttribute('use-xid') !== null,
						separateAxis: el.getAttribute('separate-axes') !== null,
						stepLine: el.getAttribute('step-line') !== null,
						aggregation: Number(el.getAttribute('aggregation')),
						strokeWidth: Number(el.getAttribute('stroke-width')),
						showBullets: el.getAttribute('show-bullets') !== null,
						showExportMenu: el.getAttribute('show-export-menu') !== null,
						smoothLine: Number(el.getAttribute('smooth-line')),
						width: el.getAttribute('width') !== null ? el.getAttribute('width') : '500',
						height: el.getAttribute('height') !== null ? el.getAttribute('height') : '400',
						color: el.getAttribute('color'),
						serverValuesLimit: Number(el.getAttribute('server-values-limit')),
						serverLimitFactor: Number(el.getAttribute('server-limit-factor')),
					},
				}),
		}).$mount(`#${chartId}`);
	}
}

for (let x = 0; x < 10; x++) {
	const baseId = `simple-table-${x}`;
	const el = window.document.getElementById(baseId);
	if (el != undefined) {
	    console.log('baseId: ' + baseId);
		new Vue({
			store,
			vuetify,
			render: (h) =>
				h(TableComponent, {
					props: {
						pointIds: el.getAttribute('point-ids'),
						startDate: el.getAttribute('start-date'),
						showTotal: el.getAttribute('total') !== null,
						showAverage: el.getAttribute('average') !== null,
						showMax: el.getAttribute('max') !== null,
						showMin: el.getAttribute('min') !== null,
						roundValue: Number(el.getAttribute('round')),
						maxWidth: el.getAttribute('width' !== null ? Number(el.getAttribute('width')) : 600 ),
						maxHeight: el.getAttribute('height' !== null ? Number(el.getAttribute('height')) : 400 ),
					},
				}),
		}).$mount(`#${baseId}`);
	}
}

if (window.document.getElementById('refresh-view') != undefined) {
	console.log('refresh-view');
	new Vue({
		store,
		render: (h) =>
			h(RefreshView, {
				props: {
					ptimeToCheckRefresh: window.document
						.getElementById('refresh-view')
						.getAttribute('ptimeToCheckRefresh'),
					pviewId: window.document.getElementById('refresh-view').getAttribute('pviewId'),
				},
			}),
	}).$mount('#refresh-view');
}

if (window.document.getElementById('live-alarms') != undefined) {
    console.log('live-alarms');
	console.log(
		`test+ ${window.document
			.getElementById('live-alarms')
			.getAttribute('show-acknowledge-btn')}`
	);

	new Vue({
		store,
		vuetify,
		render: (h) =>
			h(LiveAlarms, {
				props: {
					pShowAcknowledgeBtn: window.document
						.getElementById('live-alarms')
						.getAttribute('show-acknowledge-btn'),
					pShowMainToolbar: window.document
						.getElementById('live-alarms')
						.getAttribute('show-main-toolbar'),
					pShowSelectToAcknowledge: window.document
						.getElementById('live-alarms')
						.getAttribute('show-select-to-acknowledge'),
					pShowPagination: window.document
						.getElementById('live-alarms')
						.getAttribute('show-pagination'),
					pMaximumNumbersOfRows: window.document
						.getElementById('live-alarms')
						.getAttribute('max-number-of-rows'),
				},
			}),
	}).$mount('#live-alarms');
}
