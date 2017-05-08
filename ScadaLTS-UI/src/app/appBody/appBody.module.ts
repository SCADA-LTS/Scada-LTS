import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { MaterialModule } from '@angular/material';
import { AppBodyComponent } from './appBody.component';
import { ScriptingComponent } from './scripting/scripting.component';

@NgModule({
  declarations: [
    AppBodyComponent,
    ScriptingComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MaterialModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppBodyComponent]
})
export class AppModule { }

