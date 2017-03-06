import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-users',
    templateUrl: 'users.component.html',
    styleUrls: ['users.component.css']
})
export class UsersComponent implements OnInit {

    constructor() {
    }

    loadIframe() {
        $('#ifr').on('load', function () {
            $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').hide();
        });
    }

    ngOnInit() {
        this.loadIframe();
    }

}
