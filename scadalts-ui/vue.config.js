var webpack = require('webpack');
const fs = require('fs')
const packageJson = JSON.parse(fs.readFileSync('./package.json'))
const tag = packageJson.tag || 0;
const version = packageJson.version || 0
module.exports = {
  filenameHashing: false,

  configureWebpack: {
    devtool: 'source-map',
    plugins: [
      new webpack.DefinePlugin({
        'process.env': {
          PACKAGE_VERSION: '"' + version + '"',
          PACKAGE_TAG: '"' + tag + '"'
        }
      })
    ]
  },

  pluginOptions: {
    i18n: {
      locale: 'en',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableInSFC: true
    }
  }
}
