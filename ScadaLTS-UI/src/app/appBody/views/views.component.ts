import { Component, OnInit } from '@angular/core';
declare let $:any;

@Component({
  selector: 'views',
  templateUrl: './views.component.html',
  styleUrls: ['./views.component.css']
})
export class ViewsComponent implements OnInit {

  constructor(){}

  loadIframe(){
    /*$('#ifr').on('load', function() {
        $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').hide();
    });*/
  }



  ngOnInit(){
    this.loadIframe();
  }

}
