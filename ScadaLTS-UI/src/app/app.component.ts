import { Component, Compiler, OnInit } from '@angular/core';

@Component({
  selector: 'app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private _compiler: Compiler) {

  }

  ngOnInit() {
    this._compiler.clearCache();
  }

}

