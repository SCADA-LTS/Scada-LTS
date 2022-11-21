import { expect } from 'chai';

import dataPoint from '../../mocks/store/dataPointMock';

import PointPropTextRenderer from '@/views/DataPointDetails/PointProperties/PointPropTextRenderer';
import dataPointMock from '../../mocks/objects/DataPointMock';

import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	dataPoint,
};

/**
 * @private
 * Initialize VueWrapper for local testing
 * Prepare wrapper wiht all required stubs and props.
 */
function initWrapper(props = dataPointMock) {
	return prepareMountWrapper(
		PointPropTextRenderer,
		modules,
		{data: props}
	);
}

global.requestAnimationFrame = (cb) => cb();

describe('Point Properties Tests - Text Renderer Alphanumeric', () => {

	it('Initialize Component with Alphanumeric DP', () => {
		const wrapper = initWrapper();
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(1);
		expect(items[0].label).contains('Plain');
	});
});


describe('Point Properties Tests - Text Renderer Numeric', () => {
	let wrapper;

	beforeEach(() => {
        let numericDataPointMock = Object.assign({}, dataPointMock);
		numericDataPointMock.pointLocator.dataTypeId = 3;
		wrapper = initWrapper(numericDataPointMock);
	});

	it('Initialize Component with Numeric DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(4);
		expect(items[0].label).contains('Analog');
		expect(items[1].label).contains('Plain');
        expect(items[2].label).contains('Range');
        expect(items[3].label).contains('Time');
	});

	it('Chceck Analog', async () => {
        wrapper.vm.selected=0;
		await wrapper.vm.watchTextRendererChange(0);
        
		wrapper.find('#renderer-analog input:first-of-type').element.value = '0.00';
		wrapper.find('#renderer-analog input:first-of-type').trigger('input');
		wrapper.find('#renderer-analog > .col:nth-of-type(2) input').element.value = 'meters';
		wrapper.find('#renderer-analog > .col:nth-of-type(2) input').trigger('input');

		expect(wrapper.vm.data.textRenderer.format).to.equal('0.00');
		expect(wrapper.vm.data.textRenderer.suffix).to.equal('meters');

	});

    it('Chceck Plain', async () => {
        wrapper.vm.selected=3;
		await wrapper.vm.watchTextRendererChange(3);
        
		wrapper.find('#renderer-plain .col:nth-of-type(1) input').element.value = 'liters';
		wrapper.find('#renderer-plain .col:nth-of-type(1) input').trigger('input');
		
		expect(wrapper.vm.data.textRenderer.format).to.equal('liters');
	});

    it('Chceck Range', async () => {
        wrapper.vm.selected=4;
		await wrapper.vm.watchTextRendererChange(4);
        
		wrapper.find('#renderer-range > .col:nth-of-type(3) input').element.value = '1';
		wrapper.find('#renderer-range > .col:nth-of-type(3) input').trigger('input');
        wrapper.find('#renderer-range > .col:nth-of-type(4) input').element.value = '5';
		wrapper.find('#renderer-range > .col:nth-of-type(4) input').trigger('input');
        wrapper.find('#renderer-range > .col:nth-of-type(5) input').element.value = 'ExampleText';
		wrapper.find('#renderer-range > .col:nth-of-type(5) input').trigger('input');

        wrapper.find('#renderer-range .v-input__icon--append-outer button').trigger('click');
		
        expect(wrapper.vm.data.textRenderer.rangeValues.length).to.equal(1);
		expect(wrapper.vm.data.textRenderer.rangeValues[0].from).to.equal('1');
		expect(wrapper.vm.data.textRenderer.rangeValues[0].to).to.equal('5');
        expect(wrapper.vm.data.textRenderer.rangeValues[0].text).to.equal('ExampleText');
	});

});




describe('Point Properties Tests - Text Renderer Binary', () => {
	let wrapper;

	beforeEach(() => {
        let binaryDataPointMock = Object.assign({}, dataPointMock);
		binaryDataPointMock.pointLocator.dataTypeId = 1;
		wrapper = initWrapper(binaryDataPointMock);
	});

	it('Initialize Component with Binary DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(2);
		expect(items[0].label).contains('Binary');
		expect(items[1].label).contains('Plain');
	});

	it('Chceck Binary', async () => {
        wrapper.vm.selected=1;
		await wrapper.vm.watchTextRendererChange(1);
        
		wrapper.find('#renderer-binary > .col:nth-of-type(2) input').element.value = 'ZeroTest';
		wrapper.find('#renderer-binary > .col:nth-of-type(2) input').trigger('input');
        wrapper.find('#renderer-binary > .col:nth-of-type(4) input').element.value = 'OneTest';
		wrapper.find('#renderer-binary > .col:nth-of-type(4) input').trigger('input');

		expect(wrapper.vm.data.textRenderer.zeroLabel).to.equal('ZeroTest');
		expect(wrapper.vm.data.textRenderer.oneLabel).to.equal('OneTest');

	});

});

describe('Point Properties Tests - Text Renderer Multistate', () => {
	let wrapper;

	beforeEach(() => {
        let multistateDataPointMock = Object.assign({}, dataPointMock);
		multistateDataPointMock.pointLocator.dataTypeId = 2;
		wrapper = initWrapper(multistateDataPointMock);
	});

	it('Initialize Component with Multistate DP', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(2);
		expect(items[0].label).contains('Multistate');
		expect(items[1].label).contains('Plain');
	});

	it('Chceck Multistate', async () => {
        wrapper.vm.selected=2;
		await wrapper.vm.watchTextRendererChange(2);
        
		wrapper.find('#renderer-multistate > .col:nth-of-type(2) input').element.value = '1';
		wrapper.find('#renderer-multistate > .col:nth-of-type(2) input').trigger('input');
        wrapper.find('#renderer-multistate > .col:nth-of-type(3) input').element.value = 'TestMultiRenderer';
		wrapper.find('#renderer-multistate > .col:nth-of-type(3) input').trigger('input');

        wrapper.find('#renderer-multistate .v-input__icon--append-outer button').trigger('click');
		
        expect(wrapper.vm.data.textRenderer.multistateValues.length).to.equal(1);
		expect(wrapper.vm.data.textRenderer.multistateValues[0].key).to.equal('1');
        expect(wrapper.vm.data.textRenderer.multistateValues[0].text).to.equal('TestMultiRenderer');

	});

});
