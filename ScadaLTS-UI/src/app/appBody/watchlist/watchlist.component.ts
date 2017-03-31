import {Component, Inject, OnInit, OnDestroy, NgZone} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import {Subject} from 'rxjs/Subject';
declare let Plotly: any;
declare let $: any;
import {MdSnackBar} from '@angular/material';

interface WatchlistPattern {
    xid: void;
    name: string;
}

interface WatchlistPoints {
    name: string;
    xid: string;
}

interface WatchlistPointsValues {
    formattedValue: string;
    name: string;
    ts: number;
    value: string,
    xid: string,
    type: string;
}

@Component({
    selector: 'watchlist',
    templateUrl: './watchlist.component.html',
    styleUrls: ['./watchlist.component.css']
})


export class WatchlistComponent implements OnInit, OnDestroy {

    public static fireEvent: Subject<boolean> = new Subject();

    watchlists: {xid: string, name: string}[];
    watchlistElements: Array<WatchlistComponent> = [];
    watchlistPointsValues: Array<WatchlistComponent> = [];
    oldValues: Array<WatchlistComponent> = [];
    xid: string;
    value: WatchlistPattern;
    ts: number;
    name: WatchlistPattern;
    type: string;
    loadPointsFromSpecifiedTimeToNow;
    loadLiveChart;
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
    plot;
    range1: number;
    range2: number;
    dateFromInput: number = 1;
    dateFromUnit: string = 'minutes';
    zoomEvent: boolean = true;
    isRequestTimeRangeActiveAndUndone: boolean = false;
    isRequestSpecifiedTimeActiveAndUndone: boolean = false;
    isAnyRequestActive: boolean = false;
    dateRange1: any;
    dateRange2: any;
    motherOfDragons: boolean = true;
    activeState: string;
    isRedrawingStopped: boolean = false;
    areChartButtonsVisible: boolean = false;
    isChartShrunked: boolean = true;
    isFromSpecifiedDataLoadActive: boolean = false;
    systemPerformance: any = 5000;
    directURL: string;
    onlyLiveChartActive: boolean = true;
    isDotLineMode: boolean = false;
    dateFromContainer: number;
    isLatestActive: boolean = false;
    isChartLogarithm: boolean = false;
    stuff: boolean = true;

    //http://localhost/ScadaLTS/#/appBody/watchlist?name=first&chartHidden=true&chartSmall=true&legendHidden=true&specifiedActive=true_1_minutes&point=DP_920706

    constructor(@Inject(Http) private http: Http, public zone: NgZone, public snackBar: MdSnackBar) {
    };

    updateWatchlistTable(xid) {
        this.zoomEvent = false;
        this.isAnyRequestActive = true;
        this.checkForMultistatesAndBinaries = true;
        this.activeState = '';
        this.watchlistElements = [];
        this.http.get(`/ScadaBR/api/watchlist/getPoints/${xid}`)
            .subscribe(res => {
                this.watchlistElements = res.json();
                if (window.location.hash.match(/point=(\w+)/) && this.stuff) {
                    let xid = window.location.hash.match(/point=(\w+)/)[1];
                    this.watchlistElements = this.watchlistElements.filter(v => v.xid == xid);
                    this.stuff = false;
                }
                this.liveChart();
                this.isAnyRequestActive = false;
                setTimeout(() => {
                    this.autorangeChart();
                    this.specifyDate();
                    this.getDataFromSpecifiedTimeToNow();
                }, 500);
            }, err => {
                console.error('An error occured.' + err);
            });
        this.motherOfDragons = true;
    };

    fillDataWithScheme() {
        this.watchlistPointsValues.forEach((_, i) => {
            this.chartData.push({
                x: [],
                y: [],
                name: '',
                line: {shape: '', width: 1},
                mode: 'lines'
            });
            if (this.watchlistPointsValues[i].type !== 'NumericValue') {
                this.chartData[i]['yaxis'] = 'y2';
                this.chartData[i]['line'].shape = 'hv';
            }
        });
        this.chartData.forEach((v, i) => v.name = this.watchlistPointsValues[i].name);
        this.isFillingDataNeeded = false;
    }

