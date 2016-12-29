import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { MaterialModule } from '@angular/material';
import { WatchlistComponent } from './watchlist.component';
import { SortPipe } from '../../sort.pipe';

@NgModule({
  declarations: [
    WatchlistComponent,
    SortPipe
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MaterialModule.forRoot(),
    SortPipe
  ],
  providers: [],
  bootstrap: [WatchlistComponent]
})
export class WatchlistModule { }

