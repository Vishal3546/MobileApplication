import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private baseUrl = environment.apiUrl + '/api/v1/customers';

  constructor(private http: HttpClient) {}

  getCustomers(search?: string, page: number = 0, size: number = 10, sort: string = 'id,desc'): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    if (search) params = params.set('search', search);
    return this.http.get(this.baseUrl, { params });
  }

  getCustomer(id: string): Observable<any> {
    return this.http.get(this.baseUrl + '/' + id);
  }

  createCustomer(data: any): Observable<any> {
    return this.http.post(this.baseUrl, data);
  }

  updateCustomer(id: string, data: any): Observable<any> {
    return this.http.put(this.baseUrl + '/' + id, data);
  }

  updateCustomerStatus(id: string, status: string): Observable<any> {
    const params = new HttpParams().set('status', status);
    return this.http.patch(this.baseUrl + '/' + id + '/status', {}, { params });
  }
}