    private handle(error: any): Promise<any> {
        console.error('An error occurred!', error);
        return Promise.reject(error.message || error);
    };

    specifyDate() {
        this.dateFromContainer = this.dateFromInput || 1;
    }

    getDataFromSpecifiedTimeToNow() {
        this.zoomEvent = false;
        this.isAnyRequestActive = true;
        this.isRequestSpecifiedTimeActiveAndUndone = true;
        this.onlyLiveChartActive = false;
        clearInterval(this.loadPointsFromSpecifiedTimeToNow);
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        this.http.get(`/ScadaBR/api/utils/getTs`)
            .subscribe(res => {
                this.actualDate = res.json();
                Observable.forkJoin(
                    this.watchlistElements.map(v => {
                        return this.http.get(`/ScadaBR/api/point_value/getValuesFromTime/${this.actualDate - (this.dateFromContainer * 1000 * (this.dateFromUnit == 'minutes' ? 60 : this.dateFromUnit == 'hours' ? 3600 : this.dateFromUnit == 'days' ? 86400 : 1))}/${v.xid}`)
                            .map(res => res.json())
                            .catch(err => this.handle(err));
                    })
                ).subscribe(res => {
                    this.oldValues = res;
                    this.chartData.forEach((_, i) => this.oldValues[i].values.forEach((_, j) => this.chartData[i].x.push(new Date(this.oldValues[i].values[j].ts)) && this.chartData[i].y.push(this.oldValues[i].values[j].value)));
                    this.intervalSpecifiedTimeToNow();
                    this.redrawChart();
                    this.autorangeChart();
                    this.isRequestSpecifiedTimeActiveAndUndone = false;
                    this.isAnyRequestActive = false;
                    this.isFromSpecifiedDataLoadActive = true;
                });
                this.activeState = 'specifiedTime';
            });
    }

    loadNewDataAfterZoom() {
        clearInterval(this.loadPointsFromSpecifiedTimeToNow);
        this.isFromSpecifiedDataLoadActive = false;
        this.isAnyRequestActive = true;
        this.isRedrawingStopped = true;
        this.range1 = this.isRequestTimeRangeActiveAndUndone ? (Date.parse(this.dateRange1) - 7200000) : Date.parse(this.chartLayout.xaxis.range[0]);
        this.range2 = this.isRequestTimeRangeActiveAndUndone ? (this.isLatestActive ? this.actualDate : (Date.parse(this.dateRange2) - 7200000)) : Date.parse(this.chartLayout.xaxis.range[1]);
        if (this.isRequestTimeRangeActiveAndUndone) {
            this.chartLayout.xaxis.range[0] = Date.parse(this.dateRange1) - 7200000;
            this.chartLayout.xaxis.range[1] = this.isLatestActive ? this.actualDate : Date.parse(this.dateRange2) - 7200000;
        }
        this.chartData.forEach(v => {
            v.x = [];
            v.y = []
        });
        Observable.forkJoin(
            this.watchlistElements.map(v => {
                return this.http.get(`/ScadaBR/api/watchlist/getChartData/${v.xid}/${this.range1}/${this.range2}`)
                    .map(res => res.json())
                    .catch(err => this.handle(err));
            })
        ).subscribe(res => {
            this.oldValues = res;
            this.chartData.forEach((_, i) => this.oldValues[i].values.forEach((_, j) => {
                this.chartData[i].x.push(new Date(this.oldValues[i].values[j].ts));
                this.chartData[i].y.push(this.oldValues[i].values[j].value);
            }));
            this.redrawChart();
            if (Date.parse(this.chartLayout.xaxis.range[1]) >= this.actualDate) {
                this.isRedrawingStopped = false;
                this.onlyLiveChartActive = true;
            } else {
                this.isRedrawingStopped = true;
            }
            this.isAnyRequestActive = false;
            if (this.isRequestTimeRangeActiveAndUndone) {
                this.isRequestTimeRangeActiveAndUndone = false;
            }
            //this.setRanges();
        });
    }

