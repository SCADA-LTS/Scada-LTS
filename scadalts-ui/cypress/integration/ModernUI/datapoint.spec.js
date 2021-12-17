context('DataSources page', () => {

    describe('Page content test', () => {
        before(() => {
            cy.restLogin();
            createDataSource('VirtualDS', "XID_Cypress_01");
            cy.get('button.mdi-chevron-down').click();
        });

        after(() => {
            deleteDataSource();
        })

        it('Is "Add data point" button visible', () => {
            cy.get('.data-source-item--datapoint-list .v-btn--fab .mdi-plus').should('be.visible');
        });

        it('Is Point Creation dialog visible', () => {
            cy.get('.data-source-item--datapoint-list .v-btn--fab .mdi-plus').click();
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Create');
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Virtual Data Point');
            cy.get('.v-dialog--active .v-card__actions > :nth-child(2)').should('contain', 'Cancel');
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.disabled');
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('contain', 'Create');
        });

        it('Is numeric Data Point by default', () => {
            cy.get('.v-dialog--active .v-card__title div.v-select div.v-select__selection').should('contain', 'Numeric');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) label').should('contain', 'Data Point Name');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(2) label').should('contain', 'Data Point Export ID');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(4) label').should('contain', 'Description');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) label').should('contain', 'Change Type');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) div.v-select div.v-select__selection').should('contain', 'Random');
        });

        it('Fill form fields', () => {
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').type('DP Numeric 01')
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(2) input').clear().type("DPT_CypressNum_01")
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(4) input').clear().type("Long Data Point name description for user");
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.not.disabled');
        });

        it('Create DataPoint', () => {
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').click();
            cy.get('.datapoint-item').should('contain', 'DP Numeric 01');
            cy.get('.datapoint-item').should('contain', 'Numeric');
            cy.get('.datapoint-item').should('contain', 'Random');
        });

    });
})

function createDataSource(name, xid) {
    cy.visit('app.shtm#/datasources');
    cy.get('.v-main__wrap').get('button.v-btn--fab i.mdi-plus').click();
    cy.get('.v-dialog--active #datasource-config--name  input').clear().type(name);
    cy.get('.v-dialog--active #datasource-config--xid  input').clear().type(xid);
    cy.get('.v-dialog--active #datasource-config--accept').click();
}

function deleteDataSource() {
    // cy.get('button.mdi-chevron-down').click();
    cy.get('.data-source-item--details .mdi-delete').click();
    cy.get('.success--text > .v-btn__content').click();
}