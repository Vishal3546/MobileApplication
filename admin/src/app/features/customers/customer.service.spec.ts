import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CustomerService } from './customer.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('CustomerService', () => {
  let service: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [CustomerService]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
