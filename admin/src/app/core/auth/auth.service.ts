import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';

  // Using signals for state
  public currentUser = signal<any | null>(null);
  public isAuthenticated = signal<boolean>(false);

  constructor(private http: HttpClient) {
    this.checkToken();
  }

  private checkToken() {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (token) {
      this.isAuthenticated.set(true);
    }
  }

  login(credentials: any): Observable<any> {
    return this.http.post('/api/v1/auth/login', credentials).pipe(
      tap((res: any) => {
        if (res.data && res.data.token) {
          localStorage.setItem(this.TOKEN_KEY, res.data.token);
          localStorage.setItem('refresh_token', res.data.refreshToken);
          this.isAuthenticated.set(true);
        }
      })
    );
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('refresh_token');
    this.isAuthenticated.set(false);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  refreshToken(): Observable<any> {
    const refreshToken = localStorage.getItem('refresh_token');
    return this.http.post('/api/v1/auth/refresh', { refreshToken }).pipe(
      tap((res: any) => {
        if (res.data && res.data.accessToken) {
          localStorage.setItem(this.TOKEN_KEY, res.data.accessToken);
          localStorage.setItem('refresh_token', res.data.refreshToken);
        }
      })
    );
  }

}
