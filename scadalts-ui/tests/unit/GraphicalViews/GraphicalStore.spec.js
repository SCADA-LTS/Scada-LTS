
import { expect } from 'chai';
import { createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import gvStore from '../../../src/store/graphicalViews/index';
import GraphicalViewItem from '../../../src/models/GraphicalViewItem';
import HTMLComponent from '../../../src/components/GraphicalView/ViewComponents/VCHtml/model';

describe('💠️ Graphical View Store ::Test Scenario', () => {

    let store;

    before(() => {
        const localVue = createLocalVue();
        localVue.use(Vuex);
        store = new Vuex.Store(gvStore);
    });

    describe("Mutations verification", () => {
        
        it('Is SET_GRAPHICAL_PAGE_EDIT change to EditMode', () => {
            store.commit('SET_GRAPHICAL_PAGE_EDIT', true);
            expect(store.state.graphicalPageEdit).to.be.true;
        });
        
        it("Is SET_GRAPHICAL_PAGE_ICONIFY is changed to true", () => {
            expect(store.state.graphicalPageIconify).to.be.false;
            store.commit('SET_GRAPHICAL_PAGE_ICONIFY', true);
            expect(store.state.graphicalPageIconify).to.be.true;
        });
        
        it('Is SET_GRAPHICAL_PAGE_EDIT change to ReadMode', () => {
            expect(store.state.graphicalPageIconify).to.be.true;
            store.commit('SET_GRAPHICAL_PAGE_EDIT', false);
            expect(store.state.graphicalPageEdit).to.be.false;
            expect(store.state.graphicalPageIconify).to.be.false;
        });
        
        it('Is TOGGLE_GRAPHICAL_PAGE_ICONIFY activated 2 times return to begining', () => {
            expect(store.state.graphicalPageIconify).to.be.false;
            store.commit('TOGGLE_GRAPHICAL_PAGE_ICONIFY');
            expect(store.state.graphicalPageIconify).to.be.true;
            store.commit('TOGGLE_GRAPHICAL_PAGE_ICONIFY');
            expect(store.state.graphicalPageIconify).to.be.false;
        });
        
        it('Is SET_GRAPHICAL_PAGE set page and resolution', () => {
            expect(store.state.graphicalPage).to.be.null;
            const page = new GraphicalViewItem(1);
            store.commit('SET_GRAPHICAL_PAGE', page);
            expect(store.state.graphicalPage).to.be.equal(page);
            expect(store.state.resolution.width).to.be.equal(page.width);
            expect(store.state.resolution.height).to.be.equal(page.height);
            expect(store.state.resolution.width).to.be.a('number');
            expect(store.state.resolution.height).to.be.a('number');
        });
        
        it('Is SET_GRAPHICAL_PAGE_BACKUP set pageBackup', () => {
            expect(store.state.graphicalPageBackup).to.be.null;
            const page = new GraphicalViewItem(1);
            store.commit('SET_GRAPHICAL_PAGE_BACKUP', page);
            expect(store.state.graphicalPageBackup).to.not.be.equal(page);
            expect(JSON.stringify(store.state.graphicalPageBackup)).to.be.equal(JSON.stringify(page));
        });
        
        it('Is REVERT_GRAPHICAL_PAGE reset the page state to backup', () => {
            const page = new GraphicalViewItem(2);
            page.name = 'MochaTest';
            store.commit('SET_GRAPHICAL_PAGE', page);
            expect(store.state.graphicalPage.name).to.be.equal('MochaTest');
            store.commit('REVERT_GRAPHICAL_PAGE');
            expect(store.state.graphicalPage.name).to.be.equal('');
        });
        
        it('Is REVERT_GRAPHICAL_PAGE reset the page state to blank if Backup is null', () => {
            const page = new GraphicalViewItem(2);
            
            page.name = 'MochaTest2';
            store.state.graphicalPageBackup = null;
            expect(store.state.graphicalPageBackup).to.be.null;

            store.commit('SET_GRAPHICAL_PAGE', page);
            expect(store.state.graphicalPage.name).to.be.equal('MochaTest2');

            store.commit('REVERT_GRAPHICAL_PAGE');
            expect(store.state.graphicalPage).to.be.null;
            expect(store.state.resolution.width).to.be.equal(1024);
            expect(store.state.resolution.height).to.be.equal(768);
        });
        
        it('Is SET_GRAPHICAL_PAGE_BACKGROUND change the backgroundFilename and resolution', () => {
            const page = new GraphicalViewItem(1);
            store.commit('SET_GRAPHICAL_PAGE', page);
            expect(store.state.graphicalPage.backgroundFilename).to.be.null;
            expect(store.state.resolution.width).to.be.equal(1024);
            const background = {
                imgUrl: 'testURL',
                width: 800,
                height: 600
            }
            store.commit('SET_GRAPHICAL_PAGE_BACKGROUND', background);
            expect(store.state.graphicalPage.backgroundFilename).to.be.equal('testURL');
            expect(store.state.resolution.width).to.be.equal(800);
            expect(store.state.resolution.height).to.be.equal(600);
        });
        
        it('Is RESET_GRAPHICAL_PAGE_BACKGROUND revert the canvas resolution', () => {
            store.commit('RESET_GRAPHICAL_PAGE_BACKGROUND');
            expect(store.state.graphicalPage.backgroundFilename).to.be.null;
            expect(store.state.resolution.width).to.be.equal(1024);
            expect(store.state.resolution.height).to.be.equal(768);
        });

        it('Is UPDATE_GRAPHICAL_PAGE_RESOLUTION change to valid canvas resolution', () => {
            const page = new GraphicalViewItem(1);
            page.resolution = 0;
            store.commit('SET_GRAPHICAL_PAGE', page);
            store.commit('UPDATE_GRAPHICAL_PAGE_RESOLUTION');
            expect(store.state.resolution.width).to.be.equal(640);
            expect(store.state.resolution.height).to.be.equal(480);
        });

        it('Is UPDATE_GRAPHICAL_PAGE_RESOLUTION to unsupported type change canvas resolution to default', () => {
            const page = new GraphicalViewItem(1);
            page.resolution = 100;
            store.commit('SET_GRAPHICAL_PAGE', page);
            store.commit('UPDATE_GRAPHICAL_PAGE_RESOLUTION');
            expect(store.state.resolution.width).to.be.equal(1024);
            expect(store.state.resolution.height).to.be.equal(768);
        });

        it('Is ADD_COMPONENT_TO_PAGE set index 0 to component if array is empty', () => {
            const component = new HTMLComponent();
            expect(store.state.graphicalPage.viewComponents.length).to.be.equal(0);
            store.commit('ADD_COMPONENT_TO_PAGE', component);
            expect(store.state.graphicalPage.viewComponents.length).to.be.equal(1);
            expect(store.state.graphicalPage.viewComponents[0].index).to.be.equal(0);
        });

        it('Is ADD_COMPONENT_TO_PAGE set index 1 to another component added to array', () => {
            const component = new HTMLComponent();
            component.x = 100;
            component.y = 100;
            expect(store.state.graphicalPage.viewComponents.length).to.be.equal(1);
            store.commit('ADD_COMPONENT_TO_PAGE', component);
            expect(store.state.graphicalPage.viewComponents.length).to.be.equal(2);
            expect(store.state.graphicalPage.viewComponents[1].index).to.be.equal(1);
        });

        it('Is SET_COMPONENT_EDIT create componentBackup', () => {
            const component = new HTMLComponent();
            expect(store.state.componentEditBackup).to.be.null;
            store.commit('SET_COMPONENT_EDIT', component);
            expect(store.state.componentEdit).to.be.equal(0);
            expect(store.state.componentEditBackup.content).to.be.equal("<p>HTML Component</p>");
        });

        it('Is REVERT_COMPONENT_EDIT create componentBackup', () => {
            const component = store.state.graphicalPage.viewComponents[0];
            component.content = "TestMocha"
            expect(component.content).to.be.equal("TestMocha");
            store.commit('REVERT_COMPONENT_EDIT');
            expect(store.state.graphicalPage.viewComponents[0].content).to.be.equal("<p>HTML Component</p>");
        });

        it('Is DELETE_COMPONENT_EDIT delete the active component', () => {
            expect(store.state.graphicalPage.viewComponents.length).to.be.equal(2);
            store.commit('DELETE_COMPONENT_EDIT');
            expect(store.state.graphicalPage.viewComponents.length).to.be.equal(1);
        });


        
    })

});
