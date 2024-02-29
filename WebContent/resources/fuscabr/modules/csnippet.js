/******************************************
 * FUScaBR - "Funções úteis para o ScadaBR"
 * License: MIT
 ******************************************/
"use strict";

fuscabr.csnippet = {
    loadHtmlTemplate: function() {
		var conf = fuscabr.csnippet.conf;
        var select = document.getElementById("fuscabr-csnippet-html-select");
        var selectedOption = select[select.selectedIndex].value;
        select.disabled = true;
        
        var changeEvent = new Event("change");
        var textarea = document.getElementById("staticPointContent");
        
        ajaxGet(conf.htmlTemplatesFolder + selectedOption, function(response) {
			textarea.value = response;
			textarea.dispatchEvent(changeEvent);
			select.disabled = false;
		}, conf.templatesLoadTimeout, function() {
			textarea.value = "<!-- Connection Timeout, please try again -->";
			textarea.dispatchEvent(changeEvent);
			select.disabled = false;
		}, function() {
			textarea.value = "<!-- Error! Template file not found. -->";
			textarea.dispatchEvent(changeEvent);
			select.disabled = false;
		});
    },

    loadServerScriptTemplate: function() {
		var conf = fuscabr.csnippet.conf;
        var select = document.getElementById("fuscabr-csnippet-script-select");
        var selectedOption = select[select.selectedIndex].value;
        select.disabled = true;
        
        var changeEvent = new Event("change");
        var textarea = document.getElementById("graphicRendererScriptScript");
        
        ajaxGet(conf.serverScriptTemplatesFolder + selectedOption, function(response) {
			textarea.value = response;
			textarea.dispatchEvent(changeEvent);
			select.disabled = false;
		}, conf.templatesLoadTimeout, function() {
			textarea.value = "/* Connection Timeout, please try again */";
			textarea.dispatchEvent(changeEvent);
			select.disabled = false;
		}, function() {
			textarea.value = "/* Error! Template file not found. */";
			textarea.dispatchEvent(changeEvent);
			select.disabled = false;
		});
    },
    
    createHtmlInterface: function() {
        ajaxGet("resources/fuscabr/templates/internal/codeSnippetDiv.html", function(response) {
            var newHtml = response.replace("TEMPLATE", "html");
            
            var editor = document.getElementById("htmlEditor");
            editor.innerHTML = newHtml + editor.innerHTML;
            editor.querySelector("img").addEventListener("click", fuscabr.csnippet.loadHtmlTemplate);

            var localizable = editor.querySelectorAll("*[data-i18n]");
		    for (var i of localizable) {
    			var message = i.getAttribute("data-i18n");
	    		if (fuscabr.common.i18n.csnippet[message])
		    		localizeElement(i, fuscabr.common.i18n.csnippet[message]);
		    }

            var optionsArr = new Array();
            optionsArr.push(document.createElement("option"));
            for (var i of fuscabr.csnippet.conf.htmlTemplates) {
                var option = document.createElement("option");
                option.value = i.file;
                option.innerHTML = fuscabr.common.i18n.csnippet[i.i18n] || i.desc;
                optionsArr.push(option);
            }
            optionsArr.sort(function(a, b) {
                return (a.innerHTML.localeCompare(b.innerHTML));
            });
			
			var fragment = document.createDocumentFragment();
            for (var i in optionsArr)
                fragment.appendChild(optionsArr[i]);
                
			document.getElementById("fuscabr-csnippet-html-select").appendChild(fragment);
            document.querySelector("#staticEditorPopup td[align='right']").addEventListener("click", fuscabr.csnippet.resetSelectedTemplates, true);

        });
    },

    createServerScriptInterface: function() {
		ajaxGet("resources/fuscabr/templates/internal/codeSnippetDiv.html", function(response) {
            var newHtml = response.replace("TEMPLATE", "script");

            var editor = document.getElementById("graphicRenderer_script");
            editor.innerHTML = editor.innerHTML.replace("<br>", newHtml);
            editor.querySelector("img").addEventListener("click", fuscabr.csnippet.loadServerScriptTemplate);

            var localizable = editor.querySelectorAll("*[data-i18n]");
		    for (var i of localizable) {
    			var message = i.getAttribute("data-i18n");
	    		if (fuscabr.common.i18n.csnippet[message])
		    		localizeElement(i, fuscabr.common.i18n.csnippet[message]);
		    }

            var optionsArr = new Array();
            optionsArr.push(document.createElement("option"));
            for (var i of fuscabr.csnippet.conf.serverScriptTemplates) {
                var option = document.createElement("option");
                option.value = i.file;
                option.innerHTML = fuscabr.common.i18n.csnippet[i.i18n] || i.desc;
                optionsArr.push(option);
            }
            optionsArr.sort(function(a, b) {
                return (a.innerHTML.localeCompare(b.innerHTML));
            });

            var fragment = document.createDocumentFragment();
            for (var i in optionsArr)
                fragment.appendChild(optionsArr[i]);
                
			document.getElementById("fuscabr-csnippet-script-select").appendChild(fragment);
            document.querySelector("#graphicRendererEditorPopup td[align='right']").addEventListener("click", fuscabr.csnippet.resetSelectedTemplates, true);

        });
    },

    resetSelectedTemplates: function() {
        var htmlSelect = document.getElementById("fuscabr-csnippet-html-select");
        var scriptSelect = document.getElementById("fuscabr-csnippet-script-select");
        htmlSelect.selectedIndex = 0;
        scriptSelect.selectedIndex = 0;
    },

    // Get module settings
    getSettings: function() {
        ajaxGetJson("resources/fuscabr/conf/modules/csnippet.json", function(response) {
            fuscabr.csnippet.conf = response;
            fuscabr.csnippet.init();
        });
    },
    
    // Init module
    init: function() {
        if (!fuscabr.csnippet.conf) {
            fuscabr.csnippet.getSettings();
        } else {
            //console.info("Code Snippet module loaded.");
            fuscabr.csnippet.createHtmlInterface();
            fuscabr.csnippet.createServerScriptInterface();
        }
    }
 }

fuscabr.csnippet.init();
