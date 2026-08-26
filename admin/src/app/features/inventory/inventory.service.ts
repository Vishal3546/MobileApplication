import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + '/api/v1/inventory';

  getInventoryList(page: number, size: number, status?: string, search?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) params = params.set('status', status);
    if (search) params = params.set('search', search);

    return this.http.get(this.API_URL, { params });
  }

  reserveInventory(id: string, branchId?: string): Observable<any> {
    let params = new HttpParams();
    if (branchId) params = params.set('branchId', branchId);
    return this.http.post(`${this.API_URL}/${id}/reserve`, null, { params });
  }
}
