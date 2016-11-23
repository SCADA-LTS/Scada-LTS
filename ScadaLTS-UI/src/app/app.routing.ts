import { Routes, RouterModule } from '@angular/router';

import { AppBodyComponent } from './appBody/index';
import { LoginComponent } from './login/index';
import { RegistrationComponent } from './registration/index';

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'appBody', component: AppBodyComponent },
  { path: 'registration', component: RegistrationComponent },

  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);
