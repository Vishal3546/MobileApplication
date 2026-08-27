import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NetworkInventoryDetail } from './network-inventory-detail';

describe('NetworkInventoryDetail', () => {
  let component: NetworkInventoryDetail;
  let fixture: ComponentFixture<NetworkInventoryDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetworkInventoryDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(NetworkInventoryDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
