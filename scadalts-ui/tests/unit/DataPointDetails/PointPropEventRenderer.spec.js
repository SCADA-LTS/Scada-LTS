import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import dataPoint from '../../mocks/store/dataPointMock';

import PointPropEventRenderer from '@/views/DataPointDetails/PointProperties/PointPropEventRenderer';
import dataPointMock from '../../mocks/objects/DataPointMock';

const modules = {
	dataPoint,
};

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
	let wrapper;

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

	it('Add range', async () => {
		await wrapper.vm.watchEventRendererChange(4);
		wrapper.find('#renderer-range input:first-of-type').element.value = '2';
		wrapper.find('#renderer-range input:first-of-type').trigger('input');
		wrapper.find('#renderer-range > .col:nth-of-type(2) input').element.value = '4';
		wrapper.find('#renderer-range > .col:nth-of-type(2) input').trigger('input');
		wrapper.find('#renderer-range > .col:nth-of-type(3) input').element.value =
			'ExampleLabel';
		wrapper.find('#renderer-range > .col:nth-of-type(3) input').trigger('input');
		expect(wrapper.vm.rangeRenderer.from).to.equal('2');
		expect(wrapper.vm.rangeRenderer.to).to.equal('4');
		expect(wrapper.vm.rangeRenderer.text).to.equal('ExampleLabel');

		wrapper.find('#renderer-range button').trigger('click');
		expect(wrapper.vm.data.eventTextRenderer.rangeEventValues.length).to.equal(2);
		expect(wrapper.vm.data.eventTextRenderer.rangeEventValues[1].text).to.equal(
			'ExampleLabel',
		);
	});

	it('Delete range', async () => {
		await wrapper.vm.watchEventRendererChange(4);
		expect(wrapper.vm.data.eventTextRenderer.rangeEventValues.length).to.equal(2);
		wrapper
			.find('#renderer-range button[class*="mdi-close-circle-outline"]:last-of-type')
			.trigger('click');
		expect(wrapper.vm.data.eventTextRenderer.rangeEventValues.length).to.equal(1);
	});
});

describe('Point Properties Tests - Event Renderer Binary', () => {
	let wrapper;

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

	it('Add Binary Keys', async () => {
		wrapper.vm.selected = 1;
		await wrapper.vm.watchEventRendererChange(1);
		wrapper.find('#renderer-binary > .col:nth-of-type(1) input').element.value =
			'ZeroText';
		wrapper.find('#renderer-binary > .col:nth-of-type(1) input').trigger('input');
		wrapper.find('#renderer-binary > .col:nth-of-type(2) input').element.value =
			'OneText';
		wrapper.find('#renderer-binary > .col:nth-of-type(2) input').trigger('input');

		expect(wrapper.vm.data.eventTextRenderer.oneLabel).to.equal('OneText');
		expect(wrapper.vm.data.eventTextRenderer.zeroLabel).to.equal('ZeroText');
	});
});

describe('Point Properties Tests - Event Renderer Multistate', () => {
	let wrapper;

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

	it('Add and Delete Multistate Key', async () => {
		wrapper.vm.selected = 2;
		await wrapper.vm.watchEventRendererChange(2);
		wrapper.find('#renderer-multistate > .col:nth-of-type(1) input').element.value = '0';
		wrapper.find('#renderer-multistate > .col:nth-of-type(1) input').trigger('input');
		wrapper.find('#renderer-multistate > .col:nth-of-type(2) input').element.value =
			'TestingMultistate';
		wrapper.find('#renderer-multistate > .col:nth-of-type(2) input').trigger('input');
		expect(wrapper.vm.multistateRenderer.key).to.equal('0');
		expect(wrapper.vm.multistateRenderer.text).to.equal('TestingMultistate');

		wrapper.find('#renderer-multistate button').trigger('click');
		expect(wrapper.vm.data.eventTextRenderer.multistateEventValues.length).to.equal(1);
		expect(wrapper.vm.data.eventTextRenderer.multistateEventValues[0].key).to.equal('0');
		expect(wrapper.vm.data.eventTextRenderer.multistateEventValues[0].text).to.equal(
			'TestingMultistate',
		);

		await wrapper.vm.$nextTick();

		expect(wrapper.vm.data.eventTextRenderer.multistateEventValues.length).to.equal(1);
		wrapper
			.find('#renderer-multistate button[class*="mdi-close-circle-outline"]:last-of-type')
			.trigger('click');
		expect(wrapper.vm.data.eventTextRenderer.multistateEventValues.length).to.equal(0);
	});
});

describe('Point Properties Tests - Event Renderer None', () => {
	let wrapper;

	beforeEach(() => {
		let binaryDataPointMock = Object.assign({}, dataPointMock);
		binaryDataPointMock.pointLocator.dataTypeId = 4;
		wrapper = mountFunction({
			propsData: {
				data: binaryDataPointMock,
			},
		});
	});

	it('Initialize Component with Alphanumeric DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(1);
		expect(items[0].label).contains('None');
	});
});
