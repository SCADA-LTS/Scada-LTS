import { Component } from '@angular/core';

@Component({
  selector: 'app-static-content',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app works!';
  isHiddenSubMenu = [false, false, false, false, false];

  private hideElements(){
    this.isHiddenSubMenu = this.isHiddenSubMenu.map(v => v = false);
  }
}
