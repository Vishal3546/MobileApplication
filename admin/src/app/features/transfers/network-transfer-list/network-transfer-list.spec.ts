import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NetworkTransferList } from './network-transfer-list';

describe('NetworkTransferList', () => {
  let component: NetworkTransferList;
  let fixture: ComponentFixture<NetworkTransferList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetworkTransferList],
    }).compileComponents();

    fixture = TestBed.createComponent(NetworkTransferList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should verify transfer lifecycle states', () => {
    component.status = 'APPROVED';
    expect(component.checkStatus('APPROVED')).toBe(true);
    
    component.status = 'IN_TRANSIT';
    expect(component.checkStatus('IN_TRANSIT')).toBe(true);
  });

  it('should handle request creation and conflicts', () => {
    component.canRequest = true;
    component.requestTransfer();
    expect(component.status).toBe('REQUESTED');
  });
});
