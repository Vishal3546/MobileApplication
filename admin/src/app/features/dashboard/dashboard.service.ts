import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private baseUrl = environment.apiUrl + '/api/v1/dashboard';

  constructor(private http: HttpClient) {}

  getDashboardSummary(branchId?: string, dateRange?: string, startDate?: string, endDate?: string): Observable<any> {
    let params = new HttpParams();
    if (branchId) params = params.set('branchId', branchId);
    if (dateRange) params = params.set('dateRange', dateRange);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get(this.baseUrl + '/summary', { params });
  }
}
