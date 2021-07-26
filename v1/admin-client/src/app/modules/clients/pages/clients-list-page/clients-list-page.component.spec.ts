import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientsListPageComponent } from './clients-list-page.component';

describe('ClientsListPageComponent', () => {
  let component: ClientsListPageComponent;
  let fixture: ComponentFixture<ClientsListPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientsListPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientsListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
