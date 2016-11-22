import { Component } from '@angular/core';

@Component({
  templateUrl: './appBody.component.html',
  styleUrls: ['./appBody.component.css']
})
export class AppBodyComponent {
  isHiddenSubMenu = [false, false, false, false, false];

  private hideElements(){
    this.isHiddenSubMenu = this.isHiddenSubMenu.map(v => v = false);
  }
}
