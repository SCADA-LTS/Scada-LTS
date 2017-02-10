import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'system',
  templateUrl: 'system.component.html',
  styleUrls: ['system.component.css']
})
export class SystemComponent implements OnInit {

  systemPerformance: string;


  constructor() {

  }

  exportUserSystemPerformance(){
    localStorage.setItem('systemPerf', JSON.stringify(this.systemPerformance));
  }


  ngOnInit() {
    localStorage['systemPerf'] == undefined ? this.systemPerformance = 'low' : this.systemPerformance = JSON.parse(localStorage.getItem('systemPerf'));
  }

}
