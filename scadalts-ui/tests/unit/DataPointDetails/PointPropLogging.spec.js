import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import dataPoint from '../../mocks/store/dataPointMock';
import mainStore from '../../mocks/store/index';

import PointPropLogging from '@/views/DataPointDetails/PointProperties/PointPropLogging';
import dataPointMock from '../../mocks/objects/DataPointMock';

const modules = {
    dataPoint
}

const store = new Vuex.Store({ modules, state: mainStore.state });

global.requestAnimationFrame = (cb) => cb();

const localVue = createLocalVue();
localVue.use(i18n);
localVue.use(Vuex);
const vuetify = Vuetify;

const mountFunction = (options) => {
	return mount(PointPropLogging, {
		store,
		localVue,
		vuetify,
		i18n,
		...options,
	});
};

describe('Point Properties Tests - Logging properties', () => {
    let wrapper
    
	beforeEach(() => {
		wrapper = mountFunction({
			propsData: {
				data: dataPointMock,
			},
		});
	});

	it('Initialize Component', () => {
		const items = wrapper.find('.v-select:first-of-type').props('items');
		expect(items.length).to.equal(5);
        expect(wrapper.vm.data.intervalLoggingType).to.equal(1);
        expect(wrapper.get('.row[style="display: none;"]').html()).to.contain('Interval logging period every');
	});

    it('Change to "Interval" property', async () => {
        wrapper.vm.data.intervalLoggingType = 4;
        await wrapper.vm.$nextTick();
        expect(wrapper.text()).contains('logging period');
    })

    it('Clear DataPoint cache', async () => {
        await wrapper.vm.clearCache();
        expect(wrapper.vm.response.status).to.equal(true);
    })

});
