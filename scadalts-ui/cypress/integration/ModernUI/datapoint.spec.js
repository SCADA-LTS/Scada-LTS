before(() => {
    cy.restLogin();
    createDataSource('VirtualDS', "XID_Cypress_01");
    cy.get('button.mdi-chevron-down').click();
});

after(() => {
    deleteDataSource();
})

context('Create Data Points form DataSource page', () => {

    describe('Numeric Data Point', () => {
        
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

        it('Fill basic form fields', () => {
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').type('DP Numeric 01')
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(2) input').clear().type("DPT_CypressNum_01")
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(4) input').clear().type("Long Data Point name description for user");
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.not.disabled');
        });

        describe("Brownian Change Type", () => {
            it('Is Change Type Brownian selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().should('have.length', 5);
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).should('contain', 'Brownian');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).click();
                });
            })
    
            it('Is Brownian change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Minimum');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) label').should('contain', 'Maximum');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) label').should('contain', 'Maximum Change');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(4) label').should('contain', 'Start value');
            });
    
            it('Is Brownian change type filled correctly', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) input').clear().type(10)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) input').clear().type(111)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) input').clear().type(15)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(4) input').clear().type(50)
            })
        })

        

        describe("Increment Change Type", () => {

            it('Is Change Type Increment selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().should('have.length', 5);
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).should('contain', 'Increment');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).click();
                });
            })
    
            it('Is Increment change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Minimum');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) label').should('contain', 'Maximum');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) label').should('contain', 'Maximum Change');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(4) label').should('contain', 'Roll');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(5) label').should('contain', 'Start value');
            });
    
            it('Is Increment change type filled correctly', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) input').clear().type(5)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) input').clear().type(50)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) input').clear().type(5)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(5) input').clear().type(10)
            });

        })

        

        

        describe("No Change Type", () => {

            it('Is type No Change selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(2).should('contain', 'No Change');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(2).click();
                });
            })
    
            it('Is No chagne type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Start value');
            });
    
            it('Is No change change type filled correctly', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) input').clear().type(5)
            });
        })

        describe("Random Change Type", () => {

            it('Is Change Type Random selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(3).should('contain', 'Random');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(3).click();
                });
            })
    
            it('Is Random change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Minimum');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) label').should('contain', 'Maximum');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) label').should('contain', 'Start value');
            });
    
            it('Is Random change type filled correctly', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) input').should('have.value', '10')
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) input').clear().type(5)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) input').should('have.value', '5')
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) input').should('have.value', '100')
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) input').clear().type(50)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) input').should('have.value', '50')
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) input').should('have.value', '12')
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) input').clear().type(25)
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) input').should('have.value', '25')
            });

            it('Create DataPoint', () => {
                cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').click();
                cy.get('.datapoint-item').should('contain', 'DP Numeric 01');
                cy.get('.datapoint-item').should('contain', 'Numeric');
                cy.get('.datapoint-item').should('contain', 'Random');
            });
            
        })

    });

    describe('Binary Data Point', () => {

        it('Is Point Creation dialog visible', () => {
            cy.get('.data-source-item--datapoint-list .v-btn--fab .mdi-plus').click();
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Create');
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Virtual Data Point');
        });


        it('Is Binary point type available', () => {
            cy.get('.v-dialog--active .v-card__title div.v-select').click().then(() => {
                cy.get('.menuable__content__active > div[role="listbox"]').children().should('have.length', 4);
                cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).should('contain', 'Binary');
                cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).click();
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) label').should('contain', 'Change Type');
            });
        });

        it('Fill basic form fields', () => {
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').type('DP Binary 01')
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(2) input').clear().type("DPT_CypressBin_01")
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(4) input').clear().type("Secondary Data Point for End to End Testing");
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.not.disabled');
        });

        describe("Alternate Change Type", () => {

            it('Is Change Type Alternate selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).should('contain', 'Alternate');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).click();
                });
            })
    
            it('Is Alternate change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row label').should('contain', 'Start Value');
            });

        });

        describe("No Change Type", () => {

            it('Is No Change Type selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).should('contain', 'No Change');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).click();
                });
            })
    
            it('Is No Change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row label').should('contain', 'Start Value');
            });
        });

        describe("Random Type", () => {

            it('Is No Change Type selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(2).should('contain', 'Random');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(2).click();
                });
            })
    
            it('Is Random type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row label').should('contain', 'Start Value');
            });

            it('Create DataPoint', () => {
                cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').click();
                cy.get('.datapoint-item').eq(1).should('contain', 'DP Binary 01');
            });
        })
    })

    describe('Multistate Data Point', () => {

        it('Is Point Creation dialog visible', () => {
            cy.get('.data-source-item--datapoint-list .v-btn--fab .mdi-plus').click();
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Create');
            cy.get('.v-dialog--active .v-card__title').should('contain', 'Virtual Data Point');
        });


        it('Is Binary point type available', () => {
            cy.get('.v-dialog--active .v-card__title div.v-select').click().then(() => {
                cy.get('.menuable__content__active > div[role="listbox"]').children().should('have.length', 4);
                cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).should('contain', 'Multistate');
                cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).click();
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) label').should('contain', 'Change Type');
            });
        });

        it('Fill basic form fields', () => {
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').type('DP Multistate 01')
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(2) input').clear().type("DPT_CypressMs_01")
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(4) input').clear().type("Secondary State Data Point for Integers");
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.not.disabled');
        });

        describe("Increment Change Type", () => {

            it('Is Change Type Increment selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).should('contain', 'Increment');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(0).click();
                });
            })
    
            it('Is Alternate change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Values');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) label').should('contain', 'Roll');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) label').should('contain', 'Initail Value');
            });

        });

        describe("No Change Type", () => {

            it('Is No Change Type selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).should('contain', 'No Change');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(1).click();
                });
            })
    
            it('Is No Change type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row label').should('contain', 'Start Value');
            });
        });

        describe("Random Type", () => {

            it('Is No Change Type selected', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(2) > :nth-child(1) .v-select').click().then(() => {
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(2).should('contain', 'Random');
                    cy.get('.menuable__content__active > div[role="listbox"]').children().eq(2).click();
                });
            })
    
            it('Is Random type valid', () => {
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(1) label').should('contain', 'Values');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(2) label').should('contain', 'Roll');
                cy.get('.v-dialog--active .v-card__text form > :nth-child(3) > :nth-child(1) > .row > :nth-child(3) label').should('contain', 'Initail Value');
            });

            it('Create DataPoint', () => {
                cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').click();
                cy.get('.datapoint-item').eq(2).should('contain', 'DP Multistate 01');
            });
        })
    })

    describe('Edit Data Point', () => {

        it('Is Edit Creation dialog visible', () => {
            cy.get('.datapoint-item:nth-of-type(1) .mdi-dots-vertical').should('be.visible');
            cy.get('.datapoint-item:nth-of-type(1) .mdi-dots-vertical').click().then(() => {
                cy.get('.menuable__content__active > div.v-list').children().should('have.length', 4);
                cy.get('.menuable__content__active > div.v-list').children().eq(0).should('contain', 'Details');
                cy.get('.menuable__content__active > div.v-list').children().eq(1).should('contain', 'Edit');
                cy.get('.menuable__content__active > div.v-list').children().eq(2).should('contain', 'Copy');
                cy.get('.menuable__content__active > div.v-list').children().eq(3).should('contain', 'Delete');
                cy.get('.menuable__content__active > div.v-list').children().eq(1).click().then(() => {
                    cy.get('.v-dialog--active .v-card__title').should('contain', 'Update');
                })
            })
        });

        it('Is numeric Data Point values valid', () => {
            cy.get('.v-dialog--active .v-card__title div.v-select div.v-select__selection').should('contain', 'Numeric');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').should('have.value', 'DP Numeric 01');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(2) input').should('have.value', 'DPT_CypressNum_01');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(4) input').should('have.value', 'Long Data Point name description for user');
        });

        it('Modify form fields', () => {
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').clear()
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.disabled');
            cy.get('.v-dialog--active .v-card__text form > :nth-child(1) > :nth-child(1) input').clear().type('DP Numeric 01 Modified');
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').should('be.not.disabled');
        });

        it('Save changes', () => {
            cy.get('.v-dialog--active .v-card__actions > :nth-child(3)').click().then(() => {
                cy.get('.datapoint-item:nth-of-type(1)').should('contain', 'DP Numeric 01 Modified');
            });
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