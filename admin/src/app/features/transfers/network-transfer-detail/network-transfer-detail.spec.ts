import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NetworkTransferDetail } from './network-transfer-detail';

describe('NetworkTransferDetail', () => {
  let component: NetworkTransferDetail;
  let fixture: ComponentFixture<NetworkTransferDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetworkTransferDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(NetworkTransferDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
