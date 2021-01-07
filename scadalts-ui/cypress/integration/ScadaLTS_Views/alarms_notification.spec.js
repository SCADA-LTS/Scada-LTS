context('Test PLC Alarms Page Vue.JS page', () => {
	beforeEach(() => {
		cy.clearCookies();
		cy.visit('/login.htm');
		cy.get('input#username').type('admin').should('have.value', 'admin');
		cy.get('input#password').type('admin').should('have.value', 'admin');
		cy.get('.login-button > input').click();
		cy.location('pathname').should('include', 'watch_list');
	});

	afterEach(() => {
		cy.visit('/login.htm');
		cy.get('a[href="logout.htm"]').click();
	});

	describe('Test PLC Alarms Notification page View', () => {
		beforeEach(() => {
			cy.get('a[href="logout.htm"]').click();
			cy.visit('/login.htm');
			cy.get('input#username').type('admin').should('have.value', 'admin');
			cy.get('input#password').type('admin').should('have.value', 'admin');
			cy.get('.login-button > input').click();
			cy.get('.userName').should('contain', 'admin');
		});

		afterEach(() => {
			cy.visit('/login.htm');
		});

		it('Is PLC Alarms Notification List accessible via URL', () => {
			cy.visit('app.shtm#/alarm-notifications');
			cy.get('h1').should('contain', 'Notification List');
		});

		it('Is PLC Alarms Notification List accessible via Button', () => {
			cy.get('[href="app.shtm"] > .ptr').click();
			cy.get('[href="#/alarm-notifications"]').click();
			cy.get('h1').should('contain', 'Notification List');
		});
	});
});
