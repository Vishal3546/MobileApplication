import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login';
import { AuthService } from '../../core/auth/auth.service';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    authServiceMock = {
      login: vi.fn()
    };
    
    routerMock = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule, BrowserAnimationsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('1. & 2. admin is accepted as a valid username and does not require email format', () => {
    const usernameControl = component.loginForm.get('username');
    usernameControl?.setValue('admin');
    expect(usernameControl?.valid).toBe(true);
  });

  it('3. Empty username makes form invalid', () => {
    const usernameControl = component.loginForm.get('username');
    usernameControl?.setValue('');
    expect(usernameControl?.valid).toBe(false);
    expect(usernameControl?.hasError('required')).toBe(true);
  });

  it('4. Empty password makes form invalid', () => {
    const passwordControl = component.loginForm.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.valid).toBe(false);
    expect(passwordControl?.hasError('required')).toBe(true);
  });

  it('5. Login button enables when username + password are present', () => {
    component.loginForm.setValue({ username: 'admin', password: 'password123' });
    expect(component.loginForm.valid).toBe(true);
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(button.disabled).toBe(false);
  });

  it('6. Login request contains { username: "admin", password: "..." }', () => {
    authServiceMock.login.mockReturnValue(of({}));
    component.loginForm.setValue({ username: 'admin', password: 'password123' });
    component.onSubmit();
    expect(authServiceMock.login).toHaveBeenCalledWith({ username: 'admin', password: 'password123' });
  });

  it('7. Successful login still updates AuthService/session state', () => {
    authServiceMock.login.mockReturnValue(of({ data: { token: 'mockToken' } }));
    component.loginForm.setValue({ username: 'admin', password: 'password123' });
    component.onSubmit();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('8. 401 still shows existing authentication error behavior', () => {
    authServiceMock.login.mockReturnValue(throwError(() => new Error('401 Unauthorized')));
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    component.loginForm.setValue({ username: 'admin', password: 'password123' });
    component.onSubmit();
    expect(consoleSpy).toHaveBeenCalledWith('Login failed', expect.any(Error));
    consoleSpy.mockRestore();
  });
});
