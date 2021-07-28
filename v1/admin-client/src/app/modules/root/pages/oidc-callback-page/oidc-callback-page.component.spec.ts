import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OidcCallbackPageComponent } from './oidc-callback-page.component';

describe('OidcCallbackPageComponent', () => {
  let component: OidcCallbackPageComponent;
  let fixture: ComponentFixture<OidcCallbackPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OidcCallbackPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OidcCallbackPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
