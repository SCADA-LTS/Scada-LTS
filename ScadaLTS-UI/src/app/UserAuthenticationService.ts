import {Injectable} from '@angular/core';
import {Http} from '@angular/http';

@Injectable()
export class UserAuthenticationService {
    isUserAuthenticated: boolean = false;
    username: string;

    constructor(private http: Http) {
    }

    authentication() {
        this.http.get(`http://localhost/ScadaBR/api/auth/isLogged/${this.username}`)
            .subscribe(res => {
                    this.isUserAuthenticated = res.json();
                },
                err => {
                    console.error('An error occured.' + err);
                });
    }


}