import { TreeDialogService } from './tree-dialog.service';
import { MaterialModule } from '@angular/material';
// Angular Imports
import { NgModule } from '@angular/core';

// This Module's Components
import { TreeDialogComponent } from './tree-dialog.component';

@NgModule({
    imports: [
         MaterialModule.forRoot()
    ],
    declarations: [
        TreeDialogComponent,
    ],
    exports: [
        TreeDialogComponent,
    ],
    providers: [
        TreeDialogService,
    ],
    entryComponents: [
        TreeDialogComponent
    ],
})
export class TreeDialogModule {

}
