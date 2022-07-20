import { expect } from 'chai';

import storeMailingList from '../../mocks/store/recipientListMock';
import storeUsersMock from '../../mocks/store/usersMock';
import { exampleUser, blankUser } from '../../mocks/objects/UserMocks';

import UserDetails from '@/views/Users/UserList/UserDetails';
import { prepareMountWrapper } from '../../utils/testing-utils';

const modules = {
	storeMailingList,
	storeUsersMock,
};

global.requestAnimationFrame = (cb) => cb();
global.cancelAnimationFrame = window.clearTimeout;

context('💠️ Test User Details Scenario', () => {
	let wrapper2;

    describe('Component Initialization::exampleUser::nonAdmin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserDetails, modules, {
                userDetails: exampleUser,
                userProfiles: [{id:0, name: "None"}],
                edit: false,
            });
        });

        it('Is header and translation loaded', async() => {
            await wrapper2.vm.$nextTick();
            expect(wrapper2.get('h2').text()).to.equal('User Details');
            const fromText = wrapper2.get('#user-details--form').text();
            expect(fromText).to.include('Username');
            expect(fromText).to.include('Receive audit events');
            expect(fromText).to.include('First Name');
            expect(fromText).to.include('Last Name');
            expect(fromText).to.include('Email address');
            expect(fromText).to.include('Phone number');
            expect(fromText).to.include('Receive alarm emails');
            expect(fromText).to.include('UI Theme');
        });

        it('Are sections for admin editor not visible', () => {
            const fromText = wrapper2.get('#user-details--form').text();
            expect(fromText).to.not.include('User Profile');
            expect(fromText).to.not.include('Administrator');
            expect(fromText).to.not.include('Hide Menu');
            expect(fromText).to.not.include('Disabled');
            expect(fromText).to.not.include('Home URL address');
        });

        it('Is prop exampleUser data loaded', () => {
            const fromHtml = wrapper2.get('#user-details--form');
            
            expect(fromHtml.find('#user-form--username').element.value).to.equal(exampleUser.username);
            expect(fromHtml.find('#user-form--audit-events').element.value).to.equal('');
            expect(fromHtml.find('#user-form--first-name').element.value).to.equal(exampleUser.firstName);
            expect(fromHtml.find('#user-form--last-name').element.value).to.equal(exampleUser.lastName);
            expect(fromHtml.find('#user-form--email').element.value).to.equal(exampleUser.email);
            expect(fromHtml.find('#user-form--phone').element.value).to.equal(exampleUser.phone);
        });

        it('Is Username and Email field disabled', () => {
            const fromHtml = wrapper2.get('#user-details--form');

            expect(fromHtml.find('#user-form--username').attributes().disabled).to.equal('disabled');
            expect(fromHtml.find('#user-form--email').attributes().disabled).to.equal('disabled');
        })

    }) 

    describe('Component Initialization::exampleUser::Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserDetails, modules, {
                userDetails: exampleUser,
                userProfiles: [{id:0, name: "None"}],
                edit: false,
            });
            wrapper2.vm.$store.state.loggedUser.admin = true;
        });

        it('Is prop exampleUser data loaded', async () => {
            const fromHtml = wrapper2.get('#user-details--form');
        
            expect(fromHtml.find('#user-form--username').element.value).to.equal(exampleUser.username);
            expect(fromHtml.find('#user-form--userprofiles').element.value).to.equal('');
            expect(fromHtml.find('#user-form--first-name').element.value).to.equal(exampleUser.firstName);
            expect(fromHtml.find('#user-form--last-name').element.value).to.equal(exampleUser.lastName);
            expect(fromHtml.find('#user-form--email').element.value).to.equal(exampleUser.email);
            expect(fromHtml.find('#user-form--phone').element.value).to.equal(exampleUser.phone);
            expect(fromHtml.find('#user-form--admin').element.value).to.equal('');
            expect(fromHtml.find('#user-form--disabled').element.value).to.equal('');
            expect(fromHtml.find('#user-form--audit-events').element.value).to.equal('');
            expect(fromHtml.find('#user-form--hide-menu').element.value).to.equal('');
            expect(fromHtml.find('#user-form--alarm-events').element.value).to.equal('');
            expect(fromHtml.find('#user-form--theme').element.value).to.equal('');
            expect(fromHtml.find('#user-form--homeurl').element.value).to.equal('');
        });

    })

    describe('Component Initialization::blankUser::Admin', () => {

        before(() => {
            wrapper2 = prepareMountWrapper(UserDetails, modules, {
                userDetails: blankUser,
                userProfiles: [{id:0, name: "None"}],
                edit: true,
            });
            wrapper2.vm.$store.state.loggedUser.admin = true;
            wrapper2.vm.userPassword = 'pa$$word';
        });

        it('Is exampleUser data loaded for Admin', () => {

            const fromText = wrapper2.get('#user-details--form').text();
            expect(fromText).to.include('Username');
            expect(fromText).to.include('User Profile');
            expect(fromText).to.include('Password');
            expect(fromText).to.include('Repeat password');
            expect(fromText).to.include('First Name');
            expect(fromText).to.include('Last Name');
            expect(fromText).to.include('Email address');
            expect(fromText).to.include('Phone number');
            
        });

        it('User creation - blank - no passing', () => {
            expect(wrapper2.vm.isFormValid()).to.equal(false);
        });

        it('User creation - disable user profile if admnistrator', async() => { 
            wrapper2.vm.userDetails.admin = true;
            await wrapper2.vm.$nextTick();
            expect(wrapper2.get('#user-form--userprofiles').find("input").attributes().disabled).to.equal('disabled');
            expect(wrapper2.get('#user-form--hide-menu').find("input").attributes().disabled).to.equal('disabled');
        })

        it('User creation - filled - passing', async() => {
            wrapper2.get('#user-form--username').setValue("mockedusertest");
            wrapper2.get('#user-form--password-repeat').setValue("pa$$word");
            wrapper2.get('#user-form--email').setValue("mocked@mail.com");
            await wrapper2.vm.$nextTick();

            expect(wrapper2.vm.isFormValid()).to.equal(true);
        });

    })
	
});
