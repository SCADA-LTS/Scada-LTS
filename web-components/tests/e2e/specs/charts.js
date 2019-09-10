describe("Test - ScadaLTS Charts", function () {
    it("Create test DataSource", function () {

        cy.visit('http://localhost:8080/ScadaLTS')
        cy.get("#username").type("admin").should('have.value', 'admin')
        cy.get("#password").type("admin")
        cy.contains('Login').click()
        cy.url().should('include', '/watch_list.shtm')

        cy.visit('http://localhost:8080/ScadaLTS/data_sources.shtm')
        cy.get("#dataSourceTypes").select("Virtual Data Source").should('have.value','1')
        cy.get("#dataSourceTypesContent").within(() => {
            cy.get(".ptr").click()
        })

        cy.url().should('include', '/data_source_edit.shtm?typeId=1')
        cy.get("#dataSourceName").type("TestDS")
        cy.get("#updatePeriodType").select("1")
        cy.get("#dsSaveImg").click()
        cy.get("#editImg-1").click()

        cy.get("#pointDetails").within(() => {
            cy.get("#name").type("NumericPoint")
            cy.get("#settable").check()
            cy.get("#dataTypeId").select("3")
            cy.get("#changeTypeId").select("5")
            cy.get("#divCH5").within(() => {
                cy.get("input").type("0")
            })
            cy.get("#pointSaveImg").click()
            cy.contains('Point details saved')
        })

        cy.get("#pointsList").within(() => {
            cy.get('.ptr[src="images/brick_stop.png"]').click()
        })
        cy.get("#dsStatusImg").click()
        cy.visit('http://localhost:8080/ScadaLTS/data_sources.shtm')
        cy.contains('TestDS')
    })

    it("Test - Live Point Update", function () {

        cy.visit('http://localhost:8080/ScadaLTS')
        cy.get("#username").type("admin").should('have.value', 'admin')
        cy.get("#password").type("admin")
        cy.contains('Login').click()
        cy.url().should('include', '/watch_list.shtm')

        cy.visit('http://localhost:8080/ScadaLTS/data_sources.shtm')
        cy.contains('TestDS').parent().parent().within(()=>{
            cy.get('.ptr[src="images/arrow_out.png"]').click();
        })
        cy.contains("NumericPoint").parent().within(() => {
            cy.get('.ptr[src="images/icon_comp.png"]').click();
        })

        cy.contains("NumericPoint")
        cy.contains("Point details")
        cy.contains("History")
        cy.get('#vue-ui').within(() => {
            cy.contains("NumericPoint")
        })
        cy.get('#pointChange').within(() => {
            cy.get("input").clear().type("2.0")
            cy.get("a").click()
        })
        cy.get("#pointValue").should("contain", "2.0")
        cy.wait(2000)
        cy.get("circle").should('have.length', 3)
        cy.get('#pointChange').within(() => {
            cy.get("input").clear().type("1.0")
            cy.get("a").click()
        })
        cy.wait(2000)
        cy.get("circle").should('have.length', 5)

    })

    it("Test - Watch List Point Update", function () {

        cy.visit('http://localhost:8080/ScadaLTS')
        cy.get("#username").type("admin").should('have.value', 'admin')
        cy.get("#password").type("admin")
        cy.contains('Login').click()
        cy.url().should('include', '/watch_list.shtm')


        cy.get('.ptr[src="images/add.png"]').last().click();
        cy.contains("NumericPoint").parent().within(() => {
            cy.get('img[src="images/bullet_go.png"]').click();
        })
        cy.get("#watchListTable").within(() => {
            cy.contains("NumericPoint");
        })
        cy.contains("Add chart").click()
        cy.get("circle").should('have.length', 5)


    })

    it("Delete test DataSource", function () {

        cy.visit('http://localhost:8080/ScadaLTS')
        cy.get("#username").type("admin").should('have.value', 'admin')
        cy.get("#password").type("admin")
        cy.contains('Login').click()
        cy.url().should('include', '/watch_list.shtm')

        cy.visit('http://localhost:8080/ScadaLTS/data_sources.shtm')
        cy.contains('TestDS').parent().parent().within(()=>{
            cy.get('.ptr[src="images/icon_ds_delete.png"]').click();
        })
        cy.contains("TestDS").should('not.exist')

    })
})