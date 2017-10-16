import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class ConnectionService {

    apiURL = '/ScadaBR/api/';

    constructor(private http: Http) { }

    /**
     * Authenticate session
     * Establish connection with a remote server
     * @param username - API username
     * @param password - API user password
     */
    authenticate(username: string, password: string): void {

        this.http.get(this.apiURL + 'auth/' + username + '/' + password).subscribe(
            response => {
                console.debug(response);
            }, err => {
                console.error('Error during the authentication! ' + err);
            }
        );

    }

    /**
     * Get Data From Remote Server
     * Use HTTP GET method to recive any data from server.
     * @param request - API URL method to invoke
     * @param parameters - request parameters (ex. DataPointXid)
     */
    getDataFromRemote(request: string, parameters: string): Promise<any> {

        return this.http.get(this.apiURL + request + parameters).toPromise()
            .then(response => {
                console.debug(response);
                return response;
            });
    }

    /**
     * Post Data To Remote Server
     * Use HTTP POST method to send data to server.
     * @param request - API URL method to invoke
     * @param parameters - request parameters (ex. DataPointXid value)
     */
    postDataToRemote(request: string, parameters: string): void {

        const headers = new Headers({ 'Content-Type': 'application/text;charset=utf-8' });
        this.http.post(this.apiURL + request + parameters, '', { headers: headers })
            .subscribe((data) => console.info('POST Done!'));

    }
}
