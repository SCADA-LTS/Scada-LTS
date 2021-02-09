const CHART_CONFIGURATION_TEMPLATE = {
	legend: {},
	cursor: {},
	scrollbarX: { type: 'Scrollbar' },
	xAxes: [
		{
			id: 'dateAxis1',
			type: 'DateAxis',
			dataFields: {
				value: 'date',
			},
			dateFormats: {
				second: 'HH:mm:ss',
				minute: 'HH:mm:ss',
				hour: 'HH:mm',
				day: 'MMM dd',
			},
			groupData: true,
			groupCount: 500,
		},
		{
			id: 'dateAxis2',
			type: 'DateAxis',
			dataFields: {
				value: 'date2',
			},
			dateFormats: {
				second: 'HH:mm:ss',
				minute: 'HH:mm:ss',
				hour: 'HH:mm',
				day: 'MMM dd',
			},
		},
	],
	yAxes: [
		{
			id: 'valueAxis1',
			type: 'ValueAxis',
			dataFields: {
				value: '01',
			},
		},
		{
			id: 'valueAxis2',
			type: 'ValueAxis',
			syncWithAxis: 'valueAxis1',
			dataFields: {
				value: '01',
			},
		},
		{
			id: 'logAxis',
			type: 'ValueAxis',
			logarithmic: true,
			dataFields: {
				value: '01',
			},
		},
		{
			id: 'binAxis',
			type: 'ValueAxis',
			dataFields: {
				value: '01',
			},
			syncWithAxis: 'valueAxis1',
			renderer: {
				opposite: true,
			},
		},
	],
	series: [],
	exporting: {
		menu: {
			align: 'right',
			verticalAlign: 'top',
		},
		filePrefix: 'Scada_Chart_' + String(new Date().getTime()),
	},
};

const CHART_SERIES_TEMPLATE = {
	id: undefined,
	name: undefined,
	type: undefined,
	stroke: undefined,
	fill: undefined,
	yAxis: undefined,
	tooltipText: undefined,
	strokeWidth: 3,
	fillOpacity: 0,
	xAxis: 'dateAxis1',
	minBulletDistance: 15,
	tensionX: 1,
	startLocation: 0.5,
	dataFields: {
		dateX: 'date',
		valueY: undefined,
	},
	tooltip: {
		pointerOrientation: 'vertical',
		background: {
			fill: '#F00',
			cornerRadius: 20,
			strokeOpacity: 0,
		},
		label: {
			minWidth: 40,
			minHeight: 40,
			textAlign: 'middle',
			textValign: 'middle',
		},
	},
	bullets: [
		{
			type: 'CircleBullet',
			circle: {
				radius: 0,
				strokeWidth: 2,
			},
		},
	],
};

const CHART_DEFAULT_COLORS = [
	'#39B54A',
	'#69FF7D',
	'#166921',
	'#690C24',
	'#B53859',
	'#734FC1',
];

export { CHART_CONFIGURATION_TEMPLATE, CHART_SERIES_TEMPLATE, CHART_DEFAULT_COLORS };
