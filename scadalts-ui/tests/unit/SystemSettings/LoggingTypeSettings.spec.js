/**
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
import { expect } from 'chai';

import DefaultLoggingTypeSettingsComponent from '@/views/SystemSettings/DefaultLoggingTypeComponent';
import systemSettings from '../../mocks/store/systemSettingsMock';
import dataPoint from '../../mocks/store/dataPointMock';

import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	systemSettings,
	dataPoint,
};

/**
 * @private
 * Initialize VueWrapper for local testing
 * Prepare wrapper wiht all required stubs and props.
 */
function initWrapper() {
	return prepareMountWrapper(
		DefaultLoggingTypeSettingsComponent, 
		modules,
		{},
		{stubs: ['VSelect', 'VIcon']}
	);
}

describe('SystemSettings - LoggingType Settings Tests', () => {
	const wrapper = initWrapper();

	it('Initialize Component', () => {
		expect(wrapper.name()).to.equal('DefaultLoggingTypeSettingsComponent');
	});

	it('Test loading variable from Vuex store', () => {
		expect(wrapper.vm.defaultLoggingType).to.equal(3);
		expect(wrapper.vm.defaultLoggingTypeStore).to.equal(3);
	});

	it('Test value saving', async () => {
		wrapper.vm.defaultLoggingType = 5;
		wrapper.vm.saveData();
		wrapper.vm.$nextTick(() => {
			expect(wrapper.vm.defaultLoggingType).to.equal(5);
			expect(wrapper.vm.defaultLoggingTypeStore).to.equal(5);
		});
	});

	it('Test changing and emitting the settings', async () => {
		wrapper.vm.defaultLoggingType = 2;
		await wrapper.vm.watchLoggingTypeChange();
		expect(wrapper.vm.isDefaultLoggingTypeEdited).to.be.true;
		await wrapper.vm.$nextTick();
		expect(wrapper.emitted().changed[0][0].component).to.equal(
			'defaultLoggingTypeSettingsComponent',
		);
		expect(wrapper.emitted().changed[0][0].changed).to.be.true;
	});

	it('Test changing and restoring data', async () => {
		wrapper.vm.defaultLoggingType = 1;
		await wrapper.vm.watchLoggingTypeChange();
		expect(wrapper.vm.isDefaultLoggingTypeEdited).to.be.true;
		await wrapper.vm.restoreData();
		expect(wrapper.vm.defaultLoggingType).to.equal(3);
	});
});
