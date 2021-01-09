/**
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import AlarmNotifications from '@/views/AlarmNotifications';
import i18n from '@/i18n';

const localVue = createLocalVue();
localVue.use(Vuex);
localVue.use(Vuetify);

const storeAlarmsNotifications = {
	state: {},

	mutations: {},

	actions: {
		getAllPlcDataSources(context) {
			return new Promise((resolve) => {
				let ds = [
					{
						id: 22,
						name: 'UNIT_TEST_DS',
						xid: 'UNIT_DS_01',
					},
				];
				resolve(ds);
			});
		},
		getAllMailingLists(context, datasourceId) {
			return new Promise((resolve) => {
				let ml = [
					{
						id: 1,
						inactiveIntervals: [],
						name: 'TestUnit',
						recipientType: 1,
						referenceAddress: null,
						referenceId: 2,
						xid: 'TEST_ML_UNIT_1',
					},
				];
				resolve(ml);
			});
		},
		getPlcEventHandlers(context, datasourceId) {
			return new Promise((resolve) => {
				let eh = [
					{
						id: 11,
						xid: 'TEST_EH_UNIT_1',
						alias: 'UnitTest_EventHandler',
						eventTypeId: 1,
						eventTypeRef1: 2,
						eventTypeRef2: 3,
						recipients: [{ recipientType: 1, referenceId: 1, referenceAddress: null }],
					},
				];
				resolve(eh);
			});
		},
	},
};

const modules = {
	storeAlarmsNotifications,
};

const store = new Vuex.Store({ modules });

describe('PLC Alarms Notification Tests', () => {
	const wrapper = mount(AlarmNotifications, {
		store,
		localVue,
		i18n,
		stubs: ['VSelect'],
	});

	it('Initialize Component', () => {
		expect(wrapper.name()).to.equal('AlarmNotifications');
		expect(wrapper.vm.mailingLists[0].id).to.equal(1);
		expect(wrapper.vm.mailingLists[0].name).to.equal('TestUnit');
		expect(wrapper.vm.eventHandlers[0].id).to.equal(11);
		expect(wrapper.vm.eventHandlers[0].alias).to.equal('UnitTest_EventHandler');
	});

	it('Load PLC DataSources', () => {
		wrapper.vm.initDataSources();
		wrapper.vm.$nextTick(() => {
			expect(wrapper.vm.items.length).to.equal(1);
		});
	});
});
