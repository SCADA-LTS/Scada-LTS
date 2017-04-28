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
import { PointHierarchyComponent } from './appBody/point-hierarchy/point-hierarchy.component';
import { MailingListComponent } from './appBody/mailing-list/mailing-list.component';
import { MaintenanceEventsComponent } from './appBody/maintenance-events/maintenance-events.component';
import { ImportExportComponent } from './appBody/import-export/import-export.component';
import { LogComponent } from './appBody/log/log.component';
import { SearchComponent } from './appBody/search/search.component';
import { TrendsComponent } from './appBody/trends/trends.component';
import { OldWatchlistComponent } from './appBody/old-watchlist/old-watchlist.component';
import { WleditComponent } from './appBody/wledit/wledit.component';

import { ActivationGuard } from './ActivationGuard';

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'appBody', component: AppBodyComponent, canActivate: [ActivationGuard],
    children: [
      { path: 'watchlist', component: WatchlistComponent, canActivate: [ActivationGuard] },
      { path: 'dashboard', component: DashboardComponent, canActivate: [ActivationGuard] },
      { path: 'views', component: ViewsComponent, canActivate: [ActivationGuard] },
      { path: 'system', component: SystemComponent, canActivate: [ActivationGuard] },
      { path: 'users', component: UsersComponent, canActivate: [ActivationGuard] },
      { path: 'about', component: AboutComponent, canActivate: [ActivationGuard] },
      { path: 'alarms', component: AlarmsComponent, canActivate: [ActivationGuard] },
      { path: 'reports', component: ReportsComponent, canActivate: [ActivationGuard] },
      { path: 'eventHandlers', component: EventHandlersComponent, canActivate: [ActivationGuard] },
      { path: 'dataSources', component: DataSourcesComponent, canActivate: [ActivationGuard] },
      { path: 'scheduledEvents', component: ScheduledEventsComponent, canActivate: [ActivationGuard] },
      { path: 'compoundEvents', component: CompoundEventsComponent, canActivate: [ActivationGuard] },
      { path: 'pointLinks', component: PointLinksComponent, canActivate: [ActivationGuard] },
      { path: 'pointHierarchy', component: PointHierarchyComponent, canActivate: [ActivationGuard] },
      { path: 'mailingList', component: MailingListComponent, canActivate: [ActivationGuard] },
      { path: 'maintenanceEvents', component: MaintenanceEventsComponent, canActivate: [ActivationGuard] },
      { path: 'importExport', component: ImportExportComponent, canActivate: [ActivationGuard] },
      { path: 'log', component: LogComponent, canActivate: [ActivationGuard] },
      { path: 'search', component: SearchComponent, canActivate: [ActivationGuard] },
      { path: 'trends', component: TrendsComponent, canActivate: [ActivationGuard] },
      { path: 'oldWatchlist', component: OldWatchlistComponent, canActivate: [ActivationGuard] },
      { path: 'wledit', component: WleditComponent, canActivate: [ActivationGuard] },
      { path: '', component: DashboardComponent, canActivate: [ActivationGuard] }
    ]},

  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});

