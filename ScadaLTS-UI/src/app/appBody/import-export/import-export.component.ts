import { Component, OnInit } from '@angular/core';
declare let $: any;

@Component({
  selector: 'app-import-export',
  templateUrl: 'import-export.component.html',
  styleUrls: ['import-export.component.css']
})
export class ImportExportComponent implements OnInit {

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
