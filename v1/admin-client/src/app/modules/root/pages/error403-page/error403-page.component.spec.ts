import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Error403PageComponent } from './error403-page.component';

describe('Error403PageComponent', () => {
  let component: Error403PageComponent;
  let fixture: ComponentFixture<Error403PageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Error403PageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Error403PageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
