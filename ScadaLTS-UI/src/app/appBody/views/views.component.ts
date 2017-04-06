import { Component, Inject, OnInit, ViewContainerRef } from '@angular/core';
import { Http } from '@angular/http';
import { MdDialog, MdDialogRef } from '@angular/material';

declare let $:any;

@Component({
  selector: 'views',
  templateUrl: './views.component.html',
  styleUrls: ['./views.component.css']
})

export class ViewsComponent implements OnInit {
  
  dataTree:any;
  createdTree:boolean = false;
  editDialogTree:boolean = false;

  moveStart:any;
  
  getEditDialogTree() {
    return this.editDialogTree;
  }

  constructor(@Inject(Http) public http: Http){   

      this.http.get(`../ScadaBR/api/view_hierarchy/getAll`)
            .subscribe(res => {
                this.dataTree = res.json();
            });

      console.log("this.editDialogTree:"+this.getEditDialogTree());
  }

    // loadIframe(){
  
  //   this.http.get(`../ScadaBR//api/view_hierarchy/getFirstViewId`)
  //           .subscribe(res => {
  //               var view = res.json();
  //               console.log(view);
  //               $('#ifr').attr('src','/ScadaBR/views.shtm?viewId='+view.key).on('load', function() {
  //                 $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle, #graphical').hide();
  //                 $('#infoSelectedViews').text(view.title);
  //               });
  //           });
  // }

  loadIframe() {
    $('#ifr').on('load', function () {
      $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').hide();
    });
  }

  refreshTree() {

     this.http.get(`../ScadaBR/api/view_hierarchy/getAll`)
            .subscribe(res => {
                this.dataTree = res.json();
                $("#viewsHierarchyDiv").fancytree("getTree").reload(this.dataTree);
            });
  }

