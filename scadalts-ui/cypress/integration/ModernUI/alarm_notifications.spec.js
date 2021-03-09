before(() => {
    cy.restLogin();
    cy.fixture('AlarmNotifications/BasicConfiguration').then(configuration => {
        cy.loadConfiguration(configuration);
        cy.visit('app.shtm#/alarm-notifications');
    });
});

after(() => {
    cy.restLogin();
    cy.visit('data_sources.shtm');
    cy.get('img[src="images/icon_ds_delete.png"]').click({ multiple: true });
    cy.visit('mailing_lists.shtm');
    for(let i = 0; i < 6; i++) {
        cy.get('#mailingListsTable > tbody:nth-of-type(2)').click();
        cy.get('#mailingListDetails #deleteMailingListImg').click();
    };
    cy.visit('users.shtm');
    for(let i = 0; i < 4; i++) {
        cy.get('#usersTable > tbody:nth-of-type(3)').click();
        cy.get('#userDetails #deleteImg').click();
        cy.wait(500);
    };
    
})

context("Scenario - PLC Alarm Page validation", () => {
    
    describe("Test - Validate visible elements", () => {

        it('Is Header displayed properly', () => {
            cy.get('h1').contains('PLC Alarms Notification List');
            cy.get('.v-main__wrap > .container .container > .row .col:nth-of-type(2) .mdi-pencil').should('be.visible')
            cy.get('.v-main__wrap > .container .container > .row .col:nth-of-type(3) .v-select').should('be.visible')
            cy.get('.v-main__wrap > .container .container > .row .col:nth-of-type(3) .v-select .v-select__slot > label').contains('Select mailing list')
            cy.get('.v-main__wrap > .container .container > .row .col:nth-of-type(4) .v-select').should('be.visible')            
        })

        it('Is Mailing List select list rendered properly', () => {
            cy.get('.v-main__wrap > .container .container > .row .col:nth-of-type(3) .v-select').trigger('click');
            cy.get('div.v-menu__content > div[role="listbox"]').find('div.v-list-item').should('have.length', 6);
            cy.get('h1').trigger('click');
        })

        it('Is Alarm Data Source List rendered properly', () => {
            cy.restLogin();
            cy.get('.v-main__wrap > .container .container > .row .col:nth-of-type(3) .v-select').trigger('click');
            cy.get('div.v-menu__content > div[role="listbox"] div.v-list-item:first-of-type').trigger('click');
            cy.get('.v-treeview > .v-treeview-node:nth-of-type(1) .v-treeview-node__content > .v-treeview-node__label').contains('VDS_TEST_01');
            cy.get('.v-treeview > .v-treeview-node:nth-of-type(2) .v-treeview-node__content > .v-treeview-node__label').contains('VDS_TEST_02');
        })

        it('Is Alarm Data Point List rendered properly', () => {
            cy.restLogin();
            cy.get('.v-treeview > .v-treeview-node:nth-of-type(1) button').trigger('click');
            cy.get('.v-treeview > .v-treeview-node:nth-of-type(1) > .v-treeview-node__children').find('div.v-treeview-node').should('have.length', 8);
            cy.get('.v-treeview > .v-treeview-node:nth-of-type(2) button').trigger('click');
            cy.get('.v-treeview > .v-treeview-node:nth-of-type(2) > .v-treeview-node__children').find('div.v-treeview-node').should('have.length', 1);

        })
    })
})