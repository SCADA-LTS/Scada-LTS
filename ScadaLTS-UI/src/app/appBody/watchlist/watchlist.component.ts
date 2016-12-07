import {Component, OnInit} from '@angular/core';
import {Http} from '@angular/http';
declare var c3:any;

@Component({
  selector: 'watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit {

  items;

  // constructor(private http: Http) {
  //   this.http.get('/app /appBody/watchlist/items.json')
  //     .subscribe(res => this.items = res.json());
  // };

  watchlist = [];

  private addItemToWatchlist(item) {
    if (this.watchlist.indexOf(item) == -1)
      this.watchlist.push(item);
  };

  private start()  {

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
