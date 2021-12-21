import { expect } from 'chai';
import { prepareMountWrapper } from "../../utils/testing-utils";
import autoManualMock from '../../mocks/store/autoManualMock';

import AutoManual from "@/components/graphical_views/cmp2/AutoManual.vue";
import { is } from 'core-js/core/object';

const modules = {
    autoManualMock
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

describe('💠️ AutoManual Unit Test Scenario', () => {
    let wrapper;

    let configuration1 = {
        state: {
            analiseInOrder: [
                {
                    name: 'Manual PLC',
                    toChecked: [
                        {
                            xid: 'DP_PLC_M',
                            equals: '==true',
                            describe: 'Working in Manual Mode',
                        },
                    ],
                },
                {
                    name: 'Pump Working',
                    toChecked: [
                        {
                            xid: 'DP_pump_01',
                            equals: '==true',
                            describe: 'Pump is working',
                        },
                    ],
                },
            ],
        },
        control: {
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
        },
    };

    before(() => {
        wrapper = prepareMountWrapper(AutoManual, modules, {
            pLabel: "UnitTest",
            pConfig: configuration1,
            pTimeRefresh: 5000,
            pxIdViewAndIdCmp: "ID_02",
            pWidth: 550,
            pDebugRequest: true,
        });
    })

    describe('Component Initialization', () => {
        it('Is component mounted', async() => {
            await wrapper.vm.$nextTick();
            
            expect(wrapper.get(".cmp-state").text()).to.equal("NOT-CHECKED");
        });

        it('Is component rendering pLabel', () => {
            expect(wrapper.get(".cmp-label").text()).to.equal("UnitTest");
        });

        it('Is component prop pTimeRefresh initialized', () => {
            expect(wrapper.vm.pTimeRefresh).to.equal(5000);
        })

        it('Is component prop pxIdViewAndIdCmp initialized', () => {
            expect(wrapper.vm.pxIdViewAndIdCmp).to.equal("ID_02");
        })

        it('Is component prop pWidth initialized', () => {
            expect(wrapper.vm.pWidth).to.equal(550);  
        })

        it('Is component pDebugRequest initialized', () => {
            expect(wrapper.vm.pDebugRequest).to.equal(true);
            console.debug = () => {};
        })

        it('Is errorHandlers initialized', () => {
            expect(wrapper.vm.errorHandlers).to.be.an('map');
            expect(wrapper.vm.errorHandlers).to.have.all.keys('Manual PLC', 'Pump Working');
        })    
    })

    describe('Method verification', () => {
        it('Is getPointsToBeChecked working correctly', () => {
            let condition = {
                name: 'Manual PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '==true',
                        describe: 'Working in Manual Mode',
                    },
                ],
            };
            let result = wrapper.vm.getPointsToBeChecked(condition)
            expect(result).to.be.an('array');
            expect(result).to.have.lengthOf(1);
            expect(result[0]).to.equal('DP_PLC_M');            
        });

        it('Is checkOneCondition working correctly', () => {
            let condition = {
                name: 'Manual PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '==true',
                        describe: 'Working in Manual Mode',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "true", xid: "DP_PLC_M"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(true);
            expect(wrapper.vm.componentState).to.equal("Manual PLC");
        });

        it('Is checkOneCondition working correctly (false)', () => {
            let condition = {
                name: 'Manual PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '==true',
                        describe: 'Working in Manual Mode',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "false", xid: "DP_PLC_M"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(false);
        });

        it('Is checkOneCondition working correctly Multiple conditions', () => {
            wrapper.vm.errorHandlers.set("Multiple PLC",{connection: true, messages: []});
            let condition = {
                name: 'Multiple PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '==true',
                        describe: 'Working in Auto Mode',
                    },
                    {
                        xid: 'DP_pump_01',
                        equals: '==true',
                        describe: 'Pump is not working',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "true", xid: "DP_PLC_M"}, {value: "true", xid: "DP_pump_01"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(true);
            expect(wrapper.vm.componentState).to.equal("Multiple PLC");
        });

        it('Is checkOneCondition noting correctly failed conditions', () => {
            wrapper.vm.errorHandlers.set("Multiple PLC",{connection: true, messages: []});
            let condition = {
                name: 'Multiple PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '==true',
                        describe: 'Working in Auto Mode',
                        toNote: true,
                    },
                    {
                        xid: 'DP_pump_01',
                        equals: '==true',
                        describe: 'Pump is not working',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "false", xid: "DP_PLC_M"}, {value: "true", xid: "DP_pump_01"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(false);
            expect(wrapper.vm.errorHandlers.get("Multiple PLC").messages).to.have.lengthOf(1);
        });

        it('Is checkOneCondition moving to next conditions', () => {
            wrapper.vm.errorHandlers.set("Pump Failed",{connection: true, messages: []});
            let condition = {
                name: 'Pump Failed',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '==true',
                        describe: 'Working in Auto Mode',
                        toNote: true,
                        toNext: true,
                    },
                    {
                        xid: 'DP_pump_01',
                        equals: '==true',
                        describe: 'Pump is not working',
                        toNote: true,
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "false", xid: "DP_PLC_M"}, {value: "false", xid: "DP_pump_01"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(false);
            expect(wrapper.vm.errorHandlers.get("Pump Failed").messages).to.have.lengthOf(2);
        });

        it('Is checkOneCondition working correctly for ">"', () => {
            let condition = {
                name: 'Manual PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '>10',
                        describe: 'Working in Manual Mode',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "12", xid: "DP_PLC_M"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(true);
            expect(wrapper.vm.componentState).to.equal("Manual PLC");

        });

        it('Is checkOneCondition working correctly for ">" (false)', () => {
            let condition = {
                name: 'Manual PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '>10',
                        describe: 'Working in Manual Mode',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "10", xid: "DP_PLC_M"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(false);
        });

        it('Is checkOneCondition working correctly for ">="', () => {
            let condition = {
                name: 'Manual PLC',
                toChecked: [
                    {
                        xid: 'DP_PLC_M',
                        equals: '>=10',
                        describe: 'Working in Manual Mode',
                    },
                ],
            };
            wrapper.vm.dataPointValues = [{value: "10", xid: "DP_PLC_M"}];
            let result = wrapper.vm.checkOneCondition(condition);
            expect(result).to.equal(true);
        });

        it('Is checkConditions working correctly return error', () => {
            wrapper.vm.getDataPointValues = () => { throw Error("Error")}
            wrapper.vm.checkConditions();
            let errorHandler = wrapper.vm.errorHandlers.get('Manual PLC');
            expect(errorHandler.connection).to.equal(false);
            expect(errorHandler.messages).to.have.lengthOf(1);
        });
    })
})