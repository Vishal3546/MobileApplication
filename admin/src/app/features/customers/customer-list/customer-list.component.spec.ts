import { TestBed } from '@angular/core/testing';
import { CustomerListComponent } from './customer-list.component';

describe('CustomerListComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CustomerListComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(CustomerListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
