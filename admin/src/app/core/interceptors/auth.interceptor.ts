import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { throwError, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

let isRefreshing = false;
let refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  let authReq = req;
  const token = authService.getToken();

  if (token) {
    authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);

          return authService.refreshToken().pipe(
            switchMap((newToken: any) => {
              isRefreshing = false;
              if (newToken && newToken.data && newToken.data.accessToken) {
                  refreshTokenSubject.next(newToken.data.accessToken);
                  return next(req.clone({ setHeaders: { Authorization: `Bearer ${newToken.data.accessToken}` } }));
              }
              authService.logout();
              router.navigate(['/login']);
              return throwError(() => error);
            }),
            catchError((err) => {
              isRefreshing = false;
              authService.logout();
              router.navigate(['/login']);
              return throwError(() => err);
            })
          );
        } else {
          return refreshTokenSubject.pipe(
            filter(token => token != null),
            take(1),
            switchMap(jwt => {
              return next(req.clone({ setHeaders: { Authorization: `Bearer ${jwt}` } }));
            })
          );
        }
      }
      return throwError(() => error);
    })
  );
};
