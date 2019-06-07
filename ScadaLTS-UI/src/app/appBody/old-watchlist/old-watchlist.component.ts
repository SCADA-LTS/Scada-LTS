import {Component, OnInit} from '@angular/core';
declare let $: any;

@Component({
    selector: 'app-old-watchlist',
    templateUrl: 'old-watchlist.component.html',
    styleUrls: ['old-watchlist.component.css']
})
export class OldWatchlistComponent implements OnInit {

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

