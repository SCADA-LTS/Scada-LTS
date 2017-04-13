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

  constructor(@Inject(Http) public http: Http, public dialogSelectViewWithEdtHierarchyView: MdDialog ){}

  openDlgSelectViewWithEdtHierarchyView(){
        this.dialogSelectViewWithEdtHierarchyView.open(DlgSelectViewWithEdtHierarchyView, this.dialogSelectViewWithEdtHierarchyView);
  };

  loadIframe() {
    this.http.get(`../ScadaBR//api/view_hierarchy/getFirstViewId`)
             .subscribe(res => {
                 var view = res.json();
                 console.log(view);
                 $('#ifr').attr('src','/ScadaBR/views.shtm?viewId='+view.key).on('load', function () {
                    $('#ifr').contents().find('#mainHeader, #subHeader, .footer, .smallTitle').hide();
                    $('#infoSelectedViews').text(view.title);
                });
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
  <md-dialog-actions>
      <button md-mini-fab (click)="openDlgAddFolderHierarchyView()" mdTooltip="Create new folder" [mdTooltipPosition]="'below'"><md-icon>create_new_folder</md-icon></button>  
      <button md-mini-fab (click)="deleteFolderHierarchyView()" mdTooltip="Delete folder" [mdTooltipPosition]="'below'"><i class="material-icons orange600">face</i></button>  
  </md-dialog-actions>
  <md-dialog-content>
      <div id="tree"></div>
  </md-dialog-content> 
  `
})
export class DlgSelectViewWithEdtHierarchyView{

  dataTree:any;

  constructor(@Inject(Http) public http: Http, public dialogAddFolderHierarchyView: MdDialog, public dialogConfirmDeleteFolderHierarchyView: MdDialog){
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
                    url:'../ScadaBR/api/view_hierarchy/moveView/' + data.otherNode.key + '/' + node.key ,
                    success: function(request){
                      console.log(request);
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
        //TODO catch error !!!
        this.http.get(`../ScadaBR/api/view_hierarchy/createFolder/` + this.foldername + '/-1')
            .subscribe(res => {
              this.refDlg.close();
        });
        
        // /*$.ajax({
        //     type: "GET",
        //     dataType: "json",
        //     url:'../ScadaBR/api/view_hierarchy/createFolder/' + this.foldername + '/-1',
        //     success: function(request){
        //         this.myclose();
        //     },
        //     error: function(XMLHttpRequest, textStatus, errorThrown) {
        //       alert(JSON.parse(XMLHttpRequest.responseText).message);
        //       //TODO
        //       /*$("#validateTipAddFolder").text(JSON.parse(XMLHttpRequest.responseText).message).addClass("ui-state-error");*/
        //       /*console.log(JSON.parse(XMLHttpRequest.responseText).message);
        //       console.log(textStatus);
        //       console.log(errorThrown);
        //     }
        // });*/
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

