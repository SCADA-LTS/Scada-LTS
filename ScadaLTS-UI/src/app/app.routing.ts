import { Routes, RouterModule } from '@angular/router';

import { AppBodyComponent } from './appBody/index';
import { LoginComponent } from './login/index';
import { RegistrationComponent } from './registration/index';
import { WatchlistComponent } from './appBody/watchlist/index';
import { DashboardComponent } from './appBody/dashboard/index';

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'appBody', component: AppBodyComponent,
    children: [
      { path: 'watchlist', component: WatchlistComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: '', component: DashboardComponent }
    ]},

  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);
