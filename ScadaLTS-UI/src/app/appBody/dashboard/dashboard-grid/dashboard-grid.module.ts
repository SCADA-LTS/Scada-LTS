import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdIconModule, MdToolbarModule, MdButtonModule } from '@angular/material';
import { MdSidenavModule, MdInputModule, MdSelectModule } from '@angular/material';
import { MdDialogModule, MdDialog } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DashboardGridComponent } from './dashboard-grid.component';
import { DashboardGridNavigatorComponent } from './navigator/navigator.component';
import { DashboardGridPageComponent } from './page/page.component';
import { ItemModule } from './item/item.module';
import { DashboardGridService } from './dashboard-grid.service';
import { ConnectionService } from '../utils/connection.service';
import { ViewNavigatorSettingsDialogComponent } from './dialogs/view-navigator-settings-dialog.component';
import { ViewNavigatorDialogComponent } from './dialogs/view-navigator-dialog.component';
import { ItemEditorDialogComponent } from './dialogs/item-editor-dialog.component';
import { StorageService } from '../utils/storage.service';

@NgModule({
    declarations: [
        DashboardGridComponent,
        DashboardGridNavigatorComponent,
        DashboardGridPageComponent,
        ItemEditorDialogComponent,
        ViewNavigatorDialogComponent,
        ViewNavigatorSettingsDialogComponent,
    ],
    entryComponents: [
        ViewNavigatorDialogComponent,
        ViewNavigatorSettingsDialogComponent,
        ItemEditorDialogComponent
    ],
    imports: [
        MdToolbarModule,
        MdIconModule,
        MdButtonModule,
        MdSidenavModule,
        MdDialogModule,
        MdSelectModule,
        MdInputModule,
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        ItemModule
    ],
    providers: [
        DashboardGridService,
        StorageService,
        ConnectionService
    ],
    exports: [
        DashboardGridComponent
    ]
})
export class DashboardGridModule { }
