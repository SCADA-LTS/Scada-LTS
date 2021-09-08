import { expect } from 'chai';

import storeMailingList from '../../mocks/store/recipientListMock';
import storeUsersMock from '../../mocks/store/usersMock';
import { exampleUser, blankUser } from '../../mocks/objects/UserMocks';

import UserDetails from '@/views/UserList/UserDetails';
import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	storeMailingList,
	storeUsersMock,
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test User Details Scenario', () => {
	let wrapper2;

    function getInput(order) {
        return `#rl-section-details form .row div:nth-of-type(${order}) .v-input:first-of-type input`;
    }

    function getInputLabel(order) {
        return `#rl-section-details form .row div:nth-of-type(${order}) .v-input:first-of-type`;
    }

    function getSelect(row, order) {
        return `#rl-section-details form .row:nth-of-type(${row}) div:nth-of-type(${order}) .v-select:first-of-type`;
    }

    describe('Component Initialization::exampleUser::nonAdmin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserDetails, modules, {
                userDetails: exampleUser,
                userProfiles: [{id:0, name: "None"}],
                userPassword: '',
                edit: false,
            });
        });

        it('Is header and translation loaded', async() => {
            await wrapper2.vm.$nextTick();
            expect(wrapper2.get('h2').text()).to.equal('User Details');
        });

        it('Is exampleUser data loaded', () => {
            expect(wrapper2.get(getInput(1)).element.value).to.equal('unittester');
            expect(wrapper2.get(getInput(3)).element.value).to.equal('Tester');
            expect(wrapper2.get(getInput(4)).element.value).to.equal('Mock');
            expect(wrapper2.get(getInput(5)).element.value).to.equal('test@mock.com');
            expect(wrapper2.get(getInput(6)).element.value).to.equal('48121212');
        });

        it('Are Username and Email address fields disabled', () => {
            expect(wrapper2.find(getInput(1)).attributes().disabled).to.equal('disabled');
            expect(wrapper2.find(getInput(5)).attributes().disabled).to.equal('disabled');
        });

        it('Are settings for admin user not visible', () => {
            const componentBody = wrapper2.find('#rl-section-details form').text();
            expect(componentBody).to.not.contain('Administrator');
            expect(componentBody).to.not.contain('User Profile');
            expect(componentBody).to.not.contain('Disabled');
            expect(componentBody).to.not.contain('Hide menu');
        });
    }) 

    describe('Component Initialization::exampleUser::Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserDetails, modules, {
                userDetails: exampleUser,
                userProfiles: [{id:0, name: "None"}],
                userPassword: '',
                edit: false,
            });
            wrapper2.vm.$store.state.loggedUser.admin = true;
        });

        it('Is exampleUser data loaded for Admin', () => {
            expect(wrapper2.get(getInput(1)).element.value).to.equal('unittester');
            expect(wrapper2.find(getSelect(1,2)).props('items').length).to.equal(1);
            expect(wrapper2.get(getInput(3)).element.value).to.equal('Tester');
            expect(wrapper2.get(getInput(4)).element.value).to.equal('Mock');
            expect(wrapper2.get(getInput(5)).element.value).to.equal('test@mock.com');
            expect(wrapper2.get(getInput(6)).element.value).to.equal('48121212');
            expect(wrapper2.find(getSelect(3, 1)).props('items').length).to.equal(5);
            expect(wrapper2.find(getSelect(3, 2)).props('items').length).to.equal(3);
        });

    })

    describe('Component Initialization::blankUser::Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserDetails, modules, {
                userDetails: blankUser,
                userProfiles: [{id:0, name: "None"}],
                userPassword: 'pa$$word',
                edit: true,
            });
            wrapper2.vm.$store.state.loggedUser.admin = true;
        });

        it('Is exampleUser data loaded for Admin', () => {
            expect(wrapper2.get(getInputLabel(1)).text()).to.equal('Username');
            expect(wrapper2.get(getInputLabel(3)).text()).to.equal('Password');
            expect(wrapper2.get(getInputLabel(4)).text()).to.equal('Repeat password');
            expect(wrapper2.get(getInputLabel(5)).text()).to.equal('First Name');
            expect(wrapper2.get(getInputLabel(6)).text()).to.equal('Last Name');
            expect(wrapper2.get(getInputLabel(7)).text()).to.equal('Email address');
            expect(wrapper2.get(getInputLabel(8)).text()).to.equal('Phone number');
        });

        it('User creation - blank - no passing', () => {
            expect(wrapper2.vm.isFormValid()).to.equal(false);
        });

        it('User creation - filled - passing', async() => {
            await wrapper2.find(getInput(1)).setValue("mockedusertest");
            await wrapper2.find(getInput(4)).setValue("pa$$word");
            await wrapper2.find(getInput(7)).setValue("mocked@mail.com");
            await wrapper2.find(getInput(8)).setValue("48123321");
            await wrapper2.vm.$nextTick();

            expect(wrapper2.vm.isFormValid()).to.equal(true);
        });

        

    })
	
});
