import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientGeneralTabComponent } from './client-general-tab.component';

describe('ClientGeneralTabComponent', () => {
  let component: ClientGeneralTabComponent;
  let fixture: ComponentFixture<ClientGeneralTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientGeneralTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientGeneralTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
