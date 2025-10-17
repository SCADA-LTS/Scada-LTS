import svgJS from 'svg.js';

export default {
	install: (Vue) => {
		Vue.prototype.$svg = svgJS;
	},
};
