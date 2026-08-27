import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopList } from './shop-list';

describe('ShopList', () => {
  let component: ShopList;
  let fixture: ComponentFixture<ShopList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopList],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should support pagination and filtering', () => {
    // Assert pagination and filter states can be updated
    component.onPageChange({ pageIndex: 1, pageSize: 20, length: 100 });
    expect(component.pageIndex).toBe(1);
    expect(component.pageSize).toBe(20);

    component.onFilterChange({ status: 'ACTIVE', search: 'Main' });
    expect(component.filter.status).toBe('ACTIVE');
    expect(component.filter.search).toBe('Main');
  });

  it('should handle permission control for editing', () => {
    component.hasEditPermission = false;
    fixture.detectChanges();
    // Simulate check
    expect(component.canEditShop()).toBe(false);
    
    component.hasEditPermission = true;
    expect(component.canEditShop()).toBe(true);
  });
});
