before(() => {
    cy.logout();
})

context('Scenario - Login', () => {
    

    describe('Test simple login', () => {
        

        it('Is login box visible', () => {
            cy.visit('/login.htm');
            const loginBox = cy.get('.login-container');
            loginBox.should('be.visible');
            loginBox.find('.form-box').should('have.length', 2);
            loginBox.get('input[type="submit"]').should('be.visible');
        });
        it('Login as default user', () => {
            cy.visit('/login.htm');
            cy.get('input[name="username"]').type('admin');
            cy.get('input[name="password"]').type('admin');
            cy.get('input[type="submit"]').click();
            cy.url().should('include', '/watch_list.shtm');
        });
        it('Is navigation to new UI visible', () => {
            cy.url().should('include', '/watch_list.shtm');
            const navbar = cy.get('nav');
            navbar.should('be.visible');
            navbar.find('a[href="app.shtm#/watch-list"]').should('be.visible');
            navbar.find('img[src="images/desktop.png"]').should('be.visible');
        });

        
    })

    describe('Open new User interface', () => {
        before(() => {
            cy.restLogin();
            cy.visit('/watch_list.shtm');
        })

        it('Open using button', () => {
            cy.get('nav').find('a[href="app.shtm#/watch-list"]').click();
            cy.get('#app').should('be.visible');
        });
        it('Are main bar elements visible', () => {
            const header = cy.get('header.v-app-bar');
            header.should('be.visible');
            header.get('i.mdi-account-tie').should('be.visible');
        });
        it('Are navigation bar elements visible (10 elements)', () => {
            const navbar = cy.get('nav');
            navbar.should('be.visible');
            const entryList = navbar.get('.v-navigation-drawer__content > .v-list');
            entryList.children().should('have.length', 10);
        });
        it('Is back to new UI button works', () => {
            cy.get('nav').find('.v-navigation-drawer__content > .v-list').children().last().click();
            cy.get('#app').should('be.not.visible');
        });
    })

    
})