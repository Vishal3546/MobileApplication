import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-invoices-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, EmptyStateComponent],
  template: `
    <div class="invoices-page">
      <div class="header">
        <h1>🧾 Invoices - Bill</h1>
        <p>Buy/Sell bills - PDF download</p>
      </div>
      <app-empty-state
        icon="receipt_long"
        title="Invoices Coming Soon - 2026"
        message="Bill PDF feature jaldi aayega. Har buy/sell pe auto bill banega with QR code."
        hint="Current me Sales/Purchases se bill generate hota hai. Future: WhatsApp share, Print."
        actionLabel="Sales Dekho"
        actionIcon="point_of_sale">
      </app-empty-state>
    </div>
  `,
  styles: [`.invoices-page { max-width: 800px; margin: 0 auto; } .header h1 { margin: 0; font-size: 24px; font-weight: 800; } .header p { color: #64748b; }`]
})
export class InvoicesListComponent {}
