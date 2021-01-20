const { Context } = require('ag-grid-community');
import { createVirtualDataSource, addVirtualDataPoint } from '../../utils/utils';

context('Test System Settings Vue.JS page', () => {
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

	describe('Open System Settings as non-admin user', () => {
		beforeEach(() => {
			cy.get('a[href="logout.htm"]').click();
			cy.visit('/login.htm');
			cy.get('input#username').type('nonadmin').should('have.value', 'nonadmin');
			cy.get('input#password').type('nonadmin').should('have.value', 'nonadmin');
			cy.get('.login-button > input').click();
			cy.get('.userName').should('contain', 'nonadmin');
		});

		it('Open System Settings via URL', () => {
			cy.visit('app.shtm#/system-settings');
			cy.get('.alert').should('contain', 'Not allowed to see that page');
			cy.visit('/login.htm');
		});

		it('Should not be able to navigate through menu', () => {
			cy.get('a[href="app.shtm"]').click();
			cy.get('#btn-sidebar').click();
			cy.get('.sidebar').should('be.visible');
			cy.get('.sidebar').should('not.contain', 'System Settings');
			cy.visit('/login.htm');
		});
	});

	describe('Open System Settings as admin user', () => {
		it('Open System Settings via URL', () => {
			cy.visit('app.shtm#/system-settings');
			cy.reload();
			cy.get('.alert').should('not.contain', 'Not allowed to see that page');
		});

		it('Should be able to navigate through menu', () => {
			cy.get('a[href="app.shtm"]').click();
			cy.get('#btn-sidebar').click();
			cy.get('.sidebar').should('be.visible');
			cy.get('.sidebar > a[href="#/system-settings"]').should('be.visible');
			cy.get('.sidebar > a[href="#/system-settings"]').click();
			cy.get('.alert').should('not.contain', 'Not allowed to see that page');
			cy.visit('/login.htm');
		});
	});
});

context('Test System Settings Components functionality', () => {
	beforeEach(() => {
		cy.clearCookies();
		cy.visit('/login.htm');
		cy.get('input#username').type('admin').should('have.value', 'admin');
		cy.get('input#password').type('admin').should('have.value', 'admin');
		cy.get('.login-button > input').click();
		cy.location('pathname').should('include', 'watch_list');
	});

	describe('Test Default Logging Type Setting', () => {
		beforeEach(() => {
			cy.visit('app.shtm#/system-settings');
		});

		it('Should be visible', () => {
			cy.get('#default-logging-type-setting').should('be.visible');
			cy.get('#default-logging-type-setting > .align-items-center > .col-xs-12').should(
				'contain',
				'Default Data Point logging type',
			);
		});

		it('Save do not log value', () => {
			let name = `Test_SystemSettings-${new Date().getTime()}`;
			name = name.substring(0, 30);
			cy.get('.col-xs-12 > .form-control').select('Do not log');
			cy.get('.floated-right').click();
			cy.get('.btn-success').click();

			createVirtualDataSource(name);
			addVirtualDataPoint(`DP_${name}`, 'Numeric');

			cy.get(':nth-child(5) > img[src="images/icon_comp_edit.png"]').click();
			cy.get('select#loggingType option[selected="selected"]').should(
				'contain',
				'Do not log',
			);
		});

		it('Save do not log value', () => {
			let name = `Test_SystemSettings2-${new Date().getTime()}`;
			name = name.substring(0, 30);
			cy.get('.col-xs-12 > .form-control').select('When point value changes');
			cy.get('.floated-right').click();
			cy.get('.btn-success').click();

			createVirtualDataSource(name);
			addVirtualDataPoint(`DP_${name}`, 'Numeric');

			cy.get(':nth-child(5) > img[src="images/icon_comp_edit.png"]').click();
			cy.get('select#loggingType option[selected="selected"]').should(
				'contain',
				'When point value changes',
			);
		});
	});
});
