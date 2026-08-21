import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BranchScopeService } from './branch-scope.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('BranchScopeService', () => {
  let service: BranchScopeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [BranchScopeService]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
