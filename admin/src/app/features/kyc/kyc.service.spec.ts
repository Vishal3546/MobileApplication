import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { KycService } from './kyc.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('KycService', () => {
  let service: KycService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [KycService]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
