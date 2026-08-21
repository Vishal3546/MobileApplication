import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private baseUrl = environment.apiUrl + '/api/v1/reports';

  constructor(private http: HttpClient) {}

  getSalesReport(branchId?: string, dateRange?: string, startDate?: string, endDate?: string): Observable<any> {
    let params = this.buildParams({ branchId, dateRange, startDate, endDate });
    return this.http.get(this.baseUrl + '/sales', { params });
  }

  getPurchaseReport(branchId?: string, dateRange?: string, startDate?: string, endDate?: string): Observable<any> {
    let params = this.buildParams({ branchId, dateRange, startDate, endDate });
    return this.http.get(this.baseUrl + '/purchases', { params });
  }

  getInventoryReport(branchId?: string): Observable<any> {
    let params = this.buildParams({ branchId });
    return this.http.get(this.baseUrl + '/inventory', { params });
  }
  
  getEmployeeReport(branchId?: string, dateRange?: string, startDate?: string, endDate?: string): Observable<any> {
    let params = this.buildParams({ branchId, dateRange, startDate, endDate });
    return this.http.get(this.baseUrl + '/employee', { params });
  }

  exportSalesReport(branchId?: string, dateRange?: string, startDate?: string, endDate?: string): Observable<Blob> {
    let params = this.buildParams({ branchId, dateRange, startDate, endDate });
    return this.http.get(this.baseUrl + '/sales/export', { params, responseType: 'blob' });
  }

  exportPurchaseReport(branchId?: string, dateRange?: string, startDate?: string, endDate?: string): Observable<Blob> {
    let params = this.buildParams({ branchId, dateRange, startDate, endDate });
    return this.http.get(this.baseUrl + '/purchases/export', { params, responseType: 'blob' });
  }

  exportInventoryReport(branchId?: string): Observable<Blob> {
    let params = this.buildParams({ branchId });
    return this.http.get(this.baseUrl + '/inventory/export', { params, responseType: 'blob' });
  }

  private buildParams(opts: any): HttpParams {
    let params = new HttpParams();
    if (opts.branchId) params = params.set('branchId', opts.branchId);
    if (opts.dateRange) params = params.set('dateRange', opts.dateRange);
    if (opts.startDate) params = params.set('startDate', opts.startDate);
    if (opts.endDate) params = params.set('endDate', opts.endDate);
    return params;
  }
}
