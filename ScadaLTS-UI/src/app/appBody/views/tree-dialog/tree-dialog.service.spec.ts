/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TreeDialogService } from './tree-dialog.service';

describe('TreeDialogService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TreeDialogService]
    });
  });

  it('should ...', inject([TreeDialogService], (service: TreeDialogService) => {
    expect(service).toBeTruthy();
  }));
});
