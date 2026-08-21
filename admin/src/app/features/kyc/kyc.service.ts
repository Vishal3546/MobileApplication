import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class KycService {
  private baseUrl = environment.apiUrl + '/api/v1/customers';

  constructor(private http: HttpClient) {}

  uploadKyc(customerId: string, data: any): Observable<any> {
    return this.http.post(this.baseUrl + '/' + customerId + '/documents', data);
  }

  getDocuments(customerId: string): Observable<any> {
    return this.http.get(this.baseUrl + '/' + customerId + '/documents');
  }

  approveDocument(customerId: string, documentId: string, notes?: string): Observable<any> {
    let params = new HttpParams();
    if (notes) params = params.set('notes', notes);
    return this.http.post(this.baseUrl + '/' + customerId + '/documents/' + documentId + '/approve', {}, { params });
  }

  rejectDocument(customerId: string, documentId: string, notes?: string): Observable<any> {
    let params = new HttpParams();
    if (notes) params = params.set('notes', notes);
    return this.http.post(this.baseUrl + '/' + customerId + '/documents/' + documentId + '/reject', {}, { params });
  }
}
