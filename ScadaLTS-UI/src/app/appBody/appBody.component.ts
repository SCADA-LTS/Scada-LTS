import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    templateUrl: './appBody.component.html',
    styleUrls: ['./appBody.component.css']
})
export class AppBodyComponent implements OnInit {
    isHiddenSubMenu = [false, false, false, false, false];

    constructor(private router: Router) {
    };

    private hideElements() {
        this.isHiddenSubMenu = this.isHiddenSubMenu.map(v => v = false);
    }

    messages = [
        {title: 'Element blocked'},
        {title: 'Pressure warning'},
        {title: 'Message from admin'},
        {title: 'Monthly review'}
    ];

    isMenuHidden: boolean = false;
    isMenuShrunken: boolean = false;

    private checkScreenWidth() {
        if (window.innerWidth < 1200 && window.innerWidth > 499) {
            this.isMenuShrunken = true;
            this.isMenuHidden = false;
            this.hideElements();
        } else if (window.innerWidth < 500) {
            this.isMenuShrunken = false;
            this.isMenuHidden = true;
            this.hideElements();
        } else {
            this.isMenuHidden = false;
            this.isMenuShrunken = false;
        }
    }

    private toggleMenu() {
        if (window.innerWidth < 1200 && window.innerWidth > 499) {
            this.isMenuHidden = false;
            this.isMenuShrunken = !this.isMenuShrunken;
        } else if (window.innerWidth < 500) {
            this.isMenuHidden = !this.isMenuHidden;
        } else {
            this.isMenuShrunken = !this.isMenuShrunken;
        }
    }

    private logout() {
        this.router.navigate(['/']);
    }

    ngOnInit() {
        this.checkScreenWidth();
    }

}

