import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import dataPoint from '../../mocks/store/dataPointMock';

import PointPropEventRenderer from '@/views/DataPointDetails/PointProperties/PointPropEventRenderer';
import dataPointMock from '../../mocks/objects/DataPointMock';

const modules = {
    dataPoint
}


const store = new Vuex.Store({ modules });

global.requestAnimationFrame = (cb) => cb();

const localVue = createLocalVue();
localVue.use(i18n);
localVue.use(Vuex);
const vuetify = Vuetify;

const mountFunction = (options) => {
	return mount(PointPropEventRenderer, {
		store,
		localVue,
		vuetify,
		i18n,
		...options,
	});
};

describe('Point Properties Tests - Event Renderer Numeric', () => {
    let wrapper
    
	beforeEach(() => {
		wrapper = mountFunction({
			propsData: {
				data: dataPointMock,
			},
		});
	});

	it('Initialize Component with Numeric DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(2);
		expect(items[0].label).contains('None');
		expect(items[1].label).contains('Range');
	});

});

describe('Point Properties Tests - Event Renderer Binary', () => {
    let wrapper

    beforeEach(() => {
		let binaryDataPointMock = Object.assign({}, dataPointMock);
		binaryDataPointMock.pointLocator.dataTypeId = 1;
		wrapper = mountFunction({
			propsData: {
				data: binaryDataPointMock,
			},
		});
	});

    it('Initialize Component with Binary DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(2);
		expect(items[0].label).contains('None');
		expect(items[1].label).contains('Binary');
	});

	
});

describe('Point Properties Tests - Event Renderer Multistate', () => {
    let wrapper

    beforeEach(() => {
		let binaryDataPointMock = Object.assign({}, dataPointMock);
		binaryDataPointMock.pointLocator.dataTypeId = 2;
		wrapper = mountFunction({
			propsData: {
				data: binaryDataPointMock,
			},
		});
	});

    it('Initialize Component with Multistate DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(2);
		expect(items[0].label).contains('None');
		expect(items[1].label).contains('Multistate');
	});

	
});
