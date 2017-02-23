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

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'appBody', component: AppBodyComponent,
    children: [
      { path: 'watchlist', component: WatchlistComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'views', component: ViewsComponent },
      { path: 'system', component: SystemComponent },
      { path: 'users', component: UsersComponent },
      { path: 'about', component: AboutComponent },
      { path: '', component: DashboardComponent }
    ]},

  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);

