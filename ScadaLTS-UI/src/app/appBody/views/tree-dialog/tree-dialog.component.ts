import { MdDialogRef } from '@angular/material';
import { Component } from '@angular/core';

@Component({
    moduleId: module.id,
    selector: 'tree-dialog',
    templateUrl: 'tree-dialog.component.html',
    styleUrls: ['']
})
export class TreeDialogComponent {

    public title: string;
    public message: string;

    constructor(public dialogRef: MdDialogRef<TreeDialogComponent>) {

    }

}
