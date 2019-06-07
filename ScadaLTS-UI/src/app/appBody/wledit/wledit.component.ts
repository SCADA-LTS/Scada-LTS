import {Component, OnInit} from '@angular/core';
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

    watchlistsArray = ['WL1', 'WL2', 'WL3', 'WL4', 'WL5', 'WL6'];

    pointsArray = [];
    pointName: string = '';
    bool: boolean = false;

    term$ = new Subject<string>();

    constructor(private http: Http) {
        this.term$
            .debounceTime(300)
            .subscribe(term => this.getPoints())
    }

    getPoints() {

        if (this.pointName.length >= 3) {

                this.http.get(`./points.json`)
                    .subscribe(res => {
                        this.pointsArray = res.json();
                        this.pointsArray = this.pointsArray.filter(v => v.name.includes(this.pointName));
                    });
                return console.log('fired!');

        } else {
            this.pointsArray = [];
        }
    };


    ngOnInit() {
    }

}
