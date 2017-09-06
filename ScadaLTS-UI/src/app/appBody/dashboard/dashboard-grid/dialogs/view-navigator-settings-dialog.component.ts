import { Component, Inject } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';

@Component({
    selector: 'app-dashboard-navigator-settings-dialog',
    template: `<div>
                <h1>Configuration</h1>
                <div><md-input-container>
                    <input mdInput #dataSourceXID placeholder="Configuration Data Point Export ID" value={{data}}>
                </md-input-container>
                </div>
                <div>
                    <button md-button [md-dialog-close]="dataSourceXID.value">Save</button>
                    <button md-button [md-dialog-close]="">Cancel</button>
                </div>
                `,
    styleUrls: ['view-navigator-dialogs.component.scss'],
})
export class ViewNavigatorSettingsDialogComponent {
    constructor( @Inject(MD_DIALOG_DATA) public data: any) { }
}
