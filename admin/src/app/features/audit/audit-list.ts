import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-audit-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, EmptyStateComponent],
  template: `
    <div class="audit-page">
      <div class="header">
        <h1>📜 Audit Log - Kaun Kya Kiya</h1>
        <p>Har action ka log - Security ke liye</p>
      </div>
      <app-empty-state
        icon="history"
        title="Audit Log"
        message="Har buy/sell/edit ka log yaha dikhega - Kaun, Kab, Kya kiya."
        hint="Backend me audit service already hai (AuditService). Admin panel me list API connect karna hai.">
      </app-empty-state>
    </div>
  `,
  styles: [`.audit-page { max-width: 800px; margin: 0 auto; } .header h1 { margin: 0; font-size: 24px; font-weight: 800; } .header p { color: #64748b; }`]
})
export class AuditListComponent {}
