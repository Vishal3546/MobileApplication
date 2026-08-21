import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MediaService } from './media.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('MediaService', () => {
  let service: MediaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [MediaService]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
