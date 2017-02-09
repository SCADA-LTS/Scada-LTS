import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'system',
  templateUrl: 'system.component.html',
  styleUrls: ['system.component.css']
})
export class SystemComponent implements OnInit {

  systemPerformance: string = 'low';

  constructor() { }

  ngOnInit() {
  }

}
