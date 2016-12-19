import {Component, Inject, OnInit} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
declare var c3: any;

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


  private vars = [];                                                                                      //przechowuje dane kazdego punktu z danej watchlisty
  private bool: boolean = true;

  private getValues() {
    this._values = [];
    Observable.forkJoin(
      this._watchlistElements.map(v => {                                                                  //dla kazdego punktu pobieramy z serwera jego wartosc, czas i nazwe.
        return this.http.get('http://localhost/ScadaBR/api/points/getValue/' + v.xid)
          .map(res => res.json());
      })
    ).subscribe(v => {
        this._values = v;
        if (this.bool == true) {                                                                          //funkcja ma dodac tyle pustych tablic ile punktow w wybranej watchliscie. i ma to zrobic tylko raz (!) dlatego dodany boolean.
          for (let i = 0; i < this._values.length; i++) {
            this.vars.push([]);
          }
          this.bool = false;                                                                              //zmieniamy boolean na false, petla sie juz wiecej nie wykona
        }
        this.vars.map((v,i) => v.push(this._values[i].value));                                            //do kazdej tablicy w zmiennej vars wrzucamy wartosc danego punktu
        this.start();
    });
  };


  private initiateInterval() {
    setTimeout(() => {
      this.getValues();
    }, 500);
    this.loadPoints = setInterval(() => {
      this.getValues();
    }, 5000);
  }



  private deactivateInterval() {
    clearInterval(this.loadPoints);
  }


  // private toSpline(){
  //   this.chart.transform('spline');
  // }


  public start() {

    let chart = c3.generate({
      bindto: '#chart',
      data: {
        columns: this.vars,

      },

      grid: {
        x: {
          show: false
        },
        y: {
          show: true
        }
      },
      zoom: {
        enabled: true
      }

    });

  }


  // private line() {
  //   this.chart.transform('line');
  // }
  //
  // private spline(){
  //   this.chart.transform('spline');
  // }


  ngOnInit() {
    this.start();
  }

  ngOnDestroy() {
    clearInterval(this.loadPoints);
  }

}
