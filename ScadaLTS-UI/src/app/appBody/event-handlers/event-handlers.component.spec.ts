/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { EventHandlersComponent } from './event-handlers.component';

describe('EventHandlersComponent', () => {
  let component: EventHandlersComponent;
  let fixture: ComponentFixture<EventHandlersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventHandlersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventHandlersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
