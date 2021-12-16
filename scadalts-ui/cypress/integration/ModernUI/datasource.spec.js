before(() => {
	cy.restLogin();
	cy.visit('app.shtm#/datasources');
});

context('DataSources page', () => {
    describe('Page content test', () => {
        it('Is header visible', () => {
            cy.get('.v-main__wrap').get('header').should('be.visible');
        });
        it('Is title visible', () => {
            cy.get('.v-main__wrap').get('h1').should('be.visible')
            cy.get('.v-main__wrap').get('h1').should('contain', 'Data Sources');
        });
        it('Is add button visible', () => {
            cy.get('.v-main__wrap').get('button.v-btn--fab').should('be.visible');
            cy.get('.v-main__wrap').get('button.v-btn--fab i.mdi-plus').should('be.visible');
        });
        it('Is DataSource table visible', () => {
            cy.get('.v-main__wrap').get('div.v-data-table').should('be.visible');
        });
        it('Are all DataSource table headers rendered', () => {
            const headers = cy.get('.v-main__wrap').get('table > thead > tr');
            console.log(headers);
            headers.children().should('have.length', 5);
        });
    });

    describe('Adding DataSource tests', () => {

        before(() => {
            cy.restLogin();
            cy.get('.v-main__wrap').get('button.v-btn--fab i.mdi-plus').click();
        })

        it('Is dialog title visible', () => {
            cy.get('.v-dialog--active').get('h1').should('be.visible')
            cy.get('.v-dialog--active h1').should('contain', 'Create');
        });

        it('Is DataSource type select visible', () => {
            cy.get('.v-dialog--active').get('.v-card__title div.v-select').should('be.visible');
            cy.get('.v-dialog--active').get('.v-card__title div.v-select div.v-select__selection').should('contain', 'Virtual Data Source');
        });

        it('Is "Cancel" button visible', () => {
            cy.get('.v-dialog--active').get('.v-card__actions').get('button').should('be.visible');
        });

        it('Is "Create" button disabled', () => {
            cy.get('#datasource-config--accept').should('be.disabled');
            cy.get('#datasource-config--accept').should('contain', 'Create');    
        });

        it('Fill Virtual Data Source form', () => {
            cy.get('#datasource-config--name  input').type('Test DataSource');
            cy.get('#datasource-config--xid  input').clear().type('XID_E22_01');
        });

        it('Is "Create" button enabled', () => {
            cy.get('#datasource-config--accept').should('be.not.disabled');
            cy.get('#datasource-config--accept').should('contain', 'Create');    
        });

        it('Is Data Source created', () => {
            cy.get('#datasource-config--accept').click();
        });

    })
})