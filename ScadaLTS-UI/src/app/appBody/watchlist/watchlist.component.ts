import { Component } from '@angular/core';
import { Http } from '@angular/http';

@Component({
  selector: 'watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent {

  items:Array;

  constructor(private http:Http) {
    this.http.get('/app/appBody/watchlist/items.json')
      .subscribe(res => this.items = res.json());
  };

  watchlist:Array = [];

  private addItemToWatchlist(item) {
    if (this.watchlist.indexOf(item) == -1)
      this.watchlist.push(item);
  };

}