    liveChart() {
        this.getActualDate();
        Observable.forkJoin(
            this.watchlistElements.map(v => {
                return this.http.get(`/ScadaBR/api/point_value/getValue/${v.xid}`)
                    .map(res => res.json());
            })
        ).subscribe(res => {
            this.watchlistPointsValues = res;

            if (this.isFillingDataNeeded) {
                this.fillDataWithScheme();
            }
            if (this.checkForMultistatesAndBinaries) {
                for (let i = 0; i < this.watchlistPointsValues.length; i++) {
                    if (this.watchlistPointsValues[i].type == 'BinaryValue' || this.watchlistPointsValues[i].type == 'MultistateValue') {
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
            console.log(this.chartData);
        });
    };

    //helping functions
    getActualDate() {
        this.http.get(`/ScadaBR/api/utils/getTs`)
            .subscribe(res => {
                this.actualDate = res.json();
            }, err => {
                console.error('An error occured while getting actual date. ' + err);
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
            this.watchlistPointsValues.forEach((v, i) => v.type == 'NumericValue' ? this.chartData[i]['line'].shape = 'spline' : v);
            this.counter++;
        } else if (this.counter == 1) {
            this.watchlistPointsValues.forEach((v, i) => v.type == 'NumericValue' ? this.chartData[i]['line'].shape = 'hv' : v);
            this.counter++;
        } else {
            this.watchlistPointsValues.forEach((v, i) => v.type == 'NumericValue' ? this.chartData[i]['line'].shape = 'linear' : v);
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

    logChart() {
        if (this.isChartLogarithm) {
            this.chartLayout.yaxis.type = 'scatter';
            this.isChartLogarithm = true;
        } else {
            this.chartLayout.yaxis.type = 'log';
            this.isChartLogarithm = false;
        }
        this.isChartLogarithm = !this.isChartLogarithm;
        this.redrawChart();
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

    intervalSpecifiedTimeToNow() {
        this.loadPointsFromSpecifiedTimeToNow = setInterval(() => {
            if (!this.zoomEvent) {
                this.getDataFromSpecifiedTimeToNow();
            }
        }, this.systemPerformance);
    }

    intervalLiveChart() {
        this.loadLiveChart = setInterval(() => {
            this.liveChart();
        }, this.systemPerformance);
    }

    deactivateInterval() {
        clearInterval(this.loadPointsFromSpecifiedTimeToNow);
        clearInterval(this.loadLiveChart);
    }

    setDefaultTimeRangeValues() {
        this.http.get(`/ScadaBR/api/utils/getTs`)
            .subscribe(res => {
                this.actualDate = res.json();
                this.dateRange1 = `${new Date(this.actualDate).getFullYear()}-${new Date(this.actualDate).getMonth() < 10 ? '0' + (new Date(this.actualDate).getMonth() + 1) : new Date(this.actualDate).getMonth() + 1}-${new Date(this.actualDate).getDate() < 10 ? '0' + new Date(this.actualDate).getDate() : new Date(this.actualDate).getDate()}T${new Date(this.actualDate).getHours() < 10 ? '0' + new Date(this.actualDate).getHours() : new Date(this.actualDate).getHours()}:${new Date(this.actualDate).getMinutes() < 10 ? '0' + new Date(this.actualDate).getMinutes() : new Date(this.actualDate).getMinutes()}`;
                this.dateRange2 = `${new Date(this.actualDate).getFullYear()}-${new Date(this.actualDate).getMonth() < 10 ? '0' + (new Date(this.actualDate).getMonth() + 1) : new Date(this.actualDate).getMonth() + 1}-${new Date(this.actualDate).getDate() < 10 ? '0' + new Date(this.actualDate).getDate() : new Date(this.actualDate).getDate()}T${new Date(this.actualDate).getHours() < 10 ? '0' + new Date(this.actualDate).getHours() : new Date(this.actualDate).getHours()}:${new Date(this.actualDate).getMinutes() < 10 ? '0' + new Date(this.actualDate).getMinutes() : new Date(this.actualDate).getMinutes()}`;
            }, err => {
                console.error('An error occured.' + err);
            });

    }

    setRanges() {
        let date1 = this.chartLayout.xaxis.range[0];
        let date2 = this.chartLayout.xaxis.range[1];
        this.dateRange1 = date1.replace(/:\d+\.\d+/, "").split(" ").join("T");
        this.dateRange2 = date2.replace(/:\d+\.\d+/, "").split(" ").join("T");
    }

    relay() {
        clearInterval(this.loadPointsFromSpecifiedTimeToNow);
        this.intervalSpecifiedTimeToNow();
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
    }

    toggleLineMode() {
        if (this.isDotLineMode) {
            this.chartData.forEach(v => v.mode = 'lines');
            this.isDotLineMode = false;
        } else {
            this.chartData.forEach(v => v.mode = 'lines+markers');
            this.isDotLineMode = true;
        }
        this.redrawChart();
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

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    setURL() {
        location.hash = location.hash.replace(/\?.+/, '');
        this.directURL = location.protocol + "//" + location.hostname + location.pathname + location.hash + "?" + "name=" + this.selectedWatchlist.name + "&chartHidden=" +
            this.isChartHidden + "&chartSmall=" + this.isChartShrunked + "&legendHidden=" + this.chartLayout.showlegend + "&specifiedActive=" + this.isFromSpecifiedDataLoadActive +
            "_" + this.dateFromContainer + "_" + this.dateFromUnit;
        console.log(this.directURL);
    }

    ngOnInit() {
        WatchlistComponent.fireEvent.subscribe(() => {
            this.relay();
        });

        this.http.get(`/ScadaBR/api/watchlist/getNames`)
            .subscribe(res => {
                this.watchlists = res.json();
                this.updateWatchlistTable(this.watchlists[0].xid);
                if (window.location.hash.match(/name=(\w+)/)) {
                    let name = window.location.hash.match(/name=(\w+)/)[1];
                    let vl = this.watchlists.filter(v => v.name == name);
                    this.selectedWatchlist = vl[0];
                } else {
                    this.selectedWatchlist = this.watchlists[0];
                }
                this.intervalLiveChart();
                this.intervalSpecifiedTimeToNow();
            }, err => {
                console.error('An error occured.' + err);
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

        this.getUserSystemPerformance();
        this.setDefaultTimeRangeValues();

        if (window.location.hash.match(/chartHidden=(\w+)/)) {
            this.isChartHidden = window.location.hash.match(/chartHidden=(\w+)/)[1] == 'true';
            //} if (window.location.hash.match(/chartSmall=(\w+)/)) {
            this.isChartShrunked = window.location.hash.match(/chartSmall=(\w+)/)[1] == 'true';
            this.chartLayout.height = window.location.hash.match(/chartSmall=(\w+)/)[1] == 'true' ? 600 : 870;
            //} if (window.location.hash.match(/legendHidden=(\w+)/)) {
            this.chartLayout.showlegend = window.location.hash.match(/legendHidden=(\w+)/)[1] == 'true';
            this.isFromSpecifiedDataLoadActive = window.location.hash.match(/specifiedActive=(\w+)(_\d)/)[1] == 'true';
            this.dateFromContainer = +window.location.hash.match(/(?!_)\d+(?=_)/)[0] || 1;
            this.dateFromUnit = window.location.hash.match(/_([a-z]+)/)[1];
            this.dateFromInput = this.dateFromContainer;
            console.log('input from url');
        }
        this.getDataFromSpecifiedTimeToNow();
    }

    ngOnDestroy() {
        clearInterval(this.loadPointsFromSpecifiedTimeToNow);
        clearInterval(this.loadLiveChart);
        this.stuff = true;
    }

}
