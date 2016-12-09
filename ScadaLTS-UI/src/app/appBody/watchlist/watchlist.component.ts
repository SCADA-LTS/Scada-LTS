import {Component, OnInit, Inject} from '@angular/core';
import {Http} from '@angular/http';
declare var c3: any;

@Component({
  selector: 'watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit {

  private _items: Array<WatchlistComponent> = [];

  // constructor(private http: Http) {
  //   this.http.get('http://localhost:8080/ScadaBR/api/watchlist/names.json')
  //     .subscribe(res => this._items = res.json());
  // };

  // constructor(private http: Http) {
  //   this.http.get('app/appBody/watchlist/_items.json')
  //     .subscribe(res => this._items = res.json());
  // };

  // constructor(@Inject(Http) private http: Http) {
  //   this.http.get('app/appBody/watchlist/items.json')
  //     .subscribe(res => this._items = res.json());
  // };

  constructor(@Inject(Http) private http: Http) {
    this.http.get('http://localhost:8080/ScadaBR/api/watchlist/names.json')
      .subscribe(res => this._items = res.json());
  };

  selectedWatchlist = 0;


  private _watchlistElements: Array<WatchlistComponent> = [];

  private updateWatchlistTable(name) {
    this._watchlistElements = [];
    this.http.get('http://localhost:8080/ScadaBR/api/points/getValue/' + name)
      .subscribe(res => this._watchlistElements = res.json());
  }


  private start() {

    let chart = c3.generate({
      bindto: '#chart',
      data: {
        columns: [
          ['data1', 30, 200, 100, 400, 150, 250, 130, 50, 20, 10, 40, 15, 25, 390],
          ['data2', 50, 20, 10, 40, 15, 25, 542, 30, 200, 100, 333, 150, 250]
        ],
        type: 'spline'
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

  ngOnInit() {
    this.start();
  }

}
