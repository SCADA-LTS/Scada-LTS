import {Injectable, OnInit} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from "rxjs/Observable";

@Injectable()
export class UserAuthenticationService implements OnInit {
    public isUserAuthenticated: Observable<boolean>;
    username: string = 'admin';

    constructor(private http: Http) {
        this.isUserAuthenticated = this.http.get(`http://localhost/ScadaBR/api/auth/isLogged/${this.username}`)
            .map(res => res.json())
            .share();
    }

    // x() {
    //     return this.http.get(`http://localhost/ScadaBR/api/auth/isLogged/${this.username}`)
    //         .map((res: Response) => {
    //             if (res) {
    //                 localStorage.setItem('currentUser', 'true');
    //             } else {
    //                 localStorage.clear();
    //             }
    //         })
    // }

    ngOnInit() {

    }

}