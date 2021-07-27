import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientSigningTabComponent } from './client-signing-tab.component';

describe('ClientSigningTabComponent', () => {
  let component: ClientSigningTabComponent;
  let fixture: ComponentFixture<ClientSigningTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientSigningTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientSigningTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
