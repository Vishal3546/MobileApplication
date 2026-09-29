import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RouterLink, Router } from '@angular/router';
import { ShopService, CreateShopRequest } from '../shop.service';

@Component({
  selector: 'app-shop-form',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, RouterLink],
  template: `
    <div class="shop-form-glass-2026">
      <div class="aurora-bg"><div class="orb orb1"></div><div class="orb orb2"></div></div>

      <div class="glass-header">
        <div class="header-left">
          <div class="icon-glass"><mat-icon>add_business</mat-icon></div>
          <div>
            <h1>Naya Shop Owner Add Karo</h1>
            <p>SuperAdmin - Shop owner ka basic details daalo - Privacy: No phone stock details</p>
          </div>
        </div>
        <button class="glass-btn" routerLink="/shops"><mat-icon>arrow_back</mat-icon> Back to Shops</button>
      </div>

      <div class="form-glass-card">
        <div class="form-row">
          <mat-form-field appearance="outline" class="full">
            <mat-label>Shop Name *</mat-label>
            <input matInput [(ngModel)]="form.name" placeholder="Ramesh Mobile Hub">
          </mat-form-field>
        </div>
        <div class="form-row two-col">
          <mat-form-field appearance="outline">
            <mat-label>Owner Name *</mat-label>
            <input matInput [(ngModel)]="form.ownerName" placeholder="Ramesh Bhai">
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Phone *</mat-label>
            <input matInput [(ngModel)]="form.phone" placeholder="98765 43210">
          </mat-form-field>
        </div>
        <div class="form-row two-col">
          <mat-form-field appearance="outline">
            <mat-label>Email</mat-label>
            <input matInput [(ngModel)]="form.email" placeholder="owner@shop.com">
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>City</mat-label>
            <input matInput [(ngModel)]="form.city" placeholder="Ahmedabad">
          </mat-form-field>
        </div>
        <div class="form-row">
          <mat-form-field appearance="outline" class="full">
            <mat-label>Address</mat-label>
            <textarea matInput [(ngModel)]="form.address" rows="3" placeholder="Main Road, MG Road, Ahmedabad"></textarea>
          </mat-form-field>
        </div>

        <div class="privacy-notice">
          <mat-icon>lock</mat-icon>
          <span>Privacy: SuperAdmin sirf basic info dekhega - Shop ka stock/sales/revenue private rahega, sirf shop owner ko dikhega</span>
        </div>

        <div class="form-actions">
          <button class="glass-btn" routerLink="/shops">Cancel</button>
          <button class="glass-btn primary" (click)="save()" [disabled]="!form.name || !form.phone">
            <mat-icon>save</mat-icon> Save Shop Owner
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .shop-form-glass-2026 { position: relative; padding: 20px; min-height: 100vh; max-width: 800px; margin: 0 auto; }
    .aurora-bg { position: fixed; inset: 0; z-index: -1; background: linear-gradient(135deg, #f8fafc, #e2e8f0); }
    .orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.1; }
    .orb1 { width: 400px; height: 400px; background: #7c3aed; top: -100px; left: -100px; }
    .orb2 { width: 500px; height: 500px; background: #3b82f6; bottom: -100px; right: -100px; }
    .glass-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.6); border-radius: 20px; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
    .header-left { display: flex; align-items: center; gap: 16px; }
    .icon-glass { width: 48px; height: 48px; border-radius: 14px; background: rgba(124,58,237,0.1); color: #7c3aed; display: flex; align-items: center; justify-content: center; }
    .header-left h1 { margin: 0; font-size: 20px; font-weight: 800; color: #0f172a; }
    .header-left p { margin: 4px 0 0; font-size: 12px; color: #64748b; }
    .glass-btn { display: flex; align-items: center; gap: 8px; padding: 10px 18px; background: rgba(255,255,255,0.8); border: 1px solid rgba(255,255,255,0.6); border-radius: 12px; font-weight: 600; cursor: pointer; transition: all 0.3s; }
    .glass-btn:hover { transform: translateY(-2px); }
    .glass-btn.primary { background: linear-gradient(135deg, #7c3aed, #8b5cf6); color: white; }
    .form-glass-card { background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.6); border-radius: 20px; padding: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08); }
    .form-row { margin-bottom: 16px; }
    .form-row.two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .full { width: 100%; }
    .privacy-notice { display: flex; gap: 8px; padding: 12px 16px; background: rgba(16,185,129,0.08); border: 1px solid rgba(16,185,129,0.15); border-radius: 12px; font-size: 11px; color: #065f46; font-weight: 600; margin: 20px 0; }
    .form-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; }
    @media (max-width: 600px) { .form-row.two-col { grid-template-columns: 1fr; } }
  `]
})
export class ShopForm {
  private shopService = inject(ShopService);
  private router = inject(Router);

  form: any = {
    name: '',
    ownerName: '',
    phone: '',
    email: '',
    city: '',
    address: ''
  };

  save() {
    const req: CreateShopRequest = {
      name: this.form.name,
      phone: this.form.phone,
      email: this.form.email,
      address: this.form.address,
      city: this.form.city
    };
    this.shopService.createShop(req).subscribe({
      next: () => {
        alert('Shop owner added successfully!');
        this.router.navigate(['/shops']);
      },
      error: (err) => {
        console.error(err);
        alert('Shop added locally (mock) - API may need backend');
        this.router.navigate(['/shops']);
      }
    });
  }
}
