
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ViewPage } from '../classes/view-page';
import { ViewNavigatorDialogComponent } from '../dialogs/view-navigator-dialog.component';
import { MdDialog } from '@angular/material';


@Component({
    selector: 'app-dashboard-grid-navigator',
    templateUrl: 'navigator.component.html',
    styles: [`
    .open-btn { float: left; width: 80%; text-align: left}
    .open-btn--edit { width: 100% }
    .remove-btn { float: left; width:20%; height:34px }
    .mat-btn { width: 100%}
    .edit { display:block }
    `]
})
export class DashboardGridNavigatorComponent {

    @Input() viewPagesArray: ViewPage[];
    @Output() updateViewPages = new EventEmitter<ViewPage[]>();
    @Output() openViewPage = new EventEmitter<ViewPage>();
    editEnabled: string;

    constructor(private dialog: MdDialog) { }

    open(view) {
        console.debug(view);
        this.openViewPage.emit(view);
    }

    remove(view) {
        this.viewPagesArray = this.viewPagesArray.filter(page => page !== view);
        console.debug(this.viewPagesArray);
        this.updateViewPages.emit(this.viewPagesArray);
    }

    toggleEditMode() {
        const viewListButtons: any = document.querySelectorAll('.open-btn');
        if (this.editEnabled === 'none') {
            this.editEnabled = 'block';
            viewListButtons.forEach(function (button) {
                button.classList.remove('open-btn--edit');
            });
        } else {
            this.editEnabled = 'none';
            viewListButtons.forEach(function (button) {
                button.classList.add('open-btn--edit');
            });
        }

    }

    openNewPageDialog() {
        const dialogReference = this.dialog.open(ViewNavigatorDialogComponent);
        dialogReference.afterClosed().subscribe(result => {
            if (result !== undefined) {
                let data;
                if (result.name !== '') { const name = result.name; } else { alert('Enter View Name'); return false; }
                if (result.data !== '') { data = result.data; } else { data = '[]'; }

                this.viewPagesArray.push(new ViewPage(this.viewPagesArray.length + 1, name, JSON.parse(data)));
                this.updateViewPages.emit(this.viewPagesArray);
                console.debug(this.viewPagesArray);

            }
        });
    }
}
