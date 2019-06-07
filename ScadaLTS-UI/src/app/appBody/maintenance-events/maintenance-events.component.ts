import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-maintenance-events',
    templateUrl: 'maintenance-events.component.html',
    styleUrls: ['maintenance-events.component.css']
})
export class MaintenanceEventsComponent implements OnInit {

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
