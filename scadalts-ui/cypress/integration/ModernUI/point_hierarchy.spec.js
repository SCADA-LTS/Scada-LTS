import { expect } from 'chai';

//TODO: Change the loggin to application via REST API
// TUTORIAL: https://docs.cypress.io/guides/references/best-practices.html#Organizing-Tests-Logging-In-Controlling-State

before(() => {
	cy.restLogin();
	cy.fixture('DataPointDetails/BasicConfiguration').then((configuration) => {
		cy.loadConfiguration(configuration);
		cy.visit('/app.shtm#/point-hierarchy');
	});
});

after(() => {
	cy.restLogin();
	cy.visit('data_sources.shtm');
	cy.get('img[src="images/icon_ds_delete.png"]').click();
});

context('Scenario - Data Point Hierarchy validation', () => {
    describe('Layout', () => {
        it('Should have a titile', () => {
            cy.get('h1').should('contain', 'Point Hierarchy');
        });

        it('Is toolbar visible', () => {
            cy.get('.slts--toolbar').should('be.visible');
        })

        it('Is toolbar containing 3 buttons', () => {
            cy.get('.slts--toolbar > :nth-child(1) > .row').children().should('have.length', 6);
            cy.get('.slts--toolbar > :nth-child(1) > .row > button').should('have.length', 3);
        })

        it('Are all 4 points visible', () => {
            let points = 4;
            let dialogs = 3;
            cy.get('#f0').children().should('have.length', points + dialogs);
        })
    });

    describe('Functionality', () => {
        it('Is create folder dialog visible', () => {
            cy.get('.slts--toolbar > :nth-child(1) > .row > button:nth-of-type(1)').click().then(() => {
                cy.get('.v-dialog--active').should('be.visible');
                cy.get('.v-dialog--active .v-card__title').should('contain', 'Create a new folder');
                cy.get('.v-dialog--active .v-card__text label').should('contain', 'Name');
            })
        })

        it('Type Folder name and create', () => {
            cy.get('.v-dialog--active .v-card__text input').clear().type('Folder 1');
            cy.get('.v-dialog--active .v-card__actions button.success--text').click().then(() => {
                cy.get('#f0').children().should('have.length', 8);
                cy.get('#f0 .slts-list-item--action-buttons').children().should('have.length', 3);
            });
        })

        it('Rename folder', () => {
            cy.get('#f0 .slts-list-item--action-buttons > :nth-child(1)').click().then(() => {
                cy.get('.v-dialog--active').should('be.visible');
                cy.get('.v-dialog--active .v-card__title').should('contain', 'Change folder name');
                cy.get('.v-dialog--active .v-card__text input').clear().type('Renamed One');
                cy.get('.v-dialog--active .v-card__actions button.success--text').click().then(() => {
                    cy.get('#f0').children().should('have.length', 8);
                    cy.get('.slts-list-item--title').should('contain', 'Renamed One');
                });
            })
        })

        it('Detele this folder', () => {
            cy.get('#f0 .slts-list-item--action-buttons > :nth-child(3)').click().then(() => {
                cy.get('.dialog-confirmation > .v-card__actions > .success--text').click().then(() => {
                    cy.get('#f0').children().should('have.length', 7);
                })
            })
        })
    });

    describe('Import/Export point hierarchy component', () => {
        it('Is create folder dialog visible', () => {
            cy.get('.slts--toolbar > :nth-child(1) > .row > button:nth-of-type(2)').click().then(() => {
                cy.get('.v-dialog--active').should('be.visible');
                cy.get('.v-dialog--active .v-card__title').should('contain', 'Import/Export Point Hierarchy structure');
                cy.get('.v-dialog--active .v-card__text textarea').should('have.value', '{}');
            })
        })

        it('Is Export button create JSON config', () => {
            cy.get('.v-dialog--active .v-card__text button.btn-primary').click();
        })

        it('Close dialog', () => {
            cy.get('.v-dialog--active .v-card__actions button.success--text').click();
        })
    })

    describe('Show/Hide DataSource name', () => {
        it('Enable DataSource name', () => {
            cy.get('.slts--toolbar > :nth-child(1) > .row > button:nth-of-type(3)').click().then(() => {
                cy.get('.slts-list-item--title').should('contain', 'DataPointTestDS');
            })
        })

        it('Hide DataSource name', () => {
            cy.get('.slts--toolbar > :nth-child(1) > .row > button:nth-of-type(3)').click().then(() => {
                cy.get('.slts-list-item--title').should('not.contain', 'DataPointTestDS');
            })
        })
    })
});
