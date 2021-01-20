import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

Vue.use(Vuetify);

export default new Vuetify({
	theme: {
		options: {
			customProperties: true,
		},
		themes: {
			light: {
				primary: '#458E23',
				secondary: '#404041',
				accent: '#8F248A',
				error: '#FF5252',
				info: '#2196F3',
				success: '#4CAF50',
				warning: '#FFC107',
			},
		},
	},
});
