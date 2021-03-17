before(() => {
    cy.restLogin();
    cy.fixture('RecipientList/BasicConfiguration').then(configuration => {
        cy.loadConfiguration(configuration);
        cy.visit('/app.shtm#/recipient-list')
    })
})

after(() => {
    cy.restLogin();
    cy.visit('mailing_lists.shtm');
    for(let i = 0; i < 6; i++) {
        cy.get('#mailingListsTable > tbody:nth-of-type(2)').click();
        cy.get('#mailingListDetails #deleteMailingListImg').click();
        cy.wait(500);
    };
    cy.visit('users.shtm');
    for(let i = 0; i < 5; i++) {
        cy.get('#usersTable > tbody:nth-of-type(3)').click();
        cy.get('#userDetails #deleteImg').click();
        cy.wait(500);
    };
})


context("Scenario - Recipient List validation", () => {
    
    describe("Test - Validate visible elements", () => {

        it('Is Header displayed properly', () => {
            cy.get('h1').contains('Recipient List')
            cy.get('#recipientListSection').find('div.v-list-item').should('have.length', 6);
            cy.get('#recipientListSection').find('div.v-list-item').first().contains('01_Admin_ML');
            cy.get('#recipientListCreation').find('div.v-list-item').should('have.length', 1);
            cy.get('#recipientListDetails').find('h3').contains('Select a recipient list to see details');
        });

        it('Is Recipient List details rendered properly', () => {
            cy.restLogin();
            cy.get('#recipientListSection').find('div.v-list-item').first().trigger('click');
            cy.get('#recipientListDetails #rl-section-details').find('h2').contains('Recipient list details');
            cy.get('#rl-section-details > .row > .col:nth-of-type(2) .v-input label').contains('Name');
            cy.get('#rl-section-details > .row > .col:nth-of-type(2) .v-input input').should('have.value', '01_Admin_ML')
            cy.get('#rl-section-details > .row > .col:nth-of-type(3) .v-input label').contains('Export ID');
            cy.get('#rl-section-details > .row > .col:nth-of-type(3) .v-input input').should('have.value', 'ML_TEST_01')
            cy.get('#rl-section-details > .row > .col:nth-of-type(5) .v-input input').should('be.disabled');
        });

        it('Is Recipients in Recipients Details rendered properly', () => {
            cy.restLogin();
            cy.get('#rl-section-recipients').find('div.v-list-item').should('have.length', 2);
            cy.get('#rl-section-recipients').find('div.v-list-item').first().contains('admin');        
            cy.get('#rl-section-recipients').find('div.v-list-item').first().contains('admin@yourMangoDomain.com');
            cy.get('#rl-section-recipients').find('div.v-list-item .mdi-account').should('have.length', 2);
        });

        it('Is Recipient List details changed properly', () => {
            cy.restLogin();
            cy.get('#recipientListSection').find('div.v-list-item').eq(1).trigger('click');
            cy.get('#rl-section-details > .row > .col:nth-of-type(2) .v-input label').contains('Name');
            cy.get('#rl-section-details > .row > .col:nth-of-type(2) .v-input input').should('have.value', '02_User_ML');
        });

        it('Is Plain Mail Recipient rendered properly', () => {
            cy.restLogin();
            cy.get('#recipientListSection').find('div.v-list-item').eq(2).trigger('click');
            cy.get('#rl-section-details > .row > .col:nth-of-type(2) .v-input input').should('have.value', '03_Plain_mail_ML');
            cy.get('#rl-section-recipients').find('div.v-list-item .mdi-email').should('have.length', 1);
        });

        it('Is Add User Entry Dialog rendered properly', ()=> {
            cy.get('#rl-section-details .row .heading-action-buttons').find('h3').contains('Entries');
            cy.get('#rl-section-details .row .heading-action-buttons').find('.mdi-account-plus').trigger('click');
            cy.get('.v-dialog--active').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active').find('.v-card__text .v-select').should('be.visible');
            cy.get('.v-dialog--active').find('.v-card__actions button').last().should('be.disabled');
            cy.get('.v-dialog--active').find('.v-card__text .v-select').trigger('click');
            cy.get('.v-application > .menuable__content__active > div[role="listbox"]').find('div.v-list-item').should('have.length', 6);
            cy.get('.v-dialog--active').find('.v-card__actions button').first().trigger('click', {force: true});
        });

        it('Is Add Plain Mail Entry Dialog rendered properly', ()=> {
            cy.get('#rl-section-details .row .heading-action-buttons').find('.mdi-email-plus').trigger('click');
            cy.get('.v-dialog--active').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active').find('.v-card__text .v-input').should('be.visible');
            cy.get('.v-dialog--active').find('.v-card__text .v-input label').contains('Add e-mail address');
            cy.get('.v-dialog--active').find('.v-card__actions button').last().should('be.disabled');
            cy.get('.v-dialog--active').find('.v-card__actions button').first().trigger('click');
        });

        it('Is Add Plain SMS Entry Dialog rendered properly', ()=> {
            cy.get('#rl-section-details .row .heading-action-buttons').find('.mdi-phone-plus').trigger('click');
            cy.get('.v-dialog--active').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active').find('.v-card__text .v-input').should('be.visible');
            cy.get('.v-dialog--active').find('.v-card__text .v-input label').contains('Add phone address');
            cy.get('.v-dialog--active').find('.v-card__actions button').last().should('be.disabled');
            cy.get('.v-dialog--active').find('.v-card__actions button').first().trigger('click');
        });

    })

    describe("Test - Create Recipient List", () => {
        
        it('Is Creation Dialog Visible', () => {
            cy.restLogin();
            cy.get('#recipientListCreation').find('button.v-btn--fab').trigger('click');
            cy.get('.v-dialog--active').find('.v-card__title').contains('Create Recipient List');
            cy.get('.v-dialog--active #rl-section-details > .row > .col:nth-of-type(2) .v-input label').contains('Name');
            cy.get('.v-dialog--active #rl-section-details > .row > .col:nth-of-type(2) .v-input input').should('have.value', '');
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').should('not.exist');
        })

        it('Add User Recipient', () => {
            cy.get('.v-dialog--active #rl-section-details .row .heading-action-buttons').find('.mdi-account-plus').trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__text .v-select').trigger('click');
            cy.get('.v-application > .menuable__content__active > div[role="listbox"]').find('div.v-list-item').eq(0).trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().should('not.be.disabled');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().trigger('click');
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').should('have.length', 1);
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').first().contains('admin');        
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').first().contains('admin@yourMangoDomain.com');
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item .mdi-account').should('have.length', 1);
        })

        it('Add Not valid Plain Mail Recipient', () => {
            cy.get('.v-dialog--active #rl-section-details .row .heading-action-buttons').find('.mdi-email-plus').trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__text .v-input input').last().type('notvalidmail');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().should('be.disabled');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').eq(2).trigger('click');
        })

        it('Add Not valid Plain Mail Recipient', () => {
            cy.get('.v-dialog--active #rl-section-details .row .heading-action-buttons').find('.mdi-email-plus').trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__text .v-input input').last().type('valid@mail.com');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().should('not.be.disabled');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().trigger('click');
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').should('have.length', 2);
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').eq(1).contains('valid@mail.com');
        })

        it('Add Not valid Plain SMS Recipient', () => {
            cy.get('.v-dialog--active #rl-section-details .row .heading-action-buttons').find('.mdi-phone-plus').trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__text .v-input input').last().type('notValidPhone0123');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().should('be.disabled');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').eq(2).trigger('click');
        })

        it('Add Not valid Plain SMS Recipient', () => {
            cy.get('.v-dialog--active #rl-section-details .row .heading-action-buttons').find('.mdi-phone-plus').trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__title').contains('Add recipient');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__text .v-input input').last().type('123456123');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().should('not.be.disabled');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().trigger('click');
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').should('have.length', 3);
            cy.get('.v-dialog--active #rl-section-recipients').find('div.v-list-item').eq(2).contains('123456123');
        })

        it('Add Recipient List properties', () => {
            cy.get('.v-dialog--active #rl-section-details > .row > .col:nth-of-type(2) .v-input input').type('07_Test_ML');
            cy.get('.v-dialog--active #rl-section-details > .row > .col:nth-of-type(4) .v-input').trigger('click');
            cy.get('.v-dialog--active #rl-section-details > .row > .col:nth-of-type(5) .v-input input').clear().type('2 */15 */10 * * ?');
        })

        it('Save Recipient List', () => {
            cy.restLogin();
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().trigger('click');
            cy.get('.v-snack__wrapper .v-snack__content').contains('Added successful!');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            cy.get('#recipientListSection').find('div.v-list-item').should('have.length', 7);
            cy.get('#recipientListSection').find('div.v-list-item').last().contains('07_Test_ML');
        })
    })

    describe("Test - Delete Recipient List", () => {

        before(() => {
            cy.restLogin();
            cy.reload()
        });

        it('Is Deletion confirmation dialog visible', ()=> {
            cy.get('#recipientListSection').find('div.v-list-item .mdi-minus-circle').last().trigger('click');
            cy.get('.v-dialog--active:last-of-type').find('.v-card__title').contains('Delete');
        });

        it('Is Recipient List Deleted', () => {
            cy.restLogin();
            cy.get('.v-dialog--active:last-of-type').find('.v-card__actions button').last().trigger('click');
            cy.get('.v-snack__wrapper .v-snack__content').contains('Deleted successful!');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            cy.get('#recipientListSection').find('div.v-list-item').should('have.length', 6);
        });
    })
})