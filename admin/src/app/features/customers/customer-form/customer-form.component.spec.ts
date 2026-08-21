import { TestBed } from '@angular/core/testing';
import { CustomerFormComponent } from './customer-form.component';

describe('CustomerFormComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CustomerFormComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(CustomerFormComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
