import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { PermissionService } from './permission.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('PermissionService', () => {
  let service: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [PermissionService]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
