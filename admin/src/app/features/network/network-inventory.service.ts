import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface NetworkInventoryResponse {
  id: string;
  stockCode: string;
  brand: string;
  model: string;
  maskedImei: string;
  storageCapacity?: string;
  color?: string;
  conditionSummary?: string;
  sellingPrice: number;
  status: string;
  shopId: string;
  shopName: string;
  branchId: string;
  branchName: string;
}

export interface NetworkInventoryPage {
  content: NetworkInventoryResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class NetworkInventoryService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + '/api/v1/network/inventory';

  getNetworkInventory(brand?: string, model?: string, condition?: string, page: number = 0, size: number = 20): Observable<{data: NetworkInventoryPage, message: string}> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    if (brand) params = params.set('brand', brand);
    if (model) params = params.set('model', model);
    if (condition) params = params.set('condition', condition);

    return this.http.get<{data: NetworkInventoryPage, message: string}>(this.API_URL, { params });
  }
}
