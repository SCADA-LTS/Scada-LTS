const { secureCookieProxy } = require('http-proxy-middleware-secure-cookies');
var webpack = require('webpack');
const fs = require('fs');
const path = require('path');
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
	publicPath: process.env.NODE_ENV === 'production' ? '/Scada-LTS/' : '/',
	filenameHashing: false,
	productionSourceMap: true,
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
		resolve: {
			alias: {
				'@s': path.resolve(__dirname, 'src/store/'),
				'@c': path.resolve(__dirname, 'src/components/'),
				'@layout': path.resolve(__dirname, 'src/layout/'),
				'@dialogs': path.resolve(__dirname, 'src/layout/dialogs/'),
				'@models': path.resolve(__dirname, 'src/models/'),
			},
			extensions: ['.js', '.vue', '.json']
		},
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
			'^/graphics/*': secureCookieProxy('http://localhost:8080/Scada-LTS'),
			'^/uploads/*': secureCookieProxy('http://localhost:8080/Scada-LTS'),
			'^/images/*': secureCookieProxy('http://localhost:8080/Scada-LTS'),
			'^/api/*': secureCookieProxy('http://localhost:8080/Scada-LTS'),
			'^//pointHierarchy/*': secureCookieProxy('http://localhost:8080/Scada-LTS'),
			'^/ws-scada/*': {
				target: 'http://localhost:8080/Scada-LTS',
				ws: true,
				changeOrigin: true,
			}
		},
	},
};
