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
const https = require('https');
const pkgJsonPath = require.main.paths[0].split('node_modules')[0] + 'package.json';
const json = require(pkgJsonPath);

// ----- PACKAGE.JSON VARIABLES ----- //
var tag = '0.3.0';
var milestone = 'Unknown';
var build = '0';
var branch = 'local';
var commit = 'N/A';
var actor = 'developer';
var pullRequestBranch = '';
// ----- ---------------------- ----- //
if (process.argv.length === 7 || process.argv.length === 8) {
	milestone = process.argv[2];
	build = process.argv[3];
	if(process.argv[4].startsWith("refs")) {
		branch = process.argv[4].split('/').splice(2).join('/');
	} else {
		branch = process.argv[4];
	}
	commit = process.argv[5];
	actor = process.argv[6];
	if (process.argv.length === 8) {
		pullRequestBranch = process.argv[7];
	}
}

if (!json.hasOwnProperty('scripts')) {
	json.scripts = {};
}

var options = {
	host: 'api.github.com',
	path: '/repos/SCADA-LTS/Scada-LTS/releases/latest',
	headers: { 'User-Agent': 'Mozilla/5.0' },
};

var request = https.request(options, function (res) {
	var data = '';
	res.on('data', function (chunk) {
		data += chunk;
	});
	res.on('end', function () {
		let webJson = JSON.parse(data);
		if (!!webJson) {
			if (!!webJson.tag_name) {
				tag = webJson.tag_name.replace(/[^\d.-]/g, '');
			}
		} else {
			console.warn(
				'WARNING!:\tFailed to fetch build version data!\n\t\tScada-LTS version may not be displayed properly!',
			);
			console.log(webJson);
			console.log('DEBUG: Response data from request:\n', data);
		}
		json.tag = tag;
		json.milestone = milestone;
		json.build = build;
		json.branch = branch;
		json.commit = commit;
		json.actor = actor;
		json.pullRequestBranch = pullRequestBranch;
		printBuildInformation(json);

		saveFile(pkgJsonPath, JSON.stringify(json, null, 2));
	});
});
request.on('error', function (e) {
	console.log(e.message);
});
request.end();

function printBuildInformation(buildInfo) {
	console.log('******************************************************');
	console.log('**** Scada-LTS System Settings Build Information  ****');
	console.log('******************************************************');
	console.log('ScadaLTS latest stable GitHub release:\t', buildInfo.tag);
	console.log(
		'ScadaLTS current build version:\t\t',
		`${buildInfo.milestone}.${buildInfo.build}`,
	);
	console.log('Build form GitHub branch:\t\t', buildInfo.branch);
	console.log('Build form GitHub commit:\t\t', buildInfo.commit);
	console.log('Initiated by:\t\t\t\t', buildInfo.actor);
	console.log('GitHub PullRequest branch:\t\t', buildInfo.pullRequestBranch);
	console.log('******************************************************');
}
