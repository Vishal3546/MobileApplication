import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 403) {
        console.error('Permission denied.');
        router.navigate(['/unauthorized']);
      } else if (error.status === 409) {
        console.error('Conflict detected.');
      } else if (error.status === 429) {
        console.error('Rate limited.');
      } else if (error.status >= 500) {
        console.error('Server error occurred.');
      }
      return throwError(() => error);
    })
  );
};
