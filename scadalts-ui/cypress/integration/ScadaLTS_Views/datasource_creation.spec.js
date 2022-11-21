import { createVirtualDataSource, addVirtualDataPoint, login } from '../../utils/utils';

context('Create Datasource', () => {
	before(() => {
		cy.login('admin', 'admin');
	});

	describe('Create Test Datasource', function () {
		it('Create datasource with 10 numeric datapoints', function () {
			const count = 10;
			cy.visit('/data_sources.shtm');
			createVirtualDataSource(`Test-${new Date().toISOString()}`);
			for (let i = 0; i < count; i = i + 1) {
				addVirtualDataPoint(`0${i}-Test`, 'Numeric');
			}
			cy.get('#pointsList').children().should('have.length', count);
			cy.get('img.ptr#enableAllImg').click();
			cy.get('#pointsList')
				.find('img[src="images/brick_go.png"]')
				.should('have.length', count);
			cy.get('img.ptr#dsStatusImg').click();
		});
	});
});
