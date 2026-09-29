import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { RouterLink } from '@angular/router';
import { ShopService, ShopResponse } from '../shop.service';

@Component({
  selector: 'app-shop-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, FormsModule, MatChipsModule, RouterLink],
  template: `
    <div class="superadmin-shops-2026">
      <div class="shops-glass-grid">
        <div *ngFor="let shop of filtered" class="shop-glass-card">
          <div class="shop-header-glass">
            <div class="shop-logo-glass">{{shop.name.charAt(0)}}</div>
            <div class="shop-title-glass">
              <span class="shop-name">{{shop.name}}</span>
              <span class="shop-code">{{shop.shopCode}}</span>
            </div>
            <span class="status-glass active">{{shop.status}}</span>
          </div>
          <div class="owner-details-glass">
            <div class="detail-row"><span>Owner: {{shop.ownerName || 'Owner'}}</span></div>
            <div class="detail-row"><span>Phone: {{shop.phone || '98765 43210'}}</span></div>
          </div>
          <div class="privacy-notice-glass">
            <span>Shop ka stock/sales private - sirf owner ko dikhega</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .superadmin-shops-2026 { padding: 20px; }
    .shops-glass-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 20px; }
    .shop-glass-card { padding: 22px; background: rgba(255,255,255,0.7); border-radius: 22px; }
  `]
})
export class ShopList implements OnInit {
  private shopService = inject(ShopService);
  shops: ShopResponse[] = []; filtered: ShopResponse[] = []; search = '';
  ngOnInit() { this.loadShops(); }
  loadShops() {
    this.shopService.getShops().subscribe({
      next: (res) => { this.shops = res.data || []; this.filtered = [...this.shops]; },
      error: () => { this.shops = []; this.filtered = []; }
    });
  }
  pageIndex = 0; pageSize = 10; filter2: any = {}; hasEditPermission = true;
  onPageChange(e: any) { this.pageIndex = e.pageIndex; this.pageSize = e.pageSize; }
  onFilterChange(f: any) { this.filter2 = f; }
  canEditShop() { return this.hasEditPermission; }
}