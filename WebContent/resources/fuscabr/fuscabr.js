/******************************************
 * FUScaBR - Funções úteis para o ScadaBR
 * Versão 2.0 - Licença: MIT
 ******************************************/
"use strict";

var fuscabr = {};

fuscabr.common = {
	easterEgg: function() {
		var div = document.createElement("div");
		div.style.zIndex = 99;
		div.id = "fuscabr-easter-egg";
		
		ajaxGet("resources/fuscabr/templates/internal/easterEgg.html", function(response) {
			div.innerHTML = response;
			document.body.appendChild(div);
			document.body.addEventListener("click", fuscabr.common.removeEasterEgg);
		});
	},
	
	removeEasterEgg: function() {
		var div = document.getElementById("fuscabr-easter-egg");
		div.remove();
		document.body.removeEventListener("click", fuscabr.common.removeEasterEgg); 
	},
	
	loadModule: function(url, moduleName) {
		if (!document.getElementById("fuscabr-modules")) {
			var moduleDiv = document.createElement("div");
			moduleDiv.id = "fuscabr-modules";
			moduleDiv.style.display = "none";
			document.body.appendChild(moduleDiv);
		}
		
		var module = document.createElement("script");
		module.src = url;
		document.getElementById("fuscabr-modules").appendChild(module);
	},

	getSettings: function() {
		ajaxGetJson("resources/fuscabr/conf/common.json", function(response) {
			fuscabr.common.conf = response;
			
			ajaxGetJson(fuscabr.common.conf.languageFile, function(response2) {
				fuscabr.common.i18n = response2;
				fuscabr.common.init();
			});
		
		});
		
	},

	shouldIncludeModule: function(module) {
		if (module.enabled) {
			for (var i of module.pages) {
				if (window.location.pathname.includes(i))
					return true;
			}
		}
		return false;
	},

	init: function() {
		if (!fuscabr.common.conf) {
			fuscabr.common.getSettings();
		} else {
			var conf = fuscabr.common.conf;
			for (var i of conf.modules) {
				if (fuscabr.common.shouldIncludeModule(i))
					fuscabr.common.loadModule(conf.modulesPath + i.file, i.name);
			}
			
		}
	}

}

function ajaxGet(url, callback, timeout, timeoutCallback, notFoundCallback) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200)
			callback(xhttp.responseText);
		else if (this.status == 404 && typeof notFoundCallback == "function")
			notFoundCallback();
	}
	
	if (timeout && typeof timeoutCallback == "function") {
		xhttp.timeout = timeout;
		xhttp.ontimeout = timeoutCallback();
	}

	xhttp.open("GET", url, true);
	xhttp.send();
}

function ajaxGetJson(url, callback, timeout, timeoutCallback, onErrorCallback) {
	ajaxGet(url, function(cb) {
		callback(JSON.parse(cb));
	}, timeout, timeoutCallback, onErrorCallback);
}

function tryRun(funcObj, interval, retries) {
	if (!retries) {
		return;
	} else {
		retries--;
		setTimeout(function() {
			funcObj();
			tryRun(funcObj, interval, retries);
		}, interval);
	}
}

function localizeElement(element, message) {
	var tag = element.tagName.toLowerCase();
	if (tag == "p" || tag == "option" || tag == "span" || tag == "option") {
		element.innerHTML = message;
	} else if (tag == "input") {
		element.value = message;
	} else if (tag == "img") {
		element.alt = message;
		element.title = message;
	}
}
// Iniciar após o carregamento da janela
window.addEventListener("load", fuscabr.common.init.call(fuscabr.common));
