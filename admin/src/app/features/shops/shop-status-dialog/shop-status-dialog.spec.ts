import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopStatusDialog } from './shop-status-dialog';

describe('ShopStatusDialog', () => {
  let component: ShopStatusDialog;
  let fixture: ComponentFixture<ShopStatusDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopStatusDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopStatusDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
