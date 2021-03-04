/**
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { config, createLocalVue, mount } from '@vue/test-utils';
import AlarmNotifications from '@/views/AlarmNotifications';
import i18n from '@/i18n';

const localVue = createLocalVue();
localVue.use(Vuex);
const vuetify = Vuetify;

global.requestAnimationFrame = (cb) => cb();

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
		vuetify,
		i18n,
	});

	it('Initialize Component', () => {
		expect(wrapper.name()).to.equal('AlarmNotifications');
		expect(wrapper.vm.mailingLists[0].id).to.equal(1);
		expect(wrapper.vm.mailingLists[0].name).to.equal('TestUnit');
	});

	it('Load PLC DataSources', () => {
		wrapper.vm.initDataSources();
		wrapper.vm.$nextTick(() => {
			expect(wrapper.vm.items.length).to.equal(1);
		});
	});

	it('Test getEventHandler', () => {
		let configuration = [
			{
				id: 1,
				xid: 'EH_MAIL_TEST',
				alias: 'MAIL_TEST_HANDLER',
				handlerType: 2,
				eventTypeId: 1,
				eventTypeRef1: 1,
				eventTypeRef2: 1,
				recipients: [
					{
						recipientType: 1,
						referenceId: 1,
						referenceAddress: null,
					},
				],
			},
			{
				id: 2,
				xid: 'EH_SMS_TEST',
				alias: 'MAIL_SMS_HANDLER',
				handlerType: 5,
				eventTypeId: 1,
				eventTypeRef1: 1,
				eventTypeRef2: 1,
				recipients: [
					{
						recipientType: 1,
						referenceId: 1,
						referenceAddress: null,
					},
				],
			},
		];
		let x = wrapper.vm.getEventHandler(configuration, 2);
		expect(x).to.equal(configuration[0]);
		x = wrapper.vm.getEventHandler(configuration, 5);
		expect(x).to.equal(configuration[1]);
		configuration = configuration.filter((e) => {
			return e.id !== 2;
		});
		x = wrapper.vm.getEventHandler(configuration, 5);
		expect(x).to.equal(null);
	});

	it('Test saveDatapoint', () => {
		wrapper.vm.items = [
			{
				id: 1,
				name: 'DS',
				children: [
					{
						id: 1,
						name: 'DP',
						configuration: [],
						mail: [
							{ active: false, config: true, handler: 1, mlId: 1 },
							{ active: true, config: true, handler: 1, mlId: 2 },
						],
						sms: [
							{ active: false, config: false, handler: 2, mlId: 1 },
							{ active: true, config: true, handler: 2, mlId: 2 },
						],
					},
				],
			},
		];

		const config = [
			{
				id: 1,
				xid: 'EH_MAIL_TEST',
				alias: 'MAIL_TEST_HANDLER',
				handlerType: 2,
				eventTypeId: 1,
				eventTypeRef1: 1,
				eventTypeRef2: 1,
				recipients: [
					{
						recipientType: 1,
						referenceId: 1,
						referenceAddress: null,
					},
				],
			},
		];

		wrapper.vm.saveDatapoint(1, config, 2);

		expect(wrapper.vm.items[0].children[0].mail[0].config).to.equal(false);
		expect(wrapper.vm.items[0].children[0].configuration).to.equal(config);
	});
});
