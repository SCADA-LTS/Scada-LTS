/*****************************************************************
 * Custom client side scripts for graphical views
 ****************************************************************/

/*****************************************************************
 * 06.02.2015 Pawel
 * Script for render color changing progress bar in predefined 
 * canvas.
 * @arg0 canvas element used to draw on
 * @arg1 minimal value
 * @arg2 value below which bar is red
 * @arg3 value below which bar is yellow
 * @arg4 maximal value below which bar is green
 * @arg5 actual value (in most cases should be set to this.value)
 * @arg6 boolean; if true bar is vertical
 * 		 if false bar is horizontal 
 ****************************************************************/
function renderBar() {
		var canvas = document.getElementById(arguments[0]);
		var height = canvas.offsetHeight;
		var width = canvas.offsetWidth;
		var min = arguments[1];
		var step1 = arguments[2];
		var step2 = arguments[3];
		var max = arguments[4];
		var value = arguments[5];
		var isVertical = arguments[6];

		var context = canvas.getContext("2d");
		
		context.beginPath();
		if(isVertical){
			var val = (value-min)*height/(max-min);
			context.rect(0, height-val, width, val);
		} else {
			var val = (value-min)*width/(max-min);
			context.rect(0, 0, val, height);
		}
		if(value<step1){
			context.fillStyle = "#FF0000";
		} else if(value<step2){
			context.fillStyle = "#FFFF00";
		} else {
			context.fillStyle = "#00FF00";
		}
		context.fill();
}

/*****************************************************************
 * 20.02.2015 Pawel
 * Script for hiding and unhiding specified images  
 * @arg0 set of components to hide/unhide
 ****************************************************************/
function showAndHide() {
	var toHide = isMoreImagesVisible(arguments[0]);
	var componentsList = arguments[0];
	if(toHide){
		for(var i=0; i<componentsList.length; i++){
			componentsList[i].style.visibility = 'hidden';
		}
	} else {
		for(var i=0; i<componentsList.length; i++){
			componentsList[i].style.visibility = 'visible';
		}
	}
}

/*****************************************************************
 * 20.02.2015 Pawel
 * Script for checking if there are more visible elements than
 * the hidden ones in given set 
 * @arg0 set of components
 * @return boolean if there are more visible elements
 ****************************************************************/
function isMoreImagesVisible(){
	var componentsList = arguments[0];
	var visible = 0, hidden = 0;
	for(var i=0; i<componentsList.length; i++){
		if(componentsList[i].style.visibility == "hidden"){
			++hidden;
		}
		else {
			++visible;
		}
	}
	return visible >= hidden;
}

/*****************************************************************
 * 20.02.2015 Pawel
 * Script for finding images which has specified text in src
 * attribute in document
 * @arg0 text that should be in src of component
 * @return set of components with given text in src attribute
 ****************************************************************/
function findImagesBySrc() {
	var images = Array.prototype.slice.apply(document.getElementsByTagName('img'));
	var resultImages = [];
	for(var i=0; i<images.length; i++){
		if(images[i].src.indexOf(arguments[0]) !== -1){
			resultImages.push(images[i]);
		}
	}
	return resultImages;
}

/*****************************************************************
 * 20.02.2015 Pawel
 * Script for finding broken images in document
 * @return set of broken images
 ****************************************************************/
function findBrokenImages() {
	var images = Array.prototype.slice.apply(document.getElementsByTagName('img'));
	var resultImages = [];
	for(var i=0; i<images.length; i++){
		if(!images[i].complete || (typeof images[i].naturalWidth != "undefined" && images[i].naturalWidth == 0)){
			resultImages.push(images[i]);
		}
	}
	return resultImages;
}