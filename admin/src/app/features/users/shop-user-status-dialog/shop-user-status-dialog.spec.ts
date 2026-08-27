import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopUserStatusDialog } from './shop-user-status-dialog';

describe('ShopUserStatusDialog', () => {
  let component: ShopUserStatusDialog;
  let fixture: ComponentFixture<ShopUserStatusDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopUserStatusDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopUserStatusDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
