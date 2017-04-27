import {Injectable, OnInit} from '@angular/core';
import {Http} from '@angular/http';

@Injectable()
export class UserAuthenticationService implements OnInit {
    isUserAuthenticated: boolean = false;
    username: string = 'admin';

    constructor(private http: Http) {}


    ngOnInit(){
        this.http.get(`http://localhost/ScadaBR/api/auth/isLogged/${this.username}`)
            .subscribe(res => {
                    this.isUserAuthenticated = res.json();
                },
                err => {
                    console.error('An error occured.' + err);
                });
    }


}