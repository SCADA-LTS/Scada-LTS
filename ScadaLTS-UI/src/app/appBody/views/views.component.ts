import { toUnicode } from 'punycode';
import { Component, Inject, OnInit, ViewContainerRef } from '@angular/core';
import { Http } from '@angular/http';
import { MdDialog, MdDialogRef } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from "@angular/forms";

declare let $:any;

@Component({
  selector: 'views',
  templateUrl: './views.component.html',
  styleUrls: ['./views.component.css']
})

export class ViewsComponent implements OnInit {

  private viewKey:number=-1;

  constructor(@Inject(Http) public http: Http, public dialogSelectViewWithEdtHierarchyView: MdDialog ){}

  openDlgSelectViewWithEdtHierarchyView(){

    let dialogRef = this.dialogSelectViewWithEdtHierarchyView.open(DlgSelectViewWithEdtHierarchyView, this.dialogSelectViewWithEdtHierarchyView);
    dialogRef.afterClosed().subscribe(result => {
          this.viewKey = result;
          console.log(this.viewKey);        
    });
  };

  loadIframe() {
    this.http.get(`../ScadaBR//api/view_hierarchy/getFirstViewId`)
             .subscribe(res => {
                 var view = res.json();
                 console.log(view);
                 this.viewKey = view.key;
                 $('#ifr').attr('src','/ScadaBR/views.shtm?viewId='+view.key).on('load', function () { 
                    //var iframeDocument = document.getElementById('myframe').contentWindow.document;
                    $('#ifr').contents().find('#mainHeader, #subHeader, #graphical, #fsOut, .footer').css("display","none");
                    $('#ifr').css("visibility","visible");
                    // $('#infoSelectedViews').text(view.title);
                });
    }); 
  }

  addView() {
    $('#ifr').css("visibility","hidden");             
    $('#ifr').attr('src','/ScadaBR/view_edit.shtm').on('load', function() {
        $('#ifr').contents().find('#mainHeader, #subHeader, #graphical, #fsOut, .footer').css("display","none");
        $('#ifr').css("visibility","visible");
        // $('#infoSelectedViews').text($("#tree").fancytree("getTree").getActiveNode().title);
    });
  }

  editView() {
    $('#ifr').css("visibility","hidden");             
    $('#ifr').attr('src','/ScadaBR/view_edit.shtm?viewId='+ this.viewKey).on('load', function() {
        $('#ifr').contents().find('#mainHeader, #subHeader, #graphical, #fsOut, .footer').css("display","none");
        $('#ifr').css("visibility","visible");
        // $('#infoSelectedViews').text($("#tree").fancytree("getTree").getActiveNode().title);
    });

  }

  updateTips( t,  tips ) {
      tips
        .text( t )
        .addClass( "ui-state-highlight" );
  }

  ngOnInit(){
    this.loadIframe();
    
  }
}

@Component({
  selector: 'dialog-select-view',
  template: `
  <h4>Select view</h4>
  <md-dialog-actions *ngIf="isRoleAdmin">
        <md-toolbar>
          <button class="button_toolbar" md-mini-fab (click)="openDlgAddFolderHierarchyView()" mdTooltip="Create new folder" [mdTooltipPosition]="'below'"><md-icon>create_new_folder</md-icon></button>  
          <button class="button_toolbar" md-mini-fab (click)="deleteFolderHierarchyView()" mdTooltip="Delete folder" [mdTooltipPosition]="'below'"><md-icon>delete</md-icon></button>  
        </md-toolbar>
  </md-dialog-actions>
  
  <md-dialog-content>
      <div id="tree"></div>
  </md-dialog-content> 
  <md-dialog-actions>
      <button md-button (click)="select()" mdTooltip="Select view" [mdTooltipPosition]="'below'">Select</button>  
      <button md-button md-button-close (click)="refDlg.close();" mdTooltip="Close" [mdTooltipPosition]="'below'">Close</button>  
  </md-dialog-actions>
  `,
  styleUrls: ['./views.component.css']
})
export class DlgSelectViewWithEdtHierarchyView {

  dataTree:any;
  isRoleAdmin:boolean = false;

  public getParentId = function(node) {
    if (node != undefined) {
      var parentId=0;
      try {
        parentId = parseInt(node.parent.key);
      	if (isNaN(parentId)) {
      	  parentId=-1;
      	}
      } catch (e) { }
      return parentId;
    } else {
      return undefined;
    }
  }

