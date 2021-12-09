import { expect } from 'chai';
import { prepareMountWrapper } from "../../utils/testing-utils";
import graphicalViewModule from '../../mocks/store/graphicalView/graphpage.mock';

import GraphicalViewPage from '@/views/GraphicalViews/GraphicalViewPage.vue';

const modules = {
    graphicalViewModule
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

describe('💠️ Graphical View :: Page Test Scenario', () => {
    let wrapper;

    before(() => {
        wrapper = prepareMountWrapper(GraphicalViewPage, modules, {});
    });



    describe('Method verification', () => {

        it('Is fetchGraphicalView() method working', async () => {
            wrapper.vm.fetchGraphicalView(1);
            await wrapper.vm.$nextTick();

            expect(wrapper.vm.viewPage.id).to.equal(1);
        });

        it('Is getImageSets() method working', async () => {
            await wrapper.vm.getImageSets();
            expect(wrapper.vm.$store.state.graphicalViewModule.imageSets.length).equal(2);
        });

        it('Is updateComponents() method working', () => {
            expect(wrapper.vm.changes).equal(0);
            wrapper.vm.updateComponents();
            expect(wrapper.vm.changes).equal(1);
        })

        it('Is showAllComponents() method working', () => {
            wrapper.vm.selectedComponents = [
                {
                    id: 1,
                    visible: false,
                }, {
                    id: 2,
                    visible: false,
                }
            ];
            expect(wrapper.vm.hiddenComponents.length).equal(0);
            wrapper.vm.hiddenComponents = wrapper.vm.selectedComponents;
            expect(wrapper.vm.hiddenComponents.length).equal(2);
            wrapper.vm.showAllComponents();
            expect(wrapper.vm.hiddenComponents.length).equal(0);
            expect(wrapper.vm.selectedComponents[0].visible).equal(true);
            expect(wrapper.vm.selectedComponents[1].visible).equal(true);
        });

        it('Is moveComponentDown() method working', async () => {
            wrapper.vm.selectedComponents = [{
                selected: true,
                visible: true,
                menuEdit: false,
                component: {
                    x: 10,
                    y: 10,
                    z: 3,
                },
                moveComponentDown: function () {
                    this.component.z -= 1;
                }
            }];
            await wrapper.vm.$nextTick();
            wrapper.vm.moveComponentDown();
            expect(wrapper.vm.activeComponent.component.z).equal(2);
        });

        it('Is moveComponentToBottom() method working', async () => {
            wrapper.vm.selectedComponents = [{
                component: {
                    z: 3,
                },
                moveComponentToBottom: function () {
                    this.component.z = 1;
                }
            }];
            await wrapper.vm.$nextTick();
            wrapper.vm.moveComponentToBottom();
            expect(wrapper.vm.activeComponent.component.z).equal(1);
        });

        it('Is moveComponentUp() method working', async () => {
            wrapper.vm.selectedComponents = [{
                component: {
                    z: 3,
                },
                moveComponentUp: function () {
                    this.component.z += 1;
                }
            }];
            await wrapper.vm.$nextTick();
            wrapper.vm.moveComponentUp();
            expect(wrapper.vm.activeComponent.component.z).equal(4);
        });

        it('Is moveComponentToTop() method working', async () => {
            wrapper.vm.selectedComponents = [{
                component: {
                    z: 3,
                },
                moveComponentToTop: function () {
                    this.component.z = 10;
                }
            }];
            await wrapper.vm.$nextTick();
            wrapper.vm.moveComponentToTop();
            expect(wrapper.vm.activeComponent.component.z).equal(10);
        });

        it('Is toggleComponentVisibility() method working', () => {

            wrapper.vm.selectedComponents = [{
                visible: true,
            }];
            wrapper.vm.hiddenComponents = [];
            expect(wrapper.vm.activeComponent.visible).equal(true);
            expect(wrapper.vm.hiddenComponents.length).equal(0);
            wrapper.vm.toggleComponentVisibility();
            expect(wrapper.vm.activeComponent.visible).equal(false);
            expect(wrapper.vm.hiddenComponents.length).equal(1);
            wrapper.vm.toggleComponentVisibility();
            expect(wrapper.vm.activeComponent.visible).equal(true);
            expect(wrapper.vm.hiddenComponents.length).equal(0);
        });

    })

    describe('Component Initialization', () => {
        it('Is canvas visible', async () => {
            expect(wrapper.get('.canvas')).to.exist;
        });

        it('Toolbar not-visible in "read" mode', () => {
            expect(wrapper.find('.canvas-toolbar').exists()).equal(false);
        });

        it('Toolbar visible in "edit" mode', async () => {
            wrapper.vm.$store.state.graphicalViewModule.graphicalPageEdit = true;
            await wrapper.vm.$nextTick();
            expect(wrapper.find('.canvas-toolbar').exists()).equal(true);
        });

        it('Aligment toolbar is containing 12 elements', () => {
            const toolbar = wrapper.findAll('.canvas-toolbar--actions');
            expect(toolbar.at(0).findAll('button').length).to.equal(11);
        })

        it('Are all aligment buttons disabled if none component is selected', async () => {
            wrapper.vm.selectedComponents = [];
            await wrapper.vm.$nextTick();
            const aligmentButtons = wrapper.findAll('.canvas-toolbar--actions').at(0).findAll('button');
            for (let i = 0; i < aligmentButtons.length; i++) {
                expect(aligmentButtons.at(i).html()).to.include('disabled');
            }
        });

        it('Basic aligment buttons should be enabled if one component is selected', async () => {
            wrapper.vm.selectedComponents = [{visible: true}];
            await wrapper.vm.$nextTick();
            const aligmentButtons = wrapper.findAll('.canvas-toolbar--actions').at(0).findAll('button');
            
            for (let i = 0; i < 6; i++) {
                expect(aligmentButtons.at(i).html()).to.not.include('disabled');
            }

            for (let i = 6; i < 8; i++) {
                expect(aligmentButtons.at(i).html()).to.include('disabled');
            }

            expect(aligmentButtons.at(8).html()).to.not.include('disabled');
            expect(aligmentButtons.at(9).html()).to.include('disabled');
            expect(aligmentButtons.at(10).html()).to.include('disabled');
        });

        it('All aligment Context buttons should be enabled if two components are selected', async () => {
            wrapper.vm.selectedComponents = [{visible: true}, {visible: true}];
            await wrapper.vm.$nextTick();
            const aligmentButtons = wrapper.findAll('.canvas-toolbar--actions').at(0).findAll('button');
            
            for (let i = 0; i < 6; i++) {
                expect(aligmentButtons.at(i).html()).to.not.include('disabled');
            }

            for (let i = 6; i < 8; i++) {
                expect(aligmentButtons.at(i).html()).to.include('disabled');
            }

            expect(aligmentButtons.at(8).html()).to.not.include('disabled');
            expect(aligmentButtons.at(9).html()).to.not.include('disabled');
            expect(aligmentButtons.at(10).html()).to.include('disabled');
        });

        it('All aligment buttons should be enabled if three components are selected', async () => {
            wrapper.vm.selectedComponents = [{visible: true}, {visible: true}, {visible: true}];
            await wrapper.vm.$nextTick();
            const aligmentButtons = wrapper.findAll('.canvas-toolbar--actions').at(0).findAll('button');
            
            for (let i = 0; i < 10; i++) {
                expect(aligmentButtons.at(i).html()).to.not.include('disabled');
            }

            expect(aligmentButtons.at(10).html()).to.include('disabled');
        });



    })
})