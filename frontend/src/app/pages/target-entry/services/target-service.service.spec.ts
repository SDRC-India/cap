import { TestBed } from '@angular/core/testing';

import { TargetServiceService } from './target-service.service';

describe('TargetServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TargetServiceService = TestBed.get(TargetServiceService);
    expect(service).toBeTruthy();
  });
});
