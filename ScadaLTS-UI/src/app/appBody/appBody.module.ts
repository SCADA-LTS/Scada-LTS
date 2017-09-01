import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { MdIconModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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
    MdIconModule
  ],
  bootstrap: [AppBodyComponent]
})
export class AppModule { }

