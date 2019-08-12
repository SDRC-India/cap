import { TestBed, async, inject } from '@angular/core/testing';

import { DocumentWriteGuard } from './document-write.guard';

describe('DocumentWriteGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DocumentWriteGuard]
    });
  });

  it('should ...', inject([DocumentWriteGuard], (guard: DocumentWriteGuard) => {
    expect(guard).toBeTruthy();
  }));
});
