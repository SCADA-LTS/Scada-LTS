import { Component } from '@angular/core';
import { Http } from '@angular/http';
declare var c3:any;
@Component({
  selector: 'watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent {

  items;

  constructor(private http:Http) {
    this.http.get('/app/appBody/watchlist/items.json')
      .subscribe(res => this.items = res.json());
  };

  watchlist = [];

  private addItemToWatchlist(item) {
    if (this.watchlist.indexOf(item) == -1)
      this.watchlist.push(item);
  };


  // type = 'line';
  // data = {
  //   labels: ["January", "February", "March", "April", "May", "June", "July"],
  //   datasets: [
  //     {
  //       label: "Watchlist chart",
  //       data: [65, 59, 80, 81, 56, 55, 40],
  //       backgroundColor: 'transparent',
  //       borderColor: '#0099ff',
  //       borderWidth: 1,
  //       lineTension: 0
  //     },
  //     {
  //       label: 'Line Component',
  //       data: [30, 20, 10, 44, 12, 76, 58],
  //       backgroundColor: 'transparent',
  //       borderColor: '#00664d',
  //       borderWidth: 1,
  //       lineTension: 0
  //     },
  //     {
  //       label: "Watchlist chart",
  //       data: [54, 17, 8, 11, 65, 32, 48],
  //       backgroundColor: 'transparent',
  //       borderColor: '#660066',
  //       borderWidth: 1,
  //       lineTension: 0
  //     },
  //     {
  //       label: "Watchlist chart",
  //       data: [7, 92, 23, 32, 75, 34, 98],
  //       backgroundColor: 'transparent',
  //       borderColor: '#b30000',
  //       borderWidth: 1,
  //       lineTension: 0
  //     }
  //   ]
  // };
  //
  // options = {
  //   responsive: true,
  //   maintainAspectRatio: false,
  //   legend: {
  //     display: true,
  //     labels: {
  //       fontColor: 'rgb(255, 99, 132)',
  //       fontFamily: 'Raleway'
  //     }
  //   }
  // };
  //
  // legend:boolean = true;




}
