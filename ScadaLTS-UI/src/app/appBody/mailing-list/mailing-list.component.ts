import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-mailing-list',
    templateUrl: 'mailing-list.component.html',
    styleUrls: ['mailing-list.component.css']
})
export class MailingListComponent implements OnInit {

    constructor() {
    }

    loadIframe() {
        $('#ifr').on('load', function () {
            $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').css("display","none");
            $('#ifr').css("visibility","visible");
        });
    }

    ngOnInit() {
        this.loadIframe();
    }

}