  showViewHierarchy() {
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
                return true;
              },
              dragEnter: function(node, data) {
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


                if (data.otherNode.folder) {
                  if (node.folder) {
                    $.ajax({
                      type: "GET",
                      dataType: "json",
                      url:'../ScadaBR/api/view_hierarchy/moveFolder/' + data.otherNode.key + '/' + node.key ,
                      success: function(request){

                        $.ajax({
                          type: "GET",
          	              dataType: "json",
          	              url:'../ScadaBR/api/view_hierarchy/getAll',
          	              success: function(data){
                            console.log(data);
                            $("#viewsHierarchyDiv").fancytree("getTree").reload(data);
                            $("#dialogConfirmDelete").dialog("close");
                          },
          	              error: function(XMLHttpRequest, textStatus, errorThrown) {
          	                alert(textStatus);
          	              }
                        });
                        //TODO optimalization using moveTo  
                        //data.otherNode.moveTo(node, data.hitMode);
                      },
                      error: function(XMLHttpRequest, textStatus, errorThrown) {
                        console.log(JSON.parse(XMLHttpRequest.responseText).message);
                        console.log(textStatus);
                        console.log(errorThrown);
                     }
                    });
                  }
                } else {
                  $.ajax({
                    type: "GET",
                    dataType: "json",
                    url:'../ScadaBR/api/view_hierarchy/moveView/' + data.otherNode.key + '/' + node.key ,
                    success: function(request){
                      data.otherNode.moveTo(node, data.hitMode);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                      console.log(JSON.parse(XMLHttpRequest.responseText).message);
                      console.log(textStatus);
                      console.log(errorThrown);
                   }
                  });
                }
              }
            },
            source: this.dataTree
        });
        this.createdTree = true;
        $( "#dialogViewsHierarchy" ).dialog( "open" );
      } else {
        $( "#dialogViewsHierarchy" ).dialog( "open" );
      }
  }

  createNewFolder() {
    $( "#nameAddFolder" ).val("");
    $( "#addFolder" ).dialog("open");
  }

  editFolder() {
    if ($("#viewsHierarchyDiv").fancytree("getTree").getActiveNode() != null) {
      $( "#nameEditFolder" ).val( $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().title );
      $( "#editFolder" ).dialog("open");
    } else {
      $("#infoDialogText").text("Please select folder to edit");
      $("#infoDialog" ).dialog( "open" );
    }
  }

  deleteFolder() {
    //TODO check is folder 
    $( "#dialogConfirmDelete" ).dialog("open");
  }
  
  // TODO validate
  editFolderFunc() {
      //TODO rewrite to this.http.get
      $.ajax({
            type: "GET",
          	dataType: "json",
          	url:'../ScadaBR/api/view_hierarchy/editFolder/' + $("#nameEditFolder").val() + '/' + $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().key,
          	success: function(msg){
              console.log(msg);
  
              $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().setTitle($("#nameEditFolder").val());
              
          		$( "#editFolder" ).dialog("close");
          	},
          	error: function(XMLHttpRequest, textStatus, errorThrown) {
          	  console.log(textStatus);
          	}
          });
  }

  updateTips( t,  tips ) {
      tips
        .text( t )
        .addClass( "ui-state-highlight" );
      /*setTimeout(function() {
        tips.removeClass( "ui-state-highlight", 1500 );
      }, 500 );*/
  }
  
  // TODO validate
  addFolderFunc() {
    
    // validate TODO check 
    var valid = true;
    $('#addFolder').removeClass( "ui-state-error" );
    
    console.log($("#nameAddFolder").val());
    // check this name is used
    if (valid) {
        $.ajax({
            type: "GET",
            dataType: "json",
            url:'../ScadaBR/api/view_hierarchy/createFolder/' + $("#nameAddFolder").val() + '/-1',
            success: function(request){
              console.log(request);

               $.ajax({
                  type: "GET",
          	      dataType: "json",
          	      url:'../ScadaBR/api/view_hierarchy/getAll',
          	      success: function(data){
                    console.log(data);
                    $("#viewsHierarchyDiv").fancytree("getTree").reload(data);
                    $("#dialogConfirmDelete").dialog("close");
                  },
          	      error: function(XMLHttpRequest, textStatus, errorThrown) {
          	        alert(textStatus);
          	      }
                });  

              // TODO replace code above to optimalization 
              /*$("#viewsHierarchyDiv").fancytree("getRootNode").
                addChildren({
                  title: $("#nameAddFolder").val(),
                  tooltip: "",
                  folder: true
                });*/
              
              $( "#addFolder" ).dialog("close");
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
              alert(JSON.parse(XMLHttpRequest.responseText).message);
              //TODO
              /*$("#validateTipAddFolder").text(JSON.parse(XMLHttpRequest.responseText).message).addClass("ui-state-error");*/
              console.log(JSON.parse(XMLHttpRequest.responseText).message);
              console.log(textStatus);
              console.log(errorThrown);
            }
        });
    }
  }

  ngOnInit(){
    this.loadIframe();
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
            $("#infoDialogText").text("Select view, not folder");
            $("#dialogInfo" ).dialog( "open" );
          } else {
            
            $('#ifr').attr('src','/ScadaBR/views.shtm?viewId='+ $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().key).on('load', function() {
              $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle, #graphical').hide();
              console.log($("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().title);
              $("#infoSelectedViews").text( $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().title );
            });

            $(this).dialog( "close" );
          }
        },
        Cancel: function() {
          $( this ).dialog( "close" );
        }
      }
    });
    $( "#infoDialog" ).dialog({
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

    $( "#addFolder" ).dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      buttons: {
        Save: this.addFolderFunc,
        Cancel: function() {
          $(this).dialog( "close" );
        }
      },
      close: function() {
        $(this).dialog("close");
      }
    });

    $( "#editFolder" ).dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      buttons: {
        Save: this.editFolderFunc,
        Cancel: function() {
          $(this).dialog( "close" );
        }
      },
      close: function() {
        $(this).dialog("close");
      }
    });

    $( "#dialogConfirmDelete" ).dialog({
      autoOpen: false,
      resizable: false,
      height: "auto",
      width: 400,
      modal: true,
      buttons: {
        "Delete item": function() {

          if ($("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().folder) {

            $.ajax({
              type: "GET",
          	  dataType: "json",
          	  url:'../ScadaBR//api/view_hierarchy/deleteFolder/' + $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().key,
          	  success: function(msg){
                $.ajax({
                  type: "GET",
          	      dataType: "json",
          	      url:'../ScadaBR/api/view_hierarchy/getAll',
          	      success: function(data){
                    console.log(data);
                    $("#viewsHierarchyDiv").fancytree("getTree").reload(data);
                    $("#dialogConfirmDelete").dialog("close");
                  },
          	      error: function(XMLHttpRequest, textStatus, errorThrown) {
          	        alert(textStatus);
          	      }
                });  
          	  },
          	  error: function(XMLHttpRequest, textStatus, errorThrown) {
          	    alert(textStatus);
          	  }
            });

          } else {

            $.ajax({
              type: "GET",
          	  dataType: "json",
          	  url:'../ScadaBR//api/view_hierarchy/deleteView/' + $("#viewsHierarchyDiv").fancytree("getTree").getActiveNode().key,
          	  success: function(msg){
                $.ajax({
                  type: "GET",
          	      dataType: "json",
          	      url:'../ScadaBR/api/view_hierarchy/getAll',
          	      success: function(data){
                    console.log(data);
                    $("#viewsHierarchyDiv").fancytree("getTree").reload(data);
                    $("#dialogConfirmDelete").dialog("close");
                  },
          	      error: function(XMLHttpRequest, textStatus, errorThrown) {
          	        alert(textStatus);
          	      }
                });  
          	  },
          	  error: function(XMLHttpRequest, textStatus, errorThrown) {
          	    alert(textStatus);
          	  }
            });
          }
        },
        Cancel: function() {
          $( this ).dialog( "close" );
        }
      }
    });
    this.loadIframe();
  }
  ngOnDestroy(){ //test
    $( "#dialogViewsHierarchy" ).dialog( "close" );
  }
}



