import {Component, OnInit, AfterViewInit} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/debounceTime';

@Component({
    selector: 'app-wledit',
    templateUrl: 'wledit.component.html',
    styleUrls: ['wledit.component.sass']
})


export class WleditComponent implements OnInit {

    pointsArray = [];
    pointName: string = '';
    bool: boolean = false;
    watchlists = [];
    selectedWatchlist;
    watchlistPoints = [];
    x;

    term$ = new Subject<string>();

    constructor(private http: Http) {
        this.term$
            .debounceTime(300)
            .subscribe(term => this.getPoints())
    }

    getPoints(){
        //todo
    }

    watchlistChanged(){
        this.http.get(`/ScadaBR/api/watchlist/getPoints/${this.selectedWatchlist.xid}`)
            .subscribe(res => {
                this.watchlistPoints = res.json();
            }, err => {
                console.error('An error occured.' + err);
            });
        console.log(this.watchlistPoints);

        this.http.get('http://thecatapi.com/api/images/get?format=html&results_per_page=10')
            .map(res => res)
            .subscribe(res => {
                this.x = res;
                console.log(this.x._body);
            });

    }

    ngOnInit() {
        this.http.get(`/ScadaBR/api/watchlist/getNames`)
            .subscribe(res => {
                this.watchlists = res.json();
                this.selectedWatchlist = this.watchlists[0];
                this.http.get(`/ScadaBR/api/watchlist/getPoints/${this.selectedWatchlist.xid}`)
                    .subscribe(res => {
                        this.watchlistPoints = res.json();
                    }, err => {
                        console.error('An error occured.' + err);
                    });
            }, err => {
                console.error('An error occured.' + err);
            });
    }

}
