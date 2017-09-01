import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MdCardModule, MdButtonModule, MdIconModule, MdDialogModule} from '@angular/material';
import { MdMenuModule } from '@angular/material';

import { LibexModule } from '@scadalts/scadalts-dashbord-components';

import { BrowserModule } from '@angular/platform-browser';
import { IncrementatorComponent } from '@scadalts/scadalts-dashbord-components/incrementator/incrementator.component';
import { CameraComponent } from '@scadalts/scadalts-dashbord-components/camera/camera.component';
import { ItemDirective } from './item.directive';
import { ItemService } from './item.service';
import { FrameComponent } from './frame.component';
import { IframeComponent } from '@scadalts/scadalts-dashbord-components/iframe/iframe.component';


@NgModule({
    imports: [CommonModule, LibexModule, BrowserModule, MdCardModule, MdButtonModule, MdIconModule, MdDialogModule, MdMenuModule],
    /* Entry Components - custom components declaration */
    entryComponents: [CameraComponent, IncrementatorComponent, IframeComponent],
    exports: [ItemDirective, FrameComponent],
    declarations: [ItemDirective, FrameComponent],
    providers: [ItemService]
})
export class ItemModule { }
