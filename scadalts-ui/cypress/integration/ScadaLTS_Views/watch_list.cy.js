context('Create WatchList', () => {
	function createWatchList(name, count) {
		cy.visit('/watch_list.shtm');
		cy.get('img.ptr[src="images/add.png"]').last().click();
		cy.get('#watchListSelect').select('(unnamed)');
		cy.get('#wlEditImg')
			.trigger('mouseover')
			.get('#newWatchListName')
			.type(`{selectall}${name}`);
		cy.get('#saveWatchListNameLink').click();

		let pointList = cy.get('.dojoTreeNodeLabelTitle');
		pointList.get('img[src="images/bullet_go.png"]').each(($el, index, $list) => {
			if (count > 0) {
				cy.wrap($el).click();
				count = count - 1;
			}
		});
	}

	function login(username, password) {
		cy.visit('/login.htm');
		cy.get('input#username').type(username);
		cy.get('input#password').type(password);
		cy.get('.login-button > input').click();
	}

	beforeEach(() => {
		login('admin', 'admin');
	});

	describe('Create WatchList', function () {
		let watchListState = [1, 5, 10];
		watchListState.forEach((element) => {
			it(`Create Watch List with ${element} numeric datapoints`, function () {
				createWatchList(`Test_WL_${element}`, element);
				cy.get('#watchListTable').children().should('have.length', element);
			});
		});
	});
});
