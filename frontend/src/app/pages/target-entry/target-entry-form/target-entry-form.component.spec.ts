import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TargetEntryFormComponent } from './target-entry-form.component';

describe('TargetEntryFormComponent', () => {
  let component: TargetEntryFormComponent;
  let fixture: ComponentFixture<TargetEntryFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TargetEntryFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TargetEntryFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
