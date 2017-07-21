/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { WleditComponent } from './wledit.component';

describe('WleditComponent', () => {
  let component: WleditComponent;
  let fixture: ComponentFixture<WleditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WleditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WleditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
