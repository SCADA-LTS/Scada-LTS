/******************************************
 * FUScaBR - "Funções úteis para o ScadaBR"
 * Version 2.0 - License: MIT
 ******************************************/
 "use strict";

fuscabr.chart = {
	createdCharts: [],

	// Main loop
	loop: function() {
		// Create new charts, if needed
		var inputData = document.querySelectorAll("div:not(.fuscabr-chart-container) > div > input.fuscabr-chart-data");
		cont = inputData.length;
		for (var i = 0; i < cont; i++) {
			fuscabr.chart.createNewCharts(inputData[i]);
		}

		// Update already created charts
		var cont = fuscabr.chart.createdCharts.length;
		for (var i = 0; i < cont; i++) {
			var err = fuscabr.chart.updateCharts(fuscabr.chart.createdCharts[i]);
			if (err == -1) {
				fuscabr.chart.createdCharts.splice(i, 1);
			}
		}
		
	},

	// Create new charts
	createNewCharts: function(element) {
		// Load Chart.js if not present
		if (typeof Chart == "undefined") {
			this.loadChartJs();
			return;
		}

		// Create HTML elements and get chart data
		var formatedData = this.formatData(element.value);
		var container = element.parentElement.parentElement;
		var conf = formatedData[0];
		var newCanvas = document.createElement("canvas");
		var newDiv = document.createElement("div");
		
		// Get time format options for time-based charts
		var timeOptions = this.conf.timeOptions;
		
		// Add elements to the page structure
		newCanvas.classList.add("fuscabr-chart-canvas");
		newDiv.style = "position: relative; height: " + conf.height + "px; width: " + conf.width + "px;";
		newDiv.appendChild(newCanvas);
		container.appendChild(newDiv);
		container.classList.add("fuscabr-chart-container");
		
		// Create a chart
		var ctx = newCanvas.getContext("2d");
		var chart = new Chart(ctx, {
			type: conf.type,
			data: conf.data
		});
		chart.options.elements.line.tension = 0;
		chart.options.maintainAspectRatio = false;
		
		// Adjust chart parameters
		if (conf.beginAtZero == true) {
			chart.options.scales.yAxes[0].ticks.beginAtZero = true;
		}
		if (conf.animations == false) {
			chart.options.animation.duration = 0;
		}
		if (conf.timeBased == true) {
			chart.options.scales.xAxes[0].type = "time";
			chart.options.scales.xAxes[0].time = timeOptions;
		} else {
			chart.options.scales.xAxes[0].ticks.display = false;
			chart.options.scales.xAxes[0].gridLines.drawTicks = false;
		}
		if (conf.showTitle == true) {
			chart.options.title.display = true;
			chart.options.title.text = conf.title;
		}

		if (this.conf.enableDatapointWarnings) {
			this.invalidDatapointWarning(container, formatedData[1]);
		}

		// Update chart and save it in "createdCharts" array
		chart.update();
		fuscabr.chart.createdCharts.push(chart);
	},

	// Update already created charts
	updateCharts: function(element) {
		var container = element.canvas.parentElement.parentElement;
		var formatedData = new Array();
		var conf = {};
		// Destroy chart if server script goes missing 
		try {
			formatedData = this.formatData(container.querySelector(".fuscabr-chart-data").value);
			conf = formatedData[0];
		} catch {
			fuscabr.chart.destroyChart(element);
			return -1;
		}
		
		// Destroy chart if chart type changes (to recreate it later) 
		if (element.config.type != conf.type) {
			fuscabr.chart.destroyChart(element);
			return -1;
		}

		// Destroy chart if scale type changes (to recreate it later)
		if (element.options.scales.xAxes[0]) {
			var isTimeInChart = element.options.scales.xAxes[0].type == "time";
			var isTimeInConf = conf.timeBased;
			if (isTimeInChart != isTimeInConf) {
				fuscabr.chart.destroyChart(element);
				return -1;
			}
		}
		
		// Destroy chart if more series are added
		if (element.data.datasets.length != conf.data.datasets.length) {
			fuscabr.chart.destroyChart(element);
			return -1;
		}
		
		// Update chart height/width
		element.canvas.parentElement.style.height = conf.height + "px";
		element.canvas.parentElement.style.width = conf.width + "px";
		
		// Update chart parameters
		var changed = false;
		var foo = element.options.scales.yAxes[0].ticks.beginAtZero;
		if (foo != conf.beginAtZero) {
			element.options.scales.yAxes[0].ticks.beginAtZero = conf.beginAtZero;
			changed = true;
		}
		foo = (element.options.animation.duration > 0);
		if (foo != conf.animations) {
			element.options.animation.duration = (conf.animations == true) ? 1000 : 0;
			changed = true;
		}
		foo = element.options.title.display;
		if (foo != conf.showTitle) {
			element.options.title.display = conf.showTitle;
			element.options.title.text = conf.title;
			changed = true;
		}
		
		// Update numeric chart data
		var size = conf.data.datasets.length;	
		for (var i = 0; i < size; i++) {
			var chartData = element.data.datasets[i].data;
			var newData = conf.data.datasets[i].data;
			if (JSON.stringify(chartData) != JSON.stringify(newData)) {
				element.data.datasets[i].data = newData;
				changed = true;
			}
			
			chartData = element.data.datasets[i].label;
			newData = conf.data.datasets[i].label;
			if (chartData != newData) {
				element.data.datasets[i].label = newData;
				changed = true;
			}
			
			chartData = element.data.datasets[i].backgroundColor;
			newData = conf.data.datasets[i].backgroundColor;
			if (chartData != newData) {
				element.data.datasets[i].backgroundColor = newData;
				element.data.datasets[i].borderColor = newData;
				changed = true;
			}
			
		}
		
		if (this.conf.enableDatapointWarnings) {
			this.invalidDatapointWarning(container, formatedData[1]);
		}

		// Redraw chart if there are changes
		if (changed == true) {
			element.update();
		}

	},

	// Destroy charts when needed
	destroyChart: function(element) {
		// Get chart parents
		var parent = element.canvas.parentElement;
		var container = parent.parentElement;
		
		// Destroy chart an its parents
		container.classList.remove("fuscabr-chart-container");
		element.destroy();
		parent.remove();
	},

	// Create an warning about incompatible data recieved
	invalidDatapointWarning: function(container, message) {
		if (!container.querySelector(".fuscabr-chart-warning")) {
			var warn = document.createElement("span");
			warn.className = "fuscabr-chart-warning";
			warn.innerHTML = message;
			container.appendChild(warn);
		} else {
			container.querySelector(".fuscabr-chart-warning").innerHTML = message;
		}
	},

	// Format data recieved
	formatData: function(dataStr) {
		var dataObj = JSON.parse(dataStr);

		var returnArr = [ {}, "" ];
		returnArr[0] = dataObj;

		for (var i in returnArr[0].data.datasets) {
			var dataset = returnArr[0].data.datasets[i];

			if (dataset.backgroundColor == "undefined")
				returnArr[0].data.datasets[i].backgroundColor = this.conf.fallbackBackgroundColor;

			if (dataset.borderColor == "undefined")
				returnArr[0].data.datasets[i].borderColor = this.conf.fallbackBorderColor;	

			// No data for datapoint
			if (dataset.data.length == 0)
				continue;
				
			if (returnArr[0].timeBased) {
				var foo = String(dataset.data[0].y);
				
				if (isNaN(parseFloat(foo)) == false) {
					// Numeric dataset, ignore
					continue;
				} else if (foo == "false" || foo == "true") {
					// Binary dataset, convert to numeric
					for (var i of returnArr[0].data.datasets[i].data) {
						if (String(i.y) == "true")
							i.y = 1;
						else
							i.y = 0;
					}
					continue;
				} else {
					// Alphanumeric dataset, remove from response
					returnArr[0].data.datasets.splice(i, 1);

					// Create an warning, if enabled
					if (this.conf.enableDatapointWarnings)
						returnArr[1] += "&quot;" + dataset.label + "&quot; is not a numeric or binary datapoint <br>";
				}

			} else {
				var foo = String(dataset.data[0]);
				
				if (isNaN(parseFloat(foo)) == false) {
					// Numeric dataset, ignore
					continue;
				} else if (foo == "false" || foo == "true") {
					// Binary dataset, convert to numeric
					for (var i of returnArr[0].data.datasets[i].data) {
						if (String(i) == "true")
							i = 1;
						else
							i = 0;
					}
					continue;
				} else {
					// Alphanumeric dataset, remove from response
					returnArr[0].data.datasets.splice(i, 1);
					
					// Create an warning, if enabled
					if (this.conf.enableDatapointWarnings)
						returnArr[1] += "&quot;" + dataset.label + "&quot; is not a numeric or binary datapoint <br>";
				}

			}

		};
		return returnArr;
	},

	// Load Chart.js script
	loadChartJs: function() {
		if (!document.getElementById("fuscabr-chartjs")) {
			var chartJs = document.createElement("script");
        	chartJs.src = this.conf.chartJsFile;
			chartJs.id = "fuscabr-chartjs";
        	chartJs.onload = fuscabr.chart.loop;

        	document.getElementById("fuscabr-modules").appendChild(chartJs);
		}
	},

	// Get module settings
	getSettings: function()	{
		ajaxGetJson("resources/fuscabr/conf/modules/chart.json", function(response) {
			fuscabr.chart.conf = response;
			fuscabr.chart.init();
		});
	},

	// Init module
	init: function() {
		if (!fuscabr.chart.conf) {
			fuscabr.chart.getSettings();
		} else {
			//console.info("Chart module loaded.");
			fuscabr.chart.loop();
			setInterval(function() {
            	fuscabr.chart.loop.call(fuscabr.chart);
			}, fuscabr.chart.conf.refreshTime);
		}
	}
}

fuscabr.chart.init();
