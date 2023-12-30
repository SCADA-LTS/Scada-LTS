import Vue from 'vue';
import { createVuetify} from 'vuetify/lib/framework';

Vue.use(createVuetify);

export default new createVuetify({
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
