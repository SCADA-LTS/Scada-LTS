import { expect } from 'chai';

import VirtualDataSourceEditor from '@/components/datasources/VirtualDataSource/config.vue';
import { prepareMountWrapper } from '../../utils/testing-utils';

import dataSource from '../../mocks/store/dataSourceMock';

const modules = {
    dataSource
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test DataSource Component - Virtual Scenario', () => {
	let wrapper;

    describe('Visual tests for Create', () => {

        before(() => {
            wrapper = prepareMountWrapper(VirtualDataSourceEditor, modules);
        });

        it('Are DataSource labels rendered', async() => {
            await wrapper.vm.$nextTick();
            const dialogText = wrapper.get('#datasource-config').text();
            expect(dialogText).to.contain('Create  Virtual Data Source');
            expect(dialogText).to.contain('DataSource Name');
            expect(dialogText).to.contain('DataSource Export Id');
            expect(dialogText).to.contain('Update Period');
            expect(dialogText).to.contain('Time period');
            expect(dialogText).to.contain('Create');
        });

        it('Is Create Button disabled by default', () => {
            const btn = wrapper.get('#datasource-config--accept');
            expect(btn.attributes().disabled).to.equal('disabled');
        });

        it('Is Create Button enabled when name is not empty', async  () => {
            const nameInput = wrapper.get('#datasource-config--name input');
            nameInput.setValue("MockDataSource");
            await wrapper.vm.$nextTick();
            const btn = wrapper.get('#datasource-config--accept');
            expect(btn.attributes().disabled).to.equal('disabled');
        });

        it('Is Create button disabled when ExportID is empty', async () => {
            const nameInput = wrapper.get('#datasource-config--xid input');
            nameInput.setValue("");
            await wrapper.vm.$nextTick();
            const btn = wrapper.get('#datasource-config--accept');
            expect(btn.attributes().disabled).to.equal('disabled');
            nameInput.setValue("MTS_01234");
        });

        it('Is Create button disabled when ExportID is not Unique', async () => {
            const nameInput = wrapper.get('#datasource-config--xid input');
            nameInput.setValue("NOT_UNIQUE");
            await wrapper.vm.$nextTick();
            const btn = wrapper.get('#datasource-config--accept');
            expect(btn.attributes().disabled).to.equal('disabled');
            nameInput.setValue("MTS_01234");
        });

    });

    describe('Visual tests for Update', () => {

        before(() => {
            wrapper = prepareMountWrapper(VirtualDataSourceEditor, modules, {
                createMode: false,
                datasource: {
                    name: "MockEditDS",
                    xid: "MTS_0123456789",
                    updatePeriod: 10,
                    updatePeriodType: 2,
                }
            });
        });

        it('Are DataSource labels rendered', async() => {
            await wrapper.vm.$nextTick();
            const dialogText = wrapper.get('#datasource-config').text();
            expect(dialogText).to.contain('Update  Virtual Data Source');
            expect(dialogText).to.contain('DataSource Name');
            expect(dialogText).to.contain('DataSource Export Id');
            expect(dialogText).to.contain('Update Period');
            expect(dialogText).to.contain('Time period');
            expect(dialogText).to.contain('Update');
        });    

    });

    describe('Logic tests', () => {
        before(() => {
            wrapper = prepareMountWrapper(VirtualDataSourceEditor, modules, {
                createMode: false,
                datasource: {
                    id: 10,
                    name: "MockEditDS",
                    xid: "MTS_0123456789",
                    updatePeriod: 10,
                    updatePeriodType: 2,
                }
            });
        });

        it('Is name changing', async() => {
            const value = "MockDataSourceEdit";
            const nameInput = wrapper.get('#datasource-config--name input');
            nameInput.setValue(value);
            expect(wrapper.vm.datasource.name).to.equal(value);
        });

        it('Is ExportID changing', async() => {
            const value = "XID_TEST_01";
            const nameInput = wrapper.get('#datasource-config--xid input');
            nameInput.setValue(value);
            expect(wrapper.vm.datasource.xid).to.equal(value);
        }); 

        it('Is Update Period time changing', async() => {
            const value = "15";
            const nameInput = wrapper.get('#datasource-config--update-periods input');
            nameInput.setValue(value);
            expect(wrapper.vm.datasource.updatePeriods).to.equal(value);
        });

        it('Is saving event working', async() => {
            const btn = wrapper.get('#datasource-config--accept');
            btn.trigger('click');
            await wrapper.vm.$nextTick();
            const response = wrapper.emitted().saved[0][0];
            expect(response.name).to.equal("MockDataSourceEdit");
            expect(response.xid).to.equal("XID_TEST_01");
            expect(response.updatePeriods).to.equal("15");
        });

    });
	
});
