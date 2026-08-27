import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ShopUserForm } from './shop-user-form';

describe('ShopUserForm', () => {
  let component: ShopUserForm;
  let fixture: ComponentFixture<ShopUserForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopUserForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ShopUserForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
