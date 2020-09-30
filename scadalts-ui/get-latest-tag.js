/**
 * This script get the latest release tag from Scada-LTS GitHub repository
 * and update the tag property in package.json file. Run this script before
 * releasing the new Scada-LTS version for production. 
 * 
 * To run this script type:
 * 'npm run-script version update'
 * 
 * This tag is displayed in the new Scada-LTS SystemSettings view.
 * Also there on this page there is a UI version 
 * from the version property in the package.json file.
 * Remember to increment this value!
 */
const saveFile = require('fs').writeFileSync;
const https = require("https");
const pkgJsonPath = require.main.paths[0].split('node_modules')[0] + 'package.json';
const json = require(pkgJsonPath);

//------- DEFAULT TAG VERSION --------------//
var tag = '0.1.0'
//--------DEFAULT TAG VERSION -------------//

if (!json.hasOwnProperty('scripts')) {
  json.scripts = {};
}

var options = {
    host: 'api.github.com', 
    path: '/repos/SCADA-LTS/Scada-LTS/releases/latest',
    headers: { 'User-Agent': 'Mozilla/5.0' }
}

var request = https.request(options, function (res) {
    var data = '';
    res.on('data', function(chunk) {
        data += chunk;
    });
    res.on('end', function() {
        let webJson = JSON.parse(data);
        tag = webJson.tag_name.replace(/[^\d.-]/g, '');;
        json.tag = tag;
        saveFile(pkgJsonPath, JSON.stringify(json, null, 2));
    });
});
request.on('error', function (e) {
    console.log(e.message);
});
request.end();




