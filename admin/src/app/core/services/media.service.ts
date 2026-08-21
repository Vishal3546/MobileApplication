import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MediaService {
  private baseUrl = environment.apiUrl + '/api/v1/media';

  constructor(private http: HttpClient) {}

  getMediaPreviewUrl(mediaId: string): Observable<string> {
    return this.http.get(this.baseUrl + '/' + mediaId, { responseType: 'blob' }).pipe(
      map(blob => URL.createObjectURL(blob))
    );
  }

  revokePreviewUrl(url: string) {
    if (url && url.startsWith('blob:')) {
      URL.revokeObjectURL(url);
    }
  }
}
