import { expect } from 'chai';
import { prepareMountWrapper } from "../../utils/testing-utils";

import Watchdog from "@/components/graphical_views/watchdog";

const modules = {};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

describe('💠️ Watchdog Unit Basic Test Scenario', () => {
    let wrapper;

    before(() => {
        wrapper = prepareMountWrapper(Watchdog, modules);
    })

    describe('Component Default Initialization', () => {

        it('Is component prop interval initialized', async () => {
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.interval).to.equal(10000);
        })

        it('Is component prop wdIp is by default equal to null', () => {
            expect(wrapper.vm.wdIp).to.equal(null);
        })

        it('Is component prop wdPort is by default equal to null', () => {
            expect(wrapper.vm.wdPort).to.equal(null);
        })

        it('Is component prop dpValidation is by default equal to null', () => {
            expect(wrapper.vm.dpValidation).to.equal(null);
        })

        it('Is component prop dpFailure is by default equal to false', () => {
            expect(wrapper.vm.dpFailure).to.equal(false);
        })

        it('Is Alive Interval running', async () => {
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.isAliveInterval).to.not.equal(null);
        })
    })
})

describe('💠️ Watchdog Unit Props Test Scenario', () => {
    let wrapper;

    before(() => {
        wrapper = prepareMountWrapper(Watchdog, modules, {
            name: 'MochaTests',
            interval: 5000,
            wdIp: '127.0.0.1',
            wdPort: 1234,
            dpValidation: [{
                xid: 'DP_TEST',
                value: 1,
                check: 'equal',
            }],
        });
    })

    describe('Component Default Initialization', () => {

        it('Is component prop interval initialized', async () => {
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.interval).to.equal(5000);
        })

        it('Is component prop wdIp is equal to "127.0.0.1"', () => {
            expect(wrapper.vm.wdIp).to.equal('127.0.0.1');
        })

        it('Is component prop wdPort is equal to 1234', () => {
            expect(wrapper.vm.wdPort).to.equal(1234);
        })

        it('Is component prop dpValidation xid is equal to object config', () => {
            expect(wrapper.vm.dpValidation[0].xid).to.equal("DP_TEST");
        })

        it('Is component prop dpFailure is by default equal to false', () => {
            expect(wrapper.vm.dpFailure).to.equal(false);
        })

    })
})


describe('💠️ Watchdog Methods Unit Test Scenario', () => {
    let wrapper;

    before(() => {
        wrapper = prepareMountWrapper(Watchdog, modules);
    })

    describe('Check Point Condition Tests', () => {

        it('Binary DP value true, check "equal true" should be true', () => {
            const dp = {
                check: 'equal',
                value: true,
            }
            const response = {
                type: 'BinaryValue',
                value: 'true',
            }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(true);
        })

        it('Binary DP value "false", check "equal true" should be false', () => {
            const dp = {
                check: 'equal',
                value: true,
            }
            const response = {
                type: 'BinaryValue',
                value: 'false',
            }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Binary DP value "true", check "not_equal true" should be false', () => {
            const dp = { check: 'not_equal', value: true }
            const response = { type: 'BinaryValue', value: 'true' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Binary DP value "true", check "greater true" should be false', () => {
            const dp = { check: 'greater', value: true }
            const response = { type: 'BinaryValue', value: 'true' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Binary DP value "true", check "less true" should be false', () => {
            const dp = { check: 'less', value: true }
            const response = { type: 'BinaryValue', value: 'true' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Numeric DP value "1.0", check "equal 1" should be true', () => {
            const dp = { check: 'equal', value: 1 }
            const response = { type: 'NumericValue', value: '1.0' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(true);
        })

        it('Numeric DP value "1.5", check "equal 1" should be false', () => {
            const dp = { check: 'equal', value: 1 }
            const response = { type: 'NumericValue', value: '1.5' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Numeric DP value "1.5", check "not_equal 1" should be true', () => {
            const dp = { check: 'not_equal', value: 1 }
            const response = { type: 'NumericValue', value: '1.5' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(true);
        })

        it('Numeric DP value "1.5", check "greater 1" should be true', () => {
            const dp = { check: 'greater', value: 1 }
            const response = { type: 'NumericValue', value: '1.5' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(true);
        })

        it('Numeric DP value "1.5", check "less 1" should be false', () => {
            const dp = { check: 'less', value: 1 }
            const response = { type: 'NumericValue', value: '1.5' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Numeric DP value "1.5", check "less 1.5" should be false', () => {
            const dp = { check: 'less', value: 1.5 }
            const response = { type: 'NumericValue', value: '1.5' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(false);
        })

        it('Numeric DP value "1.5", check "less_equal 1.5" should be true', () => {
            const dp = { check: 'less_equal', value: 1.5 }
            const response = { type: 'NumericValue', value: '1.5' }
            expect(wrapper.vm.checkPointCondition(dp, response)).to.equal(true);
        })

        

    })
})