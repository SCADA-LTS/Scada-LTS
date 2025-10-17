import { expect } from 'chai';

import storeSynopticPanel from '../../mocks/store/synopticPanelMock';

import SynopticPanelMenu from '@/views/SynopticPanel/SynopticPanelMenu.vue';
import { prepareMountWrapper, prepareShallowMountWrapper } from '../../utils/testing-utils';

const modules = {
	storeSynopticPanel,
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test Synoptic Panel Menu Scenario', () => {
	let wrapper;

	before(() => {
		wrapper = prepareShallowMountWrapper(SynopticPanelMenu, modules, {
            stubs: ['router-view']
        });
    });

	describe('Component Initailization', () => {
        it('Is header loaded', () => {
            expect(wrapper.get('h1').text()).to.equal('Synoptic Panel');
        })

        it('Is Synoptic Panel loaded 3 panels on init', () => {
            expect(wrapper.vm.synopticPanelList.length).to.equal(3);
        })

        it('Is add Synoptic Panel button visible', () => {
            expect(wrapper.find('button .mdi-plus')).to.exist;
        })

        it('Is Active Panel not defined', () => {
            expect(wrapper.vm.activePanel).to.be.undefined;
        })
    })

})
