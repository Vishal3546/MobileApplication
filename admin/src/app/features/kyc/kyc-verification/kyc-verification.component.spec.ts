import { TestBed } from '@angular/core/testing';
import { KycVerificationComponent } from './kyc-verification.component';

describe('KycVerificationComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [KycVerificationComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(KycVerificationComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
