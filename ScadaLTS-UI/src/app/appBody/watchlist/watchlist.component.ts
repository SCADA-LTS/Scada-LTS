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
    private loadPoints;
    private chartData = [];
    private bool: boolean = true;
    private chartBool: boolean = true;
    private dataBoolean: boolean = true;
    private lastActualization;
    private selectedWatchlist;

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
                    return this.http.get(`http://localhost/ScadaBR/api/points/getValue/${v.xid}`)
                        .map(res => res.json());
                })
            ).subscribe(res => {
                this._values = res;
                if (this.bool) {
                    for (let i = 0; i < this._values.length; i++) {
                        this.chartData.push({x: [], y: [], name: ''});
                    }
                    this.chartData.map((v, i) => v.name = this._values[i].name);
                    this.bool = false;
                }
                if (this.chartData[0].x.length < 10) {
                    this.chartData.map((v, i) => v.x.push(new Date(this._values[i].ts)) && v.y.push(this._values[i].value));
                } else {
                    this.chartData.map(v => v.x.splice(0, 1) && v.y.splice(0, 1));
                    this.chartData.map((v, i) => v.x.push(new Date(this._values[i].ts)) && v.y.push(this._values[i].value));
                }
                if (this.chartBool) {
                    this.redrawChart();
                }
                this.lastActualization = new Date(this._values[0].ts);
            });
        }
    };

    private initiateChart() {
        Plotly.newPlot('plotly', this.chartData);
    }

    private redrawChart() {
        Plotly.redraw('plotly', this.chartData);
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
