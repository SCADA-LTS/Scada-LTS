import {Component, Inject, OnInit} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
declare let c3: any;
declare let Plotly: any;

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
    bool: boolean = true;
    lastActualization;
    selectedWatchlist;
    chartLayout;
    x: boolean = false;
    help: boolean = true;
    isLinearChart: boolean = true;
    xx: boolean = true;
    getDataFromPast: boolean = false;
    values;
    help2: boolean = true;
    plot;
    range: number;
    dd: any = new Date();
    date2: any = `${this.dd.getFullYear()}-${this.dd.getMonth() < 10 ? '0'+(this.dd.getMonth()+1) : this.dd.getMonth()+1}-${this.dd.getDate() < 10 ? '0'+this.dd.getDate() : this.dd.getDate()}T${this.dd.getHours()}:${this.dd.getMinutes() < 10 ? '0'+this.dd.getMinutes() : this.dd.getMinutes()}`;

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
            }
        };

    };

    updateWatchlistTable(xid) {
        this.help = true;
        this._watchlistElements = [];
        this.http.get(`http://localhost/ScadaBR/api/watchlist/getPoints/${xid}`)
            .subscribe(res => {
                this._watchlistElements = res.json();
                this.getValues();
            });
    };


    getValues() {
        if (this.chart) {
            Observable.forkJoin(
                this._watchlistElements.map(v => {
                    return this.http.get(`http://localhost/ScadaBR/api/point_value/getValue/${v.xid}`)
                        .map(res => res.json());
                })
            ).subscribe(res => {
                this._values = res;

                if (this.bool) {
                    for (let i = 0; i < this._values.length; i++) {
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
                    }
                    this.chartData.forEach((v, i) => v.name = this._values[i].name);
                    this.bool = false;
                }

                if (this.getDataFromPast) {
                    this.help2 = false;
                    this.chartData.forEach(v => {
                        v.x = [];
                        v.y = []
                    });

                    Observable.forkJoin(
                        this._watchlistElements.map(v => {
                            return this.http.get(`http://localhost/ScadaBR/api/point_value/getValuesFromTime/${this.changed ? this.range : Date.parse(this.date2) - 3600000}/${v.xid}`)
                                .map(res => res.json());
                        })
                    ).subscribe(res => {
                        this._oldValues = res;

                        for (let i = 0; i < this.chartData.length; i++) {
                            for (let j = 0; j < this._oldValues[i].values.length; j++) {
                                this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)
                            }

                        }
                        this.redrawChart();
                        this.getDataFromPast = false;
                        console.log('loaded data from past');
                    });
                }


                if (this.help2) {
                    this.chartData.forEach((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));
                }


                this.lastActualization = new Date(this._values[0].ts);

                if (this.help) {

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
                    this.help = false;
                    this.chartData.forEach(v => v['mode'] = 'lines');


                } else {
                    this.redrawChart();


                    // for (let i = 1; i < 11; i++) {
                    //     document.getElementsByClassName('drag')[i].addEventListener('mouseup', () => {
                    //         this.chart = true;
                    //         console.log('mouseup'+i);
                    //
                    //     });
                    // }

                }

                this.plot = document.getElementById('plotly');
                this.plot.on('plotly_relayout', () => {
                    this.range = Date.parse(this.chartLayout.xaxis.range[0]);
                    console.log('zmienilo');
                    this.changed = true;
                    this.chart = true;
                    this.getDataFromPast = true;
                });

                for (let i = 1; i < 11; i++) {
                    document.getElementsByClassName('drag')[i].addEventListener('mousedown', () => {
                        this.chart = false;
                        console.log('mousedown'+i);

                    });
                }


                this.help2 = true;

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

    cleanChartBeforeDraw() {
        this.chartData = [];
        this.bool = true;
        Plotly.newPlot('plotly', this.chartData);
    }

    initiateInterval() {
        this.loadPoints = setInterval(() => {
            this.getValues();
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
