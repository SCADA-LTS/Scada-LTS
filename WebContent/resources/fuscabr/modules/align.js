/******************************************
 * FUScaBR - "Funções úteis para o ScadaBR"
 * License: MIT
 ******************************************/
"use strict";

fuscabr.align = {
	selectedItems: [],
	lastAnchorPosition: {},
	currentAnchorPosition: {},
	elementsFollowAnchor: false,
	

	/**
	 *	Interface-related methods
	 */


	// Create the Graphical User Interface (GUI) for alignments
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

	// Show (or hide) alignment GUI
	showInterface: function(show) {
		if (show) {
			document.getElementById("fuscabr-align-start-stop").value = fuscabr.common.i18n.align["stop_alignment"];
			document.getElementById("fuscabr-align-start-stop").setAttribute("onclick", "fuscabr.align.stop()");
			document.getElementById("fuscabr-align-controls").style.display = "";
		} else {
			document.getElementById("fuscabr-align-start-stop").value = fuscabr.common.i18n.align["start_alignment"];
			document.getElementById("fuscabr-align-start-stop").setAttribute("onclick", "fuscabr.align.start()");
			document.getElementById("fuscabr-align-controls").style.display = "none";
		}
	},

	// Localize the alignment GUI
	localizeInterface: function() {
		var localizable = document.querySelectorAll("#fuscabr-align *[data-i18n]");
		for (var i of localizable) {
			var message = i.getAttribute("data-i18n");
			if (fuscabr.common.i18n.align[message])
				localizeElement(i, fuscabr.common.i18n.align[message]);
		}
	},

	// Prevent View elements from being deleted while alignment mode is active
	preventDeleteElements: function(enable) {
		if (enable) {
			this.deleteViewComponentBackup = deleteViewComponent;
			deleteViewComponent = function() {
				alert(fuscabr.common.i18n.align["cannot_delete_components"]);
			}
		} else {
			deleteViewComponent = this.deleteViewComponentBackup;
			this.deleteViewComponentBackup = null;
		}
	},


	/**
	 *	"Selection Mode" related methods
	 */


	startSelectionMode: function() {
		// This trigger updates selectable elements after add
		// new elements to the Graphical View
		if (!document.getElementById("fuscabr-update-trigger")) {
			var updateTrigger = document.querySelector("img[onclick='addViewComponent()']");
			updateTrigger.addEventListener("click", fuscabr.align.updateSelectionMode);
			updateTrigger.id = "fuscabr-update-trigger";
		}
	
		// Make all View components selectable
		var elements = document.querySelectorAll("#viewContent > div:not(.fuscabr-align):not(.windowDiv)");
		for (var element of elements) {
			this.hoverResponsivity(element, true);
			element.classList.add("fuscabr-align");

			// Create a div to prevent accidental clicks
			var pcDiv = document.createElement("div");
			pcDiv.classList.add("fuscabr-align-preventclick");
			pcDiv.style = "position: absolute; top: 0px; left: 0px; width: 100%; height: 100%;";
			element.appendChild(pcDiv);
		}
	},

	stopSelectionMode: function() {	
		// Remove update trigger
		var updateTrigger = document.getElementById("fuscabr-update-trigger");
		updateTrigger.removeEventListener("click", fuscabr.align.updateSelectionMode);
		updateTrigger.id = "";

		// Make all View components not selectable
		var elements = document.querySelectorAll("#viewContent > div.fuscabr-align");
		for (var element of elements) {
			this.hoverResponsivity(element, false);
			element.classList.remove("fuscabr-align");

			// Remove accidental click protection
			element.querySelector(".fuscabr-align-preventclick").remove();
		}
		
		// Unselect previously selected items
		for (var i = 0; i < this.selectedItems.length; i++) {
			if (i == 0)
				this.unmakeAnchor(this.selectedItems[i]);
			else
				this.unmakeSelected(this.selectedItems[i]);
		}
		this.selectedItems = [];
	},

	updateSelectionMode: function() {
		setTimeout(function() {
			fuscabr.align.startSelectionMode.call(fuscabr.align);
		}, 200);
	},

	// Enable or disable responsiveness to mouse clicks or hovering
	hoverResponsivity: function(element, enable) {
		if (enable) {
			element.addEventListener("mouseover", fuscabr.align.drawHoverBorder);
			element.addEventListener("mouseout", fuscabr.align.clearHoverBorder);
			element.addEventListener("click", fuscabr.align.selectItem);
		} else {
			element.removeEventListener("mouseover", fuscabr.align.drawHoverBorder);
			element.removeEventListener("mouseout", fuscabr.align.clearHoverBorder);
			element.removeEventListener("click", fuscabr.align.selectItem);
			// Clear remanescent borders
			element.style.margin = "";
			element.style.border = "";
		}
	},

	drawHoverBorder: function() {
		var conf = fuscabr.align.conf;
		this.style.margin = "-2px";
		this.style.border = "2px solid " + conf.hoverBorderColor;
	},

	clearHoverBorder: function() {
		this.style.margin = "";
		this.style.border = "";
	},


	/**
	 *	Element selection and anchor-related methods
	 *
	 *	In FUScaBR Align module, the "anchor element" is always
	 *	the first selected element. This element is used as a
	 *	reference to alignment methods (except for "distribution").
	 */


	// Select elements that can be aligned
	selectItem: function() {
		fuscabr.align.selectedItems.push(this);

		// Remove responsivity
		fuscabr.align.hoverResponsivity(this, false);

		// Select the element
		if (fuscabr.align.selectedItems.length == 1)
			fuscabr.align.makeAnchor(this);
		else
			fuscabr.align.makeSelected(this);
	},

	makeSelected: function(element) {
		var conf = fuscabr.align.conf;
		element.style.margin = "-2px";
		element.style.border = "2px solid " + conf.elementBorderColor;
	},

	unmakeSelected: function(element) {
		element.style.margin = "";
		element.style.border = "";
	},

	makeAnchor: function(element) {
		var conf = fuscabr.align.conf;
		// Anchor borders
		element.style.margin = "-2px";
		element.style.border = "2px solid " + conf.anchorBorderColor;
		// Anchor label
		var label = document.createElement("span");
		label.id = "fuscabr-anchor";
		label.innerHTML = fuscabr.common.i18n.align["anchor"] || "Anchor";
		label.style = "text-transform: uppercase; font-size: 9px; z-index: 20; \
						position: absolute; top: -15px; color: " + conf.anchorBorderColor;
		element.appendChild(label);
		// Anchor listeners
		element.addEventListener("mouseup", fuscabr.align.updateAnchorPosition);
		
		fuscabr.align.updateAnchorPosition(true);
	},

	unmakeAnchor: function(element) {
		// Remove borders
		this.unmakeSelected(element);
		// Remove label
		document.getElementById("fuscabr-anchor").remove();
		// Remove listeners
		element.removeEventListener("mouseup", fuscabr.align.updateAnchorPosition);
		// Clear anchor position in GUI
		document.getElementById("fuscabr-align-x-anchor").value = "";
		document.getElementById("fuscabr-align-y-anchor").value = "";
	},	

	enableAnchorFollowing: function(enable) {
		if (enable) {
			this.elementsFollowAnchor = true;
		} else {
			this.elementsFollowAnchor = false;
			document.getElementById("fuscabr-anchor-following").checked = false;
		}
	},

	// Get current position of anchor element
	getAnchorPosition: function() {
		// Get anchor's CSS position
		var xPos = fuscabr.align.selectedItems[0].style.left.replace("px", "");
		var yPos = fuscabr.align.selectedItems[0].style.top.replace("px", "");
		
		return {x: xPos, y: yPos};
	},

	// Update current position of anchor element
	updateAnchorPosition: function() {
		// Get anchor's CSS position
		var pos = fuscabr.align.getAnchorPosition();
		
		if (fuscabr.align.currentAnchorPosition != {})
			fuscabr.align.lastAnchorPosition = fuscabr.align.currentAnchorPosition;

		fuscabr.align.currentAnchorPosition = pos;

		// Update GUI position values
		document.getElementById("fuscabr-align-x-anchor").value = pos.x;
		document.getElementById("fuscabr-align-y-anchor").value = pos.y;

		updateViewComponentLocation(fuscabr.align.selectedItems[0].id);

		if (fuscabr.align.elementsFollowAnchor) {
			fuscabr.align.followAnchor();
		}
	},


	/**
	 *	Keyboard shortcut methods
	 */


	// Enable/disable shortcuts to move the anchor
	enableMovingShortcuts: function(enable) {
		if (enable) {
			window.addEventListener("keydown", fuscabr.align.moveKeyboardShortcuts);
		} else {
			window.removeEventListener("keydown", fuscabr.align.moveKeyboardShortcuts);
			document.getElementById("fuscabr-keyboard-moving").checked = false;
		}
	},

	// Enable/disable shortcuts to align elements
	enableAlignShortcuts: function(enable) {
		if (enable)
			document.body.addEventListener("keyup", fuscabr.align.alignKeyboardShortcuts);
		else
			document.body.removeEventListener("keyup", fuscabr.align.alignKeyboardShortcuts);
	},

	// Define keyboard shortcuts to alignment functions
	alignKeyboardShortcuts: function(evt) {
		if (evt.code == "KeyA") {
			fuscabr.align.alignLeft();
		} else if (evt.code == "KeyD") {
			fuscabr.align.alignRight();
		} else if (evt.code == "KeyW") {
			fuscabr.align.alignTop();
		} else if (evt.code == "KeyX") {
			fuscabr.align.alignBottom();
		} else if (evt.code == "KeyF") {
			fuscabr.align.centerHorizontal();
		} else if (evt.code == "KeyG") {
			fuscabr.align.centerVertical();
		} else if (evt.code == "KeyR") {
			fuscabr.align.distributeHorizontal();
		} else if (evt.code == "KeyT") {
			fuscabr.align.distributeVertical();
		} else if (evt.code == "Escape") {
			fuscabr.align.stop();
		} else if (evt.code == "KeyS") {
			fuscabr.align.restart();
		}
	},
	
	// Define shortcuts to use keyboard arrows in anchor moving
	moveKeyboardShortcuts: function(evt) {
		var offset = fuscabr.align.conf.moveAnchorOffset;
		if (evt.code == "ArrowUp") {
			evt.preventDefault();
			fuscabr.align.relativeMoveAnchor(0, -(offset));
		} else if (evt.code == "ArrowDown") {
			fuscabr.align.relativeMoveAnchor(0, +(offset));
			evt.preventDefault();
		} else if (evt.code == "ArrowLeft") {
			fuscabr.align.relativeMoveAnchor(-(offset), 0);
			evt.preventDefault();
		} else if (evt.code == "ArrowRight") {
			fuscabr.align.relativeMoveAnchor(+(offset), 0);
			evt.preventDefault();
		}
	},
 

	/**
	 *	Moving/align methods
	 */
 

	// Move all selected elements to follow anchor displacement
	followAnchor: function() {
		var cap = this.currentAnchorPosition;
		var lap = this.lastAnchorPosition;
		
		var xOffset = cap.x - lap.x;
		var yOffset = cap.y - lap.y;

		var elements = fuscabr.align.selectedItems;

		for (var i = 1; i < elements.length; i++) {
			var currentX = parseInt(elements[i].style.left.replace("px", ""));
			var currentY = parseInt(elements[i].style.top.replace("px", ""));

			elements[i].style.left = (currentX + xOffset) + "px";
			elements[i].style.top =  (currentY + yOffset) + "px";

			updateViewComponentLocation(elements[i].id);
		}

	},

	// Move anchor to an absolute coordinate (called from GUI)
	absoluteMoveAnchor: function() {
		// Get position specified in GUI
		var xPos = document.getElementById("fuscabr-align-x-anchor").value;
		var yPos = document.getElementById("fuscabr-align-y-anchor").value;
		
		// Update position
		fuscabr.align.selectedItems[0].style.left = Math.round(xPos) + "px";
		fuscabr.align.selectedItems[0].style.top = Math.round(yPos) + "px";
		
		fuscabr.align.updateAnchorPosition();
	},

	// Move anchor relatively (called from keyboard arrows shortcut)
	relativeMoveAnchor: function(xOffset, yOffset) {
		// Get actual position
		var pos = fuscabr.align.getAnchorPosition();
		
		// Calc the offset and move anchor
		if (xOffset) {
			var newX = pos.x - (pos.x % xOffset) + xOffset;
			fuscabr.align.selectedItems[0].style.left = Math.round(newX) + "px";
		}
		if (yOffset) {
			var newY = pos.y - (pos.y % yOffset) + yOffset;
			fuscabr.align.selectedItems[0].style.top = Math.round(newY) + "px";
		}
		
		fuscabr.align.updateAnchorPosition();
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
		
		// Disable anchor following
		var followingDisabled = false;
		if (this.elementsFollowAnchor) {
			this.elementsFollowAnchor = false;
			followingDisabled = true;
		}

		// Position elements equally spaced from each other
		for (var i = 1; i < foo; i++) {
			var pastX = Number(copyArray[(i - 1)].style.left.replace("px", ""));
			var pastWidth = Number(copyArray[(i - 1)].clientWidth);
			
			var newX = pastX + pastWidth + space;
			copyArray[i].style.left = Math.round(newX) + "px";
			updateViewComponentLocation(copyArray[i].id);
			
			// Update anchor position
			this.updateAnchorPosition();
		}

		// Re-enable anchor following
		if (followingDisabled) {
			this.elementsFollowAnchor = true;
			followingDisabled = false;
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
		
		// Disable anchor following
		var followingDisabled = false;
		if (this.elementsFollowAnchor) {
			this.elementsFollowAnchor = false;
			followingDisabled = true;
		}

		// Position elements equally spaced from each other
		for (var i = 1; i < foo; i++) {
			var pastY = Number(copyArray[(i - 1)].style.top.replace("px", ""));
			var pastHeight = Number(copyArray[(i - 1)].clientHeight);
			
			var newY = pastY + pastHeight + space;
			copyArray[i].style.top = Math.round(newY) + "px";
			updateViewComponentLocation(copyArray[i].id);
			
			// Update anchor position
			this.updateAnchorPosition();
		}
		
		// Re-enable anchor following
		if (followingDisabled) {
			this.elementsFollowAnchor = true;
			followingDisabled = false;
		}
	},


	/**
	 *	Main methods
	 */


	// Start alignment
	start: function() {
		this.startSelectionMode();
		this.enableAlignShortcuts(true);
		this.preventDeleteElements(true);
		this.showInterface(true);
	},

	// Stop alignment
	stop: function() {
		this.stopSelectionMode();
		this.enableAlignShortcuts(false);
		this.enableMovingShortcuts(false);
		this.enableAnchorFollowing(false);
		this.preventDeleteElements(false);
		this.showInterface(false);
	},

	// Restart alignment (clear selection)
	restart: function() {
		this.stopSelectionMode();
		this.startSelectionMode();
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
