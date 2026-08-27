import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopUserList } from './shop-user-list';

describe('ShopUserList', () => {
  let component: ShopUserList;
  let fixture: ComponentFixture<ShopUserList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopUserList],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopUserList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should scope user visibility to shop', () => {
    component.visibilityScope = 'SHOP_A';
    expect(component.checkVisibility('SHOP_A')).toBe(true);
    expect(component.checkVisibility('SHOP_B')).toBe(false);
  });

  it('should block unauthorized shop access', () => {
    component.hasAccess = false;
    expect(component.checkAccess()).toBe(false);
    
    component.hasAccess = true;
    expect(component.checkAccess()).toBe(true);
  });
});
