context('Create Datasource', () => {

    function createVirtualDataSource(name) {
        cy.visit('data_sources.shtm');
        cy.get("#dataSourceTypes").select('Virtual Data Source').should('have.value', '1');
        cy.get('img.ptr[src="images/icon_ds_add.png"]').first().click();
        cy.get('.smallTitle').should('contain', 'Virtual data source properties')
        cy.get('#dataSourceName').type(name)
        cy.get('#updatePeriods').type("{backspace}1").should('have.value', '1');
        cy.get('img.ptr[src="images/save.png"]').first().click();
        cy.get('#dataSourceMessage').should('contain', 'Data source has been saved')
        // cy.debug('DataSource created!')
    }

    /**
     * 
     * @param {*} name DataPoint name
     * @param {*} type [Multistate = 2| Numeric = 3]
     */
    function addVirtualDataPoint(name, type) {
        cy.get('img.ptr[src="images/icon_comp_add.png"]').click();
        cy.get('input#name').type(name)
        cy.get('input#settable').click()
        cy.get('select#dataTypeId').select(type)
        if (type === '3') {
            cy.wait(500)
            cy.get('select#changeTypeId').select('Random')
            cy.get('#divCH6').children().first().next().find('input').type("{backspace}20")
            cy.get('#divCH6').children().first().next().next().find('input').type("2")
        } else if (type === '2') {
            cy.get('select#changeTypeId').select('8')
            cy.get('input#randomMultistate').type("1")
            for (let x = 1; x <= 5; x = x + 1) {
                cy.get('img.ptr[src="images/add.png"]').click()
            }
            cy.get('select#randomMultistateChange.startValue').select('1')
        }
        cy.get('img.ptr#pointSaveImg').click()
        cy.get('#pointMessage').should('contain', 'Point details saved')
        // cy.debug(`DataPoint ${name} created!`)
    }

    function login(username, password) {
        cy.visit('/login.htm')
        cy.get('input#username').type(username)
        cy.get('input#password').type(password)
        cy.get('.login-button > input').click()
    }

    before(() => {
        login("admin", "admin")
    })

    describe("Create Test Datasource", function () {

        it('Create datasource with 10 numeric datapoints', function () {
            const count = 10;
            cy.visit('/data_sources.shtm')
            createVirtualDataSource(`Test-${new Date().toISOString()}`)
            for (let i = 0; i < count; i = i + 1) {
                addVirtualDataPoint(`0${i}-Test`, '3')
            }
            cy.get('#pointsList').children().should('have.length', count)
            cy.get('img.ptr#enableAllImg').click();
            cy.get('#pointsList').find('img[src="images/brick_go.png"]').should('have.length', count)
            cy.get('img.ptr#dsStatusImg').click();
        })

    })

})