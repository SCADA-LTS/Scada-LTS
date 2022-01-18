/**
 * Logging to Scada-LTS Cypress Method
 *
 * Classic log in approach that allow user to log into the Scada-LTS application.
 */
Cypress.Commands.add('login', (username = 'admin', password = 'admin') => {
	const loginPath = '/login.htm';

	const log = Cypress.log({
		name: 'login',
		displayName: 'LOGIN',
		message: [`🔐️ Authenticating | ${username} `],
		autoEnd: false,
	});

	cy.server();
	cy.route('POST', '**/MiscDwr.initializeLongPoll.dwr').as('loginUser');

	cy.location('pathname', { log: false }).then((currentPath) => {
		if (currentPath !== loginPath) {
			cy.visit(loginPath, { log: false });
		}
	});

	log.snapshot('before');

	cy.get('input#username', { log: false }).type(username, { log: false });
	cy.get('input#password', { log: false }).type(password, { log: false });
	cy.get('.login-button > input', { log: false }).click({ log: false });

	cy.wait('@loginUser').then(() => {
		log.snapshot('after');
		log.end();
	});
});

/**
 * Logging to Scada-LTS with REST Interface
 *
 * Fast loggin in function that reduce the execution time.
 */
Cypress.Commands.add('restLogin', (username = 'admin', password = 'admin') => {
	const log = Cypress.log({
		name: 'restLogin',
		displayName: 'REST_LOGIN',
		message: [`🔐️ Authenticating | ${username} `],
		autoEnd: false,
	});

	cy.clearCookies({ log: false });
	log.snapshot('before');
	cy.request({
		log: false,
		url: `/api/auth/${username}/${password}`,
	})
		.its('body', { log: false })
		.should('include', 'true');
	log.snapshot('after');
	log.end();
});

/**
 * Load Scada-LTS configuration using Import/Export section.
 *
 * Provide Configuration as JSON object. This methot will try
 * import that configuration to the application and report if
 * something went wrong.
 */
Cypress.Commands.add('loadConfiguration', (configuration) => {
	const log = Cypress.log({
		name: 'loadConfiguration',
		displayName: 'LOADING_DATA',
		message: [`⏳️ Loading configuration`],
		autoEnd: false,
	});

	cy.server({
		whitelist: () => {
			return true;
		},
	});

	cy.visit('/emport.shtm', { log: false });
	log.snapshot('before');
	cy.get('#emportData', { log: false })
		.then(($input) => {
			$input.text(JSON.stringify(configuration));
			cy.get('#importJsonBtn', { log: false }).click({ log: false });
			cy.wait(5000);
			cy.get('#alternateMessage', { log: false }).contains('Import complete');
		})
		.then(() => {
			log.snapshot('after');
			log.end();
		});
});
