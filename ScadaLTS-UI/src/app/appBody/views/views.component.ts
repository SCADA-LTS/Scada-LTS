import { Component, Inject, OnInit, ViewContainerRef } from '@angular/core';
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

  constructor(@Inject(Http) private http: Http){
      this.http.get(`/ScadaBR/api/view_hierarchy/getAll`)
            .subscribe(res => {
                this.tree = res.json();
                console.log(this.tree);
            });
      console.log(this.tree);
  }

  loadIframe(){
    //TODO get name viewId and set name
    $('#ifr').attr('src','/ScadaBR/views.shtm?viewId=').on('load', function() {
      $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle, #graphical').hide();
    });
  }

  showViewHierarchy() {
      console.log("showViewHierarchy()");
      console.log(this.tree);
      if (this.createdTree == false) {
         $('#viewsHierarchyDiv').fancytree({
            extensions: ["dnd"],
            dnd: {
              autoExpandMS: 200,
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
        $( "#dialogViewsHierarchy" ).dialog( "open" );
      } else {
        $( "#dialogViewsHierarchy" ).dialog( "open" );
      }
  }

  createNewFolder() {
    $( "#addOrEditFolder" ).dialog("open");
  }

  addFolder() {
    var valid = true;
     /* allFields.removeClass( "ui-state-error" );
 
      valid = valid && checkLength( name, "username", 3, 16 );
      valid = valid && checkLength( email, "email", 6, 80 );
      valid = valid && checkLength( password, "password", 5, 16 );
 
      valid = valid && checkRegexp( name, /^[a-z]([0-9a-z_\s])+$/i, "Username may consist of a-z, 0-9, underscores, spaces and must begin with a letter." );
      valid = valid && checkRegexp( email, emailRegex, "eg. ui@jquery.com" );
      valid = valid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );
 
      if ( valid ) {
        $( "#users tbody" ).append( "<tr>" +
          "<td>" + name.val() + "</td>" +
          "<td>" + email.val() + "</td>" +
          "<td>" + password.val() + "</td>" +
        "</tr>" );
        dialog.dialog( "close" );
      }*/
      alert("test7");
      return valid;
  }
  
  ngOnInit(){
    $( "#dialogViewsHierarchy" ).dialog({
      autoOpen: false,
      show: {
        effect: "blind",
        duration: 500
      },
      hide: {
        effect: "blind",
        duration: 500
      },
      buttons: {
        "Select view": function() {
          if ($("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().folder) {
            this.textInfoDialog = "Select view, not folder";
            $("#dialogInfo" ).dialog( "open" );
          } else {
            $("#infoSelectedViews").text($("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().title);
            $('#ifr').attr('src','/ScadaBR/views.shtm?viewId='+ $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().key).on('load', function() {
              $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle, #graphical').hide();
            });
            // load new view in iframe.
            $(this).dialog( "close" );
          }
        },
        Cancel: function() {
          $( this ).dialog( "close" );
        }
      }
    });
    $( "#dialogInfo" ).dialog({
      autoOpen: false,
      show: {
        effect: "blind",
        duration: 500
      },
      hide: {
        effect: "blind",
        duration: 500
      }
    });

    $( "#addOrEditFolder" ).dialog({
      autoOpen: false,
      height: 200,
      width: 350,
      modal: true,
      buttons: {
        "Create a new folder": this.addFolder,
        Cancel: function() {
          $(this).dialog( "close" );
        }
      },
      close: function() {
        $(this).form[ 0 ].reset();
        $(this).allFields.removeClass( "ui-state-error" );
      }
    });

    this.loadIframe();
  }

}

