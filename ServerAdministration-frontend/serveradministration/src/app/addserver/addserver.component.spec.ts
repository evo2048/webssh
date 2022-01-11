import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddserverComponent } from './addserver.component';

describe('AddserverComponent', () => {
  let component: AddserverComponent;
  let fixture: ComponentFixture<AddserverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddserverComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddserverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
