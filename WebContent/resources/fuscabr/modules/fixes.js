/******************************************
 * FUScaBR - "Funções úteis para o ScadaBR"
 * Version 2.0 - License: MIT
 ******************************************/
 "use strict";

fuscabr.fixes = {
	// Prevent the Graphic View from being accidentally deleted 
	preventAccidentalViewDeleting: function() {
		var btnDelete = document.querySelector("input[name='delete']");
		btnDelete.type = "button";
		btnDelete.name = "delete-disabled";
		btnDelete.addEventListener("click", fuscabr.advanced.deleteView);
	},

	// Delete the Graphical View after confirmation 
	deleteView: function() {
		if (window.confirm("WARNING! \nYou are about to DELETE this Graphical View! \n\nContinue?")) {
			var viewForm = document.querySelector("form[name='view']");
			var hiddenInput = document.createElement("input");
			hiddenInput.type = "hidden";
			hiddenInput.name = "delete";
			hiddenInput.value = "Apagar";
			viewForm.appendChild(hiddenInput);
			viewForm.submit();
		}
	},

	// Change ScadaBR's "positionEditor()" function 
	changePositionEditor: function() {
		var positionEditor_backup = positionEditor;
		var positionEditor = this.positionEditor;
	},

	// Alternative function for the standard "positionEditor()"
	positionEditor: function (compId, editorId) {
		// (FUScaBR)
		// Position and display the renderer editor.
		var pDim = getNodeBounds($("c"+ compId));
		var editDiv = document.getElementById(editorId);
		editDiv.style.left = (pDim.x + 20) + "px";
		editDiv.style.top = (pDim.y + 10) + "px";
	},

	// Get module settings
    getSettings: function() {
		ajaxGetJson("resources/fuscabr/conf/modules/fixes.json", function(response) {
			fuscabr.fixes.conf = response;
			fuscabr.fixes.init();
		});
	},
	
	// Init module
	init: function() {
		if (!fuscabr.fixes.conf) {
			fuscabr.fixes.getSettings();
		} else {
			//console.info("Fixes module loaded.");
			if (fuscabr.fixes.conf.enablePreventViewDeleting)
            	fuscabr.fixes.preventAccidentalViewDeleting();
			
			if (fuscabr.fixes.conf.enableChangePositionEditor)
				fuscabr.fixes.changePositionEditor();
		}
	}
}

fuscabr.fixes.init();
