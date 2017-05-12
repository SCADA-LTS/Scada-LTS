import { Routes, RouterModule } from '@angular/router';

import { AppBodyComponent } from './appBody/index';
import { LoginComponent } from './login/index';
import { RegistrationComponent } from './registration/index';
import { WatchlistComponent } from './appBody/watchlist/index';
import { DashboardComponent } from './appBody/dashboard/index';
import { ViewsComponent } from './appBody/views/index';
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

import { WorksheetAccessGuard } from './ActivationGuard';

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'appBody', component: AppBodyComponent,
    children: [
      { path: 'watchlist', component: WatchlistComponent/*, canActivate: [WorksheetAccessGuard]*/ },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'views', component: ViewsComponent },
      { path: 'system', component: SystemComponent },
      { path: 'users', component: UsersComponent },
      { path: 'about', component: AboutComponent },
      { path: 'alarms', component: AlarmsComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'eventHandlers', component: EventHandlersComponent },
      { path: 'dataSources', component: DataSourcesComponent },
      { path: 'scheduledEvents', component: ScheduledEventsComponent },
      { path: 'compoundEvents', component: CompoundEventsComponent },
      { path: 'pointLinks', component: PointLinksComponent },
      { path: 'scripting', component: ScriptingComponent },
      { path: 'pointHierarchy', component: PointHierarchyComponent },
      { path: 'mailingList', component: MailingListComponent },
      { path: 'maintenanceEvents', component: MaintenanceEventsComponent },
      { path: 'importExport', component: ImportExportComponent },
      { path: 'log', component: LogComponent },
      { path: 'search', component: SearchComponent },
      { path: 'trends', component: TrendsComponent },
      { path: 'oldWatchlist', component: OldWatchlistComponent },
      { path: 'wledit', component: WleditComponent },
      { path: '', component: WatchlistComponent }
    ]},

  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});

