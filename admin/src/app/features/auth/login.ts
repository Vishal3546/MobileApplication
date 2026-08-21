import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, MatCardModule, MatInputModule, MatButtonModule],
  template: `
    <mat-card class="login-card">
      <mat-card-header>
        <mat-card-title>Login</mat-card-title>
      </mat-card-header>
      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
        <mat-card-content>
          <mat-form-field appearance="fill" class="full-width">
            <mat-label>Email</mat-label>
            <input matInput formControlName="email" type="email">
          </mat-form-field>
          <mat-form-field appearance="fill" class="full-width">
            <mat-label>Password</mat-label>
            <input matInput formControlName="password" type="password">
          </mat-form-field>
        </mat-card-content>
        <mat-card-actions align="end">
          <button mat-raised-button color="primary" type="submit" [disabled]="loginForm.invalid">Login</button>
        </mat-card-actions>
      </form>
    </mat-card>
  `,
  styles: [`
    .login-card {
      width: 400px;
      padding: 20px;
    }
    .full-width {
      width: 100%;
      margin-bottom: 10px;
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: (err) => console.error('Login failed', err) // handled by interceptor
      });
    }
  }
}
