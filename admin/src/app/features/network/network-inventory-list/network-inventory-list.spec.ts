import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NetworkInventoryList } from './network-inventory-list';

describe('NetworkInventoryList', () => {
  let component: NetworkInventoryList;
  let fixture: ComponentFixture<NetworkInventoryList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetworkInventoryList],
    }).compileComponents();

    fixture = TestBed.createComponent(NetworkInventoryList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should only display SHOP_NETWORK visible entries', () => {
    component.itemsVisible = 'SHOP_NETWORK';
    expect(component.checkNetworkVisibility('SHOP_NETWORK')).toBe(true);
    expect(component.checkNetworkVisibility('PRIVATE')).toBe(false);
  });

  it('should mask IMEI for unauthorized users', () => {
    component.isImeiMasked = true;
    expect(component.checkMasking()).toBe(true);
  });

  it('should handle permission-controlled request action', () => {
    component.hasPermission = false;
    expect(component.canRequest()).toBe(false);
    
    component.hasPermission = true;
    expect(component.canRequest()).toBe(true);
  });
});
