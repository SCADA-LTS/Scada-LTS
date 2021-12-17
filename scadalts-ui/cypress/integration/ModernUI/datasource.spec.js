// before(() => {
// 	cy.restLogin();
// 	cy.visit('app.shtm#/datasources');
// });

context('DataSources page', () => {

    describe('Page content test', () => {
        before(() => {
            cy.restLogin();
            
            cy.visit('app.shtm#/datasources');
            cy.get('.v-main__wrap').get('button.v-btn--fab i.mdi-plus').click();
        })

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

    describe('Editing DataSource test', () => {

        it('Expand DataSource details', () => {
            cy.get('button.mdi-chevron-down').click();
            cy.get('.data-source-item--details').should('be.visible');
            cy.get('.data-source-item--details .mdi-alert-decagram').should('be.visible');
            cy.get('.data-source-item--details .mdi-pencil').should('be.visible');
            cy.get('.data-source-item--details .mdi-delete').should('be.visible');
            cy.get('.data-source-item--datapoint-list').should('be.visible');
            cy.get('.data-source-item--datapoint-list').should('contain', 'This Data Source does not contain any DataPoints. Create the first one!');
        });

        it('Is edit dialog visible', () => {
            cy.get('.data-source-item--details .mdi-pencil').click();
            cy.get('.v-dialog--active').get('h1').should('be.visible');
            cy.get('.v-dialog--active').get('h1').should('contain', 'Update');
            cy.get('.v-dialog--active').get('.v-card__title > .row > .col-4').should('contain', '');
            cy.get('.v-dialog--active #datasource-config--accept').should('contain', 'Update');    
        });

        it('Is form filled with valid data', () => {
            cy.get('.v-dialog--active #datasource-config--name  input').should('have.value', 'Test DataSource');
            cy.get('.v-dialog--active #datasource-config--xid  input').should('have.value', 'XID_E22_01');
            cy.get('.v-dialog--active #datasource-config--xid  input').should('have.value', 'XID_E22_01');
            cy.get('.v-dialog--active #datasource-config--update-periods  input').should('have.value', '5');
            cy.get('.v-dialog--active #datasource-config--period-type .v-select__selection').should('contain', 'Second(s)');
        });

        it('Is "Update" button enabled', () => {
            cy.get('.v-dialog--active #datasource-config--accept').should('be.not.disabled');
        });

        it('Remove DataSource name, Update disabled', () => {
            cy.get('.v-dialog--active #datasource-config--name  input').clear();
            cy.get('.v-dialog--active #datasource-config--accept').should('be.disabled');
        });

        it('Fill DataSource name, Update enabled', () => {
            cy.get('.v-dialog--active #datasource-config--name  input').type('New DataSource TestName');
            cy.get('.v-dialog--active #datasource-config--accept').should('be.not.disabled');
        });

        it('Change the update period to 10 seconds', () => {
            cy.get('.v-dialog--active #datasource-config--update-periods  input').clear().type(10);
            cy.get('.v-dialog--active #datasource-config--update-periods  input').should('have.value', '10');
        })

        it('Save changes', () => {
            cy.get('.v-dialog--active #datasource-config--accept').click().then(() => {
                cy.get('tbody > tr > :nth-child(2)').should('contain', 'New DataSource TestName');
            });
        })
    })

    describe('Deleting DataSource test', () => {
        it('Expand DataSource details', () => {
            cy.get('button.mdi-chevron-down').click();
            cy.get('.data-source-item--details').should('be.visible');
            cy.get('.data-source-item--details .mdi-delete').should('be.visible');
        });

        it('Is delete confirm dialog visible', () => {
            cy.get('.data-source-item--details .mdi-delete').click();
            cy.get('.v-dialog--active').get('h1').should('be.visible');
        });

        it('Delte DataSource', () => {
            cy.get('.success--text > .v-btn__content').click();
        })
    })
})