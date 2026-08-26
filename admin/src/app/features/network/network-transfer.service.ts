import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface StockTransferResponse {
  id: string;
  transferNumber: string;
  fromBranchId: string;
  toBranchId: string;
  status: string;
  requestedByUsername?: string;
  approvedByUsername?: string;
  requestedAt?: string;
  approvedAt?: string;
  completedAt?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  items: any[];
}

@Injectable({
  providedIn: 'root'
})
export class NetworkTransferService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + '/api/v1/inventory/transfers';

  requestNetworkTransfer(inventoryItemId: string, notes?: string): Observable<{data: StockTransferResponse, message: string}> {
    let params = new HttpParams().set('inventoryItemId', inventoryItemId);
    if (notes) {
      params = params.set('notes', notes);
    }
    
    return this.http.post<{data: StockTransferResponse, message: string}>(`${this.API_URL}/network-request`, null, { params });
  }

  getTransfer(id: string): Observable<{data: StockTransferResponse, message: string}> {
    return this.http.get<{data: StockTransferResponse, message: string}>(`${this.API_URL}/${id}`);
  }

  transitionTransfer(id: string, status: string): Observable<{data: StockTransferResponse, message: string}> {
    let params = new HttpParams().set('status', status);
    return this.http.post<{data: StockTransferResponse, message: string}>(`${this.API_URL}/${id}/transition`, null, { params });
  }

  completeTransfer(id: string): Observable<{data: StockTransferResponse, message: string}> {
    return this.http.post<{data: StockTransferResponse, message: string}>(`${this.API_URL}/${id}/complete`, null);
  }
}
