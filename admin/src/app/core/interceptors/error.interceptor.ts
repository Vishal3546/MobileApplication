import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let message = 'Kuch galat hua';

      if (error.status === 0) {
        message = 'Internet nahi hai ya server down hai - Check karo';
        snackBar.open(message, 'Retry', { duration: 5000, panelClass: 'error-snackbar' });
      } else if (error.status === 400) {
        message = error.error?.message || 'Galat data bheja hai';
        snackBar.open(message, 'OK', { duration: 4000 });
      } else if (error.status === 401) {
        message = 'Session khatam - Login phir se karo';
        snackBar.open(message, 'Login', { duration: 3000 });
        router.navigate(['/login']);
      } else if (error.status === 403) {
        message = 'Permission nahi hai - Admin se baat karo';
        snackBar.open(message, 'OK', { duration: 4000 });
        router.navigate(['/dashboard']);
      } else if (error.status === 404) {
        message = 'Data nahi mila - 404';
        // Don't show snackbar for 404, let component handle with empty-state
        console.warn('404:', req.url);
      } else if (error.status === 409) {
        message = error.error?.message || 'Conflict - Ye data already hai';
        snackBar.open(message, 'OK', { duration: 4000 });
      } else if (error.status === 422) {
        message = error.error?.message || 'Validation fail - Data check karo';
        snackBar.open(message, 'OK', { duration: 5000 });
      } else if (error.status === 429) {
        message = 'Bahut zyada request - Thoda ruko (Rate limit)';
        snackBar.open(message, 'OK', { duration: 5000 });
      } else if (error.status >= 500) {
        message = 'Server me error hai - 500. Team ko bataya gaya';
        snackBar.open(message + ' - Retry karo', 'Retry', { duration: 6000 });
        console.error('Server error:', error);
      }

      // Log for debugging
      console.error(`[${error.status}] ${req.method} ${req.url}:`, error.error);

      return throwError(() => error);
    })
  );
};
