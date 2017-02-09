import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {Http} from '@angular/http';
import {FormGroup, FormBuilder, Validators} from "@angular/forms";

@Component({
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})

export class LoginComponent {

    private username: string = "";
    private password: string = "";
    private showModal: boolean = false;
    private response: string;
    private isPasswordIncorrect: boolean = false;
    isFormEmpty: boolean = true;
    form: FormGroup;
    isLoggingActive: boolean = false;

    private modal() {
        this.showModal = !this.showModal;
    }

    private handle(error: any): Promise<any> {
        console.error('An error occurred!', error);
        return Promise.reject(error.message || error);
    };

    constructor(public fb: FormBuilder, private http: Http, private router: Router) {
        this.username = 'admin';
        this.password = 'admin';
        this.form = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    };


    private login() {
        this.isPasswordIncorrect = false;
        this.isLoggingActive = true;
        console.log(this.form.status);
        if (this.form.status == 'VALID') {
            this.http.get(`/ScadaBR/api/auth/${this.username}/${this.password}`)
                .map(res => res.json())
                .catch(this.handle)
                .subscribe(res => {
                    this.response = res;
                    if (this.response) {
                        console.log('access granted');
                        this.router.navigate(['/appBody']);
                    } else {
                        console.log('access denied');
                        this.isPasswordIncorrect = true;
                    }
                    this.isLoggingActive = false;
                });
        } else {
            this.isFormEmpty = false;
        }
    }

}
