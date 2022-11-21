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
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test Recipient List Details Scenario', () => {
	let wrapper2;

	before(() => {
		wrapper2 = prepareMountWrapper(RecipientListDetails, modules, {
			recipientList: mlMock,
			edit: false,
		});
	});

	describe('Component Initailization', () => {
		it('Is header and translation loaded', async () => {
			await wrapper2.vm.$nextTick();
			expect(wrapper2.get('h2').text()).to.equal('Recipient list details');
		});

		it('Is props test data loaded', () => {
			expect(
				wrapper2.get('#rl-section-details .col-8 .v-input:first-of-type input').element
					.value
			).to.equal('Example MailingList');

			expect(
				wrapper2.get('#rl-section-details .col-4 .v-input:first-of-type input').element
					.value
			).to.equal('ML0001');
		});

		it('Are 3 test recipients entries loaded correctly', () => {
			expect(wrapper2.findAll('.col-12 > .v-list > .v-list-item').length).to.equal(3);
		});

		it('Is Admin user recipient loaded correctly', () => {
			expect(
				wrapper2
					.get('.col-12 > .v-list > .v-list-item:nth-of-type(1) .v-list-item__title')
					.text()
			).to.contain('admin');
			expect(
				wrapper2
					.get('.col-12 > .v-list > .v-list-item:nth-of-type(1) .v-list-item__subtitle')
					.text()
			).to.contain('admin@yourMangoDomain.com');
		});

		it('Is Tester user recipient loaded correctly', () => {
			expect(
				wrapper2
					.get('.col-12 > .v-list > .v-list-item:nth-of-type(2) .v-list-item__title')
					.text()
			).to.contain('tester');
			expect(
				wrapper2
					.get('.col-12 > .v-list > .v-list-item:nth-of-type(2) .v-list-item__subtitle')
					.text()
			).to.contain('tester@mail.com');
		});

		it('Is plain email recipient loaded correctly', () => {
			expect(
				wrapper2
					.get('.col-12 > .v-list > .v-list-item:nth-of-type(3) .v-list-item__title')
					.text()
			).to.contain('mail@mail.com');
		});
	});

	describe('Test recipient creation - type:user', () => {
		it('Is dialog visible', async() => {
			await wrapper2.find('.heading-action-buttons .mdi-account-plus').trigger('click');
			expect(wrapper2.find('#dialog-recipient-add')).to.exist;
			expect(wrapper2.find('#dialog-recipient-add .v-select')).to.exist;
		});

		it('Is "add" button disabled on init', () => {
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.equal('disabled');
		});
	})

	describe('Test recipient creation - type:clear_mail', () => {

		it('Is dialog visible', async() => {
			await wrapper2.find('.heading-action-buttons .mdi-email-plus').trigger('click');
			expect(wrapper2.find('#dialog-recipient-add')).to.exist;
			expect(wrapper2.find('#dialog-recipient-add .v-input')).to.exist;
			expect(wrapper2.find('#dialog-recipient-add').text()).to.contain('Add e-mail address');
		});

		it('Is "add" button disabled on init', () => {
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.equal('disabled');
		});

		it('Test not valid input', async() => {
			wrapper2
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('notpropermail');
			await wrapper2.vm.$nextTick();
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.equal('disabled');
			expect(wrapper2.find('#dialog-recipient-add transition-group-stub').text()).to.equal(
				'E-mail must be valid'
			);
		})

		it('Test valid input', async() => {
			await wrapper2
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('proper@mail.com');
			await wrapper2.vm.$nextTick();
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.be.undefined;
		})

	})

	describe('Test recipient creation - type:clear_sms', () => {

		it('Is dialog visible', async() => {
			await wrapper2.find('.heading-action-buttons .mdi-phone-plus').trigger('click');
			expect(wrapper2.find('#dialog-recipient-add')).to.exist;
			expect(wrapper2.find('#dialog-recipient-add .v-input')).to.exist;
			expect(wrapper2.find('#dialog-recipient-add').text()).to.contain('Add phone address');
		});

		it('Is "add" button disabled on init', () => {
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.equal('disabled');
		});

		it('Test not valid input', async() => {
			wrapper2
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('123aeerwe');
			await wrapper2.vm.$nextTick();
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.equal('disabled');
			expect(wrapper2.find('#dialog-recipient-add transition-group-stub').text()).to.equal(
				'Phone number must be valid'
			);
		})

		it('Test valid input', async() => {
			await wrapper2
			.find('#dialog-recipient-add .v-input:first-of-type input')
			.setValue('123345567');
			await wrapper2.vm.$nextTick();
			expect(
				wrapper2
					.find('#dialog-recipient-add .v-card__actions > button:last-of-type')
					.attributes().disabled
			).to.be.undefined;
		})
	})

	describe('Check the Inactive Interval', () => {

		it('Validate Inactive Interval', () => {
			expect(wrapper2.vm.recipientList.inactiveIntervals.length).to.equal(4);
			expect(wrapper2.vm.inactiveTime.length).to.equal(7);
		})

		it('Is first hour of inactive interval equal true', () => {
			for (let x = 0; x < 4; x++) {
				expect(wrapper2.vm.inactiveTime[0][0][x]).to.equal(true);
			}
		})

		it('Is second hour of inactive interval equal false', () => {
			for (let x = 0; x < 4; x++) {
				expect(wrapper2.vm.inactiveTime[0][1][x]).to.equal(false);
			}
		})

		it('Test ConvertInactiveIntervals method 1D -> 3D', () => {
			wrapper2.vm.convertInactiveIntervals([0, 1, 2, 3, 4, 5, 6, 7]);
			for (let y = 0; y < 2; y++) {
				for (let x = 0; x < 4; x++) {
					expect(wrapper2.vm.inactiveTime[0][y][x]).to.equal(true);
				}
			}
		})

		it('Test ConvertInactiveIntervals method 3D -> 1D', () => {
			let result = wrapper2.vm.convertInactiveIntervals(wrapper2.vm.inactiveTime);
			expect(result.length).to.equal(8);
		})
	})
});