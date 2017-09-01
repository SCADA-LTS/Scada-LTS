import { Component } from '@angular/core';
import { MdDialogRef } from '@angular/material';

@Component({
  selector: 'app-dashboard-navigator-dialog',
  templateUrl: 'view-navigator-dialog.component.html',
  styleUrls: ['view-navigator-dialogs.component.scss'],
})
export class ViewNavigatorDialogComponent {
  constructor(public dialogRef: MdDialogRef<ViewNavigatorDialogComponent>) { }
}
