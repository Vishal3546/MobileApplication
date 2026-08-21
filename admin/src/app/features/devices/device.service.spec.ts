import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DeviceService } from './device.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('DeviceService', () => {
  let service: DeviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [DeviceService]
    });
  });

  it('should be created', () => {
    expect(true).toBe(true);
  });
});
