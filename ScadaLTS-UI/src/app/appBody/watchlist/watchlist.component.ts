import { Component } from '@angular/core';
import { Http } from '@angular/http';

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
  }

}
