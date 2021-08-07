import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRolePageComponent } from './create-role-page.component';

describe('CreateRolePageComponent', () => {
  let component: CreateRolePageComponent;
  let fixture: ComponentFixture<CreateRolePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateRolePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateRolePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
