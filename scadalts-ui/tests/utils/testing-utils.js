/**
 * Testing Utils for Scada-LTS UI application
 *
 * Set of useful functions to provide better testing expierience.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
import { createLocalVue, mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import i18n from '@/i18n';
import router from '@/router/index.js';
import * as uiv from 'uiv';

import mainStore from '../mocks/store/index';

/**
 * Prepare Mount Wrapper
 *
 * Prepare ScadaLTS frontend VueTest wrapper that contains
 * all reqired dependencies like translations, Vuex and Vuetify modules.
 * Initialize everything using simple JavaScript function.
 *
 * @param {VueComponent} component - Vue component to be tested
 * @param {Object} storeModules - Vuex Modules to be included in tests
 * @param {Object} [propsData] - Vue component properties to be passed to the test
 * @param  {...any} options - Additional parameters like propsData and so on...
 */
export function prepareMountWrapper(component, storeModules, propsData = {}, ...options) {
	addElemWithDataAppToBody();
	const localVue = createLocalVue();
	const store = new Vuex.Store({ modules: storeModules, state: mainStore.state });
	const vuetify = Vuetify;
	localVue.use(i18n);
	localVue.use(uiv);
	localVue.use(Vuex);
	localVue.use(router);

	return mount(component, {
		localVue,
		vuetify,
		store,
		i18n,
		propsData,
		router,
		...options,
	});
}

/**
 * Prepare Shallow Mount Wrapper
 *
 * Prepare ScadaLTS frontend VueTest shallow wrapper that contains
 * all reqired dependencies like translations, Vuex and Vuetify modules.
 * Instead of classic Mount Wrapper it has stubbed the child components,
 * that can increase the testing performacne. It is usefull to prepare simple
 * unit tests.
 *
 * @param {VueComponent} component - Vue component to be tested
 * @param {Object} storeModules - Vuex Modules to be included in tests
 * @param {Object} [propsData] - Vue component properties to be passed to the test
 * @param  {...any} options - Additional parameters like propsData and so on...
 */
export function prepareShallowMountWrapper(
	component,
	storeModules,
	propsData = {},
	...options
) {
	addElemWithDataAppToBody();
	const localVue = createLocalVue();
	const store = new Vuex.Store({ modules: storeModules, state: mainStore.state });
	const vuetify = Vuetify;
	localVue.use(i18n);
	localVue.use(uiv);
	localVue.use(Vuex);
	localVue.use(router);

	return shallowMount(component, {
		localVue,
		vuetify,
		store,
		i18n,
		propsData,
		...options,
	});
}

/**
 * Local Storage Mock Class
 *
 * To perform Local storage operations without browser
 * just use this LocalStorage Mock class.
 */
export class LocalStorageMock {
	constructor() {
		this.store = {};
	}

	clear() {
		this.store = {};
	}

	getItem(key) {
		return this.store[key] || null;
	}

	setItem(key, value) {
		this.store[key] = JSON.stringify(value);
	}

	removeItem(key) {
		delete this.store[key];
	}
}

/**
 * Add Waraping element to the body to make Vuetify not
 * complaining about missing data-app atribure.
 * @private
 */
function addElemWithDataAppToBody() {
	const app = document.createElement('div');
	app.setAttribute('data-app', true);
	document.body.append(app);
}
