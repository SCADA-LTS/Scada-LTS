import { expect } from 'chai';
import { prepareMountWrapper } from "../../utils/testing-utils";
import graphicalViewModule from '../../mocks/store/graphicalView/mainpage.mock';

import GraphicalView from '@/views/GraphicalViews/index.vue';
import GraphicalViewItem from '../../../src/models/GraphicalViewItem';

const modules = {
    graphicalViewModule
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

describe('💠️ Graphical View Index :: Page Test Scenario', () => {
    let wrapper;

    before(() => {
        wrapper = prepareMountWrapper(GraphicalView, modules, {});
    });



    describe('Method verification', () => {
        it('Is fetchGraphicalViewList() method working', async () => {
            wrapper.vm.graphicalViewList = [];
            wrapper.vm.fetchGraphicalViewList(1);
            await wrapper.vm.$nextTick();

            expect(wrapper.vm.graphicalViewList.length).to.equal(1);
            expect(wrapper.vm.graphicalViewList[0].id).to.equal(1);
        });

        it('Is changeToEditMode() method working', async () => {
            expect(wrapper.vm.editMode).equal(false);
            wrapper.vm.activePage = new GraphicalViewItem(1);
            await wrapper.vm.changeToEditMode();
            expect(wrapper.vm.editMode).equal(true);
        });

        it('Is onRouteChanged() method working', () => {
            wrapper.vm.activeGraphicalView = 0;
            wrapper.vm.onRouteChanged(1);
            expect(wrapper.vm.activeGraphicalView).equal(1);
        });

        it('Is toggleFullScreen() method working', () => {
            wrapper.vm.fullscreenEnabled = true;
            wrapper.vm.toggleFullScreen();
            expect(wrapper.vm.fullscreenEnabled).equal(false);
        });

        it('Is editCancel() method working', () => {
            wrapper.vm.editCancel();
            expect(wrapper.vm.createMode).equal(false);
            expect(wrapper.vm.editMode).equal(false);
        });
    });

    
    describe('Component verification', () => {
        describe('Read mode', () => {
            it('Is title visible', () => {
                expect(wrapper.find('h1').html()).to.include("Graphical Views");
            })

            it("Is only create button visible when there is no active View", async () => {
                wrapper.vm.activeGraphicalView = null;
                await wrapper.vm.$nextTick();
                const toolbarButtons = wrapper.find('.toolbar--action-butons').findAll('button');
                expect(toolbarButtons.length).to.equal(1);
            })

            it('Are all 4 buttons visible', async () => {
                wrapper.vm.activeGraphicalView = 1;
                await wrapper.vm.$nextTick();
                const toolbarButtons = wrapper.find('.toolbar--action-butons').findAll('button');
                expect(toolbarButtons.length).to.equal(4);
            });

            it("Is only create button visible when user has no right to active View", async () => {
                wrapper.vm.$store.commit('SET_USER_ACCESS', 1);
                await wrapper.vm.$nextTick();
                expect(wrapper.vm.userAccess).equal(1);
                expect(wrapper.vm.activeGraphicalView).equal(1);
                const toolbarButtons = wrapper.find('.toolbar--action-butons').findAll('button');
                expect(toolbarButtons.length).to.equal(2);
            })
        })

        describe('Edit mode', () => {
            it('Are edit form fields visible', async () => {
                wrapper.vm.changeToEditMode();
                await wrapper.vm.$nextTick();
                const editForm = wrapper.find('.toolbar--edit-form');
                expect(editForm.html()).to.include("Name");
                expect(editForm.html()).to.include("Export ID");
                expect(editForm.html()).to.include("Annonymous Access");
                expect(editForm.html()).to.include("Canvas size");
            })

            it('Are action buttons visible', async () => {
                const buttons = wrapper.find('.slts-page-actions--floating').findAll('button');
                expect(buttons.length).to.equal(2);
                expect(buttons.at(0).html()).to.include("Cancel");
                expect(buttons.at(1).html()).to.include("Update");
            })

            it('Is form not valid if name is empty', async() => {
                wrapper.vm.activePage.name = "";
                await wrapper.vm.$nextTick();
                expect(wrapper.vm.isFormValid()).equal(false);
                expect(wrapper.find('.slts-page-actions--floating').findAll('button').at(1).html()).to.include('disabled');
            })

            it('Is form valid if name is not Empty', async() => {
                wrapper.vm.activePage.name = "Example Test";
                await wrapper.vm.$nextTick();
                expect(wrapper.vm.isFormValid()).equal(true);
            })

            it('Is form not valid if xid is empty', async() => {
                wrapper.vm.activePage.xid = "";
                await wrapper.vm.$nextTick();
                expect(wrapper.vm.isFormValid()).equal(false);
                await wrapper.vm.$nextTick();
                expect(wrapper.find('.slts-page-actions--floating').findAll('button').at(1).html()).to.include('disabled');
            })

            it('Is form valid if xid is not empty', async() => {
                wrapper.vm.activePage.xid = "TEST_XID";
                await wrapper.vm.$nextTick();
                expect(wrapper.vm.isFormValid()).equal(true);
                await wrapper.vm.$nextTick();
                expect(wrapper.find('.slts-page-actions--floating').findAll('button').at(1).html()).to.not.include('disabled');
            })

            it('Is form not valid if xid is not unique', async() => {
                wrapper.vm.activePage.xid = "BAD_XID";
                await wrapper.vm.$nextTick();
                await wrapper.vm.checkXidUnique();
                expect(wrapper.vm.isFormValid()).equal(false);
                expect(wrapper.find('.slts-page-actions--floating').findAll('button').at(1).html()).to.include('disabled');
            })
        })
    });
})