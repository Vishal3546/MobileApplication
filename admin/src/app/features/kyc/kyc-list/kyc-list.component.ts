import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-kyc-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, EmptyStateComponent],
  template: `
    <div class="kyc-page">
      <div class="header">
        <h1>🪪 KYC - Optional for 2nd Hand Shop</h1>
        <p>Aapke shop ke liye optional hai - Simple hisab me iski zarurat nahi</p>
      </div>

      <mat-card class="warning-card">
        <mat-icon>info</mat-icon>
        <div>
          <strong>Second Hand Shop Note:</strong> Aapne bola tha customer ka kaam nahi, owner hi data dalega.
          Isliye KYC optional hai. Agar chahiye to yaha se manage kar sakte ho, nahi to skip karo.
          <br><br>
          <strong>Simple Hisab Me:</strong> Sirf Name/Phone kaafi hai. Aadhaar/PAN ki zarurat nahi.
          <br>
          <strong>Compliance Ke Liye:</strong> Agar bada shop hai aur police verification chahiye to KYC use karo.
        </div>
      </mat-card>

      <app-empty-state
        icon="verified_user"
        title="KYC - Optional Feature"
        message="Simple record-keeping ke liye KYC skip kar sakte ho. Sirf Name/Phone se kaam chalega."
        hint="Backend me KYC encryption (AES/GCM) already secure hai. Frontend me upload/list ready hai but optional."
        actionLabel="Customers Dekho"
        actionIcon="people">
      </app-empty-state>
    </div>
  `,
  styles: [`
    .kyc-page { max-width: 900px; margin: 0 auto; }
    .header h1 { margin: 0; font-size: 24px; font-weight: 800; }
    .header p { color: #64748b; margin-bottom: 20px; }
    .warning-card { display: flex; gap: 12px; padding: 16px !important; background: #fffbeb !important; border: 1px solid #fde68a !important; border-radius: 12px !important; margin-bottom: 20px; }
    .warning-card mat-icon { color: #d97706; }
  `]
})
export class KycListComponent {}
