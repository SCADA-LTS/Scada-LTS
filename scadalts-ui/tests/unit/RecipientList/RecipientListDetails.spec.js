import { expect } from 'chai';

import storeMailingList from '../../mocks/store/recipientListMock';
import storeUsersMock from '../../mocks/store/usersMock';
import mlMock from '../../mocks/objects/MailingListObjectMock';

import RecipientListDetails from '@/views/RecipientList/RecipientListDetails';
import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	storeMailingList,
	storeUsersMock,
};

global.requestAnimationFrame = (cb) => cb();

describe('Recipient List Details Tests', () => {
	
	it('Initialize Component', async () => {
		const wrapper = prepareMountWrapper(
			RecipientListDetails, 
			modules,
			{recipientList: mlMock, edit: false}
		);
		await wrapper.vm.$nextTick();
		expect(wrapper.get('h2').text()).to.equal('Recipient list details');
		expect(
			wrapper.get('#section-mail-details .col-8 .v-input:first-of-type input').element
				.value
		).to.equal('Example MailingList');
		expect(
			wrapper.get('#section-mail-details .col-4 .v-input:first-of-type input').element
				.value
		).to.equal('ML0001');
		await wrapper.vm.$nextTick();
		expect(wrapper.findAll('.col-12 > .v-list > .v-list-item').length).to.equal(3);
		expect(
			wrapper
				.get('.col-12 > .v-list > .v-list-item:nth-of-type(1) .v-list-item__title')
				.text()
		).to.contain('admin');
		expect(
			wrapper
				.get('.col-12 > .v-list > .v-list-item:nth-of-type(1) .v-list-item__subtitle')
				.text()
		).to.contain('admin@yourMangoDomain.com');
		expect(
			wrapper
				.get('.col-12 > .v-list > .v-list-item:nth-of-type(2) .v-list-item__title')
				.text()
		).to.contain('tester');
		expect(
			wrapper
				.get('.col-12 > .v-list > .v-list-item:nth-of-type(2) .v-list-item__subtitle')
				.text()
		).to.contain('tester@mail.com');
		expect(
			wrapper
				.get('.col-12 > .v-list > .v-list-item:nth-of-type(3) .v-list-item__title')
				.text()
		).to.contain('mail@mail.com');
	});

	it('Create Recipient User Test', async () => {
		const wrapper = prepareMountWrapper(
			RecipientListDetails, 
			modules,
			{recipientList: mlMock, edit: false}
		);
		await wrapper.vm.$nextTick();
		await wrapper.find('.heading-action-buttons .mdi-account-plus').trigger('click');
		expect(wrapper.find('#dialog-recipient-add').isVisible()).to.equal(true);
		expect(wrapper.find('#dialog-recipient-add .v-select').isVisible()).to.equal(true);
		expect(wrapper.find('#dialog-recipient-add .v-select').isVisible()).to.equal(true);
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.equal('disabled');
	});

	it('Create Recipient Mail Test', async () => {
		const wrapper = prepareMountWrapper(
			RecipientListDetails, 
			modules,
			{recipientList: mlMock, edit: false}
		);
		await wrapper.vm.$nextTick();

		await wrapper.find('.heading-action-buttons .mdi-email-plus').trigger('click');
		expect(wrapper.find('#dialog-recipient-add').isVisible()).to.equal(true);
		expect(wrapper.find('#dialog-recipient-add .v-input').isVisible()).to.equal(true);
		expect(wrapper.find('#dialog-recipient-add').text()).to.contain('Add e-mail address');
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.equal('disabled');
		wrapper
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('notpropermail');
		await wrapper.vm.$nextTick();
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.equal('disabled');
		expect(wrapper.find('#dialog-recipient-add transition-group-stub').text()).to.equal(
			'E-mail must be valid'
		);
		await wrapper
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('proper@mail.com');
		await wrapper.vm.$nextTick();
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.be.undefined;
	});

	it('Create Recipient Phone Test', async () => {
		const wrapper = prepareMountWrapper(
			RecipientListDetails, 
			modules,
			{recipientList: mlMock, edit: false}
		);
		await wrapper.vm.$nextTick();

		await wrapper.find('.heading-action-buttons .mdi-phone-plus').trigger('click');
		expect(wrapper.find('#dialog-recipient-add').isVisible()).to.equal(true);
		expect(wrapper.find('#dialog-recipient-add .v-input').isVisible()).to.equal(true);
		expect(wrapper.find('#dialog-recipient-add').text()).to.contain('Add phone address');
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.equal('disabled');
		wrapper
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('123aeerwe');
		await wrapper.vm.$nextTick();
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.equal('disabled');
		expect(wrapper.find('#dialog-recipient-add transition-group-stub').text()).to.equal(
			'Phone number must be valid'
		);
		await wrapper
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('123345567');
		await wrapper.vm.$nextTick();
		expect(
			wrapper
				.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
				.attributes().disabled
		).to.be.undefined;
	});

	it('Check the InactiveInterval convertion', () => {
		const wrapper = prepareMountWrapper(
			RecipientListDetails, 
			modules,
			{recipientList: mlMock, edit: false}
		);
		// Initialization tests
		expect(wrapper.vm.recipientList.inactiveIntervals.length).to.equal(4);
		expect(wrapper.vm.inactiveTime.length).to.equal(7);
		for (let x = 0; x < 4; x++) {
			expect(wrapper.vm.inactiveTime[0][0][x]).to.equal(true);
		}
		for (let x = 0; x < 4; x++) {
			expect(wrapper.vm.inactiveTime[0][1][x]).to.equal(false);
		}

		// Convert to component test
		wrapper.vm.convertInactiveIntervals([0, 1, 2, 3, 4, 5, 6, 7]);
		for (let y = 0; y < 2; y++) {
			for (let x = 0; x < 4; x++) {
				expect(wrapper.vm.inactiveTime[0][y][x]).to.equal(true);
			}
		}

		// Convert from component test
		let result = wrapper.vm.convertInactiveIntervals(wrapper.vm.inactiveTime);
		expect(result.length).to.equal(8);
	});
});
