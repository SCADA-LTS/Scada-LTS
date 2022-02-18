const { secureCookieProxy } = require('http-proxy-middleware-secure-cookies');
var webpack = require('webpack');
const fs = require('fs');
const packageJson = JSON.parse(fs.readFileSync('./package.json'));
const tag = packageJson.tag || 0;
const version = packageJson.version || 0;
const milestone = packageJson.milestone || 0;
const build = packageJson.build || 0;
const branch = packageJson.branch || 'local';
const commit = packageJson.commit || 'N/A';
const pullRequestNumber = packageJson.pullRequestNumber || 'false';
const pullRequestBranch = packageJson.pullRequestBranch || '';
module.exports = {
	publicPath: process.env.NODE_ENV === 'production' ? '/ScadaBR/' : '/',
	filenameHashing: false,
	productionSourceMap: false,
	configureWebpack: {
		devtool: 'source-map',
		plugins: [
			new webpack.DefinePlugin({
				'process.env': {
					PACKAGE_VERSION: '"' + version + '"',
					PACKAGE_TAG: '"' + tag + '"',
					SCADA_LTS_MILESTONE: '"' + milestone + '"',
					SCADA_LTS_BUILD: '"' + build + '"',
					SCADA_LTS_BRANCH: '"' + branch + '"',
					SCADA_LTS_COMMIT: '"' + commit + '"',
					SCADA_LTS_PULLREQUEST_NUMBER: '"' + pullRequestNumber + '"',
					SCADA_LTS_PULLREQUEST_BRANCH: '"' + pullRequestBranch + '"',
				},
			}),
		],
	},
	pluginOptions: {
		i18n: {
			locale: 'en',
			fallbackLocale: 'en',
			localeDir: 'locales',
			enableInSFC: true,
		},
	},
	transpileDependencies: ['vuetify'],
	devServer: {
		proxy: {
			'^/api/*': secureCookieProxy('http://localhost:8080/ScadaBR'),
			'^//pointHierarchy/*': secureCookieProxy('http://localhost:8080/ScadaBR'),
			'^/ws-scada/*': {
				target: 'http://localhost:8080/ScadaBR',
				ws: true,
				changeOrigin: true,
			}
		},
	},
};
