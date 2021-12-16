import { expect } from 'chai';

import dataPoint from '../../mocks/store/dataPointMock';

import PointPropLogging from '@/views/DataObjects/DataPointDetails/PointProperties/PointPropLogging';
import dataPointMock from '../../mocks/objects/DataPointMock';
import eventDetectorModule from '../../mocks/store/dataPointDetailsMock'

import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	dataPoint,
	eventDetectorModule
};

/**
 * @private
 * Initialize VueWrapper for local testing
 * Prepare wrapper wiht all required stubs and props.
 */
function initWrapper() {
	return prepareMountWrapper(PointPropLogging, modules, { data: dataPointMock });
}

global.requestAnimationFrame = (cb) => cb();

describe('Point Properties Tests - Logging properties', () => {
	it('Initialize Component', () => {
		const wrapper = initWrapper();
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(5);
		expect(wrapper.vm.data.loggingType).to.equal(1);
		expect(wrapper.get('.row[style="display: none;"]').html()).to.contain(
			'Interval logging period every',
		);
	});

	it('Change to "Interval" property', async () => {
		const wrapper = initWrapper();
		wrapper.vm.data.loggingType = 4;
		await wrapper.vm.$nextTick();
		expect(wrapper.text()).contains('logging period');
	});

	it('Clear DataPoint cache', async () => {
		const wrapper = initWrapper();
		await wrapper.vm.clearCache();
		expect(wrapper.vm.response.status).to.equal(true);
	});
});
