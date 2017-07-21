import { Component, OnInit } from '@angular/core';
declare let $: any;

@Component({
  selector: 'app-data-sources',
  templateUrl: 'data-sources.component.html',
  styleUrls: ['data-sources.component.css']
})
export class DataSourcesComponent implements OnInit {

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
