import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import storeMailingList from '../../mocks/store/recipientListMock';
import storeUsersMock from '../../mocks/store/usersMock';

import RecipientList from '@/views/RecipientList'

const modules = {
    storeMailingList,
    storeUsersMock,
};

const store = new Vuex.Store({ modules });

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = (cb) => cb();

describe('Recipient List Tests', () => {
	const localVue = createLocalVue();
	localVue.use(i18n);
	localVue.use(Vuex);
	const vuetify = Vuetify;

	const mountFunction = (options) => {
		return mount(RecipientList, {
			store,
			localVue,
			vuetify,
			i18n,
			stubs: [ ],
			...options,
		});
	};

	it('Initialize Component', () => {
		const wrapper = mountFunction();
        expect(wrapper.get('h1').text()).to.equal('Recipient List');
		expect(wrapper.get('h3').text()).to.equal('Select a recipient list to see details');
        expect(wrapper.get('.v-skeleton-loader')).to.exist;
	});

    it('Load recipient lists', async () => {
		const wrapper = mountFunction();
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.recipeintLists.length).to.equal(3);
        expect(wrapper.vm.recipeintListsLoaded).to.equal(true);
        expect(wrapper.find('#recipientListSection')).to.exist;
        await wrapper.vm.$nextTick();
        const lists = wrapper.findAll('#recipientListSection .v-list-item');
        expect(lists.length).to.equal(3);
	});

    it('Select recipient list', async () => {
        const wrapper = mountFunction();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        wrapper.find('#recipientListSection .v-list-item:first-of-type').trigger('click');
        await wrapper.vm.$nextTick();
        expect(wrapper.get('h2').text()).to.equal('Recipient list details');
    });

    it('Create recipient list', async () => {
        const wrapper = mountFunction();
        wrapper.find('.v-list-item .mdi-plus').trigger('click');
        await wrapper.vm.$nextTick();
        expect(wrapper.get('.v-card__title').text()).to.equal('Create Recipient List');
    });

    it('Delete recipient list', async () => {
        const wrapper = mountFunction();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        wrapper.find('#recipientListSection .v-list-item:first-of-type .mdi-minus-circle').trigger('click');
        await wrapper.vm.$nextTick();
        expect(wrapper.get('.v-dialog--active .v-card__text').text()).to.equal('Are you sure you want to delete this mailing list? This operation cannot be undone.');
        wrapper.find('.v-dialog--active .v-card__actions .success--text').trigger('click');
    });

});
