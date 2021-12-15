import { expect } from 'chai';

import DataSourceConfigBase from '@/components/datasources/DataSourceConfig.vue';
import { prepareMountWrapper } from '../../utils/testing-utils';

import dataSource from '../../mocks/store/dataSourceMock';

const modules = {
    dataSource
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test DataSourceConfig Base Component', () => {
	let wrapper;
    

    describe('Test base visual elements::create', () => {
        let dialogText;

        before(async () => {
            wrapper = prepareMountWrapper(DataSourceConfigBase, modules);
            await wrapper.vm.$nextTick();
            dialogText = wrapper.get('#datasource-config').text();
        });

        it('Is title rendered', () => {
            expect(dialogText).to.contain('Create  Data Source Config');
        });

        it('Is data source name field rendered', () => {
            expect(dialogText).to.contain('DataSource Name');
        });

        it('Is data source export id field rendered', () => {
            expect(dialogText).to.contain('DataSource Export Id');
        });

        it('Is update period field rendered', () => {
            expect(dialogText).to.contain('Update Period');
        });

        it('Is time period field rendered', () => {
            expect(dialogText).to.contain('Time period');
        });

        it('Is create button visible', () => {
            expect(dialogText).to.contain('Create');
        });
    });

    describe('Test base visual elements::edit', () => {
        let dialogText;

        before(async () => {
            wrapper = prepareMountWrapper(DataSourceConfigBase, modules, {
                creator: false,
            });
            await wrapper.vm.$nextTick();
            dialogText = wrapper.get('#datasource-config').text();
        });

        it('Is title rendered', () => {
            expect(dialogText).to.contain('Update  Data Source Config');
        });

        it('Is update button visible', () => {
            expect(dialogText).to.contain('Update');
        });
    });

    describe('Test base visual elements::create::no-polling', () => {
        let dialogText;

        before(async () => {
            wrapper = prepareMountWrapper(DataSourceConfigBase, modules, {
                polling: false,
            });
            await wrapper.vm.$nextTick();
            dialogText = wrapper.get('#datasource-config').text();
        });

        it('Is title rendered', () => {
            expect(dialogText).to.contain('Create  Data Source Config');
        });

        it('Is update period field not rendered', () => {
            expect(dialogText).to.not.contain('Update Period');
        });

        it('Is time period field not rendered', () => {
            expect(dialogText).to.not.contain('Time period');
        });
    });

    describe('Test base logic elements::create', () => {

        before(async () => {
            wrapper = prepareMountWrapper(DataSourceConfigBase, modules);
        });

        it('Is Unique XID generated on mounted() hook', async() => {
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.datasource.xid).to.equal('UNIT_XID_01');
        });

        it('CheckXidUnique valid', async() => {
            wrapper.vm.checkXidUnique()
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.xidUnique).to.be.true;
        });

        it('CheckXidUnique not-valid', async() => {
            wrapper.vm.datasource.xid = 'NOT_UNIQUE';
            wrapper.vm.checkXidUnique()
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.xidUnique).to.be.false;
        });

        it('Is accept event working', async() => {
            expect(wrapper.emitted().accept).to.be.undefined;
            wrapper.vm.accept();
            expect(wrapper.emitted().accept).to.not.be.undefined;
        });

        it('Is cancel event working', async() => {
            expect(wrapper.emitted().cancel).to.be.undefined;
            wrapper.vm.cancel();
            expect(wrapper.emitted().cancel).to.not.be.undefined;
        });


    });
	
});
