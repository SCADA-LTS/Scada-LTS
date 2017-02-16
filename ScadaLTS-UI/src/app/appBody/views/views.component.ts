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
    $('#viewsHierarchyDiv').fancytree({
        source: [
          {title: "Node 1", key: "1"},
            {title: "Folder 2", key: "2", folder: true, children: [
              {title: "Node 2.1", key: "3", myOwnAttr: "abc"},
              {title: "Node 2.2", key: "4"}
            ]}
        ],
        extensions: ["edit", "dnd", "table", "gridnav"]
     });
  }

  ngOnInit(){
    this.loadIframe();
  }

}
