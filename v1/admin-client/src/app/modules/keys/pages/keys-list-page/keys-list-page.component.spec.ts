import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KeysListPageComponent } from './keys-list-page.component';

describe('KeysListPageComponent', () => {
  let component: KeysListPageComponent;
  let fixture: ComponentFixture<KeysListPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KeysListPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(KeysListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
