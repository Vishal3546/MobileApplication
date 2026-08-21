import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DeviceService {
  private baseUrl = environment.apiUrl + '/api/v1/devices';

  constructor(private http: HttpClient) {}

  getDevices(imei?: string, brand?: string, model?: string, page: number = 0, size: number = 10, sort: string = 'id,desc'): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    if (imei) params = params.set('imei', imei);
    if (brand) params = params.set('brand', brand);
    if (model) params = params.set('model', model);
    return this.http.get(this.baseUrl, { params });
  }

  getDevice(id: string): Observable<any> {
    return this.http.get(this.baseUrl + '/' + id);
  }

  createDevice(data: any): Observable<any> {
    return this.http.post(this.baseUrl, data);
  }

  updateDevice(id: string, data: any): Observable<any> {
    return this.http.put(this.baseUrl + '/' + id, data);
  }

  updateDeviceStatus(id: string, status: string): Observable<any> {
    return this.http.patch(this.baseUrl + '/' + id + '/status', { status });
  }

  getConditionHistory(id: string): Observable<any> { return this.http.get(this.baseUrl + '/' + id + '/conditions/history'); }
  getInspectionHistory(id: string): Observable<any> { return this.http.get(this.baseUrl + '/' + id + '/inspections/history'); }
  getDeviceMedia(id: string): Observable<any> { return this.http.get(this.baseUrl + '/' + id + '/media'); }
  getLifecycleHistory(id: string): Observable<any> { return this.http.get(this.baseUrl + '/' + id + '/lifecycle'); }
}
