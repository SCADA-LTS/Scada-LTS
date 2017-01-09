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

    private _watchlists: Array<WatchlistComponent> = [];
    private _watchlistElements: Array<WatchlistComponent> = [];
    private _values: Array<WatchlistComponent> = [];
    private xid: string;
    private value: string;
    private ts: number;
    private name: any;
    private type: string;
    private loadPoints;
    private chartData = [];
    private bool: boolean = true;
    private chartBool: boolean = true;
    private dataBoolean: boolean = true;
    private lastActualization;
    private selectedWatchlist;
    private chartLayout;
    private x: boolean = false;
    help: boolean = true;
    isHvChart: boolean = true;
    isLinearChart: boolean = true;

    constructor(@Inject(Http) private http: Http) {
        this.http.get(`http://localhost:/ScadaBR/api/watchlist/getNames`)
            .subscribe(res => this._watchlists = res.json());
        setTimeout(() => {
            this.updateWatchlistTable(this._watchlists[0].xid);
            this.selectedWatchlist = this._watchlists[0];
            this.initiateInterval();
        }, 500);
    };

    private updateWatchlistTable(xid) {
        //this.initiateChart();
        this.help = true;
        this._watchlistElements = [];
        this.http.get(`http://localhost/ScadaBR/api/watchlist/getPoints/${xid}`)
            .subscribe(res => {
                this._watchlistElements = res.json();
                this.getValues();

            });
    };

    private cleanChartBeforeDraw() {
        this.chartData = [];
        this.bool = true;
        Plotly.newPlot('plotly', this.chartData);
    }

    private getValues() {

        if (this.dataBoolean) {
            Observable.forkJoin(
                this._watchlistElements.map(v => {
                    return this.http.get(`http://localhost/ScadaBR/api/point_value/getValue/${v.xid}`)
                        .map(res => res.json());
                })
            ).subscribe(res => {
                this._values = res;
                if (this.bool) {
                    for (let i = 0; i < this._values.length; i++) {
                        this.chartData.push({x: [], y: [], name: '', line: {shape: '', width: 1}});
                        if (this._values[i].type !== 'NumericValue') {
                            this.chartData[i]['yaxis'] = 'y2';
                            this.chartData[i]['line'].shape = 'hv';

                        }
                    }
                    this.chartData.map((v, i) => v.name = this._values[i].name);
                    this.bool = false;
                }
                //v.x.push(new Date(this._values[i].ts))
                if (this.chartData[0].x.length < 10) {
                    this.chartData.map((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));
                } else {
                    this.chartData.map(v => v.x.splice(0, 1) && v.y.splice(0, 1));
                    this.chartData.map((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));
                }


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
                        this.chartLayout = {
                            yaxis2: {
                                titlefont: {color: '#000'},
                                tickfont: {color: '#aa00ff'},
                                overlaying: 'y',
                                side: 'right',
                                showticklabels: true,
                                gridcolor: '#eeccff'
                            }
                        };
                    }

                    this.initiateChart();
                    this.help = false;
                } else {
                    this.redrawChart();
                }
                console.log(this.chartData);


                this.lastActualization = new Date(this._values[0].ts);

            });
        }

    };

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
        this.chartData.map(v => v['line'].width = 1);
        for (let i = 0; i < this._values.length; i++) {
            if (this._values[i].type == 'NumericValue') {
                this.chartData[i]['line'].shape = 'spline';
            }
        }
        this.redrawChart();
    }

    toLinearChart() {
        this.isLinearChart = true;
        this.chartData.map(v => v['line'].width = 1);
        for (let i = 0; i < this._values.length; i++) {
            if (this._values[i].type == 'NumericValue') {
                this.chartData[i]['line'].shape = 'linear';
            }
        }
        this.redrawChart();
    }

    toDotChart() {
        this.isLinearChart = false;
        this.chartData.map(v => v['line'].width = 0);
        this.redrawChart();
    }



    toHvChart() {
        this.isHvChart = true;
        for (let i = 0; i < this._values.length; i++) {
            if (this._values[i].type == 'BinaryValue' || this._values[i].type == 'MultistateValue') {
                this.chartData[i]['line'].shape = 'hv';
            }
        }
        this.redrawChart();
    }

    toHvhChart() {
        this.isHvChart = false;
        for (let i = 0; i < this._values.length; i++) {
            if (this._values[i].type == 'BinaryValue' || this._values[i].type == 'MultistateValue') {
                this.chartData[i]['line'].shape = 'hvh';
            }
        }
        this.redrawChart();
    }

    private initiateChart() {
        Plotly.newPlot('plotly', this.chartData, this.chartLayout);
    }

    private redrawChart() {
        Plotly.redraw('plotly', this.chartData, this.chartLayout);
    }

    private initiateInterval() {
        this.loadPoints = setInterval(() => {
            this.getValues();
        }, 5000);
    }

    private pauseChart() {
        this.chartBool = !this.chartBool;
    }

    private pauseDataDownload() {
        this.dataBoolean = !this.dataBoolean;
    }

    private deactivateInterval() {
        clearInterval(this.loadPoints);
    }

    ngOnInit() {
        this.initiateChart();
    }

    ngOnDestroy() {
        clearInterval(this.loadPoints);
    }

}
