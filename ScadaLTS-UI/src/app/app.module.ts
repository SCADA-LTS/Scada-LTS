import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { MaterialModule } from '@angular/material';

import { AppComponent } from './index';
import { routing } from './app.routing';

import { AppBodyComponent } from './appBody/index';
import { LoginComponent } from './login/index';
import { RegistrationComponent } from './registration/index';
import { WatchlistComponent } from './appBody/watchlist/index';
import { DashboardComponent } from './appBody/dashboard/index';
import { ViewsComponent } from './appBody/views/index';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SystemComponent } from './appBody/system/system.component';
import { UsersComponent } from './appBody/users/users.component';
import { AboutComponent } from './appBody/about/about.component';
import { AlarmsComponent } from './appBody/alarms/alarms.component';
import { ReportsComponent } from './appBody/reports/reports.component';
import { EventHandlersComponent } from './appBody/event-handlers/event-handlers.component';
import { DataSourcesComponent } from './appBody/data-sources/data-sources.component';
import { ScheduledEventsComponent } from './appBody/scheduled-events/scheduled-events.component';
import { CompoundEventsComponent } from './appBody/compound-events/compound-events.component';
import { PointLinksComponent } from './appBody/point-links/point-links.component';
import { ScriptingComponent } from './appBody/scripting/scripting.component';
import { PointHierarchyComponent } from './appBody/point-hierarchy/point-hierarchy.component';
import { MailingListComponent } from './appBody/mailing-list/mailing-list.component';
import { MaintenanceEventsComponent } from './appBody/maintenance-events/maintenance-events.component';
import { ImportExportComponent } from './appBody/import-export/import-export.component';
import { LogComponent } from './appBody/log/log.component';
import { SearchComponent } from './appBody/search/search.component';
import { TrendsComponent } from './appBody/trends/trends.component';
import { OldWatchlistComponent } from './appBody/old-watchlist/old-watchlist.component';
import { WleditComponent } from './appBody/wledit/wledit.component';
import { DlgSelectViewWithEdtHierarchyView } from './appBody/views/views.component';
import { DlgAddFolderHierarchyView } from './appBody/views/views.component';
import { DlgConfirmDeleteFolderHierarchyView } from './appBody/views/views.component';

import { ClipboardModule } from 'ngx-clipboard';

import { WorksheetAccessGuard } from './ActivationGuard';
import { UserAuthenticationService } from './UserAuthenticationService';


@NgModule({
  declarations: [
    AppComponent,
    AppBodyComponent,
    LoginComponent,
    RegistrationComponent,
    WatchlistComponent,
    DashboardComponent,
    ViewsComponent,
    DlgSelectViewWithEdtHierarchyView,
    DlgAddFolderHierarchyView,
    DlgConfirmDeleteFolderHierarchyView,
    SystemComponent,
    UsersComponent,
    AboutComponent,
    AlarmsComponent,
    ReportsComponent,
    EventHandlersComponent,
    DataSourcesComponent,
    ScheduledEventsComponent,
    CompoundEventsComponent,
    PointLinksComponent,
    ScriptingComponent,
    PointHierarchyComponent,
    MailingListComponent,
    MaintenanceEventsComponent,
    ImportExportComponent,
    LogComponent,
    SearchComponent,
    TrendsComponent,
    OldWatchlistComponent,
    WleditComponent
  ],
  entryComponents: [
    DlgSelectViewWithEdtHierarchyView,
    DlgAddFolderHierarchyView,
    DlgConfirmDeleteFolderHierarchyView
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    MaterialModule.forRoot(),
    MaterialModule,
    routing,
    NgbModule.forRoot(),
    ClipboardModule
  ],
  providers: [WorksheetAccessGuard, UserAuthenticationService],
  bootstrap: [AppComponent]
})
export class AppModule { }

