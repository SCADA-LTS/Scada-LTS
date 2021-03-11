const username = 'admin';
const password = 'admin';

//TODO: Change the loggin to application via REST API
before(() => {
	cy.clearCookies();
	cy.request('GET', `/api/auth/${username}/${password}`);
});

context('Test Data Point Deatils page', () => {
	before(() => {
		cy.visit('/app.shtm#/datapoint-details/1');
	});

	describe('Test Point Details page components', () => {
		it('Open Point Details main page', () => {
			cy.get('h1').should('contain', '00-Test');
			cy.get('.pointDetailsCards').should('have.length', 2);
			cy.get('svg').should('exist');
			cy.get('svg desc').contains('JavaScript chart by amCharts');
		});

		it('Open Data Point User Comments', () => {
			cy.get('.v-badge > .v-btn').click();
			cy.get('.v-menu__content .v-list-item__icon > i').should('be.visible');
			cy.get('.v-main__wrap > :nth-child(1)').click();
			cy.get('.v-menu__content .v-list-item__icon > i').should('not.be.visible');
		});

		it('Toggle Data Point', () => {
			cy.request('GET', `/api/auth/${username}/${password}`);
			cy.get('h1 > .v-btn--is-elevated').click();
			cy.get('.success--text').click();
			cy.get('h1 > .v-btn--is-elevated').should(
				'have.css',
				'background-color',
				'rgb(255, 82, 82)',
			); //green

			cy.get('h1 > .v-btn--is-elevated').click();
			cy.get('.success--text').click();
			cy.get('h1 > .v-btn--is-elevated').should(
				'have.css',
				'background-color',
				'rgb(69, 142, 35)',
			); //green

			cy.get('h1 > .v-btn--is-elevated').click();
			cy.get(':nth-child(2) > .v-btn__content').click();
			cy.get('h1 > .v-btn--is-elevated').should(
				'have.css',
				'background-color',
				'rgb(69, 142, 35)',
			); //green
		});

		it('Open Point properties dialog', () => {
			openPointPropertiesDialog();
			cy.get('h3').contains(' Point properties ');
		});
	});

	describe('Test Point Properties dialog', () => {
		it('Validate visible components', () => {
			cy.get('h3').contains(' Point properties ');
			cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains(
				'Event Detectors',
			);
			cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains(
				'Logging properties',
			);
			cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains(
				'Text renderer properties',
			);
			cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains(
				'Event renderer properties',
			);
			cy.get('.v-dialog__content--active > .v-dialog > .v-card > .v-card__text').contains(
				'Chart renderer',
			);
		});

		it('Change point name and validate', () => {
			cy.request('GET', `/api/auth/${username}/${password}`);
			cy.get(
				'.v-card__text > :nth-child(1) > :nth-child(1) > :nth-child(1) > :nth-child(2) input',
			)
				.clear()
				.type('Cypress Datapoint');
			cy.get('.v-card__actions > .primary--text > .v-btn__content').click();
			cy.get('.v-snack__wrapper .v-snack__content').contains(' Updated successful! ');
			cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
			cy.get('h1').should('contain', 'Cypress Datapoint');
			openPointPropertiesDialog();

			cy.get(
				'.v-card__text > :nth-child(1) > :nth-child(1) > :nth-child(1) > :nth-child(2) input',
			)
				.clear()
				.type('00-Test');
			cy.get('.v-card__actions > .primary--text > .v-btn__content').click();
			cy.get('.v-snack__wrapper .v-snack__content').contains(' Updated successful! ');
			cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
			cy.get('h1').should('contain', '00-Test');
			openPointPropertiesDialog();
		});

		it('Change point description and validate', () => {
			const DESCRIPTION_TEXT = 'Example datapoint long description';
			cy.get(
				'.v-card__text > :nth-child(1) > :nth-child(1) > :nth-child(1) > :nth-child(4) input',
			)
				.clear()
				.type(DESCRIPTION_TEXT);
			cy.get('.v-card__actions > .primary--text > .v-btn__content').click();
			cy.get('.v-snack__wrapper .v-snack__content').should('be.visible');
			cy.get('.small-description').should('contain', DESCRIPTION_TEXT);
			openPointPropertiesDialog();
			cy.get(
				'.v-card__text > :nth-child(1) > :nth-child(1) > :nth-child(1) > :nth-child(4) input',
			).clear();
		});
	});
});

function openPointPropertiesDialog() {
	cy.get('.row > .v-btn i.mdi-pencil').click();
}
