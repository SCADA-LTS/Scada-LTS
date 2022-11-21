import { expect } from 'chai';

import dataPoint from '../../mocks/store/dataPointMock';

import PointPropChartRenderer from '@/views/DataPointDetails/PointProperties/PointPropChartRenderer';
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
		PointPropChartRenderer, 
		modules,
		{data: props}
	);
}

describe('Point Properties Tests --- Chart Renderer', () => {

	it('Initialize Component', () => {
		const wrapper = initWrapper();
		expect(wrapper.name()).to.equal('PointPropChartRenderer');
		expect(wrapper.vm.data.id).to.equal(1);
		expect(wrapper.vm.data.chartRenderer).to.equal(null);
		expect(wrapper.vm.selected).to.equal(-1);
	});

	it('Select has 4 Items', () => {
		const wrapper = initWrapper();
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(4);
	});

	it('Select type Table', () => {
		const wrapper = initWrapper();
		wrapper.vm.watchChartRendererChange(0);
		expect(wrapper.vm.data.chartRenderer.limit).to.equal(2);
		expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('TABLE');
	});

	it('Select type Image', async () => {
		const wrapper = initWrapper();
		wrapper.vm.watchChartRendererChange(1);
		expect(wrapper.vm.data.chartRenderer.timePeriod).to.equal(2);
		expect(wrapper.vm.data.chartRenderer.numberOfPeriods).to.equal(1);
		expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('IMAGE');
	});

	it('Select type Statistics', () => {
		const wrapper = initWrapper();
		wrapper.vm.watchChartRendererChange(2);
		expect(wrapper.vm.data.chartRenderer.timePeriod).to.equal(2);
		expect(wrapper.vm.data.chartRenderer.numberOfPeriods).to.equal(1);
		expect(wrapper.vm.data.chartRenderer.includeSum).to.equal(false);
		expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('STATS');
	});

	it('Load Image Chart Renderer', () => {
		let complexDataPointMock = Object.assign({}, dataPointMock);
		complexDataPointMock.chartRenderer = Object.assign(
			{},
			modules.dataPoint.state.chartRenderersTemplates[1],
		);
		const wrapper = initWrapper(complexDataPointMock);
		expect(wrapper.vm.data.chartRenderer).to.not.equal(null);
		expect(wrapper.vm.selected).to.equal(1);
		expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('IMAGE');
	});
});
