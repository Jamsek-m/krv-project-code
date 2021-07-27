import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateClientPageComponent } from './create-client-page.component';

describe('CreateClientPageComponent', () => {
  let component: CreateClientPageComponent;
  let fixture: ComponentFixture<CreateClientPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateClientPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateClientPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
