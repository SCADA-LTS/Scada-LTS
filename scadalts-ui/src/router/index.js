import Vue from 'vue';
import { createRouter } from 'vue-router';

import About from '@/views/About.vue';
import LoginPage from '@/views/LoginPage/index.vue';

import alarmRoutes from './routes-alarms';
import exampleRoutes from './routes-examples';
import systemRoutes from './routes-system';
import userRoutes from './routes-users';
import dataSourceRoutes from './routes-datasources';
import eventRoutes from './routes-events';

import GraphicalView from '@/views/GraphicalViews/index.vue';

import PublicView from '@/views/GraphicalViews/AnonymousViewPage.vue';
import ReportTabs from '@/views/Reports/ReportTabs.vue';
import ReportsPage from '@/views/Reports/ReportsPage.vue';
import ReportsData from '@/views/Reports/ReportsData.vue';

import SynopticPanelMenu from '@/views/SynopticPanel/SynopticPanelMenu.vue';
import SynopticPanelItem from '@/views/SynopticPanel/SynopticPanelItem.vue';
import WatchList from '@/views/WatchList/index.vue';
import WatchListItem from '@/views/WatchList/WatchListItem.vue';
import UnauthorizedPage from '@/views/401.vue';

import store from '@/store/index';
import GraphicalViewPage from '@/views/GraphicalViews/GraphicalViewPage.vue';

Vue.use(createRouter);

const routing = new createRouter({
	mode: 'hash',
	base: process.env.BASE_URL,
	routes: [
		
		{
			path: '/login',
			name: 'login',
			component: LoginPage,
		},
		{
			path: '/401',
			name: '401',
			component: UnauthorizedPage,
		},
		{
			path: '/about',
			name: 'about',
			component: About,
			meta: {
				requiresAuth: true
			},
		},
        {
            path: '/graphical-view',
			name: 'graphical-view',
			component: GraphicalView,
			meta: {
                requiresAuth: true
            },
			children: [
				{
					path: ':id',
					component: GraphicalViewPage
				}
			]

        },
		{
            path: '/public-view',
			name: 'public-view',
			component: PublicView,
			children: [
				{
					path: ':id',
					component: GraphicalViewPage
				}
			]

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
			path: '/reports',
			name: 'reports',
			component: ReportTabs,
			meta: {
				requiresAuth: true,
			}
		},
		{
			path: '/watch-list',
			name: 'watch-list',
			component: WatchList,
			children: [
				{
					path: ':id',
					component: WatchListItem,
				}
			]
		},
        ...alarmRoutes,
        ...dataSourceRoutes,
        ...eventRoutes,
        ...userRoutes,
        ...systemRoutes,
		...exampleRoutes,		

	],
});

routing.beforeEach((to, from, next) => {
	if (to.meta.requiresAuth) {
		if (!store.state.loggedUser) {
			store.dispatch('getUserInfo')
				.catch(() => {
					next({ name: 'login' });
				})
		}
	}
	if(to.meta.requiresAdmin) {
		if(!!store.state.loggedUser && !store.state.loggedUser.admin) {
			next({ name: '401' });
		}
	}
	next();
})

export default routing;
