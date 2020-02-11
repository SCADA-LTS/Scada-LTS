context('Verify Modern Watch List Page and Modern Charts', () => {



    before(() => {
        cy.clearCookies()
        cy.visit('/login.htm')
        cy.get('input#username').type("admin").should('have.value', 'admin')
        cy.get('input#password').type("admin").should('have.value', 'admin')
        cy.get('.login-button > input').click()
        cy.location('pathname').should('include', 'watch_list')
        // cy.info('Make sure that you have preapred WatchList with at least one datapoint active')

    })

    describe("Modern Watch List", function () {

        it('Open Modern Watch List Page', function () {
            cy.visit('/modern_watch_list.shtm')
            cy.location('pathname').should('include', 'modern_watch_list.shtm')
        })
        it("Validate Modern Charts Vue component exist", function () {
            cy.get(".smallTitle").should('contain', 'Modern Chart')
        })
    })
    describe("Chart with 1 datapoint", function () {

        it('Create chart', function () {
            cy.get('#watchListSelect').select("Test_WL_1");
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.hello').find('svg')
        })
        it('Modify chart - set to Line Chart', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('.radio-button[value="line"]').click()
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
        })
        it('Modify chart - set to Step Line Chart', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('.radio-button[value="stepLine"]').click()
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
        })
        it('Modify chart - change Chart Color', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('.farbtastic-overlay').click(50, 2)
            cy.get('.farbtastic-solid').should('have.css', 'background-color').and('eq', 'rgb(255, 3, 0)')
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
        })
        it('Modify chart - change to Static Chart', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('.radio-button[value="live"]').click()
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
        })
        it('Modify chart - change values from last: 6 hours', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('#live-sd').type('{backspace}6')
            cy.get('#live-sd').should('have.value', '6')
            cy.get('#live-sd').next().select('hour')
            cy.get('#live-sd').next().should('have.value', 'hour')
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
        })
        it('Modify chart - change values from last: 1 day', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('#live-sd').type('{backspace}1')
            cy.get('#live-sd').should('have.value', '1')
            cy.get('#live-sd').next().select('day')
            cy.get('#live-sd').next().should('have.value', 'day')
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
        })
        it('Modify chart - change to Static Chart', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('.radio-button[value="static"]').click()
        })
        it('Modify chart - cancel', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/cross.png"]').click()
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('.radio-button[value="live"]').should('not.be.checked')
            cy.get('.settings-btn[src="/ScadaBR/images/cross.png"]').click()
        })
        it('Delete chart', function () {
            cy.get('.settings-btn[src="/ScadaBR/images/delete.png"]').click()
            cy.get('.chart-container horizontal').should('not.contain', 'svg')
        })
    })
    describe('Chart with multiple datapoints', function () {
        it('Create chart with 5 numeric datapoints', function () {
            cy.get('#watchListSelect').select("Test_WL_5");
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.hello').find('svg')
            cy.get('.settings-btn[src="/ScadaBR/images/delete.png"]').click()
        })
        it('Create chart with 10 numeric datapoints', function () {
            cy.get('#watchListSelect').select("Test_WL_10");
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.hello').find('svg')
            cy.get('.settings-btn[src="/ScadaBR/images/delete.png"]').click()
        })
    })
    describe("Chart memory tests", function () {
        it('Modify chart and save. Validate cookie', function () {
            cy.get('#watchListSelect').select("Test_WL_1");
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.settings-btn[src="/ScadaBR/images/cog.png"]').click()
            cy.get('#live-sd').type('{backspace}3')
            cy.get('#live-sd').should('have.value', '3')
            cy.get('.settings-btn[src="/ScadaBR/images/accept.png"]').click()
            cy.getCookie('WatchListChartDashboard_admin').should('exist')
            cy.get('.settings-btn[src="/ScadaBR/images/delete.png"]').click()
        })
    })
    describe("Additional test - multiple charts", function () {
        it('Create 2 same charts', function () {
            cy.get('#watchListSelect').select("Test_WL_1");
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.hello').should('have.length', 2)
            cy.get('.settings-btn[src="/ScadaBR/images/delete.png"]').click({ multiple: true })
            cy.get('.hello').should('not.exist')
        })
        it('Create 2 different charts', function () {
            cy.get('#watchListSelect').select("Test_WL_5");
            let pointList = cy.get(".dojoTreeNodeLabelTitle");
            let count = 5;
            pointList.get('img[src="images/bullet_go.png"]').each(($el, index, $list) => {
                if (count > 0) {
                    cy.wrap($el).click()
                    count = count - 1
                }
            })
            cy.get('#watchListTable').get('input[type="checkbox"]').click({ multiple: true, force: true });
            cy.get('#watchListTable').get('input[type="checkbox"]').first().click({force: true});
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('#watchListTable').get('input[type="checkbox"]').first().click({force: true});
            cy.get('#watchListTable').get('input[type="checkbox"]').eq(1).click({force: true});
            cy.get('.scada-widget .settings').find('button').first().click()
            cy.get('.hello').should('have.length', 2)
        })
    })

})