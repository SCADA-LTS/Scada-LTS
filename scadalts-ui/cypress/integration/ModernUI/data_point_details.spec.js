import { expect } from "chai";

//TODO: Change the loggin to application via REST API
// TUTORIAL: https://docs.cypress.io/guides/references/best-practices.html#Organizing-Tests-Logging-In-Controlling-State

before(() => {
    cy.restLogin();
    cy.fixture('DataPointDetails/BasicConfiguration').then(configuration => {
        cy.loadConfiguration(configuration);
        cy.visit('/app.shtm#/datapoint-list');
        cy.get('.v-data-table__wrapper > table > tbody > tr:first-of-type').click();
    });
})

after(() => {
    cy.restLogin();
    cy.visit('data_sources.shtm');
    cy.get('img[src="images/icon_ds_delete.png"]').click();
})

context("Scenario - Data Point List validation", () => {

    function getTableRow(row, column) {
        return `tbody > tr:nth-of-type(${row}) > td:nth-of-type(${column})`
    }

    before(() => {
        cy.visit('/app.shtm#/datapoint-list');
    })

    describe("Test - Validate Data Point List table", () => {

        it('Is Table headers displayed properly', () => {
            cy.get('thead th:nth-of-type(1) > span').contains('Status');
            cy.get('thead th:nth-of-type(2) > span').contains('Data Source');
            cy.get('thead th:nth-of-type(3) > span').contains('Name');
            cy.get('thead th:nth-of-type(4) > span').contains('Description');
            cy.get('thead th:nth-of-type(5) > span').contains('Point Type');
            cy.get('thead th:nth-of-type(6) > span').contains('XID');
        });

        it('Is Binary Data Point rendered propery', () => {
            cy.get(getTableRow(1,3)).contains('Test Binary DP 01');
            cy.get(getTableRow(1,5)).contains('Binary');
            cy.get(getTableRow(1,6)).contains('DP_TEST_0001');
        })

        it('Is Multistate Data Point rendered propery', () => {
            cy.get(getTableRow(2,3)).contains('Test Multistate DP 01');
            cy.get(getTableRow(2,5)).contains('Multistate');
            cy.get(getTableRow(2,6)).contains('DP_TEST_0002');
        })

        it('Is Numeric Data Point rendered propery', () => {
            cy.get(getTableRow(3,3)).contains('Test Numeric DP 01');
            cy.get(getTableRow(3,5)).contains('Numeric');
            cy.get(getTableRow(3,6)).contains('DP_TEST_0003');
        })

        it('Is Alphanumeric Data Point rendered propery', () => {
            cy.get(getTableRow(4,3)).contains('Test Alphanumeric DP 01');
            cy.get(getTableRow(4,5)).contains('Alphanumeric');
            cy.get(getTableRow(4,6)).contains('DP_TEST_0004');
        })

        it('Search for component', () => {
            cy.get('.v-text-field--single-line input').type('Bin');
            cy.get('tbody > tr').should('have.length', 1);
            cy.get('.v-text-field--single-line input').clear();
            cy.get('tbody > tr').should('have.length', 4);
        })

        it('Search for not existing component', () => {
            cy.get('.v-text-field--single-line input').type('Maslo');
            cy.get('tbody > tr').should('have.length', 1);
            cy.get('tbody > tr').contains('No matching records found');
            cy.get('.v-text-field--single-line input').clear();
        })

    })

    describe("Test - Open Data Point Details from main view", () => {

        beforeEach(() => {
            cy.restLogin();
            cy.visit('/app.shtm#/datapoint-list');
        })

        it('Open Binary Data Point Details', () => {
            cy.get(getTableRow(1,1)).click();
            cy.location().should((url) => {
                expect(url.toString()).to.contain('datapoint-details');
            });
            cy.get('h1').should('contain', 'Test Binary DP 01').end();
        });

        it('Open Multistate Data Point Details', () => {
            cy.get(getTableRow(2,1)).click();
            cy.location().should((url) => {
                expect(url.toString()).to.contain('datapoint-details');
            });
            cy.get('h1').should('contain', 'Test Multistate DP 01').end();
        })

        it('Open Numeric Data Point Details', () => {
            cy.get(getTableRow(3,1)).click();
            cy.location().should((url) => {
                expect(url.toString()).to.contain('datapoint-details');
            });
            cy.get('h1').should('contain', 'Test Numeric DP 01').end();
        })

        it('Open Alphanumeric Data Point Details', () => {
            cy.get(getTableRow(4,1)).click();
            cy.location().should((url) => {
                expect(url.toString()).to.contain('datapoint-details');
            });
            cy.get('h1').should('contain', 'Test Alphanumeric DP 01').end();
        })
    })

})

