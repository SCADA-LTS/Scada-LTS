import { expect } from 'chai';
import { mount } from '@vue/test-utils';
import PointEditor from '@/components/datasources/ModBusIpDataSource/point.vue';
import ModBusDataPoint from '@/components/datasources/ModBusIpDataSource/ModBusDataPoint';
import en from '@/locales/en.json';

const slot = { template: '<div><slot /></div>' };
const checkbox = {
	props: ['value', 'label', 'hint'],
	template: '<label>{{ label }}<input type="checkbox" :checked="value" @change="$emit(\'input\', $event.target.checked)">{{ hint }}</label>',
};

[false, true].forEach((pointLocatorTest) => {
	describe(`Modbus string byte order (${pointLocatorTest ? 'locator test' : 'editor'})`, () => {
		let wrapper;
		let datapoint;

		beforeEach(async () => {
			datapoint = new ModBusDataPoint(1);
			datapoint.pointLocator.range = 3;
			datapoint.pointLocator.modbusDataType = 18;
			wrapper = mount(PointEditor, {
				propsData: { datapoint, pointLocatorTest },
				mocks: {
					$t: (key) => en[key] || key,
					$store: { state: { dataSourceState: { datapointTypes: [] } } },
				},
				stubs: {
					DataPointCreation: slot,
					'v-row': slot,
					'v-col': slot,
					'v-select': true,
					'v-text-field': true,
					'v-checkbox': checkbox,
				},
			});
			await wrapper.vm.$nextTick();
		});

		afterEach(() => wrapper.destroy());

		it('defaults to the existing high-byte-first order', () => {
			expect(datapoint.pointLocator.swapStringBytes).to.equal(false);
			expect(wrapper.find('input[type="checkbox"]').element.checked).to.equal(false);
			expect(wrapper.text()).to.contain('Swap bytes within each string register');
			expect(wrapper.text()).to.contain('the default is high byte first');
		});

		it('updates the submitted point and preserves the value on reopen', async () => {
			await wrapper.find('input[type="checkbox"]').setChecked(true);
			expect(datapoint.pointLocator.swapStringBytes).to.equal(true);
			wrapper.vm.save(datapoint);
			expect(wrapper.emitted('saved')[0][0].pointLocator.swapStringBytes).to.equal(true);
			await wrapper.setProps({ datapoint: JSON.parse(JSON.stringify(datapoint)) });
			expect(wrapper.find('input[type="checkbox"]').element.checked).to.equal(true);
		});

		it('shows the option for both string types and hides it for numeric and binary types', async () => {
			for (const type of [18, 19, 2, 8, 1]) {
				datapoint.pointLocator.modbusDataType = type;
				wrapper.vm.changeModbusDataType();
				await wrapper.vm.$nextTick();
				expect(wrapper.find('input[type="checkbox"]').exists()).to.equal(type === 18 || type === 19);
			}
		});

		it('hides the option for coil ranges and shows it for input registers', async () => {
			for (const range of [1, 2, 4]) {
				datapoint.pointLocator.range = range;
				wrapper.vm.changeRegisterRange();
				await wrapper.vm.$nextTick();
				expect(wrapper.find('input[type="checkbox"]').exists()).to.equal(range === 4);
			}
		});
	});
});
