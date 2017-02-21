import { TreeDialogService } from './tree-dialog/tree-dialog.service';
import { Component, Inject, OnInit } from '@angular/core';
import {Http} from '@angular/http';
import {MdDialog, MdDialogRef} from '@angular/material';


declare let $:any;

@Component({
  selector: 'views',
  templateUrl: './views.component.html',
  styleUrls: ['./views.component.css']
})
export class ViewsComponent implements OnInit {
  
  tree:any;
  createdTree: boolean = false;
  selectedOption: string;
  public result: any;
  
  constructor(@Inject(Http) private http: Http
    //,private dialogsService: TreeDialogService
  ){
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
    console.log("showViewHierarchy()");
    console.log(this.tree);
    if (this.createdTree == false) {
         $('#viewsHierarchyDiv').fancytree({
            extensions: ["dnd"],
            dnd: {
              autoExpandMS: 400,
              draggable: { // modify default jQuery draggable options
                zIndex: 1000,
                scroll: false,
                containment: "parent",
                revert: "invalid"
              },
              preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
              preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
          
              dragStart: function(node, data) {
                // This function MUST be defined to enable dragging for the tree.
                // Return false to cancel dragging of node.
          //    if( data.originalEvent.shiftKey ) ...          
          //    if( node.isFolder() ) { return false; }
                return true;
              },
              dragEnter: function(node, data) {
                /* data.otherNode may be null for non-fancytree droppables.
                 * Return false to disallow dropping on node. In this case
                 * dragOver and dragLeave are not called.
                 * Return 'over', 'before, or 'after' to force a hitMode.
                 * Return ['before', 'after'] to restrict available hitModes.
                 * Any other return value will calc the hitMode from the cursor position.
                 */
                // Prevent dropping a parent below another parent (only sort
                // nodes under the same parent):
          //    if(node.parent !== data.otherNode.parent){
          //      return false;
          //    }
                // Don't allow dropping *over* a node (would create a child). Just
                // allow changing the order:
          //    return ["before", "after"];
                // Accept everything:
                return true;
              },
              dragExpand: function(node, data) {
                // return false to prevent auto-expanding data.node on hover
              },
              dragOver: function(node, data) {
              },
              dragLeave: function(node, data) {
              },
              dragStop: function(node, data) {
              },
              dragDrop: function(node, data) {
                // This function MUST be defined to enable dropping of items on the tree.
                // data.hitMode is 'before', 'after', or 'over'.
                // We could for example move the source to the new target:
                data.otherNode.moveTo(node, data.hitMode);
              }
            },
            source: this.tree
        });
        this.createdTree = true;
    } else {
       $('#viewsHierarchyDiv').toggle();
    }
  }

  ngOnInit(){
    this.loadIframe();
  }

}