context("Scenario - Data Point Details validation", () => {

    before(() => {
        cy.restLogin();
        cy.visit('/app.shtm#/datapoint-list');
        cy.get('tbody > tr:nth-of-type(1) > td:nth-of-type(1)').click()
    });

    describe('Test - Validate Data Point Details components', () => {

        it('Is header rendered properly', () => {
            cy.get('h1').should('contain', 'Test Binary DP 01');
            cy.get('h1 button.v-btn--fab:first-of-type').should('have.css', 'background-color', 'rgb(69, 142, 35)')
            cy.get('h1 .mdi-decagram').should('be.visible');
            cy.get('h1 i.mdi-message-alert').should('be.visible');
            cy.get('p.small-description').contains('DP_TEST_0001');
        });

        it('Is Data Point Hisotry Values rendered properly', () => {
            cy.get('.pointDetailsCards:nth-of-type(1) .v-card__title').contains("Data Point Values");
            cy.get('.pointDetailsCards:nth-of-type(1) .v-card__text > .row:nth-of-type(1) > .col:nth-of-type(1) label').contains("Set a new value");
            cy.get('.pointDetailsCards:nth-of-type(1) .v-card__text > .row:nth-of-type(1) > .col:nth-of-type(3) label').contains("Show history form");

            cy.get('.pointDetailsCards:nth-of-type(1) .v-card__text > .row:nth-of-type(2) > .col:nth-of-type(1)').contains("Runtime");
        })

        it('Is Data Point Events rendered properly', () => {
            cy.get('.pointDetailsCards:nth-of-type(2) .v-card__title').contains("Events");
            cy.get('.pointDetailsCards:nth-of-type(2) .v-card__text > div[role="list"] .v-list-item__title').contains(" There is no events related to that DataPoint. ");
        })

        it('Is Data Chart rendered properly', () => {
            cy.get('svg').should('exist')
            cy.get('svg desc').contains('JavaScript chart by amCharts')
        })
    })

    describe('Test - Check the Behaviour of the Point Details elements', () => {

        it('Is Data Point Toggle work correctly', () => {
            cy.restLogin();
            cy.get('h1 > .v-btn--is-elevated').click();
            cy.get('.success--text').click();
            cy.get('h1 > .v-btn--is-elevated').should('have.css', 'background-color', 'rgb(255, 82, 82)'); //green
            
            cy.get('h1 > .v-btn--is-elevated').click();
            cy.get('.success--text').click();
            cy.get('h1 > .v-btn--is-elevated').should('have.css', 'background-color', 'rgb(69, 142, 35)'); //green

            cy.get('h1 > .v-btn--is-elevated').click();
            cy.get(':nth-child(2) > .v-btn__content').click();
            cy.get('h1 > .v-btn--is-elevated').should('have.css', 'background-color', 'rgb(69, 142, 35)'); //green
        });

        it('Is Data Point User Comments opening correctly', () => {
            cy.get('.v-badge > .v-btn').click();
            cy.get('.v-menu__content .v-list-item__icon > i').should('be.visible');
            cy.get('.v-main__wrap > :nth-child(1)').click();
            cy.get('.v-menu__content .v-list-item__icon > i').should('not.be.visible');
        });

        it('Is Data Point User Comments adding correctly', () => {
            cy.restLogin();
            cy.get('.v-badge > .v-btn').click();
            cy.get('#menu-data-point-comment .v-list-item__content input').clear().type("Example Comment");
            cy.get('#menu-data-point-comment .v-list-item__content button').trigger("click");

            cy.get('#menu-data-point-comment > .v-list > .v-list-item > .v-list-item__content > .v-list-item__title').contains('Example Comment');
            cy.get('#menu-data-point-comment > .v-list > .v-list-item > .v-list-item__content > .v-list-item__subtitle').contains('admin,');

            cy.get('.v-main__wrap > :nth-child(1)').click();
            cy.get('.v-menu__content .v-list-item__icon > i').should('not.be.visible');
            cy.get('.v-badge__wrapper > span[aria-label="Badge"]').contains('1');
        });

        it('Is Data Point User Comments deleting correctly', () => {
            cy.restLogin();
            cy.get('.v-badge > .v-btn').click();

            cy.get('#menu-data-point-comment  .mdi-minus-circle').trigger('click');

            cy.get('.v-main__wrap > :nth-child(1)').click();
            cy.get('.v-menu__content .v-list-item__icon > i').should('not.be.visible');
            cy.get('.v-badge__wrapper > span[aria-label="Badge"]').should('not.be.visible');
        });

        it('Is Point Properties dialog opening correctly', () => {
            openPointPropertiesDialog();
            cy.get('h3').contains(' Point properties ')
        })
    })
})

