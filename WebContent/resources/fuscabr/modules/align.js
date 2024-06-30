/******************************************
 * FUScaBR - "Funções úteis para o ScadaBR"
 * Version 2.0 - License: MIT
 ******************************************/
"use strict";

fuscabr.align = {
	// This stores items selected in alignment process
	selectedItems: [],




/* 
 *	User interface methods
 */	 



	cannotDeleteWarning: function() {
		alert(fuscabr.common.i18n.align["cannot_delete_components"]);
	},
	
	createInterface: function() {	
		if(!document.getElementById("fuscabr-align")) {
			var gui = document.createElement("div");
			gui.id = "fuscabr-align";
			gui.className = "borderDiv";
				
			ajaxGet("resources/fuscabr/templates/internal/alignDiv.html", function(response) {
				gui.innerHTML = response;
				document.getElementById("viewContent").parentElement.parentElement.appendChild(gui);
				fuscabr.align.localizeInterface();
			});
		}
	},

	localizeInterface: function() {
		var localizable = document.querySelectorAll("#fuscabr-align *[data-i18n]");
		for (var i of localizable) {
			var message = i.getAttribute("data-i18n");
			if (fuscabr.common.i18n.align[message])
				localizeElement(i, fuscabr.common.i18n.align[message]);
		}
	},



/*
 *	Responsivity methods
 */



	// Create listeners for mouse responsivity
	createListeners: function() {
		var elements = document.querySelectorAll("#viewContent > div:not(.fuscabr-align):not(.windowDiv)");
		for (var i of elements) {
			// Create listeners
			i.addEventListener("mouseover", fuscabr.align.drawHoverBorder);
			i.addEventListener("mouseout", fuscabr.align.clearBorder);
			i.addEventListener("click", fuscabr.align.selectItem);
			i.classList.add("fuscabr-align");
		}
	},

	// Remove mouse listeners
	removeListeners: function() {	
		var elements = document.querySelectorAll("#viewContent > div.fuscabr-align");
		for (var i of elements) {
			// Remove listeners
			i.removeEventListener("mouseover", fuscabr.align.drawHoverBorder);
			i.removeEventListener("mouseout", fuscabr.align.clearBorder);
			i.removeEventListener("click", fuscabr.align.selectItem);
			
			// Remove borders
			i.style.margin = "";
			i.style.border = "";
			i.classList.remove("fuscabr-align");
		}
		
		// Remove anchor-specific listeners
		if (fuscabr.align.selectedItems.length) {
			this.selectedItems[0].removeEventListener("mouseup", fuscabr.align.getAnchorPosition);
			document.getElementById("fuscabr-anchor").remove();
		}
	},

	// Update listeners (create new ones with delay)
	updateListeners: function() {
		setTimeout(fuscabr.align.createListeners, 200);
	},

	// Create keyboard shortcuts for alignments
	alignKeyboardShortcuts: function() {
		if (event.code == "KeyA") {
			fuscabr.align.alignLeft();
		} else if (event.code == "KeyD") {
			fuscabr.align.alignRight();
		} else if (event.code == "KeyW") {
			fuscabr.align.alignTop();
		} else if (event.code == "KeyX") {
			fuscabr.align.alignBottom();
		} else if (event.code == "KeyF") {
			fuscabr.align.centerHorizontal();
		} else if (event.code == "KeyG") {
			fuscabr.align.centerVertical();
		} else if (event.code == "KeyR") {
			fuscabr.align.distributeHorizontal();
		} else if (event.code == "KeyT") {
			fuscabr.align.distributeVertical();
		} else if (event.code == "Escape") {
			fuscabr.align.stop();
		} else if (event.code == "KeyS") {
			fuscabr.align.restart();
		}
	},
	
	// Create keyboard shortcuts for anchor moving
	moveKeyboardShortcuts: function(e) {
		var offset = fuscabr.align.conf.moveAnchorOffset;
		if (event.code == "ArrowUp") {
			e.preventDefault();
			fuscabr.align.relativeMoveAnchor(0, -(offset));
		} else if (event.code == "ArrowDown") {
			fuscabr.align.relativeMoveAnchor(0, +(offset));
			event.preventDefault();
		} else if (event.code == "ArrowLeft") {
			fuscabr.align.relativeMoveAnchor(-(offset), 0);
			event.preventDefault();
		} else if (event.code == "ArrowRight") {
			fuscabr.align.relativeMoveAnchor(+(offset), 0);
			event.preventDefault();
		}
	},

	// Add or remove keyboard moving listeners
	enableKeyboardMoving: function(enable) {
		if (enable) {
			window.addEventListener("keydown", fuscabr.align.moveKeyboardShortcuts);
		} else {
			window.removeEventListener("keydown", fuscabr.align.moveKeyboardShortcuts);
		}
	},
	
	// Draw a border when mouse is over a DIV
	drawHoverBorder: function() {
		var conf = fuscabr.align.conf;
		this.style.margin = "-2px";
		this.style.border = "2px solid " + conf.hoverBorderColor;
	},

	// Clear the border when mouse leaves a DIV
	clearBorder: function() {
		this.style.margin = "";
		this.style.border = "";
	},

	// Select a DIV when clicked
	selectItem: function() {
		// Remove listeners from that element
		this.removeEventListener("mouseover", fuscabr.align.drawHoverBorder);
		this.removeEventListener("mouseout", fuscabr.align.clearBorder);
		this.removeEventListener("click", fuscabr.align.selectItem);
		
		// Add element to "selectedItems" array
		fuscabr.align.selectedItems.push(this);
		
		// Create a new border around the element
		var conf = fuscabr.align.conf;
		if (fuscabr.align.selectedItems.length == 1) {
			// Anchor element
			var label = document.createElement("span");
			label.id = "fuscabr-anchor";
			label.innerHTML = fuscabr.common.i18n.align["anchor"] || "Anchor";
			label.style = "text-transform: uppercase; font-size: 9px; z-index: 20; \
			               position: absolute; top: -15px; color: " + conf.anchorBorderColor;
			this.appendChild(label);
			this.style.margin = "-2px";
			this.style.border = "2px solid " + conf.anchorBorderColor;
			this.addEventListener("mouseup", fuscabr.align.getAnchorPosition);
			fuscabr.align.getAnchorPosition();
		} else {
			// Other elements
			this.style.margin = "-2px";
			this.style.border = "2px solid " + conf.elementBorderColor; // Orange border for other elements
		}
	},



/*
 *	Align/move methods
 */
 
 
 
	// Get anchor's (first selected element) position
	getAnchorPosition: function() {
		// Get anchor's CSS position
		var xPos = fuscabr.align.selectedItems[0].style.left.replace("px", "");
		var yPos = fuscabr.align.selectedItems[0].style.top.replace("px", "");
		
		// Update GUI position values
		document.getElementById("fuscabr-align-x-anchor").value = xPos;
		document.getElementById("fuscabr-align-y-anchor").value = yPos;
		
		// Return current position
		return [xPos, yPos];
	},

	// Move anchor absolutely (by GUI)
	absoluteMoveAnchor: function() {
		// Get position specified in GUI
		var xPos = document.getElementById("fuscabr-align-x-anchor").value;
		var yPos = document.getElementById("fuscabr-align-y-anchor").value;
		
		// Update position
		fuscabr.align.selectedItems[0].style.left = Math.round(xPos) + "px";
		fuscabr.align.selectedItems[0].style.top = Math.round(yPos) + "px";
		updateViewComponentLocation(fuscabr.align.selectedItems[0].id);
	},

	// Move anchor relatively (by keyboard)
	relativeMoveAnchor: function(xOffset, yOffset) {
		// Get actual position
		var pos = fuscabr.align.getAnchorPosition();
		
		// Calc the offset and move anchor
		if (xOffset) {
			var newX = pos[0];
			newX = pos[0] - (pos[0] % xOffset) + xOffset;
			fuscabr.align.selectedItems[0].style.left = Math.round(newX) + "px";
		}
		if (yOffset) {
			var newY = pos[1];
			newY = pos[1] - (pos[1] % yOffset) + yOffset;
			fuscabr.align.selectedItems[0].style.top = Math.round(newY) + "px";
		}
		
		// Update positon
		fuscabr.align.getAnchorPosition();
		updateViewComponentLocation(fuscabr.align.selectedItems[0].id);
	},

	// Align elements to the left
	alignLeft: function() {
		// Use anchor position as reference
		var x = this.selectedItems[0].style.left;
		// Copy the anchor position
		for (var i of this.selectedItems) {
			i.style.left = x;
			updateViewComponentLocation(i.id);
		}
	},

	// Align elements to the top
	alignTop: function() {
		// Use anchor position as reference
		var y = this.selectedItems[0].style.top;
		// Copy the anchor position
		for (var i of this.selectedItems) {
			i.style.top = y;
			updateViewComponentLocation(i.id);
		}
	},

	// Align elements to the right
	alignRight: function() {
		// Use anchor position as reference
		var xLeft = Number(this.selectedItems[0].style.left.replace("px", ""));
		var width = Number(this.selectedItems[0].clientWidth);
		var xRight = xLeft + width;
		
		// Align other elements relative to the anchor
		for (var i of this.selectedItems) {
			var loopXLeft = Number(i.style.left.replace("px", ""));
			var loopWidth = Number(i.clientWidth);
			var loopXRight = loopXLeft + loopWidth;
			
			var newX = loopXLeft + (xRight - loopXRight);
			i.style.left = Math.round(newX) + "px";
			updateViewComponentLocation(i.id);
		}
	},

	// Align elements to the bottom
	alignBottom: function() {
		// Use anchor position as reference
		var yTop = Number(this.selectedItems[0].style.top.replace("px", ""));
		var height = Number(this.selectedItems[0].clientHeight);
		var yBottom = yTop + height;
		
		// Align other elements relative to the anchor
		for (var i of this.selectedItems) {
			var loopYTop = Number(i.style.top.replace("px", ""));
			var loopHeight = Number(i.clientHeight);
			var loopYBottom = loopYTop + loopHeight;
			
			var newY = loopYTop + (yBottom - loopYBottom);
			i.style.top = Math.round(newY) + "px";
			updateViewComponentLocation(i.id);
		}
	},

	// Center elements horizontally
	centerHorizontal: function() {
		// Use anchor position as reference
		var xLeft = Number(this.selectedItems[0].style.left.replace("px", ""));
		var width = Number(this.selectedItems[0].clientWidth);
		var xCenter = xLeft + (width / 2);
		
		// Center other elements relative to the anchor
		for (var i of this.selectedItems) {
			var loopXLeft = Number(i.style.left.replace("px", ""));
			var loopWidth = Number(i.clientWidth);
			var loopXCenter = loopXLeft + (loopWidth / 2);
			
			var newX = loopXLeft + (xCenter- loopXCenter);
			i.style.left = Math.round(newX) + "px";
			updateViewComponentLocation(i.id);
		}
	},

	// Center elements vertically
	centerVertical: function() {
		// Use anchor position as reference
		var yTop = Number(this.selectedItems[0].style.top.replace("px", ""));
		var height = Number(this.selectedItems[0].clientHeight);
		var yCenter = yTop + (height / 2);
		
		// Center other elements relative to the anchor
		for (var i of this.selectedItems) {
			var loopYTop = Number(i.style.top.replace("px", ""));
			var loopHeight = Number(i.clientHeight);
			var loopYCenter = loopYTop + (loopHeight / 2);
			
			var newY = loopYTop + (yCenter - loopYCenter);
			i.style.top = Math.round(newY) + "px";
			updateViewComponentLocation(i.id);
		}
	},

	// Distribute elements horizontally
	distributeHorizontal: function() {
		// Ignore if less than 3 selected items
		if (this.selectedItems.length < 3) {
			return;
		}
		
		// Order elements by position
		var copyArray = this.selectedItems.slice();
		copyArray.sort( function (a, b) {
			var aa = Number(a.style.left.replace("px", ""));
			var bb = Number(b.style.left.replace("px", ""));
			return (aa > bb ? 1 : -1);
		});
		
		// Calc free space between elements
		var foo = copyArray.length - 1;
		var minX = Number(copyArray[0].style.left.replace("px", ""));
		var maxX = Number(copyArray[foo].style.left.replace("px", ""));
		var space = (maxX - minX);
		
		for (var i = 0; i < foo; i++) {
			space -= copyArray[i].clientWidth;
		}
		space = space / foo;
		
		// Position elements equally spaced from each other
		for (var i = 1; i < foo; i++) {
			var pastX = Number(copyArray[(i - 1)].style.left.replace("px", ""));
			var pastWidth = Number(copyArray[(i - 1)].clientWidth);
			
			var newX = pastX + pastWidth + space;
			copyArray[i].style.left = Math.round(newX) + "px";
			updateViewComponentLocation(copyArray[i].id);
			
			// Update anchor position
			this.getAnchorPosition();
		}
		
	},

	// Distribute elements vertically
	distributeVertical: function() {
		// Ignore if less than 3 selected items
		if (this.selectedItems.length < 3) {
			return;
		}
		
		// Order elements by position
		var copyArray = this.selectedItems.slice();
		copyArray.sort( function (a, b) {
			var aa = Number(a.style.top.replace("px", ""));
			var bb = Number(b.style.top.replace("px", ""));
			return (aa > bb ? 1 : -1);
		});
		
		// Calc free space between elements
		var foo = copyArray.length - 1;
		var minY = Number(copyArray[0].style.top.replace("px", ""));
		var maxY = Number(copyArray[foo].style.top.replace("px", ""));
		var space = (maxY - minY);
		
		for (var i = 0; i < foo; i++) {
			space -= copyArray[i].clientHeight;
		}
		space = space / foo;
		
		// Position elements equally spaced from each other
		for (var i = 1; i < foo; i++) {
			var pastY = Number(copyArray[(i - 1)].style.top.replace("px", ""));
			var pastHeight = Number(copyArray[(i - 1)].clientHeight);
			
			var newY = pastY + pastHeight + space;
			copyArray[i].style.top = Math.round(newY) + "px";
			updateViewComponentLocation(copyArray[i].id);
			
			// Update anchor position
			this.getAnchorPosition();
		}
		
	},


	
/*
 *  Main methods
 */



	// Start alignment mode
	start: function() {
		// Create several listeners
		document.querySelector("img[onclick='addViewComponent()']").addEventListener("click", fuscabr.align.updateListeners);
		document.body.addEventListener("keyup", fuscabr.align.alignKeyboardShortcuts);
		this.createListeners();
		// Clear selected items
		this.selectedItems = [];
		// Disable delete elements ability
		this.deleteComponentBackup = deleteViewComponent;
		deleteViewComponent = this.cannotDeleteWarning;
		// Show all GUI controls
		document.getElementById("fuscabr-align-start-stop").value = fuscabr.common.i18n.align["stop_alignment"];
		document.getElementById("fuscabr-align-start-stop").setAttribute("onclick", "fuscabr.align.stop()");
		document.getElementById("fuscabr-align-controls").style.display = "";
	},

	// Stop alignment mode
	stop: function() {
		// Remove all listeners
		document.querySelector("img[onclick='addViewComponent()']").removeEventListener("click", fuscabr.align.updateListeners);
		document.body.removeEventListener("keyup", fuscabr.align.alignKeyboardShortcuts);
		this.removeListeners();
		// Disable moving by keyboard arrows
		this.enableKeyboardMoving(false);
		document.getElementById("fuscabr-keyboard-moving").checked = false;
		// Clear selected items
		this.selectedItems = [];
		// Enable delete elements ability
		deleteViewComponent = this.deleteComponentBackup;
		// Hide GUI alignment controls
		document.getElementById("fuscabr-align-start-stop").value = fuscabr.common.i18n.align["start_alignment"];
		document.getElementById("fuscabr-align-start-stop").setAttribute("onclick", "fuscabr.align.start()");
		document.getElementById("fuscabr-align-controls").style.display = "none";
	},

	// Restart alignment (clear selection)
	restart: function() {
		this.removeListeners();
		this.createListeners();
		this.selectedItems = [];
		document.getElementById("fuscabr-align-x-anchor").value = "";
		document.getElementById("fuscabr-align-y-anchor").value = "";
	},

	// Get module settings
	getSettings: function() {
		ajaxGetJson("resources/fuscabr/conf/modules/align.json", function(response) {
			fuscabr.align.conf = response;
			fuscabr.align.init();
		});
	},
	
	// Init module
	init: function() {
		if (!fuscabr.align.conf) {
			fuscabr.align.getSettings();
		} else {
			fuscabr.align.createInterface();
			//console.info("Align module loaded.");
		}
	}
}

fuscabr.align.init();
