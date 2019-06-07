import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { MdToolbarModule, MdIconModule, MdDialogModule } from '@angular/material';
import { StorageService } from '../../utils/storage.service';
import { DashboardGridNavigatorComponent } from './navigator.component';

describe('DashboardGridNavigatorComponent', () => {

    let component: DashboardGridNavigatorComponent;
    let fixture: ComponentFixture<DashboardGridNavigatorComponent>;
    let debugEl: DebugElement;

    beforeEach(async(() => {

        TestBed.configureTestingModule({
            imports: [MdToolbarModule, MdIconModule, MdDialogModule],
            declarations: [DashboardGridNavigatorComponent],
        }).compileComponents();

        fixture = TestBed.createComponent(DashboardGridNavigatorComponent);
        component = fixture.componentInstance;
        debugEl = fixture.debugElement;


    }));

    it('Initialization test', () => {
        expect(undefined).toEqual(component.editEnabled);
        expect(undefined).toEqual(component.viewPagesArray);
    });

    it('Toggle Edit test', () => {
        component.editEnabled = 'none';
        component.toggleEditMode();
        expect('block').toEqual(component.editEnabled);

    });

});
