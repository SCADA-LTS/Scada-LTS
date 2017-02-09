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
import { SortPipe } from './sort.pipe';
import { SystemComponent } from './appBody/system/system.component';

@NgModule({
  declarations: [
    AppComponent,
    AppBodyComponent,
    LoginComponent,
    RegistrationComponent,
    WatchlistComponent,
    DashboardComponent,
    ViewsComponent,
    SortPipe,
    SystemComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    MaterialModule.forRoot(),
    routing,
    NgbModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

