import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import modules from '../../mocks/store/dataPointMock';

import PointProperties from '@/views/DataPointDetails/PointProperties'
import dataPointMock from '../../mocks/objects/DataPointMock';

const store = new Vuex.Store({ modules });

global.requestAnimationFrame = cb => cb();


describe('Point Properties Tests', () => {
    const localVue = createLocalVue();
    localVue.use(i18n);
    localVue.use(Vuex);
    const vuetify = Vuetify;

    

    const mountFunction = options => {
        return mount(PointProperties, {
            store,
            localVue,
            vuetify,
            i18n,
            stubs: [
                'PointPropChartRenderer',
                'PointPropEventDetectors',
                'PointPropEventRenderer',
                'PointPropTextRenderer',
                'PointPropLogging',
                'PurgeDataDialog'
            ],
            propsData: {
                data: dataPointMock
            },
            ...options,
        })
    }

    it('Initialize Component',() => {
        const wrapper = mountFunction();
		expect(wrapper.name()).to.equal('PointProperties');
        expect(wrapper.vm.data.enabled).to.equal(true);
	});

    it('Open and Close Dialog', async () => {
        const wrapper = mountFunction();
        expect(wrapper.find(".point-properties-box").exists()).to.equal(false);
        await wrapper.find("i").trigger('click');
        expect(wrapper.find(".point-properties-box").exists()).to.equal(true);
        expect(wrapper.get(".v-card__actions > button:first-of-type").text()).to.equal('Cancel');
        await wrapper.find(".v-card__actions > button:first-of-type").trigger('click');
        expect(wrapper.get(".v-dialog").html()).to.contain('display: none');
    });

    it('Open and Save Dialog', async () => {
        const wrapper = mountFunction();
        expect(wrapper.emitted().saved).to.equal(undefined);
        await wrapper.find("i").trigger('click');
        expect(wrapper.get(".v-card__actions > button:last-of-type").text()).to.equal('Save');
        await wrapper.find(".v-card__actions > button:last-of-type").trigger('click');
        expect(wrapper.get(".v-dialog").html()).to.contain('display: none');
        expect(wrapper.emitted().saved.length).to.equal(1);
    });

    it('Toggle Data Point', async () => {
        const wrapper = mountFunction();
        await wrapper.vm.toggleDataPoint();
        expect(wrapper.vm.data.enabled).to.equal(false);
    })

});