import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-shop-approval',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink],
  template: `
    <div class="approval-glass">
      <div class="glass-header">
        <div class="icon-glass"><mat-icon>verified_user</mat-icon></div>
        <div>
          <h1>Shop Approval - Privacy Safe</h1>
          <p>SuperAdmin: Shop ko approve karo - No phone details access</p>
        </div>
      </div>
      <div class="card-glass">
        <mat-icon>lock</mat-icon>
        <p>SuperAdmin sirf shop ko approve/reject kar sakta hai. Shop ka stock, sales, phone details private rahega.</p>
        <button class="glass-btn primary" routerLink="/shops">Back to Shops</button>
      </div>
    </div>
  `,
  styles: [`
    .approval-glass { padding: 20px; max-width: 800px; margin: 0 auto; }
    .glass-header { display: flex; align-items: center; gap: 16px; padding: 20px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border-radius: 20px; margin-bottom: 20px; }
    .icon-glass { width: 48px; height: 48px; border-radius: 14px; background: rgba(16,185,129,0.1); color: #059669; display: flex; align-items: center; justify-content: center; }
    .card-glass { padding: 24px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border-radius: 20px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 16px; }
    .glass-btn { padding: 10px 20px; background: linear-gradient(135deg, #7c3aed, #8b5cf6); color: white; border: none; border-radius: 12px; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 8px; }
  `]
})
export class ShopApproval {}