context('Scenario - Data Point Properties validation', () => {

    describe('Test - Validate Point Properties components', () => {

        it('Are all components rendered properly', () => {
            cy.get('h3').contains(' Point properties ')
            cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains('Event Detectors')
            cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains('Logging properties')
            cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains('Text renderer properties')
            cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains('Event renderer properties')
            cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains('Chart renderer')
        });

        it('Is Point Properties Dialog rendered properly', () => {
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) h3 i.mdi-decagram').should('be.visible');
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) h3 button.v-btn--fab:first-of-type').should('have.css', 'background-color', 'rgb(69, 142, 35)');
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) .col-12:nth-of-type(2) label').contains('Point name');
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) .col-12:nth-of-type(3) i.mdi-database').should('be.visible');
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) .col-12:nth-of-type(4) label').contains('Description');
        });

        it('Is Logging Properties rendered properly', () => {
            cy.get('#point-prop-logging h3').contains('Logging properties');
            cy.get('#point-prop-logging > .col:nth-of-type(2) > .v-select').should('be.visible');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .v-select').should('not.be.visible');
            cy.get('#point-prop-logging > .col:nth-of-type(4)').contains('Purge after');
            cy.get('#point-prop-logging > .col:nth-of-type(5) > .v-input').should('be.visible');
            cy.get('#point-prop-logging > .col:nth-of-type(7)').contains('Default cache size');
            cy.get('#point-prop-logging > .col:nth-of-type(8) > .v-input').should('exist');

            cy.get('#point-prop-logging > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(4)').trigger('click');

            cy.get('#point-prop-logging > .col:nth-of-type(3) .v-select').should('exist');
            cy.get('#point-prop-logging > .col:nth-of-type(3) > .row > .col-6').contains('Interval logging period every');
        });

        it('Is Text Renderer rendered properly', () => {
            cy.get('#point-prop-text-renderer h3').contains('Text renderer properties');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select').should('be.visible');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Plain');
            cy.get('#point-prop-text-renderer #renderer-plain .v-input').should('be.visible');

            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 2);
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Binary');

            cy.get('#renderer-binary > .col:nth-of-type(2) label').contains("Zero")
            cy.get('#renderer-binary > .col:nth-of-type(4) label').contains("One")
        });

        it('Is Event Renderer rendered properly', () => {
            cy.get('#point-prop-event-renderer h3').contains('Event renderer properties');
            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select').should('be.visible');
            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('None');

            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 2);
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('Binary');

            cy.get('#point-prop-event-renderer > .col:nth-of-type(3) .col:nth-of-type(1) label').contains("Zero")
            cy.get('#point-prop-event-renderer > .col:nth-of-type(3) .col:nth-of-type(2) label').contains("One")
        });

        it('Is Chart Renderer rendered properly', () => {
            cy.get('#point-prop-chart-renderer h3').contains('Chart renderer');
            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select').should('be.visible');
            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('None');

            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 4);
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('Table');

            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(3)').trigger('click');
            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('Image');

            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(4)').trigger('click');
            cy.get('#point-prop-chart-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('Statistics');

        });        
    });

    describe('Test - Validate Event Detectors', () => {

        it('Is Event Detectors Select rendered properly', () => {
            cy.get('#point-prop-event-detecotrs  button').trigger('click');
            cy.get('#dialog-create-event-detector .v-card__title').contains('Create Event Detector');
            cy.get('#dialog-create-event-detector .v-card__text .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 5);
        })

        it('Is Binary Event Detector rendered properly', () => {
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) .v-select').should('exist');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(4) .v-select').should('exist');
        })

        it('Is Point Change Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row').should('not.exist');
        })

        it('Is State Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(3)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text > .row:nth-of-type(2) > .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text > .row:nth-of-type(2) > .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text > .row:nth-of-type(2) > .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('State change count');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(4) .v-select').should('exist');
        })

        it('Is No Change Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(4)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) .v-select').should('exist');
        })

        it('Is No Update Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(5)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) .v-select').should('exist');
        })

        after(() => {
            cy.get('#dialog-create-event-detector .v-card__actions > button:first-of-type').trigger('click');
        })  

    })

    describe('Test - Check the Behaviour of Point Properties elements', () => {

        it('Is Data Point name changing', () => {

            cy.restLogin()
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) .col-12:nth-of-type(2) input').clear().type('Cypress Datapoint');
            cy.get('#dialog-point-properties > .v-card__actions > .primary--text > .v-btn__content').click();
            cy.get('.v-snack__wrapper .v-snack__content').contains(' Updated successful! ');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            cy.get('h1').should('contain', 'Cypress Datapoint');
            openPointPropertiesDialog();

            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) .col-12:nth-of-type(2) input').clear().type('Test Binary DP 01');
            cy.get('#dialog-point-properties > .v-card__actions > .primary--text > .v-btn__content').click();
            cy.get('.v-snack__wrapper .v-snack__content').contains(' Updated successful! ');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            cy.get('h1').should('contain', 'Test Binary DP 01');
            openPointPropertiesDialog();
        })

        it('Is Data Point description changing', () => {
            
            const DESCRIPTION_TEXT = 'Example datapoint long description';
            cy.get('.point-properties-box .col-6:nth-of-type(1) > .row:nth-of-type(1) .col-12:nth-of-type(4) input').clear().type(DESCRIPTION_TEXT);
            cy.get('#dialog-point-properties > .v-card__actions > .primary--text > .v-btn__content').click();
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            cy.get('.small-description').should('contain', DESCRIPTION_TEXT);
            openPointPropertiesDialog();
            cy.get('.v-card__text > :nth-child(1) > :nth-child(1) > :nth-child(1) > :nth-child(4) input').clear();

        })

        it('Is Logging Properties changing', () => {
            
            cy.get('#point-prop-logging > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(4)').last().trigger('click', {force: true});

            cy.get('#point-prop-logging > .col:nth-of-type(3) > .row > .col:nth-of-type(2) input').clear().type('5');
            cy.get('#point-prop-logging > .col:nth-of-type(3) > .row > .col:nth-of-type(3) .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(3)').last().trigger('click', {force: true});

            cy.get('#point-prop-logging > .col:nth-of-type(5) input').clear().type('2');
            cy.get('#point-prop-logging > .col:nth-of-type(6) .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(1)').last().trigger('click', {force: true});

            cy.get('#point-prop-logging > .col:nth-of-type(8) input').clear().type('3');

        });

        it('Is Binary Text Renderer changing', () => {
            
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Binary');
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(1) button').trigger("click");

            cy.get('.v-menu__content > .v-color-picker canvas').should("be.visible");
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(1) button').trigger("click");
            cy.get('.v-menu__content > .v-color-picker canvas').should("not.be.visible");
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(1) button').should('have.css', 'background-color', 'rgb(255, 0, 0)')
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(2) input').clear().type('0-Label')

            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(3) button').trigger("click");
            cy.get('.v-menu__content > .v-color-picker canvas').should("be.visible");
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(3) button').trigger("click");
            cy.get('.v-menu__content > .v-color-picker canvas').should("not.be.visible");
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(3) button').should('have.css', 'background-color', 'rgb(255, 0, 0)')
            cy.get('#point-prop-text-renderer  #renderer-binary > .col:nth-of-type(4) input').clear().type('1-Label')

        })

        it('Is Binary Event Renderer changing', () => {
            
            cy.get('#point-prop-event-renderer > .col:nth-of-type(2) .v-select').trigger("click");
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(2)').last().trigger('click', {force: true});
            cy.get('#point-prop-event-renderer #renderer-binary > .col:nth-of-type(1) input').clear().type('0-Event-Label');
            cy.get('#point-prop-event-renderer #renderer-binary > .col:nth-of-type(2) input').clear().type('1-Event-Label');

        })

        it('Is Chart Renderer changing', () => {
            cy.get('#point-prop-chart-renderer > .col:nth-of-type(2) .v-select').trigger("click");
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(2)').last().trigger('click', {force: true});
            cy.get('#point-prop-chart-renderer > .col:nth-of-type(3) > .row > .col:nth-of-type(1) input').clear().type('5');
        })

        it('Save and validate with classic UI', () => {
            cy.restLogin();
            cy.get('#dialog-point-properties > .v-card__actions > .primary--text > .v-btn__content').click();
            cy.get('.v-snack__wrapper .v-snack__content').contains(' Updated successful! ');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            cy.visit('/data_sources.shtm');
            cy.get('img[src="images/arrow_out.png"]').trigger('click');
            cy.get('[colspan="5"] table > tbody > tr:nth-of-type(3) > td:nth-of-type(5) img').trigger('click');

            //Check the classic UI settings
            cy.get('#intervalLoggingSection > tr > td:nth-of-type(2) input').should('have.value', '5');
            cy.get('input#purgePeriod').should('have.value', '2');
            cy.get('input#defaultCacheSize').should('have.value', '3');
            cy.get('#eventTextRendererBinary input#eventTextRendererBinaryZero').should('have.value', '0-Event-Label');
            cy.get('#eventTextRendererBinary input#eventTextRendererBinaryOne').should('have.value', '1-Event-Label');
            cy.get('#textRendererBinaryZero').should('have.value', '0-Label');
            cy.get('#textRendererBinaryOne').should('have.value', '1-Label');
            cy.get('#chartRendererStatsNumberOfPeriods').should('have.value', '5');

            cy.restLogin();
            cy.visit('/app.shtm#/datapoint-list');
            cy.get('tbody > tr:nth-of-type(1) > td:nth-of-type(1)').click()



            openPointPropertiesDialog();

        })
    })

    describe('Test - Check the Behaviour of Point Event Detectors', () => {

        it('Is Binary Event Detector created properly', () => {
            cy.get('#point-prop-event-detecotrs  button').trigger('click');
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');

            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) input').clear().type('PED_TEST_0001');
            cy.get('#dialog-create-event-detector  .v-card__text > .row:nth-of-type(2) > .col:nth-of-type(3) .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text > .row:nth-of-type(2) > .col:nth-of-type(4) input').type('Test Alias for Binary PED');

            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) > .col:nth-of-type(5) > .row > .col:nth-of-type(2) .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) > .col:nth-of-type(5) > .row > .col:nth-of-type(3) input').clear().type('5');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) > .col:nth-of-type(5) > .row > .col:nth-of-type(4) .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');

            cy.restLogin();
            cy.get('#dialog-create-event-detector .v-card__actions > button:last-of-type').trigger('click');

            cy.get('.v-snack__wrapper .v-snack__content').contains('Added successful!');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
            
            cy.get('#point-prop-event-detecotrs-list > .row > .col:nth-of-type(1)').contains('PED_TEST_0001');
        });

        it('Is Binary Event Detector updated properly', () => {
            cy.get('#point-prop-event-detecotrs-list > .row > .col:nth-of-type(7) > .row > .col:nth-of-type(2)').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');
            cy.get('#point-prop-event-detecotrs-list > .row > .col:nth-of-type(7) > .row > .col:nth-of-type(3) input').clear().type('10');
            cy.get('#point-prop-event-detecotrs-list > .row > .col:nth-of-type(7) > .row > .col:nth-of-type(4)').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');

            cy.restLogin();
            cy.get('#point-prop-event-detecotrs-list > .row > .col:nth-of-type(2) button').trigger('click');

            cy.get('.v-snack__wrapper .v-snack__content').contains('Updated successful!');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
        });

        it('Is Binary Event Detector deleted properly', () => {
            cy.restLogin();
            cy.get('#point-prop-event-detecotrs-list > .row > .col:nth-of-type(3) button').trigger('click');
            cy.get('.v-dialog:last-of-type > .dialog-confirmation > .v-card__actions > button.success--text').last().trigger('click');

            cy.get('.v-snack__wrapper .v-snack__content').contains('Deleted successful!');
            cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');

            cy.get('#point-prop-event-detecotrs-list > .row ').should('not.exist');
        });


    })

})

