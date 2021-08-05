import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserGeneralTabComponent } from './user-general-tab.component';

describe('UserGeneralTabComponent', () => {
  let component: UserGeneralTabComponent;
  let fixture: ComponentFixture<UserGeneralTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserGeneralTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserGeneralTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
