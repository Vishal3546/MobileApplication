import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NetworkTransferRequestDialog } from './network-transfer-request-dialog';

describe('NetworkTransferRequestDialog', () => {
  let component: NetworkTransferRequestDialog;
  let fixture: ComponentFixture<NetworkTransferRequestDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetworkTransferRequestDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(NetworkTransferRequestDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
