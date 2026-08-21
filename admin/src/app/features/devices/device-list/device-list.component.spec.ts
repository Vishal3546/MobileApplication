import { TestBed } from '@angular/core/testing';
import { DeviceListComponent } from './device-list.component';

describe('DeviceListComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DeviceListComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(DeviceListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
