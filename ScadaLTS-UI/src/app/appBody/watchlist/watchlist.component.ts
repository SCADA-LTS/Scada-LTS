import {Component, Inject, OnInit, OnDestroy, NgZone} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Http, URLSearchParams, Headers, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import {Subject} from 'rxjs/Subject';
declare let Plotly: any;
declare let $: any;
import {MdSnackBar} from '@angular/material';

import 'rxjs/add/operator/do';
import 'rxjs/add/operator/take';

@Component({
    selector: 'watchlist',
    templateUrl: './watchlist.component.html',
    styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit, OnDestroy {

    public static fireEvent: Subject<boolean> = new Subject();

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
    actualDate: any;
    selectedWatchlist;
    chartLayout;
    multistatesOrBinariesDetected: boolean = false;
    checkForMultistatesAndBinaries: boolean = true;
    counter: number = 0;
    isChartHidden: boolean = true;
    values;
    help2: boolean = true;
    plot;
    range1: number;
    range2: number;
    dateFrom: number = 5;
    dateFromUnit: string = 'minutes';
    zoomEvent: boolean = true;
    isRequestTimeRangeActiveAndUndone: boolean = false;
    isRequestSpecifiedTimeActiveAndUndone: boolean = false;
    isAnyRequestActive: boolean = false;
    dateRange1: any;
    dateRange2: any;
    motherOfDragons: boolean = true;
    chart: boolean = true;
    activeState: string;
    isRedrawingStopped: boolean = false;
    areChartButtonsVisible: boolean = false;
    isChartShrunked: boolean = true;
    isFromSpecifiedDataLoadActive: boolean = false;
    systemPerformance: any = 5000;
    directURL: string;


    constructor(@Inject(Http) private http: Http, public zone: NgZone, private router: Router, private route: ActivatedRoute, public snackBar: MdSnackBar) {

        WatchlistComponent.fireEvent.subscribe(() => {
            this.relay();
        });


        this.http.get(`/ScadaBR/api/watchlist/getNames`)
            .subscribe(res => {
                this._watchlists = res.json();
                this.updateWatchlistTable(this._watchlists[0].xid);

                if (window.location.hash.match(/name=(\w+)/)) {
                    let name = window.location.hash.match(/name=(\w+)/)[1];
                    // let obj = {};
                    // obj['xid'] = window.location.hash.match(/xid=(\w+)/)[1];
                    // obj['name'] = window.location.hash.match(/name=(\w+)/)[1];
                    // this.selectedWatchlist = obj;
                    let vl = this._watchlists.filter(v => v.name == name);
                    this.selectedWatchlist = vl[0];
                    console.log('wl url')

                } else {
                    this.selectedWatchlist = this._watchlists[0];
                    console.log('wl default');
                    console.log(this.selectedWatchlist);
                }

                this.initiateInterval();
            });
        this.chartLayout = {
            autosize: true,
            height: 600,
            showlegend: true,
            legend: {
                orientation: 'h',
                bgcolor: 'transparent',
                y: -0.17,
                x: 0
            }
        };

    };

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    updateWatchlistTable(xid) {
        this.zoomEvent = false;
        this.isAnyRequestActive = true;
        this.checkForMultistatesAndBinaries = true;
        this.activeState = '';
        this._watchlistElements = [];
        this.http.get(`/ScadaBR/api/watchlist/getPoints/${xid}`)
            .subscribe(res => {
                this._watchlistElements = res.json();
                this.liveChart();
                this.isAnyRequestActive = false;
                setTimeout(() => {
                    this.autorangeChart();
                }, 500);
            });
        this.motherOfDragons = true;
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
    }

    private handle(error: any): Promise<any> {
        console.error('An error occurred!', error);
        return Promise.reject(error.message || error);
    };

    getDataFromTimeRange() {
        this.isFromSpecifiedDataLoadActive = false;
        this.zoomEvent = false;
        this.isAnyRequestActive = true;
        clearInterval(this.loadPoints);
        this.isRequestTimeRangeActiveAndUndone = true;
        this.isRedrawingStopped = true;
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        Observable.forkJoin(
            this._watchlistElements.map(v => {
                return this.http.get(`/ScadaBR/api/watchlist/getChartData/${v.xid}/${(Date.parse(this.dateRange1) - 3600000)}/${(Date.parse(this.dateRange2) - 3600000)}`)
                    .map(res => res.json())
                    .catch(this.handle)
            })
        ).subscribe(res => {
            this._oldValues = res;
            this.chartData.forEach((_, i) => this._oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)));
            this.autorangeChart();
            this.redrawChart();
            this.isRequestTimeRangeActiveAndUndone = false;
            this.isChartHidden = false;
            this.isAnyRequestActive = false;
        });
        this.activeState = 'timeRange';
    }

    getDataFromSpecifiedTimeToNow() {
        this.chartData.forEach(v => v['mode'] = 'lines');
        this.zoomEvent = false;
        this.isAnyRequestActive = true;
        this.isRequestSpecifiedTimeActiveAndUndone = true;
        clearInterval(this.loadPoints);
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        this.http.get(`/ScadaBR/api/utils/getTs`)
            .subscribe(res => {
                this.actualDate = res.json();
                Observable.forkJoin(
                    this._watchlistElements.map(v => {
                        return this.http.get(`/ScadaBR/api/point_value/getValuesFromTime/${this.actualDate - (this.dateFrom * 1000 * (this.dateFromUnit == 'minutes' ? 60 : this.dateFromUnit == 'hours' ? 3600 : this.dateFromUnit == 'days' ? 86400 : 1))}/${v.xid}`)
                            .map(res => res.json());
                    })
                ).subscribe(res => {
                    this._oldValues = res;
                    this.chartData.forEach((_, i) => this._oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)));
                    this.initiateInterval();
                    this.redrawChart();
                    this.autorangeChart();
                    this.isRequestSpecifiedTimeActiveAndUndone = false;
                    this.isChartHidden = false;
                    this.isAnyRequestActive = false;
                    this.setRanges();
                    this.isFromSpecifiedDataLoadActive = true;
                });
                this.activeState = 'specifiedTime';
            });
    }

    loadNewDataAfterZoom() {
        this.isFromSpecifiedDataLoadActive = false;
        this.isAnyRequestActive = true;
        clearInterval(this.loadPoints);
        this.isRedrawingStopped = true;
        this.range1 = Date.parse(this.chartLayout.xaxis.range[0]);
        this.range2 = Date.parse(this.chartLayout.xaxis.range[1]);
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        Observable.forkJoin(
            this._watchlistElements.map(v => {
                return this.http.get(`/ScadaBR/api/watchlist/getChartData/${v.xid}/${this.range1}/${this.range2}`)
                    .map(res => res.json());
            })
        ).subscribe(res => {
            this._oldValues = res;
            this.chartData.forEach((_, i) => this._oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this._oldValues[i].values[j].ts)) && this.chartData[i].y.push(this._oldValues[i].values[j].value)));
            this.redrawChart();
            if (Date.parse(this.chartLayout.xaxis.range[1]) >= this.actualDate) {
                this.isRedrawingStopped = false;
                this.initiateInterval();
            }
            this.isAnyRequestActive = false;
            this.setRanges();
            this.zoomEvent = false;
        });
    }

    liveChart() {
        this.getActualDate();
        Observable.forkJoin(
            this._watchlistElements.map(v => {
                return this.http.get(`/ScadaBR/api/point_value/getValue/${v.xid}`)
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
                        this.multistatesOrBinariesDetected = true;
                        break;
                    } else {
                        this.multistatesOrBinariesDetected = false;
                    }
                }
                if (this.multistatesOrBinariesDetected) {
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
                    this.zone.run(() => {
                        if (this.zoomEvent) {
                            this.loadNewDataAfterZoom();
                        }
                        this.isRedrawingStopped = false;

                    });

                });

                this.motherOfDragons = false;
            }

            for (let i = 0; i < 11; i++) {
                let cb = () => {
                    this.isRedrawingStopped = true;
                    this.zoomEvent = true;
                };
                document.getElementsByClassName('drag')[i].addEventListener('mousedown', cb);
            }

            this.help2 = true;
            // if (this.isFromSpecifiedDataLoadActive) {
            //     this.chartData.forEach(v => {
            //         v.x.splice(0, 1);
            //         v.y.splice(0, 1)
            //     });
            // }
            this.chartData.forEach((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));

            if (this.chartData[0].x.length > 1) {
                this.chartData.forEach(v => v['mode'] = 'lines');
            }

            if (this.isRedrawingStopped == false) {
                this.redrawChart();
            }
            this.setDefaultTimeRangeValues();
        });


    };

    //helping functions
    getActualDate() {
        this.http.get(`/ScadaBR/api/utils/getTs`)
            .subscribe(res => {
                this.actualDate = res.json();
            });
    }

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

    changeNumericChartShape() {
        if (!this.counter) {
            this._values.forEach((v, i) => v.type == 'NumericValue' ? this.chartData[i]['line'].shape = 'spline' : v);
            this.counter++;
        } else if (this.counter == 1) {
            this._values.forEach((v, i) => v.type == 'NumericValue' ? this.chartData[i]['line'].shape = 'hv' : v);
            this.counter++;
        } else {
            this._values.forEach((v, i) => v.type == 'NumericValue' ? this.chartData[i]['line'].shape = 'linear' : v);
            this.counter = 0;
        }
        this.redrawChart();
    }

    initiateChart() {
        Plotly.newPlot('plotly', this.chartData, this.chartLayout, {
            modeBarButtonsToRemove: []
        });
    }

    redrawChart() {
        Plotly.redraw('plotly', this.chartData, this.chartLayout, {
            modeBarButtonsToRemove: ['toImage']
        });
    }

    autorangeChart() {
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
            if (this.isFromSpecifiedDataLoadActive) {
                this.getDataFromSpecifiedTimeToNow();
            } else {
                this.liveChart();
            }
        }, this.systemPerformance);
    }

    deactivateInterval() {
        clearInterval(this.loadPoints);
    }

    setDefaultTimeRangeValues() {
        this.http.get(`/ScadaBR/api/utils/getTs`)
            .subscribe(res => {
                this.actualDate = res.json();
                this.dateRange1 = `${new Date(this.actualDate).getFullYear()}-${new Date(this.actualDate).getMonth() < 10 ? '0' + (new Date(this.actualDate).getMonth() + 1) : new Date(this.actualDate).getMonth() + 1}-${new Date(this.actualDate).getDate() < 10 ? '0' + new Date(this.actualDate).getDate() : new Date(this.actualDate).getDate()}T${new Date(this.actualDate).getHours() < 10 ? '0' + new Date(this.actualDate).getHours() : new Date(this.actualDate).getHours()}:${new Date(this.actualDate).getMinutes() < 10 ? '0' + new Date(this.actualDate).getMinutes() : new Date(this.actualDate).getMinutes()}`;
                this.dateRange2 = `${new Date(this.actualDate).getFullYear()}-${new Date(this.actualDate).getMonth() < 10 ? '0' + (new Date(this.actualDate).getMonth() + 1) : new Date(this.actualDate).getMonth() + 1}-${new Date(this.actualDate).getDate() < 10 ? '0' + new Date(this.actualDate).getDate() : new Date(this.actualDate).getDate()}T${new Date(this.actualDate).getHours() < 10 ? '0' + new Date(this.actualDate).getHours() : new Date(this.actualDate).getHours()}:${new Date(this.actualDate).getMinutes() < 10 ? '0' + new Date(this.actualDate).getMinutes() : new Date(this.actualDate).getMinutes()}`;
            });
    }

    setRanges() {
        let date1 = this.chartLayout.xaxis.range[0];
        let date2 = this.chartLayout.xaxis.range[1];
        this.dateRange1 = date1.replace(/:\d+\.\d+/, "").split(" ").join("T");
        this.dateRange2 = date2.replace(/:\d+\.\d+/, "").split(" ").join("T");
    }

    relay() {
        clearInterval(this.loadPoints);
        this.initiateInterval();
        this.redrawChart();
        this.autorangeChart();
    }

    getScreenHeight() {
        return window.innerHeight;
    }

    toggleLegend() {
        this.chartLayout.showlegend == true ? this.chartLayout.showlegend = false : this.chartLayout.showlegend = true;
        this.redrawChart();
    }

    toggleChartSize() {
        if (this.isChartShrunked) {
            this.chartLayout.height = 870;
            this.isChartShrunked = false;
        } else {
            this.chartLayout.height = 600;
            this.isChartShrunked = true;
        }
        this.redrawChart();
        console.log(this.selectedWatchlist)
    }

    getUserSystemPerformance() {
        let systemPerf = JSON.parse(localStorage.getItem('systemPerf'));
        if (systemPerf == undefined || systemPerf == 'low') {
            this.systemPerformance = 5000;
            return 5000;
        } else if (systemPerf == 'medium') {
            this.systemPerformance = 3000;
            return 3000;
        } else {
            this.systemPerformance = 1000;
            return 1000;
        }
    }

    setURL() {
        location.hash = location.hash.replace(/\?.+/, '');
        this.directURL = location.protocol + "//" + location.hostname + location.pathname + location.hash + "?" + "name=" + this.selectedWatchlist.name + "&chartHidden=" +
            this.isChartHidden + "&chartSmall=" + this.isChartShrunked + "&legendHidden=" + this.chartLayout.showlegend;
        console.log(this.directURL);
    }

    ngOnInit() {
        this.getUserSystemPerformance();
        this.setDefaultTimeRangeValues();
        this.initiateChart();
        console.log(localStorage['systemPerf']);

        if (window.location.hash.match(/chartHidden=(\w+)/)) {
            this.isChartHidden = window.location.hash.match(/chartHidden=(\w+)/)[1] == 'true';
        }

        if (window.location.hash.match(/chartSmall=(\w+)/)) {
            this.isChartShrunked = window.location.hash.match(/chartSmall=(\w+)/)[1] == 'true';
            this.chartLayout.height = window.location.hash.match(/chartSmall=(\w+)/)[1] == 'true' ? 600 : 870;
        }

        if (window.location.hash.match(/legendHidden=(\w+)/)) {
            this.chartLayout.showlegend = window.location.hash.match(/legendHidden=(\w+)/)[1] == 'true';
        }


    }

    ngOnDestroy() {
        clearInterval(this.loadPoints);
    }

}
