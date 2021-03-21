import { expect } from 'chai';

import dataPoint from '../../mocks/store/dataPointMock';

import PointProperties from '@/views/DataPointDetails/PointProperties';
import dataPointMock from '../../mocks/objects/DataPointMock';

import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	dataPoint,
};

global.requestAnimationFrame = (cb) => cb();

/**
 * @private
 * Initialize VueWrapper for local testing
 * Prepare wrapper wiht all required stubs and props.
 */
function initWrapper() {
	return prepareMountWrapper(
		PointProperties, 
		modules,
		{data: dataPointMock },
		{stubs: [
			'PointPropChartRenderer',
			'PointPropEventDetectors',
			'PointPropEventRenderer',
			'PointPropTextRenderer',
			'PointPropLogging',
			'PurgeDataDialog',
		]}
	);
}

describe('Point Properties Tests', () => {

	it('Initialize Component', () => {
		const wrapper = initWrapper();
		expect(wrapper.name()).to.equal('PointProperties');
		expect(wrapper.vm.data.enabled).to.equal(true);
	});

	it('Open and Close Dialog', async () => {
		const wrapper = initWrapper();
		expect(wrapper.find('.point-properties-box').exists()).to.equal(false);
		await wrapper.find('i').trigger('click');
		expect(wrapper.find('.point-properties-box').exists()).to.equal(true);
		expect(wrapper.get('.v-card__actions > button:first-of-type').text()).to.equal(
			'Cancel',
		);
		await wrapper.find('.v-card__actions > button:first-of-type').trigger('click');
		expect(wrapper.get('.v-dialog').html()).to.contain('display: none');
	});

	it('Open and Save Dialog', async () => {
		const wrapper = initWrapper();
		expect(wrapper.emitted().saved).to.equal(undefined);
		await wrapper.find('i').trigger('click');
		expect(wrapper.get('.v-card__actions > button:last-of-type').text()).to.equal('Save');
		await wrapper.find('.v-card__actions > button:last-of-type').trigger('click');
		expect(wrapper.get('.v-dialog').html()).to.contain('display: none');
		expect(wrapper.emitted().saved.length).to.equal(1);
	});

	it('Toggle Data Point', async () => {
		const wrapper = initWrapper();
		await wrapper.vm.toggleDataPointDialog(true);
		expect(wrapper.vm.data.enabled).to.equal(false);
	});
});
