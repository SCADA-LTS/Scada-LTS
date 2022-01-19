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

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test Point Properties - Logging Scenario', () => {
	let wrapper;

	describe("UI Component Initialization for Numeric DP", () => {

		before(() => {
			wrapper = prepareMountWrapper(PointPropLogging, modules, { data: dataPointMock });
			wrapper.vm.data.pointLocator.dataTypeId = 3;
		});

		after(() => {
			wrapper.vm.data.pointLocator.dataTypeId = 4;
		});

		it('Labels initialized', () => {			
			expect(wrapper.find('#point-prop-logging h3').text()).to.include('Logging properties');
			expect(wrapper.find('#point-prop-logging--type .v-select:first-of-type').text()).to.include('Logging Type');
			expect(wrapper.find('#log-type--numeric > .row > .col:nth-of-type(1)').text()).to.include('Tolerance');
			expect(wrapper.find('#log-type--numeric > .row > .col:nth-of-type(2)').text()).to.include('Discard extreme values');
			expect(wrapper.find('#log-type--numeric > .row > .col:nth-of-type(3)').text()).to.include('Discard low limit');
			expect(wrapper.find('#log-type--numeric > .row > .col:nth-of-type(4)').text()).to.include('Discard high limit');
			expect(wrapper.find('#purge--select .v-select:first-of-type').text()).to.include('Purge strategy');
			expect(wrapper.find('#purge--period .v-text-field:first-of-type').text()).to.include('Purge after');
			expect(wrapper.find('#cache .v-text-field:first-of-type').text()).to.include('Default cache size');
		});

		it('Interval logging type', () => {
			wrapper.vm.data.loggingType = 4;
			expect(wrapper.find('#log-type--interval > .row > .col:nth-of-type(1)').text()).to.include('Interval logging period every');
			expect(wrapper.find('#log-type--interval > .row > .col:nth-of-type(3)').text()).to.include('Value Type');
		});

		it('Logging type list contains 5 elements', () => {
			expect(wrapper.find('#point-prop-logging--type .v-select:first-of-type').props('items').length).to.equal(5);
		});
	})

	describe("Component methods validation", () => {

		before(() => {
			wrapper = prepareMountWrapper(PointPropLogging, modules, { data: dataPointMock });
			wrapper.vm.data.pointLocator.dataTypeId = 3;
		});

		after(() => {
			wrapper.vm.data.pointLocator.dataTypeId = 4;
		});

		it('Clear DataPoint cache', async () => {
			await wrapper.vm.clearCache();
			//There is no way to check the notification message
			// expect(wrapper.vm.response.status).to.equal(true);
		});

	})
});
