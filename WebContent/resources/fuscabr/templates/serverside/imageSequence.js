/* @license MIT */

// The figure description
var description = "Image sequence";

// Configure here all images of the sequence, and its values
var images = [
	{ src: "graphics/GreenThermo/thermometer0.jpg", initial_value: 0},
	{ src: "graphics/GreenThermo/thermometer1.jpg", initial_value: 10},
	{ src: "graphics/GreenThermo/thermometer2.jpg", initial_value: 20},
	{ src: "graphics/GreenThermo/thermometer3.jpg", initial_value: 30},
	{ src: "graphics/GreenThermo/thermometer4.jpg", initial_value: 40},
	{ src: "graphics/GreenThermo/thermometer5.jpg", initial_value: 50},
	{ src: "graphics/GreenThermo/thermometer6.jpg", initial_value: 60},
	{ src: "graphics/GreenThermo/thermometer7.jpg", initial_value: 70},
];

// Use this image as fallback
var image_fallback = "/ScadaBR/graphics/GreenThermo/thermometer0.jpg";




//
// DON'T CHANGE THE CODE BELOW
//




// Return variable
var s = "";

// Sort images by value (ascending)
images.sort(function(a, b) {
	return a.initial_value - b.initial_value;
});

for (var i = 0; i < images.length; i++) {
	var loop_value  = images[i].initial_value;
	var data_point_value = value;
	
	// Change the image source
	if (data_point_value >= loop_value)
		s = "<img src='" + images[i].src + "' ";
}

// Use fallback if no image has been selected
if (s.length == 0)
	s = "<img src='" + image_fallback + "' ";

// Show description (if exists)
if (description.length)
	s += "title='" + description + "'";

// Close <img> tag
s += ">"

return s;