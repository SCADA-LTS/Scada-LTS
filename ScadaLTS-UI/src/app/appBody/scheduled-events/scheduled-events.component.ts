import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-scheduled-events',
    templateUrl: 'scheduled-events.component.html',
    styleUrls: ['scheduled-events.component.css']
})
export class ScheduledEventsComponent implements OnInit {

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