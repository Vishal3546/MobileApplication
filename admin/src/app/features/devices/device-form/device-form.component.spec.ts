import { TestBed } from '@angular/core/testing';
import { DeviceFormComponent } from './device-form.component';

describe('DeviceFormComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DeviceFormComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(DeviceFormComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
