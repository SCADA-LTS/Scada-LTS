import { Component, Inject, OnInit } from '@angular/core';
import {Http} from '@angular/http';

declare let $:any;

@Component({
  selector: 'views',
  templateUrl: './views.component.html',
  styleUrls: ['./views.component.css']
})
export class ViewsComponent implements OnInit {
  
  tree:any;
  createdTree: boolean = false;

  constructor(@Inject(Http) private http: Http){
    this.http.get(`/ScadaBR/api/view_hierarchy/getAll`)
            .subscribe(res => {
                this.tree = res.json();
                console.log(this.tree);
            });
    console.log(this.tree);
  }

  loadIframe(){
    $('#ifr').on('load', function() {
        $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').hide();
    });
  }

  showViewHierarchy() {
    if (this.createdTree == false) {
       $('#viewsHierarchyDiv').fancytree({
          source: this.tree
       });
       this.createdTree = true;
    } else {
       $('#viewsHierarchyDiv').toggle();
    }
  }

  testClick() {
    alert("test");
  }

  ngOnInit(){
    this.loadIframe();
  }

}
