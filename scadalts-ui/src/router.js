import Vue from 'vue';
import Router from 'vue-router';
import Alarms from './views/Alarms';
import AlarmTabs from './views/AlarmTabs';
import About from './views/About';
import LoginPage from './views/LoginPage';
import HistoricalAlarms from './views/HistoricalAlarms';
import EventList from './views/EventList';
import ReportsPage from './views/ReportsPage';
import UserList from './views/UserList';
import SystemSettings from './views/SystemSettings';
import AlarmNotifications from './views/AlarmNotifications';
import RecipientList from './views/RecipientList';
import UserProfiles from './views/UserProfiles';
import DataPointList from './views/DataPointDetails/DataPointList';
import DataPointDetails from './views/DataPointDetails';
import SynopticPanelMenu from './views/SynopticPanel/SynopticPanelMenu';
import SynopticPanelItem from './views/SynopticPanel/SynopticPanelItem';
import DataSources from './views/DataSources';
import WatchList from './views/WatchList';
import WatchListItem from './views/WatchList/WatchListItem';
import HistoricalAlarmsComponent from './views/components/HistoricalAlarmsComponent';

import store from './store/index';

Vue.use(Router);

const routing = new Router({
	mode: 'hash',
	base: process.env.BASE_URL,
	routes: [
		{
			path: '/',
			name: 'home',
			component: AlarmTabs,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/login',
			name: 'login',
			component: LoginPage,
		},
		{
			path: '/about',
			name: 'about',
			component: About,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/alarms',
			name: 'alarms',
			component: AlarmTabs,
			meta: {
				requiresAuth: true,
			},
			children: [
				{
					path: 'plc',
					component: Alarms,
				},
				{
					path: 'plc-history',
					component: HistoricalAlarmsComponent,
				},
				{
					path: 'scada',
					name: 'scada',
					component: EventList,
				},
			],
		},
		{
			path: '/historical-alarms',
			name: 'historical-alarms',
			component: HistoricalAlarms,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/users',
			name: 'users',
			component: UserList,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/reports',
			name: 'reports',
			component: ReportsPage,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/system-settings',
			name: 'system-settings',
			component: SystemSettings,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/alarm-notifications',
			name: 'alarm-notifications',
			component: AlarmNotifications,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/recipient-list',
			name: 'recipient-list',
			component: RecipientList,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/user-profiles',
			name: 'user-profiles',
			component: UserProfiles,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/datapoint-list',
			name: 'datapoint-list',
			component: DataPointList,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/datapoint-details/:id',
			name: 'datapoint-details',
			component: DataPointDetails,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/datasources',
			name: 'datasources',
			component: DataSources,
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: '/synoptic-panel',
			name: 'synoptic-panel',
			component: SynopticPanelMenu,
			children: [
				{
					path: ':id',
					component: SynopticPanelItem,
				},
			],
		},
		{
			path: '/watch-list',
			name: 'watch-list',
			component: WatchList,
			children: [
				{
					path: ':id',
					component: WatchListItem,
				},
			],
		},
		{
			path: '/example-ph',
			name: 'example-ph',
			component: () =>
				import(
					/* webpackChunkName: "ph" */ './views/components/ExampleExportImportPointHierarchy.vue'
				),
		},
		{
			path: '/example-test',
			name: 'example-test',
			component: () =>
				import(
					/* webpackChunkName: "test-component" */ './views/components/ExampleTest.vue'
				),
		},
		{
			path: '/example-isalive',
			name: 'example-isalive',
			component: () =>
				import(
					/* webpackChunkName: "isalive-component" */ './views/components/ExampleIsAlive.vue'
				),
		},
		{
			path: '/example-simple-component-svg',
			name: 'example-simple-component-svg',
			component: () =>
				import(
					/* webpackChunkName: "simple-component-svg" */ './views/components/ExampleSimpleComponentSVG.vue'
				),
		},
		{
			path: '/example-sleep-and-reactivation-ds',
			name: 'example-sleep-and-reactivation-ds',
			component: () =>
				import(
					/* webpackChunkName: "sleep-and-reactivation-ds-component" */ './views/components/ExampleSleepAndReactivationDS.vue'
				),
		},
		{
			path: '/example-cmp',
			name: 'example-cmp',
			component: () =>
				import(
					/* webpackChunkName: "cmp-component" */ './views/components/ExampleCmp.vue'
				),
		},
		{
			path: '/example-chart-cmp',
			name: 'example-chart-cmp',
			// component: ExampleChartCmp,
			component: () =>
				import(
					/* webpackChunkName: "example-chart-cmp" */ './views/components/ExampleChartCmp.vue'
				),
		},
		{
			path: '/example-live-alarms',
			name: 'example-live-alarms',
			component: () =>
				import(
					/* webpackChunkName: "live-alarms-component" */ './views/components/ExampleLiveAlarms.vue'
				),
		},
	],
});

routing.beforeEach((to, from, next) => {
	if (to.meta.requiresAuth) {
		if (!store.state.loggedUser) {
			store.dispatch('getUserInfo').catch(() => {
				next({ name: 'login' });
			});
		}
	}
	next();
});

export default routing;
