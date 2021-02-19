import Vuex from 'vuex';
import Vuetify from '@/plugins/vuetify';
import { expect } from 'chai';
import { createLocalVue, mount } from '@vue/test-utils';
import i18n from '@/i18n';

import dataPoint from '../../mocks/store/dataPointMock';

import PointPropChartRenderer from '@/views/DataPointDetails/PointProperties/PointPropChartRenderer'
import dataPointMock from '../../mocks/objects/DataPointMock';

const modules = {
    dataPoint
}

const store = new Vuex.Store({ modules });

describe('Point Properties Tests --- Chart Renderer', () => {
    const localVue = createLocalVue();
    localVue.use(i18n);
    localVue.use(Vuex);
    const vuetify = Vuetify;

    const mountFunction = options => {
        return mount(PointPropChartRenderer, {
            store,
            localVue,
            vuetify,
            i18n,
            ...options,
        })
    }


    it('Initialize Component',() => {
        const wrapper = mountFunction({
            propsData: {
                data: dataPointMock
            },
        });
		expect(wrapper.name()).to.equal('PointPropChartRenderer');
        expect(wrapper.vm.data.id).to.equal(1);
        expect(wrapper.vm.data.chartRenderer).to.equal(null);
        expect(wrapper.vm.selected).to.equal(-1);
	});

    it('Select has 4 Items', () => {
        const wrapper = mountFunction({
            propsData: {
                data: dataPointMock
            },
        });
        const items = wrapper.find('.v-select:first-of-type').props('items')
        expect(items.length).to.equal(4);
    })

    it('Select type Table', () => {
        const wrapper = mountFunction({
            propsData: {
                data: dataPointMock
            },
        });
        wrapper.vm.watchChartRendererChange(0);
        expect(wrapper.vm.data.chartRenderer.limit).to.equal(2);
        expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('TABLE');
    })

    it('Select type Image', async () => {
        const wrapper = mountFunction({
            propsData: {
                data: dataPointMock
            },
        });
        wrapper.vm.watchChartRendererChange(1);
        expect(wrapper.vm.data.chartRenderer.timePeriod).to.equal(2);
        expect(wrapper.vm.data.chartRenderer.numberOfPeriods).to.equal(1);
        expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('IMAGE');        
    })


    it('Select type Statistics', () => {
        const wrapper = mountFunction({
            propsData: {
                data: dataPointMock
            },
        });
        wrapper.vm.watchChartRendererChange(2);
        expect(wrapper.vm.data.chartRenderer.timePeriod).to.equal(2);
        expect(wrapper.vm.data.chartRenderer.numberOfPeriods).to.equal(1);
        expect(wrapper.vm.data.chartRenderer.includeSum).to.equal(false);
        expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('STATS');
    })

    it('Load Image Chart Renderer', () => {
        let complexDataPointMock = Object.assign({}, dataPointMock);
        complexDataPointMock.chartRenderer = Object.assign({}, modules.dataPoint.state.chartRenderersTemplates[1])
        const wrapper = mountFunction({
            propsData: {
                data: complexDataPointMock
            },
        });
        expect(wrapper.vm.data.chartRenderer).to.not.equal(null);
        expect(wrapper.vm.selected).to.equal(1);
        expect(wrapper.vm.data.chartRenderer.def.exportName).to.equal('IMAGE');

    })


});