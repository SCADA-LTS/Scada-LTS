export const systemSettings = {
	state: {
		defaultLoggingType: 1,
	},

	mutations: {
		setDefaultLoggingType(state, defaultLoggingType) {
			state.defaultLoggingType = defaultLoggingType;
		},
	},

	actions: {
		getDefaultLoggingType(context) {
			return new Promise((resolve) => {
				context.state.defaultLoggingType = 3;
				resolve(3);
			});
		},
		saveDefaultLoggingType(context) {
			return new Promise((resolve) => {
				resolve(true);
			});
		},
	},
};

export default systemSettings;
