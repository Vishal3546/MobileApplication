import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-shop-status-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  template: `
    <div class="dialog-glass">
      <div class="icon-glass"><mat-icon>store</mat-icon></div>
      <h3>Change Shop Status</h3>
      <p>SuperAdmin: Shop status change - Privacy: No access to shop's private phone data</p>
      <div class="actions">
        <button class="glass-btn">Cancel</button>
        <button class="glass-btn primary">Update Status</button>
      </div>
    </div>
  `,
  styles: [`
    .dialog-glass { padding: 24px; background: rgba(255,255,255,0.8); backdrop-filter: blur(20px); border-radius: 20px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 16px; max-width: 400px; }
    .icon-glass { width: 48px; height: 48px; border-radius: 14px; background: rgba(124,58,237,0.1); color: #7c3aed; display: flex; align-items: center; justify-content: center; }
    .actions { display: flex; gap: 12px; }
    .glass-btn { padding: 10px 20px; background: rgba(255,255,255,0.8); border: 1px solid rgba(0,0,0,0.06); border-radius: 12px; font-weight: 600; cursor: pointer; }
    .glass-btn.primary { background: linear-gradient(135deg, #7c3aed, #8b5cf6); color: white; }
  `]
})
export class ShopStatusDialog {}
