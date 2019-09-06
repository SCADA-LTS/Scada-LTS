module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080/ScadaLTS',
                ws: true,
                changeOrigin: true
            }
        }
    },
    filenameHashing : false,
    productionSourceMap: false,
    runtimeCompiler: true
}