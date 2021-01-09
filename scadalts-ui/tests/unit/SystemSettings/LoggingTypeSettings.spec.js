/**
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
import Vuex from 'vuex';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import DefaultLoggingTypeSettingsComponent from '@/views/SystemSettings/DefaultLoggingTypeComponent';
import i18n from '@/i18n';

const localVue = createLocalVue();
localVue.use(Vuex);

const systemSettings = {
	state: {
		defaultLoggingType: 1,
	},

	mutations: {
		setDefaultLoggingType(state, defaultLoggingType) {
			state.defaultLoggingType = defaultLoggingType;
		},
	},

	actions: {
		getDefaultLoggingType(context) {
			return new Promise((resolve) => {
				context.state.defaultLoggingType = 3;
				resolve(3);
			});
		},
		saveDefaultLoggingType(context) {
			return new Promise((resolve) => {
				resolve(true);
			});
		},
	},
};

const modules = {
	systemSettings,
};

const store = new Vuex.Store({ modules });

describe('SystemSettings - LoggingType Settings Tests', () => {
	const wrapper = mount(DefaultLoggingTypeSettingsComponent, {
		store,
		localVue,
		i18n,
	});

	it('Initialize Component', () => {
		expect(wrapper.name()).to.equal('DefaultLoggingTypeSettingsComponent');
	});

	it('Test loading variable from Vuex store', () => {
		expect(wrapper.vm.defaultLoggingType).to.equal(3);
		expect(wrapper.vm.defaultLoggingTypeStore).to.equal(3);
	});

	it('Test i18n English Translation', () => {
		expect(wrapper.findAll('option').at(1).html()).to.contain('All data');
		expect(wrapper.findAll('option').at(2).html()).to.contain('Do not log');
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
