import { Component, Inject, OnInit } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';
import { DialogEditItem } from '../classes/dialog-item';

@Component({
    selector: 'app-item-editor-dialog',
    templateUrl: 'item-editor-dialog.component.html',
    styleUrls: ['view-navigator-dialogs.component.scss'],
})
export class ItemEditorDialogComponent {

    labels: string[];
    values: any[];

    items: DialogEditItem[];

    constructor( @Inject(MD_DIALOG_DATA) public data: any) {
        const labels = Object.keys(this.data);
        const values = Object.values(this.data);
        this.items = [];
        for (let x = 0; x < labels.length; x++) {
            this.items.push(new DialogEditItem(labels[x], values[x]));
        }

        console.debug(this.items);
    }
}
