import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-point-links',
    templateUrl: 'point-links.component.html',
    styleUrls: ['point-links.component.css']
})
export class PointLinksComponent implements OnInit {

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