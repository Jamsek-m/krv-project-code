import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationSettingsFormComponent } from './registration-settings-form.component';

describe('RegistrationSettingsFormComponent', () => {
  let component: RegistrationSettingsFormComponent;
  let fixture: ComponentFixture<RegistrationSettingsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegistrationSettingsFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationSettingsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
