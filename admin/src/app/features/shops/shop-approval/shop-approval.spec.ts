import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopApproval } from './shop-approval';

describe('ShopApproval', () => {
  let component: ShopApproval;
  let fixture: ComponentFixture<ShopApproval>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopApproval],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopApproval);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should support approval and rejection states', () => {
    // Assert approve/reject logic is triggered correctly
    component.onApprove();
    expect(component.isApproving).toBe(true);
    
    component.onReject();
    expect(component.isRejecting).toBe(true);
  });

  it('should hide unauthorized actions', () => {
    component.hasSuperAdminPermission = false;
    fixture.detectChanges();
    // Simulate check
    expect(component.canPerformAction()).toBe(false);
    
    component.hasSuperAdminPermission = true;
    expect(component.canPerformAction()).toBe(true);
  });
});
