import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-point-hierarchy',
    templateUrl: 'point-hierarchy.component.html',
    styleUrls: ['point-hierarchy.component.css']
})
export class PointHierarchyComponent implements OnInit {

    constructor() {
    }

    loadIframe() {
        $('#ifr').on('load', function () {
            $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle, .nav').css("display","none");
            $('#ifr').css("visibility","visible");
        });
    }

    ngOnInit() {
        this.loadIframe();
    }

}
