import { Component } from '@angular/core';

@Component({
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {
  showModal = false;
  private modalFunction(){
    this.showModal = !this.showModal;
  }
}
