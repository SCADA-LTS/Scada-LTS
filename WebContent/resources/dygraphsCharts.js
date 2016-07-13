var dygraphsCharts = {};

DygraphsChart = (function() {
	function DygraphsChart(vid, cid, enableCallbacks, editMode, viewComponent) {
		var self = this;
		self.vid = vid;
		self.cid = cid;
		self.editMode = editMode;
		self.tension = 0.5;
		self.data = [0];
		self.initial = true;
		self.options = {
  			labelsDiv: document.getElementById('c'+ self.cid +'Legend'),
  			labelsSeparateLines: true,
  			labelsKMB: true,
  			legend: 'always',
  			pointSize: 2.5,
  			highlightCircleSize: 3.5,
  			connectSeparatedPoints: true,
  			axisLabelFontSize: 12,
  			xAxisLabelWidth: 70,
  			axes: {
  				x: {
  					pixelsPerLabel: 70
  				}
  			},
  			interactionModel: {}
		};
		if(enableCallbacks) {
			jQuery.extend(self.options,
			{
      			pointClickCallback: function (e, point) {
      				self.showPointConfiguration(point.name);
				},
      			interactionModel: jQuery.extend({}, Dygraph.Interaction.defaultModel, {
      				dblclick: function(event, g, context) {
      					options = {
      							valueRange: [null, null]
      					};
      					g.updateOptions(options);
      					Dygraph.Interaction.defaultModel.dblclick(event, g, context);
      				}
      			})
			});
		}
		self.g = new Dygraph(
				document.getElementById('c' + self.cid + 'Graph'),
				self.data, self.options
		);
		if(viewComponent){
			self.updateOptionsWithViewComponent(viewComponent);
		}
		self.requestData();
	}
	
	DygraphsChart.prototype.requestData = function(){
		var self = this;
		jQuery.post(ctxPath + '/enhChart', {viewId: self.vid, vcId: self.cid}, function(data, textStatus, jqXHR){
			self.updateData(data);
		});
	};

	DygraphsChart.prototype.changeVisibility = function(seriesName, checked) {
		var seriesId = this.g.getOption("labels").indexOf(seriesName) - 1;
		this.g.setVisibility(seriesId, checked);
	};

	DygraphsChart.prototype.positionLegend = function() {
		jQuery("#c"+ this.cid + "LegendBox").position({
			of: jQuery("#c" + this.cid + "Graph"),
			my: "left top",
			at: "right top",
			collision: "none none"
		});
	};
	
	DygraphsChart.prototype.changeColor = function(seriesId, color) {
		if(!color.startsWith("#")){
			color = "#" + color;
		}
		var gColors = this.g.getOption('colors');
		gColors[seriesId] = color;
		this.g.updateOptions({
			'colors' : gColors,
			valueRange: this.g.yAxisRange()
		});
	};

	DygraphsChart.prototype.changeStrokeWidth = function(seriesName, strokeWidth) {
		var options = {};
		options[seriesName] = {
			'strokeWidth' : strokeWidth,
			'pointSize': strokeWidth + 1.5,
  			'highlightCircleSize': strokeWidth + 2.5
		};
		options.valueRange = this.g.yAxisRange();
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.changePointsVisibility = function(seriesName, checked) {
		var options = {};
		options[seriesName] = {
			drawPoints : checked
		};
		options.valueRange = this.g.yAxisRange();
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.setYRange = function() {
		var min = parseFloat(jQuery('#c' + this.cid + '_yRangeMin').val());
		var max = parseFloat(jQuery('#c' + this.cid + '_yRangeMax').val());
		if (min < max) {
			options = {};
			options.valueRange = [ min, max ];
			this.g.updateOptions(options);
		} else {
			alert('Forbidden range. Min value must be lower than max value');
		}
	};

	DygraphsChart.prototype.setXRange = function() {
		var min = jQuery('#c' + this.cid + '_xRangeMin').datetimepicker('getDate').getTime();
		var max = jQuery('#c' + this.cid + '_xRangeMax').datetimepicker('getDate').getTime();
		if (min < max) {
			options = {};
			options.dateWindow = [ min, max ];
			this.g.updateOptions(options);
		} else {
			alert('Forbidden range. Min value must be lower than max value');
		}
	};

	DygraphsChart.prototype.zoomInGraphX = function() {
		var grow;
		var min = parseInt(this.g.xAxisRange()[0]);
		var max = parseInt(this.g.xAxisRange()[1]);
		if (min < max) {
			grow = (max - min) / 4;
			min += grow;
			max -= grow;
		} else if (min == max) {
		} else {
			grow = (min - max) / 4;
			min -= grow;
			max += grow;
		}
		options = {};
		minDate = min;
		maxDate = max;
		options.dateWindow = [ minDate, maxDate ];
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.zoomOutGraphX = function() {
		var grow;
		var min = parseInt(this.g.xAxisRange()[0]);
		var max = parseInt(this.g.xAxisRange()[1]);
		if (min < max) {
			grow = (max - min) / 2;
			min -= grow;
			max += grow;
		} else if (min == max) {
			min -= 1;
			max += 1;
		} else {
			grow = (min - max) / 2;
			min += grow;
			max -= grow;
		}
		options = {};
		minDate = min;
		maxDate = max;
		options.dateWindow = [ minDate, maxDate ];
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.zoomInGraphY = function() {
		var grow;
		var min = this.g.yAxisRange()[0];
		var max = this.g.yAxisRange()[1];
		if (min < max) {
			grow = (max - min) / 4;
			min += grow;
			max -= grow;
		} else if (min == max) {
		} else {
			grow = (min - max) / 4;
			min -= grow;
			max += grow;
		}
		options = {};
		options.valueRange = [ min, max ];
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.zoomOutGraphY = function() {
		var grow;
		var min = this.g.yAxisRange()[0];
		var max = this.g.yAxisRange()[1];
		if (min < max) {
			grow = (max - min) / 2;
			min -= grow;
			max += grow;
		} else if (min == max) {
			min -= 1;
			max += 1;
		} else {
			grow = (min - max) / 2;
			min += grow;
			max -= grow;
		}
		options = {};
		options.valueRange = [ min, max ];
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.exportAsImage = function() {
		if (!Dygraph.Export.isSupported()) {
			alert('Export to PNG not supported');
			return;
		}
		var img = document.getElementById('c' + this.cid + 'ExportImage');
		Dygraph.Export.asPNG(this.g, img);
		window.open(img.src);// .replace('image/png','application/octet-stream');
	};

	DygraphsChart.prototype.exportAsCSV = function() {
		var res = "\"Date\",\"Time\"";
		var labels = this.g.getOption('labels');
		var i = 1;
		for (i = 1; i < labels.length; i++) {
			res += ",\"" + labels[i] + "\"";
		}
		for (i = 0; i < this.data.length; i++) {
			res += "\n";
			var time = this.data[i][0];
			res += "\"" + time.getDate() + "/" + (time.getMonth() + 1) + "/"
					+ time.getFullYear() + "\"";
			var minutes = time.getMinutes() < 10 ? "0" + time.getMinutes()
					: time.getMinutes();
			var seconds = time.getSeconds() < 10 ? "0" + time.getSeconds()
					: time.getSeconds();
			res += ",\"" + time.getHours() + ":" + minutes + ":" + seconds
					+ "\"";
			for ( var j = 1; j < this.data[i].length; j++) {
				res += "," + (this.data[i][j] === null ? "" : this.data[i][j]);
			}
		}
		
		window.open('data:text/csv;filename=data.csv,' + encodeURIComponent(res),'data.csv');
	};

	DygraphsChart.prototype.splinePlotter = function(e, chart) {
		var ctx = e.drawingContext;
		var ctx_points = e.points;
		var num_points = chart.data.length;
		if (num_points < 3) {
			Dygraph.Plotters.linePlotter(e);
		} else {
			var p = new Array();
			for ( var i = 0; i < ctx_points.length; i++) {
				if(!isNaN(ctx_points[i].canvasx) && !isNaN(ctx_points[i].canvasy)){
					p.push(ctx_points[i].canvasx);
					p.push(ctx_points[i].canvasy);
				}
			}

			var drawPoints = typeof chart.g.getOption(e.setName).drawPoints === "undefined" ?
					chart.g.getOption('drawPoints')
					: chart.g.getOption(e.setName).drawPoints;
			var dataPointRadius = typeof chart.g.getOption(e.setName).pointSize === "undefined" ?
					chart.g.getOption('pointSize')
					: chart.g.getOption(e.setName).pointSize;
			DygraphsSplineUtils.drawSpline(ctx, p, chart.tension, e.color, drawPoints, dataPointRadius);
		}
	};

	DygraphsChart.prototype.setSplineMode = function(seriesName) {
		var options = {};
		var chart = this;
		options[seriesName] = {
			'plotter' : function(e){chart.splinePlotter(e, chart);}
		};
		options.valueRange = this.g.yAxisRange();
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.setLineMode = function(seriesName) {
		var options = {};
		options[seriesName] = {
			'plotter' : Dygraph.Plotters.linePlotter
		};
		options.valueRange = this.g.yAxisRange();
		this.g.updateOptions(options);
	};

	DygraphsChart.prototype.updateOptions = function(width, height, durationType, durationPeriods, chartType, pointProps) {
		var options = {};
		options.colors = this.g.getOption('colors');
		options.colors = options.colors == null ? [] : options.colors;
		this.chartType = chartType;
		var labels = new Array();
		labels.push("Time");
		for(var i = 0; i < pointProps.length; i++) {
			if(!isBlank(pointProps[i].pointName)){
				seriesName = isBlank(pointProps[i].alias) ? pointProps[i].pointName : pointProps[i].alias;
				labels.push(seriesName);
				options.colors[i] = pointProps[i].color;
				options[seriesName] = {};
				options[seriesName].strokeWidth = parseInt(pointProps[i].strokeWidth);
				options[seriesName].pointSize = parseInt(pointProps[i].strokeWidth) + 1.5;
				options[seriesName].highlightCircleSize = parseInt(pointProps[i].strokeWidth) + 2.5;
				options[seriesName].drawPoints = pointProps[i].showPoints;
				if(pointProps[i].lineType === "LINE") {
					options[seriesName].plotter = Dygraph.Plotters.linePlotter;
				} else if(pointProps[i].lineType === "SPLINE"){
					var chart = this;
					options[seriesName].plotter = function(e){chart.splinePlotter(e, chart);};
				}
			}
		}
		options.labels = labels;
		options.colors.splice(labels.length - 1, options.colors.length - labels.length);
		options.file = [];
		this.g.updateOptions(options);
		jQuery("#c" + this.cid + "Graph").width(width).height(height);
		this.g.resize(width, height);
		this.positionLegend();
	};
	
	DygraphsChart.prototype.updateOptionsWithViewComponent = function(viewComponent){
		var pointsProps = new Array();
    	var pointChildren = viewComponent.childComponents;
    	for (var i=0; i < pointChildren.length; i++) {
    		if(!isBlank(pointChildren[i].viewComponent.extendedName)){
	    		var color = pointChildren[i].viewComponent.color;
	    		if(!color.startsWith("#")){
	    			color = "#" + color;
	    		}
	    		var pointProps = {
	    				pointName: pointChildren[i].viewComponent.extendedName,
	        			alias: pointChildren[i].viewComponent.alias,
	        			color: color,
	        			strokeWidth: pointChildren[i].viewComponent.strokeWidth,
	        			lineType: pointChildren[i].viewComponent.lineType,
	        			showPoints: pointChildren[i].viewComponent.showPoints
	    		};
	    		pointsProps.push(pointProps);
    		}
    	}
		this.updateOptions(viewComponent.width, viewComponent.height, viewComponent.durationType,
				viewComponent.durationPeriods, viewComponent.enhancedImageChartType, pointsProps);
	};
	
	DygraphsChart.prototype.showConfiguration = function(){
		var yRange = dygraphsCharts[this.cid].g.yAxisRange();
		var xRange = dygraphsCharts[this.cid].g.xAxisRange();
		jQuery('#c' + this.cid + '_yRangeMin').val(yRange[0]);
		jQuery('#c' + this.cid + '_yRangeMax').val(yRange[1]);
		jQuery('#c' + this.cid + '_xRangeMin').datetimepicker('setDate', new Date(xRange[0]));
		jQuery('#c' + this.cid + '_xRangeMax').datetimepicker('setDate', new Date(xRange[1]));
		jQuery('#c' + this.cid + 'Config').show();
	};
	
	DygraphsChart.prototype.showPointConfiguration = function(seriesName){
		var self = this;
		jQuery('#c' + self.cid + 'PointConfigTitle').text(seriesName);
		jQuery('#c' + self.cid + '_showPoints').prop("checked", self.g.getOption("drawPoints", seriesName)).unbind().change(
					function(){
						self.changePointsVisibility(seriesName, this.checked);
					}
				);
		if(self.g.getOption("plotter", seriesName) === Dygraph.Plotters.linePlotter){
			jQuery('#c' + self.cid + '_renderModeLine').prop("checked", true);
		} else {
			jQuery('#c' + self.cid + '_renderModeSpline').prop("checked", true);
		}
		jQuery('#c' + self.cid + '_renderModeLine').unbind().change(function() {
			self.setLineMode(seriesName);
		});
		jQuery('#c' + self.cid + '_renderModeSpline').unbind().change(function() {
			self.setSplineMode(seriesName);
		});
		jQuery('#c' + self.cid + 'PointConfig').show();
	};
	
	DygraphsChart.prototype.initConfigControls = function() {
		var self = this;
		jQuery('#c' + self.cid + 'ConfigButtonDiv').position({of: jQuery("#c" + self.cid + "LegendBox"), my: "left top", at: "left bottom", collision: "none none"});
		jQuery('#c' + self.cid + 'Config').position({of: jQuery("#c" + self.cid + "LegendBox"), my: "left top", at: "left bottom", collision: "none none"});
		jQuery('#c' + self.cid + 'PointConfig').position({of: jQuery("#c" + self.cid + "LegendBox"), my: "left top", at: "left bottom", collision: "none none"});
		jQuery('#c' + self.cid + 'SeriesConfig').position({of: jQuery("#c" + self.cid + "LegendBox"), my: "left top", at: "left bottom", collision: "none none"});
		jQuery('#c' + self.cid + '_xRangeMin').datetimepicker({
			dateFormat: "d/m/yy",
			timeFormat: "H:mm:ss"
		});
		jQuery("#c" + self.cid + "_xRangeMax").datetimepicker({
			dateFormat: "d/m/yy",
			timeFormat: "H:mm:ss"
		});
		jQuery("[id^='c" + self.cid + "'][id$='_color']").each(function(index){
			jQuery(this).jPicker({
	    		images: {
	    			clientPath: 'resources/jQuery/plugins/jpicker/images/',
	    		},
	    		window: {
	    			title: "",
	    			position: {
	    				x: 'right',
	    				y: 'center'
	    			}
	    		}
	    	}, function(color){
	    		self.changeColor(index, color.val().hex);
	    	});
		});
		var labels = self.g.getOption("labels");
		jQuery("[id^='c" + self.cid + "'][id$='_strokeWidth']").each(function(index){
			jQuery(this).spinner({
				step: 1.0,
				numberFormat: "n",
				max: 10,
				min: 0,
				change: function(e, ui) {
					self.changeStrokeWidth(labels[index + 1], jQuery(this).spinner("value"));
				},
				spin: function(e, ui) {
					self.changeStrokeWidth(labels[index + 1], ui.value);
				}
			}).width("100%").parent().width("20%");
		});
		setTimeout(function(){
			jQuery('#c' + self.cid + 'Config').hide();
			jQuery('#c' + self.cid + 'PointConfig').hide();
			jQuery('#c' + self.cid + 'SeriesConfig').hide();
		}, 0);
	};
	
	DygraphsChart.prototype.updateData = function(csvData){
		if(isBlank(csvData)) return;
		var data = new Array();
		var i, j;
		var lines = csvData.split("\n");
		for(i = 0; i < lines.length; i++) {
			var bits = lines[i].split(",");
			var dataRow = new Array();
			dataRow.push(new Date(parseInt(bits[0])));
			for(j = 1; j < bits.length; j++) {
				dataRow.push(isBlank(bits[j]) ? null : parseFloat(bits[j]));
			}
			data.push(dataRow);
		}
		if(data.length === 0 || this.g.getOption("labels").length != data[0].length) return;
		this.data = data;
		this.g.updateOptions({file: data, valueRange: (this.initial || this.editMode) ? [null, null] : this.g.yAxisRange()});
		this.initial = false;
	};
	
	return DygraphsChart;

})();