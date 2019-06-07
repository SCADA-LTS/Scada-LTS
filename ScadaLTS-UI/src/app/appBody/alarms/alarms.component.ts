import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-alarms',
    templateUrl: 'alarms.component.html',
    styleUrls: ['alarms.component.css']
})
export class AlarmsComponent implements OnInit {

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
