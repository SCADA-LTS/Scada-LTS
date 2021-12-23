import { expect } from 'chai';

//TODO: Change the loggin to application via REST API
// TUTORIAL: https://docs.cypress.io/guides/references/best-practices.html#Organizing-Tests-Logging-In-Controlling-State

before(() => {
	cy.restLogin();
	cy.fixture('DataPointDetails/BasicConfiguration').then((configuration) => {
		cy.loadConfiguration(configuration);
		cy.visit('/app.shtm#/watch-list');
	});
});

after(() => {
	cy.restLogin();
	cy.visit('data_sources.shtm');
	cy.get('img[src="images/icon_ds_delete.png"]').click();
});

context('Scenario - Watch List page validation', () => {
    describe('Blank page', () => {
        it('Should have a titile', () => {
            cy.get('h1').should('contain', 'Watch List');
        });

        it('Is toolbar visible', () => {
            cy.get('.slts--toolbar').should('be.visible');
        })

        it('Is toolbar containing 1 button', () => {
            cy.get('.slts--toolbar > :nth-child(1) > span > button').should('have.length', 1);
            cy.get('.slts--toolbar > :nth-child(1) > span > button i.mdi-plus').should('be.visible');
        })

        it('Is selectbox visible', () => {
            cy.get('.slts--selectbox').should('be.visible');
            cy.get('.slts--selectbox label').should('contain', 'Active WatchList');
        })
    });

    describe('Select first watchList', () => {
        it('Is watchlist selected', () => {
            cy.get('.slts--selectbox > .v-select').click().then(() => {
                cy.get('.menuable__content__active > .v-list').children().should('have.length', 1);
                cy.get('.menuable__content__active > .v-list').children().eq(0).click().then(() => {
                    cy.get('.slts--selectbox > .v-select').should('contain', '(unnamed)');
                });
            })
        });
        it('Is toolbar containing 3 buttons', () => {
            cy.get('.slts--toolbar button').should('have.length', 3);
            cy.get('.slts--toolbar > :nth-child(1) > span > button i.mdi-plus').should('be.visible');
            cy.get('.slts--toolbar > :nth-child(2) > span > button i.mdi-pencil').should('be.visible');
            cy.get('.slts--toolbar > :nth-child(3) i.mdi-minus-circle').should('be.visible');
        })
        it('Is Point Value Dashboard visible', () => {
            cy.get('.watchlist-component').eq(0).should('be.visible');
        });
        it('Is Data Point chart visible', () => {
            cy.get('.watchlist-component').eq(1).should('be.visible');
        });

    })

    describe('Create watchList', () => {
        it('Open Creation dialog', () => {
            cy.get('.slts--toolbar button').eq(0).click().then(() => {
                cy.get('.v-dialog--active .v-card__title').should('contain', 'Watch List configuration');
                cy.get('.v-dialog--active .v-card__actions button').eq(1).should('have.attr', 'disabled');
            })
        });

        it('Are name and Export ID fields visible', () => {
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Watch List name');
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(2) label').should('contain', 'Export ID');
        });

        it('Provide a name and export ID', () => {
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(1) input').clear().type("Text WL01");
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(2) input').clear().type("WL01_XID");
            cy.get('.v-dialog--active .v-card__actions button').eq(1).should('not.be.disabled');
        });

        it('Is search DataPoint button working', () => {
            cy.get('.v-dialog--active .watchlist-section--points .v-autocomplete input[type="text"]').clear().type("Test Binary");
            cy.get('.menuable__content__active .v-list').children().should('have.length', 1);
            cy.get('.menuable__content__active .v-list').children().eq(0).click().then(() => {
                cy.get('.v-dialog--active .watchlist-section--points .v-autocomplete input[type="text"]').should('have.value', 'Test Binary DP 01');
                cy.get('.v-input__append-outer > .v-icon').click().then(() => {
                    cy.get('.v-dialog--active .watchlist-section--points .v-autocomplete input[type="text"]').should('have.value', '');
                });
            })
            
        });

        it('Create Watch List', () => {
            cy.get('.v-dialog--active .v-card__actions button').eq(1).click().then(() => {
                cy.get('.slts--selectbox > .v-select').should('contain', 'Text WL01');
                cy.get('.datapointList .list-item--content .list-item-content--name').should('contain', 'Test Binary DP 01');
            })
        })
    })

    describe('Edit WatchList', () => {
        it('Click on edit button', () => {
            cy.get('.slts--toolbar button').eq(1).click();
        })

        it('Is edit dialog visible', () => {
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Watch List configuration');
            cy.get('.v-dialog--active .v-card__actions button').eq(1).should('not.have.attr', 'disabled');
            cy.get('.v-dialog--active .v-card__actions button').eq(1).should('contain', 'Update');
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(2) input').should('have.attr', 'disabled');
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(2) input').should('have.value', 'WL01_XID');
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(1) input').should('have.value', 'Text WL01');
        })

        it('Change name', () => {
            cy.get('.v-dialog--active .v-card__text > form > :nth-child(1) > .row > :nth-child(1) input').clear().type('Cypress W1');
        })

        it('Add DataPoint', () => {
            cy.get('.v-dialog--active .watchlist-section--points .v-autocomplete input[type="text"]').clear().type("Test Numeric");
            cy.get('.menuable__content__active .v-list').children().should('have.length', 1);
            cy.get('.menuable__content__active .v-list').children().eq(0).click().then(() => {
                cy.get('.v-dialog--active .watchlist-section--points .v-autocomplete input[type="text"]').should('have.value', 'Test Numeric DP 01');
                cy.get('.v-input--is-focused > .v-input__append-outer > .v-icon').click().then(() => {
                    cy.get('.v-dialog--active .watchlist-section--points .v-autocomplete input[type="text"]').should('have.value', '');
                });
            })
        })

        it('Update Watch List', () => {
            cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__actions > .primary--text').click().then(() => {
                
            })
        })
    })

    describe('Delete watchList', () => {
        it('Click on delete button', () => {
            cy.get('.slts--toolbar button').eq(2).click();
        })

        it('Is confirmation dialog visible', () => {
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Delete Watch List');
        })

        it('Delete watchList', () => {
            cy.get('.v-dialog--active .v-card__actions button').eq(1).click().then(() => {
                cy.get('.slts--toolbar > :nth-child(1) > span > button').should('have.length', 1);
                cy.get('.slts--toolbar > :nth-child(1) > span > button i.mdi-plus').should('be.visible');
                cy.get('.watchlist-component').should('not.be.visible');
                cy.get('.slts--selectbox > .v-select').click().then(() => {
                    cy.get('.menuable__content__active > .v-list').children().should('have.length', 1);
                })
            })
        })
    })


})