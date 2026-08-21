import { TestBed } from '@angular/core/testing';
import { UsersPlaceholderComponent } from './users-placeholder.component';

describe('UsersPlaceholderComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UsersPlaceholderComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(UsersPlaceholderComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