  constructor(@Inject(Http) public http: Http, public dialogAddFolderHierarchyView: MdDialog, public dialogConfirmDeleteFolderHierarchyView: MdDialog, public refDlg: MdDialogRef<DlgSelectViewWithEdtHierarchyView>){
      
      this.http.get(`/ScadaBR//api/auth/isRoleAdmin`)
        .subscribe(resIsRoleAdm => {
          this.isRoleAdmin = resIsRoleAdm.json();
          this.http.get(`../ScadaBR/api/view_hierarchy/getAll`)
              .subscribe(res => {
                  this.dataTree = res.json();
                  $('#tree').fancytree({
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
                  console.log("dragStart:"+resIsRoleAdm.json());
                  return resIsRoleAdm.json();
                },
                dragEnter: function(node, data) {
                  console.log("dragEnter:"+resIsRoleAdm.json());
                  return resIsRoleAdm.json();
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
                  var rootId = -1;
                  var getParentId = function(node) {
                    var parentId=rootId;
                    if (node != undefined) {
                      if (data.hitMode=="before") {
                        try {
                          parentId = parseInt(node.parent.key);
                          if (isNaN(parentId)) {
                            return rootId;
                          } else {
                            return parentId;
                          }
                        } catch (e) {
                          console.log(e);
                        }
                      } else {
                          return node.key;
                      }   
                    } else {
                      return -1;
                    }
                  }
                  console.log("data.hitMode:"+data.hitMode);
                  console.log("new parentId:"+getParentId(node));
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
                              $("#tree").fancytree("getTree").reload(data);
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
                      url:'../ScadaBR/api/view_hierarchy/moveView/' + data.otherNode.key + '/' + getParentId(node) ,
                      success: function(request){
                        //$("#tree").fancytree("getTree").reload(data);
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
        });
      }, err => {
          console.error('An error occured.' + err);
      });

      
  }

  select() {
    //TODO info not select view
          if (!$("#tree").fancytree("getTree").getActiveNode().folder) {
            $('#ifr').css("visibility","hidden");             
            $('#ifr').attr('src','/ScadaBR/views.shtm?viewId='+ $("#tree").fancytree("getTree").getActiveNode().key).on('load', function() {

              $('#ifr').contents().find('#mainHeader, #subHeader, #graphical, #fsOut, .footer').css("display","none");
              $('#ifr').css("visibility","visible");
              
              // $('#infoSelectedViews').text($("#tree").fancytree("getTree").getActiveNode().title);

            });
            this.refDlg.close($("#tree").fancytree("getTree").getActiveNode().key);
          }


  }

  openDlgAddFolderHierarchyView(){
      let dialogRef = this.dialogAddFolderHierarchyView.open(DlgAddFolderHierarchyView, this.dialogAddFolderHierarchyView);
      dialogRef.afterClosed().subscribe(result => {
          this.rerfreshTree()        
      });
  };

  deleteFolderHierarchyView(){
    if ($("#tree").fancytree("getTree").getActiveNode().folder) {
      let dialogRef = this.dialogConfirmDeleteFolderHierarchyView.open(DlgConfirmDeleteFolderHierarchyView, this.dialogConfirmDeleteFolderHierarchyView);
      //TODO catch error
      dialogRef.afterClosed().subscribe(result => {
          if (result) {
             console.log("remove");
             this.http.get(`../ScadaBR//api/view_hierarchy/deleteFolder/` + $("#tree").fancytree("getTree").getActiveNode().key)
               .subscribe(res => {
                 dialogRef.close();
                 this.rerfreshTree();
             });
          }
      });
    }
  }

  rerfreshTree() {
      $.ajax({
        type: "GET",
        dataType: "json",
        url:'../ScadaBR/api/view_hierarchy/getAll',
        success: function(data){
          console.log(data);
          $("#tree").fancytree("getTree").reload(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
          alert(textStatus);
        }
      });  
  }
}

@Component({
  selector: 'dialog-add-folder',
  template: `
  
  <h4 md-dialog-title> Add folder </h4>
  <md-dialog-content>
      <div novalidate [formGroup]="form">
        <md-input-container>
              <input mdInput formControlName="foldername" placeholder="folder name" type="text" [(ngModel)]="foldername" autofocus>
        </md-input-container>
      </div>      
  </md-dialog-content> 
  <md-dialog-actions>
     <a md-button (click)="addFolder();" mdTooltip="Add Folder" [mdTooltipPosition]="'below'">Add </a>    
     <a md-button md-button-close (click)="refDlg.close();" mdTooltip="Close" [mdTooltipPosition]="'below'">Close</a>
  </md-dialog-actions>
  `
})
export class DlgAddFolderHierarchyView{
    private foldername: string = "";
    form: FormGroup;
    
    constructor(public fb: FormBuilder, private http: Http, public refDlg: MdDialogRef<DlgAddFolderHierarchyView>) {
        this.foldername = '';
        this.form = this.fb.group({
            foldername: ['', Validators.required]
        });
    };

    addFolder() {
        this.http.get(`../ScadaBR/api/view_hierarchy/createFolder/` + this.foldername + '/-1')
            .subscribe(res => {
              this.refDlg.close();
        });
    }
}

@Component({
  selector: 'dialog-confirm-delete-folder',
  template: `
  <h4 md-dialog-title>Confirm delete folder</h4>
  <md-dialog-content>
      <p>Do you want to delete the folder?</p>
  </md-dialog-content> 
  <md-dialog-actions>
     <a md-button (click)="delete();" mdTooltip="Delete" [mdTooltipPosition]="'below'">Delete </a>    
     <a md-button md-button-close (click)="refDlg.close();" mdTooltip="Close" [mdTooltipPosition]="'below'">Close</a>
  </md-dialog-actions>
  `
})
export class DlgConfirmDeleteFolderHierarchyView{
    
    constructor(public refDlg: MdDialogRef<DlgConfirmDeleteFolderHierarchyView>) {};

    delete() {
        this.refDlg.close(true);
    }
}

