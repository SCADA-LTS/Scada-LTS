import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import storeMailingList from '../../mocks/store/recipientListMock';
import storeUsersMock from '../../mocks/store/usersMock';
import mlMock from '../../mocks/objects/MailingListObjectMock';

import RecipientListDetails from '@/views/RecipientList/RecipientListDetails';

const modules = {
	storeMailingList,
    storeUsersMock,
};

const store = new Vuex.Store({ modules });

global.requestAnimationFrame = (cb) => cb();


describe('Recipient List Details Tests', () => {
	const localVue = createLocalVue();
	localVue.use(i18n);
	localVue.use(Vuex);
	const vuetify = Vuetify;

	const mountFunction = (options) => {
		return mount(RecipientListDetails, {
			store,
			localVue,
			vuetify,
			i18n,
            propsData: {
				recipientList: mlMock,
				edit: false,
			},
			...options,
		});
	};

	it('Initialize Component', async () => {
		const wrapper = mountFunction();
        await wrapper.vm.$nextTick();
		expect(wrapper.get('h2').text()).to.equal('Recipient list details');
        expect(wrapper.get('#section-mail-details .col-8 .v-input:first-of-type input').element.value).to.equal('Example MailingList')
        expect(wrapper.get('#section-mail-details .col-4 .v-input:first-of-type input').element.value).to.equal('ML0001')
        await wrapper.vm.$nextTick();
        expect(wrapper.findAll('.col-12 > .v-list > .v-list-item').length).to.equal(3);
        expect(wrapper.get('.col-12 > .v-list > .v-list-item:nth-of-type(1) .v-list-item__title').text()).to.contain('admin');
        expect(wrapper.get('.col-12 > .v-list > .v-list-item:nth-of-type(1) .v-list-item__subtitle').text()).to.contain('admin@yourMangoDomain.com');
        expect(wrapper.get('.col-12 > .v-list > .v-list-item:nth-of-type(2) .v-list-item__title').text()).to.contain('tester');
        expect(wrapper.get('.col-12 > .v-list > .v-list-item:nth-of-type(2) .v-list-item__subtitle').text()).to.contain('tester@mail.com');
        expect(wrapper.get('.col-12 > .v-list > .v-list-item:nth-of-type(3) .v-list-item__title').text()).to.contain('mail@mail.com');
	});

});
