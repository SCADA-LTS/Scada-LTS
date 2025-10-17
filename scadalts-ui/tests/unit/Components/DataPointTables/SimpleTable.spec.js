import { expect } from "chai";
import sinon from "sinon";
import { getValidDate } from '../../../../src/components/utils';
import { prepareMountWrapper } from "../../../utils/testing-utils";
import webSocketModule from '../../../mocks/store/websocketMock';
import dataPoint from '../../../mocks/store/dataPointMock';
import SimplePointTable from '../../../../src/components/graphical_views/pointTables/SimplePointTable';
import { is } from "core-js/core/object";


const modules = {
    webSocketModule,
    dataPoint
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Simple Table Component Tests', () => {

    let wrapper;

    describe('Component Initialization :: Default values', () => {
        before(() => {
            wrapper = prepareMountWrapper(SimplePointTable, modules, {
                pointIds: '1,2'
            });
        });

        it('Should the pointIds be equal 1,2', () => {
            expect(wrapper.vm.pointIds).to.be.equal('1,2');    
        })

        it('Should wsConnection be false', () => {
            expect(wrapper.vm.wsConnection).to.be.false;
        });

        it('Prop "showTotal" should be false', () => {
            expect(wrapper.vm.showTotal).to.be.false;
        });

        it('Prop "showAverage" should be false', () => {
            expect(wrapper.vm.showAverage).to.be.false;
        });

        it('Prop "showMax" should be false', () => {
            expect(wrapper.vm.showMax).to.be.false;
        });

        it('Prop "showMin" should be false', () => {
            expect(wrapper.vm.showMin).to.be.false;
        });

        it('Prop "round" should be equal 2', () => {
            expect(wrapper.vm.roundValue).to.equal(2);
        });

        it('Prop "maxWidth" should be equal 600', () => {
            expect(wrapper.vm.maxWidth).to.equal(600);
        });

        it('Prop "maxHeight" should be equal 400', () => {
            expect(wrapper.vm.maxHeight).to.equal(400);
        });
    })

    describe('Component Initialization :: Method check', () => {
        let response;
        before(async () => {
            wrapper = prepareMountWrapper(SimplePointTable, modules, {
                pointIds: '1,2'
            });
            response = await wrapper.vm.getDataPointHistoricValues(1);
        });

        it('Is API response return an object',() => {
            expect(response).to.be.an('object');
        });

        it('Is API response contain "name" property', () => {
            expect(response).to.have.property('name');
        });

        it('Is API response contain "values" property', () => {
            expect(response).to.have.property('values');
        });

        it('Is API response contain "type" property', () => {
            expect(response).to.have.property('type');
        });

        it('Is InitDataPointValues method create 2 table entries', async () => {
            wrapper.vm.initDataPointValues();
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.pointState.length).to.equal(2);
        });

        it('Is InitTimeRange method create a start date by default from 1 hour', () => {
            let date = new Date().getTime() - (3600000);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            wrapper.vm.initTimeRange();
            expect(wrapper.vm.startTimestamp).to.be.within(datePrecisionDown, datePrecisionUp);
        })

    })




    

})