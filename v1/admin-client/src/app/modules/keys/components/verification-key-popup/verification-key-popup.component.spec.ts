import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificationKeyPopupComponent } from './verification-key-popup.component';

describe('VerificationKeyPopupComponent', () => {
  let component: VerificationKeyPopupComponent;
  let fixture: ComponentFixture<VerificationKeyPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VerificationKeyPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerificationKeyPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
