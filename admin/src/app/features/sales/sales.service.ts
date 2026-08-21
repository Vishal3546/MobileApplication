import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SalesService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/v1/sales';

  getSalesList(page: number, size: number, status?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) params = params.set('status', status);

    return this.http.get(this.API_URL, { params });
  }

  cancelSale(id: string, reason: string): Observable<any> {
    const params = new HttpParams().set('reason', reason);
    return this.http.post(`${this.API_URL}/${id}/cancel`, null, { params });
  }
}
