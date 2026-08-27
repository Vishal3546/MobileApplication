import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopUserDetail } from './shop-user-detail';

describe('ShopUserDetail', () => {
  let component: ShopUserDetail;
  let fixture: ComponentFixture<ShopUserDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopUserDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopUserDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
