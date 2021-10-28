import { expect } from 'chai';
import { prepareMountWrapper } from "../../utils/testing-utils";
import autoManualMock from '../../mocks/store/autoManualMock';

import AutoManualControls from "@/components/graphical_views/cmp2/AutoManualControls.vue";

const modules = {
    autoManualMock
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

describe('💠️ AutoManualControls Unit Test Scenario', () => {
    let wrapper;

    let controls = {
            label: 'change to:',
            definitionPointToSaveValue: [
                {
                    xid: 'DP_PLC_M',
                    def: 'PLC',
                    comment: '',
                },
                {
                    xid: 'DP_pump_01',
                    def: 'PUMP',
                    comments: '',
                },
            ],
            toChange: [
                {
                    name: 'Auto',
                    save: [
                        {
                            refDefPoint: 'PLC',
                            value: '0',
                        },
                        {
                            refDefPoint: 'PUMP',
                            value: '1',
                        },
                    ],
                    runDirectlyBeforeShowSubMenu: 'true',
                },
                {
                    name: 'Manual',
                    save: [
                        {
                            refDefPoint: 'PLC',
                            value: '1',
                        },
                        {
                            refDefPoint: 'PUMP',
                            value: '1',
                        },
                    ],
                    runDirectlyBeforeShowSubMenu: 'true',
                    toChange: [
                        {
                            name: 'Stop',
                            save: [
                                {
                                    refDefPoint: 'PUMP',
                                    value: '0',
                                }
                            ],
                            confirmation: 'true',
                        },
                        {
                            name: 'Start',
                            save: [
                                {
                                    refDefPoint: 'PUMP',
                                    value: '1',
                                }
                            ],
                            confirmation: 'true',
                        },
                    ],
                },
            ],
    };

    before(() => {
        wrapper = prepareMountWrapper(AutoManualControls, modules, { controls });
    })

    describe('Component Initialization', () => {
        it('Is controls for root level set', async() => {
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.controlsLevel0).to.be.an('array');
            expect(wrapper.vm.controlsLevel0).to.have.lengthOf(2);
        });

        it('Is point definition loaded', () => {
            expect(wrapper.vm.pointDefinition).to.be.an('map');
            expect(wrapper.vm.pointDefinition).to.have.all.keys('PLC', 'PUMP');
        });
    })
})