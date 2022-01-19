import { expect } from 'chai';

import storeMailingList from '../../mocks/store/recipientListMock';
import storeUsersMock from '../../mocks/store/usersMock';
import storeUserProfilesMock from '../../mocks/store/userProfilesMock';
import { exampleUser } from '../../mocks/objects/UserMocks';

import UserList from '@/views/Users/UserList';
import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	storeMailingList,
    storeUserProfilesMock,
	storeUsersMock,
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test User List Scenario', () => {
	let wrapper2;

    describe('UI Component Initialization Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserList, modules);
            wrapper2.vm.$store.state.loggedUser.admin = true;
        });

        it('Is user list component loaded', async() => {
            const userList = wrapper2.get("#usersList")
            expect(userList.exists()).to.equal(true);
            expect(userList.text()).to.include('Admin');
            expect(userList.text()).to.include('Worker');
            const users = userList.findAll(".v-list-item");
            expect(users.length).to.equal(2);
        });

        it('Is user details rendering "blank" message', ()=> {
            const userList = wrapper2.get("#userDetails")
            expect(userList.exists()).to.equal(true);
            expect(userList.text()).to.include('Select a user to see details');
        })

        it('Is user loaded by clicking the list entry', async () => {
            const userEntry = wrapper2.get("#usersList .v-list-item:first-of-type");
            await userEntry.trigger('click');
            const userDetails = wrapper2.get("#userDetails")
            expect(userDetails.text()).to.include('User Details')
            expect(userDetails.text()).to.include('Username')
        })

        it('Is admin icon rendered correctly', () => {
            const userEntry = wrapper2.get("#usersList .v-list-item:first-of-type");
            expect(userEntry.find('i.mdi-account-tie').isVisible()).to.be.true;
            expect(userEntry.find('i.mdi-account').isVisible()).to.be.false;
        })

        it('Is non-admin icon rendered correctly', () => {
            const userEntry = wrapper2.get("#usersList .v-list-item:last-of-type");
            expect(userEntry.find('i.mdi-account').isVisible()).to.be.true;
            expect(userEntry.find('i.mdi-account-tie').isVisible()).to.be.false;
        })

        it('Is user deleted from list', async () => {
            let userEntries = wrapper2.findAll("#usersList .v-list-item");
            expect(userEntries.length).to.equal(2);
            await wrapper2.vm.deleteUser(2);
            userEntries = wrapper2.findAll("#usersList .v-list-item");
            expect(userEntries.length).to.equal(1);
        })

    });

    describe('JS Component Initialization Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserList, modules);
            wrapper2.vm.$store.state.loggedUser.admin = true;
        });

        it('Is fetchUserList working', async() => {
            wrapper2.vm.userList = [];
            expect(wrapper2.vm.userList.length).to.equal(0);
            await wrapper2.vm.fetchUserList();
            expect(wrapper2.vm.userList.length).to.equal(2);
        });

        it('Is fetchUserProfiles working', async() => {
            wrapper2.vm.userProfiles = [];
            expect(wrapper2.vm.userProfiles.length).to.equal(0);
            await wrapper2.vm.fetchUserProfiles();
            expect(wrapper2.vm.userProfiles.length).to.equal(2);
            expect(wrapper2.vm.userProfiles[0].name).to.equal('UserProfile');
            expect(wrapper2.vm.userProfiles[0].id).to.equal(1);
            expect(wrapper2.vm.userProfiles[1].name).to.equal('None');
            expect(wrapper2.vm.userProfiles[1].id).to.equal(-1);
        });

        it('Is fetchUserDetails working', async() => {
            wrapper2.vm.selectedUser = null;
            expect(wrapper2.vm.selectedUser).to.equal(null);
            await wrapper2.vm.showUserDetails({id:1});
            expect(wrapper2.vm.selectedUser).to.equal(exampleUser);
        });


    });

    describe('UI Component Initialization non-Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserList, modules);
            wrapper2.vm.$store.state.loggedUser.admin = false;
            wrapper2.vm.$store.state.loggedUser.id = 1;
        });

        it('Is user list component not visible', async() => {
            const userList = wrapper2.find("#usersList")
            expect(userList.exists()).to.equal(false);
        });

        it('Is user details component visible', async() => {
            await wrapper2.vm.showUserDetails(wrapper2.vm.loggedUser);
            await wrapper2.vm.$nextTick();
            const userDetails = wrapper2.get("#userDetails")
            expect(userDetails.text()).to.include('User Details')
            expect(userDetails.text()).to.include('Username')
        });

        it('Is user deatils loaded on mounted()', async() => {
            expect(wrapper2.vm.selectedUser).to.equal(exampleUser);
        })

    });

    
	
});
