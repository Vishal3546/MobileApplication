import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface ShopResponse {
  id: string;
  shopCode: string;
  name: string;
  legalName?: string;
  phone?: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  status: string;
  ownerUserId?: string;
  ownerName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateShopRequest {
  name: string;
  legalName?: string;
  phone?: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  ownerUserId?: string;
}

export interface UpdateShopRequest {
  name?: string;
  legalName?: string;
  phone?: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ShopService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + '/api/v1/shops';

  getShops(): Observable<{data: ShopResponse[], message: string}> {
    return this.http.get<{data: ShopResponse[], message: string}>(this.API_URL);
  }

  getShopById(id: string): Observable<{data: ShopResponse, message: string}> {
    return this.http.get<{data: ShopResponse, message: string}>(`${this.API_URL}/${id}`);
  }

  createShop(shop: CreateShopRequest): Observable<{data: ShopResponse, message: string}> {
    return this.http.post<{data: ShopResponse, message: string}>(this.API_URL, shop);
  }

  updateShop(id: string, shop: UpdateShopRequest): Observable<{data: ShopResponse, message: string}> {
    return this.http.patch<{data: ShopResponse, message: string}>(`${this.API_URL}/${id}`, shop);
  }
}
