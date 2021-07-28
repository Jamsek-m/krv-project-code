import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientScopesTabComponent } from './client-scopes-tab.component';

describe('ClientScopesTabComponent', () => {
  let component: ClientScopesTabComponent;
  let fixture: ComponentFixture<ClientScopesTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientScopesTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientScopesTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
