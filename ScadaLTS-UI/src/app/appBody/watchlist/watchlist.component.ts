import {Component, Inject, OnInit} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
declare var c3: any;
declare var Plotly:any;

@Component({
  selector: 'watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit {

  private _items: Array<WatchlistComponent> = [];
  private _watchlistElements: Array<WatchlistComponent> = [];
  private _values: Array<WatchlistComponent> = [];
  private xid: string;
  private value: string;
  private name: any;
  private loadPoints;

  constructor(@Inject(Http) private http: Http) {                                                                      //pobiera wszystkie zapisane watchlisty uzytkownika
    this.http.get('http://localhost:/ScadaBR/api/watchlist/getNames')
      .subscribe(res => this._items = res.json());
  };


  private updateWatchlistTable(xid) {                                                                     //funkcja wykonywana po kazdej zmianie wartosci w select boxie
    this._watchlistElements = [];
    this.http.get('http://localhost/ScadaBR/api/watchlist/getPoints/' + xid)
      .subscribe(res => {
        this._watchlistElements = res.json();
          this.getValues();
      });
  };


  private chartData = [];                                                                                      //przechowuje dane kazdego punktu z danej watchlisty
  private bool: boolean = true;
  private chartBool: boolean = true;

  private getValues() {
    this._values = [];
    Observable.forkJoin(
      this._watchlistElements.map(v => {
        return this.http.get('http://localhost/ScadaBR/api/points/getValue/' + v.xid)
          .map(res => res.json());
      })
    ).subscribe(res => {
      this._values = res;
      if (this.bool) {
        for (let i = 0; i < this._values.length; i++) {
          this.chartData.push({x:[], y: [], name: ''});
        }
        this.chartData.map((v,i) => v.name = this._values[i].name);
        this.bool = false;
      }
      if (this.chartData[0].x.length < 10) {
        this.chartData.map((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));
      } else {
        this.chartData.map(v => v.x.splice(0,1) && v.y.splice(0,1));
        this.chartData.map((v, i) => v.x.push(new Date()) && v.y.push(this._values[i].value));
      }
      if (this.chartBool) {
        this.redrawChart();
      }
    });
  };



  public initiateChart() {
    Plotly.newPlot('plotly', this.chartData);
  }

  private redrawChart(){
    Plotly.redraw('plotly', this.chartData);
  }



  private initiateInterval() {
    this.loadPoints = setInterval(() => {
      this.getValues();
    }, 5000);
  }

  private pauseChart(){
    this.chartBool = !this.chartBool;
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
