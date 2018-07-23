import { Component, OnInit } from '@angular/core';
declare let $: any;

@Component({
  selector: 'system',
  templateUrl: 'system.component.html',
  styleUrls: ['system.component.css']
})
export class SystemComponent implements OnInit {

  systemPerformance: string;

  constructor() {}

  exportUserSystemPerformance(){
    localStorage.setItem('systemPerf', JSON.stringify(this.systemPerformance));
  }

  loadIframe() {
        $('#ifr').on('load', function () {
            $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').css("display","none");
            $('#ifr').css("visibility","visible");
        });
    }

  ngOnInit() {
    localStorage['systemPerf'] == undefined ? this.systemPerformance = 'low' : this.systemPerformance = JSON.parse(localStorage.getItem('systemPerf'));
    this.loadIframe();
  }


}
