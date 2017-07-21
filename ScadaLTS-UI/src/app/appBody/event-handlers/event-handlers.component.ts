import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-event-handlers',
    templateUrl: 'event-handlers.component.html',
    styleUrls: ['event-handlers.component.css']
})
export class EventHandlersComponent implements OnInit {

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

