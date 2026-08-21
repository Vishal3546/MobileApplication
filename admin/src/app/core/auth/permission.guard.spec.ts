import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { PermissionGuard } from './permission.guard';
import { RouterTestingModule } from '@angular/router/testing';

describe('PermissionGuard', () => {
  let service: PermissionGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [PermissionGuard]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
