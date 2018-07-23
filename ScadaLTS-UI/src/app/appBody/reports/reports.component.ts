import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-reports',
    templateUrl: 'reports.component.html',
    styleUrls: ['reports.component.css']
})
export class ReportsComponent implements OnInit {

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
