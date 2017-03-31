import {Component, OnInit} from '@angular/core';
declare let $: any;


@Component({
    selector: 'app-compound-events',
    templateUrl: 'compound-events.component.html',
    styleUrls: ['compound-events.component.css']
})
export class CompoundEventsComponent implements OnInit {

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
