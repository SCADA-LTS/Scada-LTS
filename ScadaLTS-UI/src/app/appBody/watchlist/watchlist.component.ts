import {Component, Inject, OnInit} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
declare let c3: any;
declare let Plotly: any;
declare let $: any;

@Component({
    selector: 'watchlist',
    templateUrl: './watchlist.component.html',
    styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit {

    _watchlists: Array<WatchlistComponent> = [];
    _watchlistElements: Array<WatchlistComponent> = [];
    _values: Array<WatchlistComponent> = [];
    _oldValues: Array<WatchlistComponent> = [];
    xid: string;
    value: string;
    ts: number;
    name: any;
    type: string;
    loadPoints;
    chartData = [];
    isFillingDataNeeded: boolean = true;
    lastActualization = new Date();
    selectedWatchlist;
    chartLayout;
    x: boolean = false;
    checkForMultistatesAndBinaries: boolean = true;
    isLinearChart: boolean = true;
    xx: boolean = true;
    values;
    help2: boolean = true;
    plot;
    range: number;
    dd: any = new Date();
    dateRange1: any = `${this.dd.getFullYear()}-${this.dd.getMonth() < 10 ? '0' + (this.dd.getMonth() + 1) : this.dd.getMonth() + 1}-${this.dd.getDate() < 10 ? '0' + this.dd.getDate() : this.dd.getDate()}T${this.dd.getHours() < 10 ? '0' + this.dd.getHours() : this.dd.getHours()}:${this.dd.getMinutes() < 10 ? '0' + this.dd.getMinutes() : this.dd.getMinutes()}`;
    dateRange2: any = `${this.dd.getFullYear()}-${this.dd.getMonth() < 10 ? '0' + (this.dd.getMonth() + 1) : this.dd.getMonth() + 1}-${this.dd.getDate() < 10 ? '0' + this.dd.getDate() : this.dd.getDate()}T${this.dd.getHours() < 10 ? '0' + this.dd.getHours() : this.dd.getHours()}:${this.dd.getMinutes() < 10 ? '0' + this.dd.getMinutes() : this.dd.getMinutes()}`;

    dateFrom: number = 5;
    dateFromUnit: string = 'seconds';

    something: boolean = true;
    motherOfDragons: boolean = true;
    changed: boolean = false;
    chart: boolean = true;


    constructor(@Inject(Http) private http: Http) {
        this.http.get(`http://localhost:/ScadaBR/api/watchlist/getNames`)
            .subscribe(res => this._watchlists = res.json());
        setTimeout(() => {
            this.updateWatchlistTable(this._watchlists[0].xid);
            this.selectedWatchlist = this._watchlists[0];
            this.initiateInterval();
        }, 500);
        this.chartLayout = {
            showlegend: true,
            legend: {
                "orientation": "h",
                bgcolor: 'transparent',
                y: -0.17
            },
            transition: {
                duration: 500,
                ease: 'cubic-in-out'
            }
        };

    };

    updateWatchlistTable(xid) {
        this.checkForMultistatesAndBinaries = true;
        this._watchlistElements = [];
        this.http.get(`http://localhost/ScadaBR/api/watchlist/getPoints/${xid}`)
            .subscribe(res => {
                this._watchlistElements = res.json();
                this.liveChart();
            });
    };

    fillDataWithScheme() {
        this._values.forEach((_, i) => {
            this.chartData.push({
                x: [],
                y: [],
                name: '',
                line: {shape: '', width: 1},
                mode: 'lines+markers'
            });
            if (this._values[i].type !== 'NumericValue') {
                this.chartData[i]['yaxis'] = 'y2';
                this.chartData[i]['line'].shape = 'hv';
            }
        });
        this.chartData.forEach((v, i) => v.name = this._values[i].name);
        this.isFillingDataNeeded = false;
        console.log('filling successful!');
    }

    getDataFromTimeRange() {
        clearInterval(this.loadPoints);
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        Observable.forkJoin(
            this._watchlistElements.map(v => {
                return this.http.get(`http://localhost/ScadaBR/api/watchlist/getChartData/${v.xid}/${Date.parse(this.dateRange1)}/${Date.parse(this.dateRange2)}`)
                    .map(res => res.json());
            })
        ).subscribe(res => {
            this._oldValues = res;
            this.chartData.forEach((_, i) => this._oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)));
            clearInterval(this.loadPoints);
            console.log('loaded data time range');
            this.chartLayout.xaxis = {range: [this.dateRange1, this.dateRange2]};
            this.redrawChart();
        });
    }

    getDataFromSpecifiedTimeToNow() {
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        Observable.forkJoin(
            this._watchlistElements.map(v => {
                return this.http.get(`http://localhost/ScadaBR/api/point_value/getValuesFromTime/${Date.parse(this.getDate()) - (this.dateFrom * 1000 * (this.dateFromUnit == 'minutes' ? 60 : this.dateFromUnit == 'hours' ? 3600 : 1))}/${v.xid}`)
                    .map(res => res.json());
            })
        ).subscribe(res => {
            this._oldValues = res;
            this.chartData.forEach((_, i) => this._oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)));
            this.redrawChart();
            clearInterval(this.loadPoints);
            this.initiateInterval();
            console.log('loaded data from specified time to now');
            this.autorangeChart();
        });
    }

    loadNewDataAfterZoom() {
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        Observable.forkJoin(
            this._watchlistElements.map(v => {
                return this.http.get(`http://localhost/ScadaBR/api/point_value/getValuesFromTime/${this.range}/${v.xid}`)
                    .map(res => res.json());
            })
        ).subscribe(res => {
            this._oldValues = res;
            this.chartData.forEach((_, i) => this._oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)));
            this.redrawChart();
            clearInterval(this.loadPoints);
            this.initiateInterval();
            console.log('loaded data after zoom');
        });
    }

    liveChart() {
        if (this.chart) {
            Observable.forkJoin(
                this._watchlistElements.map(v => {
                    return this.http.get(`http://localhost/ScadaBR/api/point_value/getValue/${v.xid}`)
                        .map(res => res.json());
                })
            ).subscribe(res => {
                this._values = res;

                if (this.isFillingDataNeeded) {
                    this.fillDataWithScheme();
                }

                if (this.checkForMultistatesAndBinaries) {
                    for (let i = 0; i < this._values.length; i++) {
                        if (this._values[i].type == 'BinaryValue' || this._values[i].type == 'MultistateValue') {
                            this.x = true;
                            break;
                        } else {
                            this.x = false;
                        }
                    }

                    if (this.x) {
                        this.chartLayout.yaxis2 = {
                            titlefont: {color: '#000'},
                            tickfont: {color: '#aa00ff'},
                            overlaying: 'y',
                            side: 'right',
                            showticklabels: true,
                            gridcolor: '#eeccff'
                        }
                    }

                    this.initiateChart();
                    this.checkForMultistatesAndBinaries = false;
                }

                this.plot = document.getElementById('plotly');
                if (this.motherOfDragons) {
                    this.plot.on('plotly_relayout', () => {

                        this.range = Date.parse(this.chartLayout.xaxis.range[0]);
                        console.log('zoomed!');
                        this.loadNewDataAfterZoom();
                    });



                for (let i = 0; i < 11; i++) {
                    document.getElementsByClassName('drag')[i].addEventListener('mousedown', () => {
                        console.log('mousedown' + i);
                        clearInterval(this.loadPoints);
                    });
                }
                    this.motherOfDragons = false;
                }

                this.help2 = true;

                this.chartData.forEach((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));
                this.redrawChart();
                this.chartData.forEach(v => v['mode'] = 'lines');
                console.log(this.chartData);

            });
        }

    };

    //funkcje pomocnicze
    increaseChartLineWidth() {
        this.chartData.map(v => v['line'].width += 1);
        this.redrawChart();
    }

    decreaseChartLineWidth() {
        if (this.chartData[0]['line'].width > 1) {
            this.chartData.map(v => v['line'].width -= 1);
            this.redrawChart();
        }
    }

    toSplineChart() {
        this.isLinearChart = false;
        for (let i = 0; i < this._values.length; i++) {
            if (this._values[i].type == 'NumericValue') {
                this.chartData[i]['line'].shape = 'spline';
            }
        }
        this.redrawChart();
    }

    toLinearChart() {
        this.isLinearChart = true;
        for (let i = 0; i < this._values.length; i++) {
            if (this._values[i].type == 'NumericValue') {
                this.chartData[i]['line'].shape = 'linear';
            }
        }
        this.redrawChart();
    }

    initiateChart() {
        Plotly.newPlot('plotly', this.chartData, this.chartLayout);
    }

    redrawChart() {
        Plotly.redraw('plotly', this.chartData, this.chartLayout);
    }

    autorangeChart(){
        Plotly.relayout('plotly', {
            'xaxis.autorange': true,
            'yaxis.autorange': true
        });
    }

    cleanChartBeforeDraw() {
        this.chartData = [];
        this.isFillingDataNeeded = true;
        Plotly.newPlot('plotly', this.chartData);
    }

    initiateInterval() {
        this.loadPoints = setInterval(() => {
            this.liveChart();
        }, 5000);
    }

    deactivateInterval() {
        clearInterval(this.loadPoints);
    }

    getDate: any = function () {
        return new Date();
    };

    ngOnInit() {
        this.initiateChart();
    }

    ngOnDestroy() {
        clearInterval(this.loadPoints);
    }

}

// for (let i = 0; i < this.chartData.length; i++) {
//     for (let j = 0; j < this._oldValues[i].values.length; j++) {
//         this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)
//     }
// }
// for (let i = 1; i < 11; i++) {
//     document.getElementsByClassName('drag')[i].addEventListener('mouseup', () => {
//         this.chart = true;
//         console.log('mouseup'+i);
//
//     });
// }