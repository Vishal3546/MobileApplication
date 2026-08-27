import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface ShopSettlement {
  id: string;
  settlementNumber: string;
  sourceShop: any;
  destinationShop: any;
  transfer: any;
  grossAmount: number;
  paidAmount: number;
  remainingAmount: number;
  currency: string;
  status: string;
  dueAt: string;
  createdAt: string;
  updatedAt: string;
  payments: any[];
  disputes: any[];
}

export interface ShopLedgerSummary {
  totalReceivable: number;
  totalPayable: number;
  netBalance: number;
}

@Injectable({
  providedIn: 'root'
})
export class SettlementService {
  private apiUrl = `${environment.apiUrl}/api/v1/settlements`;

  constructor(private http: HttpClient) {}

  getSettlements(shopId?: string, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (shopId) {
      params = params.set('shopId', shopId);
    }
    
    return this.http.get<any>(this.apiUrl, { params });
  }

  getSettlement(id: string): Observable<ShopSettlement> {
    return this.http.get<ShopSettlement>(`${this.apiUrl}/${id}`);
  }

  getSummary(shopId?: string): Observable<ShopLedgerSummary> {
    let params = new HttpParams();
    if (shopId) {
      params = params.set('shopId', shopId);
    }
    return this.http.get<ShopLedgerSummary>(`${this.apiUrl}/summary`, { params });
  }

  createPayment(id: string, paymentData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/payments`, paymentData);
  }

  reconcile(id: string, data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/reconcile`, data);
  }

  raiseDispute(id: string, data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/disputes`, data);
  }

  resolveDispute(disputeId: string, data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/disputes/${disputeId}/resolve`, data);
  }
}
