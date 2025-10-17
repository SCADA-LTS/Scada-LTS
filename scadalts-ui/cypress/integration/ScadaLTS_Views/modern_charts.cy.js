context('Verify Modern Watch List Page and Modern Charts', () => {
	before(() => {
		cy.clearCookies();
		cy.visit('/login.htm');
		cy.get('input#username').type('admin').should('have.value', 'admin');
		cy.get('input#password').type('admin').should('have.value', 'admin');
		cy.get('.login-button > input').click();
		cy.location('pathname').should('include', 'watch_list');
	});

	describe('Chart with 1 datapoint', function () {
		it('Create chart', function () {
			cy.request('/api/auth/admin/admin');
			cy.get('#watchListSelect').select('Test_WL_1');
			cy.get('i[class="glyphicon glyphicon-refresh"]').click();
			cy.get('.hello').find('svg');
		});

		it('Modify chart - Change live settings', function () {
			cy.get('#live-sd').clear().type('5').should('have.value', '5');
			cy.get('#live-rrs').select('week').should('have.value', 'week');
			cy.get('#live-rr').select('5000').should('have.value', '5000');
			cy.get('i[class="glyphicon glyphicon-refresh"]').click();
			cy.get('.hello').find('svg');
		});

		it('Modify chart - Change static settings', function () {
			cy.get('#static-btn-1').click();
			cy.get('label[for="static-sd"]').should('contain', 'Start date');
			cy.get('label[for="static-sd"]').should('be.visible');
			cy.get('label[for="static-ed"]').should('contain', 'End date');
			cy.get('label[for="static-ed"]').should('be.visible');
		});

		it('Modify series - change series name', () => {
			cy.get('i[class="glyphicon glyphicon-cog"]').click();
			cy.get('.modal-body-block').should('be.visible');
			cy.get('.tab-pane > :nth-child(1) > div.col-xs-6 > .form-control')
				.clear()
				.type('Cypress Datapoint');
			cy.get('.active > a').should('contain', 'Cypress Datapoint');
			cy.get('.margin-top > :nth-child(3) > .btn').click();
			cy.get('tspan').should('contain', 'Cypress Datapoint');
		});
	});
});