context('Scenario - Numeric Data Point Properties validation', () => {

    before(() => {
        cy.restLogin();
        cy.visit('/app.shtm#/datapoint-list');
        cy.get('tbody > tr:nth-of-type(3) > td:nth-of-type(1)').click()
        openPointPropertiesDialog();
    });

    describe('Test - Validate Point Properties components', () => {

        it('Is Numeric Logging Properties rendered properly', () => {
            cy.get('#point-prop-logging h3').contains('Logging properties');
            cy.get('#point-prop-logging > .col:nth-of-type(2) > .v-select').should('be.visible');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(1) .v-select').should('not.be.visible')
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(2) .col:nth-of-type(1)').contains('Tolerance');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(2) .col:nth-of-type(3) .v-input--switch label').contains('Discard extreme values');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(2) .col:nth-of-type(4) .v-input label').contains('Discard low limit');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(2) .col:nth-of-type(5) .v-input label').contains('Discard high limit');

            cy.get('#point-prop-logging > .col:nth-of-type(4)').contains('Purge after');
            cy.get('#point-prop-logging > .col:nth-of-type(5) .v-input').should('exist');
            cy.get('#point-prop-logging > .col:nth-of-type(7)').contains('Default cache size');
            cy.get('#point-prop-logging > .col:nth-of-type(8) .v-input').should('exist');

            cy.get('#point-prop-logging > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content > div[role="listbox"] > div:nth-of-type(4)').trigger('click');

            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(1) .col:nth-of-type(1)').contains('Interval logging period every');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(1) .col:nth-of-type(3) .v-select').should('be.visible')
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(1) .col:nth-of-type(4) .v-select').should('be.visible')
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(1) .col:nth-of-type(4) .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 4);
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(3)').trigger('click');
            cy.get('#point-prop-logging > .col:nth-of-type(3) .row:nth-of-type(1) .col:nth-of-type(4) .v-select .v-select__selection').contains('Minimum');

        });

        it('Is Text Renderer rendered properly', () => {
            cy.get('#point-prop-text-renderer h3').contains('Text renderer properties');

            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 4);
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Analog');
            cy.get('#point-prop-text-renderer #renderer-analog .col:nth-of-type(1) .v-input').should('be.visible');
            cy.get('#point-prop-text-renderer #renderer-analog .col:nth-of-type(1) .v-input label').contains('Format');
            cy.get('#point-prop-text-renderer #renderer-analog .col:nth-of-type(2) .v-input').should('be.visible');
            cy.get('#point-prop-text-renderer #renderer-analog .col:nth-of-type(2) .v-input label').contains('Suffix');

            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Plain');
            cy.get('#point-prop-text-renderer #renderer-plain .col:nth-of-type(1) .v-input label').contains('Suffix');

            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(3)').trigger('click');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Range');
            cy.get('#point-prop-text-renderer #renderer-range .col:nth-of-type(1) .v-input label').contains('Format');
            cy.get('#point-prop-text-renderer #renderer-range .col:nth-of-type(2) button').should('have.css', 'background-color', 'rgb(69, 142, 35)');
            cy.get('#point-prop-text-renderer #renderer-range .col:nth-of-type(3) .v-input label').contains('From');
            cy.get('#point-prop-text-renderer #renderer-range .col:nth-of-type(4) .v-input label').contains('To');
            cy.get('#point-prop-text-renderer #renderer-range .col:nth-of-type(5) .v-input label').contains('Text');

            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(4)').trigger('click');
            cy.get('#point-prop-text-renderer > #text-renderer-selector > .v-select .v-select__selection').contains('Time');
            cy.get('#point-prop-text-renderer #renderer-time .col:nth-of-type(1) .v-input label').contains('Format');
            cy.get('#point-prop-text-renderer #renderer-time .col:nth-of-type(2) .v-input label').contains('Conversion exponent');

        });

        it('Is Event Renderer rendered properly', () => {
            cy.get('#point-prop-event-renderer h3').contains('Event renderer properties');
            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select').should('be.visible');
            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('None');

            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 2);
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#point-prop-event-renderer  > .col:nth-of-type(2) > .v-select .v-select__selection').contains('Range');
        });

    })

    describe('Test - Validate Numeric Event Detectors', () => {

        it('Is Event Detectors Select rendered properly', () => {
            cy.get('#point-prop-event-detecotrs  button').trigger('click');
            cy.get('#dialog-create-event-detector .v-card__title').contains('Create Event Detector');
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div').should('have.length', 7);
        })

        it('Is High Limit Event Detector rendered properly', () => {
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(1)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('High limit');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(4) .v-select').should('exist');
        })

        it('Is Low Limit Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(2)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('Low limit');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(4) .v-select').should('exist');
        })

        it('Is Positive CUSUM Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(6)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('Positive Limit');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) label').contains('Weight');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(4) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(5) .v-select').should('exist');
        })

        it('Is Negative CUSUM Event Detector rendered properly', () => {
            cy.get('#dialog-create-event-detector .v-card__text > .row:first-of-type .v-select').trigger('click');
            cy.get('.v-application > .v-menu__content.v-menu__content--fixed.menuable__content__active > div[role="listbox"] > div:nth-of-type(7)').trigger('click');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(1) label').contains('Export ID');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(3) label').contains('Alarm Level');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(4) label').contains('Alias');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(2) label').contains('Negative Limit');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(3) label').contains('Weight');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(4) label').contains('Duration');
            cy.get('#dialog-create-event-detector  .v-card__text .row:nth-of-type(2) .col:nth-of-type(5) .row .col:nth-of-type(5) .v-select').should('exist');
        })

    })

});


function openPointPropertiesDialog() {
    cy.get('.row > .v-btn i.mdi-pencil').click();
}