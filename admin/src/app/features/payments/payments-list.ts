import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-payments-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, EmptyStateComponent],
  template: `
    <div class="payments-page">
      <div class="header">
        <h1>💳 Payments - Len Den</h1>
        <p>Cash, UPI, Card payments ka hisab</p>
      </div>
      <app-empty-state
        icon="payments"
        title="Payments Tracking - 2026"
        message="Har transaction ka payment mode track hota hai - Cash/UPI/Card. Monthly report me dikhega."
        hint="Current: Sales me payment mode dikhta hai. Future: UPI QR, Cashbook integration.">
      </app-empty-state>
    </div>
  `,
  styles: [`.payments-page { max-width: 800px; margin: 0 auto; } .header h1 { margin: 0; font-size: 24px; font-weight: 800; } .header p { color: #64748b; }`]
})
export class PaymentsListComponent {}
