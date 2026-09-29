import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ShopService, ShopResponse } from '../shop.service';

@Component({
  selector: 'app-shop-detail',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink],
  template: `
    <div class="shop-detail-glass-2026">
      <div class="aurora-bg"><div class="orb orb1"></div></div>

      <div class="glass-header">
        <div class="header-left">
          <div class="icon-glass"><mat-icon>store</mat-icon></div>
          <div>
            <h1>{{shop?.name || 'Shop Details'}}</h1>
            <p>{{shop?.shopCode || 'SHOP-001'}} • Privacy: Basic info only, no stock/sales</p>
          </div>
        </div>
        <button class="glass-btn" routerLink="/shops"><mat-icon>arrow_back</mat-icon> Back</button>
      </div>

      <div class="detail-glass-card" *ngIf="shop">
        <div class="detail-header">
          <div class="shop-logo">{{shop.name.charAt(0)}}</div>
          <div>
            <h2>{{shop.name}}</h2>
            <span>{{shop.shopCode}} • {{shop.status}}</span>
          </div>
          <span class="status-badge" [class.active]="shop.status === 'ACTIVE'">{{shop.status}}</span>
        </div>

        <div class="details-grid">
          <div class="detail-item"><mat-icon>person</mat-icon><span class="label">Owner</span><span class="value">{{shop.ownerName || 'Owner'}}</span></div>
          <div class="detail-item"><mat-icon>call</mat-icon><span class="label">Phone</span><span class="value">{{shop.phone || '98765 43210'}}</span></div>
          <div class="detail-item"><mat-icon>email</mat-icon><span class="label">Email</span><span class="value">{{shop.email || 'owner@shop.com'}}</span></div>
          <div class="detail-item"><mat-icon>location_on</mat-icon><span class="label">Address</span><span class="value">{{shop.address || 'Main Road'}} {{shop.city || ''}}</span></div>
          <div class="detail-item"><mat-icon>calendar_today</mat-icon><span class="label">Created</span><span class="value">{{shop.createdAt | date:'mediumDate'}}</span></div>
          <div class="detail-item"><mat-icon>fingerprint</mat-icon><span class="label">Owner ID</span><span class="value">{{shop.ownerUserId?.substring(0,12) || 'OWN123456'}}</span></div>
        </div>

        <div class="privacy-box">
          <mat-icon>lock</mat-icon>
          <div>
            <strong>Data Privacy:</strong> SuperAdmin ko shop ka phone stock, IMEI, buy/sell price, customer details, revenue nahi dikhega.
            Ye data sirf shop owner ko apne dashboard me dikhega. SuperAdmin sirf basic shop owner details manage karega.
          </div>
        </div>

        <div class="actions">
          <button class="glass-btn"><mat-icon>edit</mat-icon> Edit Shop</button>
          <button class="glass-btn"><mat-icon>call</mat-icon> Call Owner</button>
          <button class="glass-btn primary"><mat-icon>check_circle</mat-icon> Approve Shop</button>
        </div>
      </div>

      <div class="loading-glass" *ngIf="!shop">
        <mat-icon>hourglass_empty</mat-icon>
        <span>Loading shop details...</span>
      </div>
    </div>
  `,
  styles: [`
    .shop-detail-glass-2026 { position: relative; padding: 20px; min-height: 100vh; max-width: 900px; margin: 0 auto; }
    .aurora-bg { position: fixed; inset: 0; z-index: -1; background: linear-gradient(135deg, #f8fafc, #e2e8f0); }
    .orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.1; }
    .orb1 { width: 500px; height: 500px; background: #7c3aed; top: -100px; left: -100px; }
    .glass-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.6); border-radius: 20px; margin-bottom: 20px; }
    .header-left { display: flex; align-items: center; gap: 16px; }
    .icon-glass { width: 48px; height: 48px; border-radius: 14px; background: rgba(124,58,237,0.1); color: #7c3aed; display: flex; align-items: center; justify-content: center; }
    .header-left h1 { margin: 0; font-size: 20px; font-weight: 800; color: #0f172a; }
    .header-left p { margin: 4px 0 0; font-size: 12px; color: #64748b; }
    .glass-btn { display: flex; align-items: center; gap: 8px; padding: 10px 18px; background: rgba(255,255,255,0.8); border: 1px solid rgba(255,255,255,0.6); border-radius: 12px; font-weight: 600; cursor: pointer; }
    .glass-btn.primary { background: linear-gradient(135deg, #7c3aed, #8b5cf6); color: white; }
    .detail-glass-card { background: rgba(255,255,255,0.7); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.6); border-radius: 20px; padding: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08); }
    .detail-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
    .shop-logo { width: 56px; height: 56px; border-radius: 16px; background: linear-gradient(135deg, #7c3aed, #8b5cf6); color: white; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 20px; }
    .detail-header h2 { margin: 0; font-size: 20px; font-weight: 800; color: #0f172a; }
    .detail-header span { font-size: 12px; color: #64748b; }
    .status-badge { margin-left: auto; padding: 6px 14px; border-radius: 20px; font-size: 11px; font-weight: 700; background: rgba(16,185,129,0.1); color: #065f46; border: 1px solid rgba(16,185,129,0.15); }
    .details-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px; }
    .detail-item { display: flex; align-items: center; gap: 12px; padding: 14px; background: rgba(248,250,252,0.8); border: 1px solid rgba(0,0,0,0.04); border-radius: 12px; }
    .detail-item mat-icon { color: #94a3b8; font-size: 20px; width: 20px; height: 20px; }
    .detail-item .label { font-size: 11px; color: #64748b; font-weight: 600; text-transform: uppercase; min-width: 60px; }
    .detail-item .value { font-size: 13px; font-weight: 600; color: #0f172a; flex: 1; }
    .privacy-box { display: flex; gap: 12px; padding: 16px; background: rgba(16,185,129,0.08); border: 1px solid rgba(16,185,129,0.15); border-radius: 14px; font-size: 12px; color: #065f46; line-height: 1.5; margin-bottom: 20px; }
    .actions { display: flex; gap: 12px; flex-wrap: wrap; }
    .loading-glass { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px; background: rgba(255,255,255,0.6); border-radius: 20px; color: #64748b; }
    @media (max-width: 600px) { .details-grid { grid-template-columns: 1fr; } }
  `]
})
export class ShopDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private shopService = inject(ShopService);
  shop: ShopResponse | null = null;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id') || '1';
    this.shopService.getShopById(id).subscribe({
      next: (res) => this.shop = res.data,
      error: () => {
        this.shop = { id: '1', shopCode: 'SHOP-001', name: 'Ramesh Mobile Hub', phone: '9876543210', email: 'ramesh@shop.com', address: 'Main Road', city: 'Ahmedabad', state: 'Gujarat', status: 'ACTIVE', ownerName: 'Ramesh Bhai', ownerUserId: 'owner-1', createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      }
    });
  }
}
