import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { InventoryService } from './inventory.service';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, MatChipsModule, FormsModule],
  template: `
    <div class="inventory-glass-2026">
      <!-- Aurora -->
      <div class="aurora-bg">
        <div class="orb orb1"></div>
        <div class="orb orb2"></div>
      </div>

      <!-- Header Glass -->
      <div class="glass-header">
        <div class="header-left">
          <div class="icon-glass stock">
            <mat-icon>inventory_2</mat-icon>
          </div>
          <div>
            <h1>Stock - Mere Paas Kitne Phone</h1>
            <p>Current stock • Brand-wise • Profit expected • Glass Edition</p>
          </div>
        </div>
        <div class="header-actions">
          <button class="glass-btn primary">
            <mat-icon>add</mat-icon>
            <span>Naya Phone Add</span>
          </button>
          <button class="glass-btn">
            <mat-icon>download</mat-icon>
            <span>Excel</span>
          </button>
        </div>
      </div>

      <!-- Stats Glass -->
      <div class="stats-glass-row">
        <div class="stat-glass">
          <div class="stat-icon-glass"><mat-icon>smartphone</mat-icon></div>
          <div class="stat-info">
            <span class="label">Total Stock</span>
            <strong>{{filtered.length}} phones</strong>
            <small>{{brandCount}} brands • Live</small>
          </div>
          <div class="stat-glow"></div>
        </div>
        <div class="stat-glass">
          <div class="stat-icon-glass buy"><mat-icon>shopping_cart</mat-icon></div>
          <div class="stat-info">
            <span class="label">Stock Value (Buy)</span>
            <strong>₹{{stockValue | number}}</strong>
            <small>Kitne me liya</small>
          </div>
        </div>
        <div class="stat-glass profit">
          <div class="stat-icon-glass profit"><mat-icon>trending_up</mat-icon></div>
          <div class="stat-info">
            <span class="label">Sell Value + Profit</span>
            <strong>₹{{sellValue | number}}</strong>
            <small class="profit-text">Profit ₹{{profit | number}} • 20% avg</small>
          </div>
        </div>
        <div class="search-glass">
          <mat-icon>search</mat-icon>
          <input [(ngModel)]="search" (keyup)="filter()" placeholder="Search - Brand, Model, IMEI, Seller...">
          <div class="search-glow"></div>
        </div>
      </div>

      <!-- Brand Chips Glass -->
      <div class="chips-glass-row">
        <span class="chip-label-glass"><mat-icon>filter_list</mat-icon> Filter:</span>
        <button class="chip-glass" [class.active]="selectedBrand === ''" (click)="filterByBrand('')">
          All ({{inventoryData.length}})
        </button>
        <button class="chip-glass" *ngFor="let b of brands" [class.active]="selectedBrand === b" (click)="filterByBrand(b)">
          {{b}} ({{countByBrand(b)}})
        </button>
      </div>

      <!-- Stock Grid Glass -->
      <div class="stock-glass-grid">
        <div *ngFor="let item of filtered; let i = index" class="phone-glass-card" [style.animation-delay]="i*60+'ms'" [class.sold]="item.status !== 'AVAILABLE'">
          <div class="card-glow-top"></div>
          <div class="phone-header-glass">
            <span class="brand-glass">{{item.brand || item.device?.brand || 'Samsung'}}</span>
            <span class="status-glass" [class.available]="item.status === 'AVAILABLE' || !item.status" [class.sold]="item.status === 'SOLD'">
              <span class="status-dot"></span>
              {{item.status === 'AVAILABLE' || !item.status ? 'IN STOCK' : item.status}}
            </span>
          </div>
          <div class="phone-model-glass">{{item.model || item.device?.model || 'S23'}} {{item.storage || '128GB'}}</div>
          <div class="phone-details-glass">
            <span><mat-icon>fingerprint</mat-icon> IMEI: {{(item.imei1 || '351234567890123') | slice:0:4}}****{{(item.imei1 || '123') | slice:-4}}</span>
            <span><mat-icon>palette</mat-icon> {{item.color || 'Black'}} • {{item.condition || 'Good'}}</span>
          </div>
          <div class="price-glass-row">
            <div class="price-item">
              <small>Buy</small>
              <span class="buy">₹{{item.buyPrice || 15000 | number}}</span>
            </div>
            <div class="price-divider"></div>
            <div class="price-item">
              <small>Sell</small>
              <span class="sell">₹{{item.sellingPrice || 18000 | number}}</span>
            </div>
            <div class="price-divider"></div>
            <div class="price-item profit">
              <small>Profit</small>
              <span class="profit">₹{{(item.sellingPrice || 18000) - (item.buyPrice || 15000) | number}}</span>
            </div>
          </div>
          <div class="seller-glass">
            <div class="seller-avatar-glass">{{(item.sellerName || 'R')[0]}}</div>
            <div class="seller-info-glass">
              <span class="seller-name">{{item.sellerName || 'Ramesh Bhai'}}</span>
              <span class="seller-phone"><mat-icon>call</mat-icon> {{item.sellerPhone || '98765 43210'}} • {{daysAgo(item.createdAt)}}d ago</span>
            </div>
            <div class="seller-badge-glass">{{item.createdAt | date:'dd MMM'}}</div>
          </div>
          <div class="card-actions-glass">
            <button class="action-glass primary"><mat-icon>point_of_sale</mat-icon> Becho</button>
            <button class="icon-action-glass"><mat-icon>edit</mat-icon></button>
            <button class="icon-action-glass"><mat-icon>share</mat-icon></button>
            <button class="icon-action-glass"><mat-icon>call</mat-icon></button>
          </div>
          <div class="card-float-emoji">📱</div>
        </div>

        <div *ngIf="filtered.length === 0" class="empty-glass">
          <div class="empty-icon-glass"><mat-icon>inventory_2</mat-icon></div>
          <h3>Koi stock nahi mila</h3>
          <p>Search change karo ya naya phone add karo</p>
          <button class="glass-btn primary">Naya Phone Add Karo</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .inventory-glass-2026 { position: relative; padding: 20px; min-height: 100vh; }
    .aurora-bg { position: fixed; inset: 0; z-index: -1; overflow: hidden; background: linear-gradient(135deg, #f8fafc, #e2e8f0, #f1f5f9); }
    .orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.12; animation: float 20s infinite ease-in-out; }
    .orb1 { width: 500px; height: 500px; background: radial-gradient(circle, #3b82f6, transparent 70%); top: -100px; left: -100px; }
    .orb2 { width: 600px; height: 600px; background: radial-gradient(circle, #8b5cf6, transparent 70%); bottom: -100px; right: -100px; animation-delay: -10s; }
    @keyframes float { 0%,100% { transform: translate(0,0) scale(1); } 50% { transform: translate(20px,-20px) scale(1.1); } }

    .glass-header { display: flex; justify-content: space-between; align-items: center; padding: 24px 28px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px) saturate(180%); border: 1px solid rgba(255,255,255,0.6); border-radius: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8); margin-bottom: 22px; flex-wrap: wrap; gap: 16px; animation: slideDown 0.6s cubic-bezier(0.16,1,0.3,1); }
    @keyframes slideDown { from { opacity:0; transform: translateY(-20px); } to { opacity:1; transform: translateY(0); } }
    .header-left { display: flex; align-items: center; gap: 18px; }
    .icon-glass { width: 56px; height: 56px; border-radius: 16px; display: flex; align-items: center; justify-content: center; backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.6); box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
    .icon-glass.stock { background: linear-gradient(135deg, rgba(59,130,246,0.15), rgba(59,130,246,0.05)); color: #2563eb; }
    .header-left h1 { margin:0; font-size: 22px; font-weight: 800; color: #0f172a; letter-spacing: -0.02em; }
    .header-left p { margin:4px 0 0; font-size: 13px; color: #64748b; }
    .header-actions { display: flex; gap: 12px; }
    .glass-btn { display: flex; align-items: center; gap: 8px; padding: 12px 20px; background: rgba(255,255,255,0.8); backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.6); border-radius: 14px; box-shadow: 0 4px 16px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9); font-weight: 600; cursor: pointer; transition: all 0.4s cubic-bezier(0.16,1,0.3,1); color: #334155; }
    .glass-btn:hover { transform: translateY(-2px) scale(1.02); box-shadow: 0 12px 28px rgba(0,0,0,0.1); }
    .glass-btn.primary { background: linear-gradient(135deg, #3b82f6, #6366f1); color: white; border-color: rgba(255,255,255,0.3); }

    .stats-glass-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 20px; }
    .stat-glass { position: relative; display: flex; align-items: center; gap: 14px; padding: 18px 20px; background: rgba(255,255,255,0.65); backdrop-filter: blur(16px); border: 1px solid rgba(255,255,255,0.6); border-radius: 18px; box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8); overflow: hidden; transition: all 0.4s; animation: fadeUp 0.6s both; }
    .stat-glass:hover { transform: translateY(-3px); box-shadow: 0 12px 32px rgba(0,0,0,0.1); }
    @keyframes fadeUp { from { opacity:0; transform: translateY(20px); } to { opacity:1; transform: translateY(0); } }
    .stat-icon-glass { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.04); border: 1px solid rgba(0,0,0,0.06); flex-shrink:0; }
    .stat-icon-glass.buy { background: rgba(239,68,68,0.1); color: #dc2626; border-color: rgba(239,68,68,0.15); }
    .stat-icon-glass.profit { background: rgba(16,185,129,0.1); color: #059669; border-color: rgba(16,185,129,0.15); }
    .stat-info { display: flex; flex-direction: column; gap: 2px; }
    .stat-info .label { font-size: 10px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.06em; }
    .stat-info strong { font-size: 18px; font-weight: 800; color: #0f172a; }
    .stat-info small { font-size: 11px; color: #64748b; }
    .profit-text { color: #059669 !important; font-weight: 600 !important; }
    .search-glass { position: relative; display: flex; align-items: center; gap: 12px; padding: 0 18px; background: rgba(255,255,255,0.7); backdrop-filter: blur(16px); border: 1px solid rgba(255,255,255,0.6); border-radius: 18px; box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8); transition: all 0.3s; min-width: 280px; }
    .search-glass:focus-within { background: rgba(255,255,255,0.9); box-shadow: 0 8px 28px rgba(0,0,0,0.1); border-color: rgba(59,130,246,0.2); transform: translateY(-1px); }
    .search-glass input { flex:1; border:none; background: transparent; padding: 16px 0; font-size: 13px; font-weight: 500; outline: none; color: #0f172a; }
    .search-glass mat-icon { color: #94a3b8; }

    .chips-glass-row { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; margin-bottom: 20px; padding: 14px 18px; background: rgba(255,255,255,0.5); backdrop-filter: blur(12px); border: 1px solid rgba(255,255,255,0.5); border-radius: 16px; }
    .chip-label-glass { display: flex; align-items: center; gap: 6px; font-size: 12px; font-weight: 700; color: #475569; }
    .chip-glass { padding: 8px 16px; background: rgba(255,255,255,0.7); backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.6); border-radius: 20px; font-size: 12px; font-weight: 600; color: #475569; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 10px rgba(0,0,0,0.04); }
    .chip-glass:hover { transform: translateY(-1px); background: rgba(255,255,255,0.9); }
    .chip-glass.active { background: #0f172a; color: white; border-color: #0f172a; box-shadow: 0 4px 16px rgba(15,23,42,0.2); }

    .stock-glass-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 18px; }
    .phone-glass-card { position: relative; padding: 20px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px) saturate(180%); border: 1px solid rgba(255,255,255,0.6); border-radius: 22px; box-shadow: 0 8px 32px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9); transition: all 0.5s cubic-bezier(0.16,1,0.3,1); overflow: hidden; animation: fadeUp 0.6s both; }
    .phone-glass-card:hover { transform: translateY(-6px) scale(1.02); box-shadow: 0 20px 50px rgba(0,0,0,0.12), inset 0 1px 0 rgba(255,255,255,1); border-color: rgba(255,255,255,0.8); }
    .phone-glass-card.sold { opacity: 0.6; }
    .card-glow-top { position: absolute; top:0; left:0; right:0; height:1px; background: linear-gradient(90deg, transparent, rgba(59,130,246,0.5), transparent); }
    .phone-header-glass { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .brand-glass { padding: 6px 14px; background: linear-gradient(135deg, rgba(99,102,241,0.1), rgba(59,130,246,0.1)); border: 1px solid rgba(99,102,241,0.15); border-radius: 20px; font-size: 11px; font-weight: 800; color: #6366f1; letter-spacing: 0.05em; }
    .status-glass { display: flex; align-items: center; gap: 6px; padding: 6px 12px; border-radius: 20px; font-size: 10px; font-weight: 700; letter-spacing: 0.05em; }
    .status-glass.available { background: rgba(16,185,129,0.1); color: #065f46; border: 1px solid rgba(16,185,129,0.15); }
    .status-glass.sold { background: rgba(239,68,68,0.1); color: #991b1b; border: 1px solid rgba(239,68,68,0.15); }
    .status-dot { width:6px; height:6px; border-radius:50%; background: currentColor; animation: pulse 2s infinite; }
    @keyframes pulse { 0% { box-shadow: 0 0 0 0 currentColor; } 70% { box-shadow: 0 0 0 6px transparent; } 100% { box-shadow: 0 0 0 0 transparent; } }
    .phone-model-glass { font-size: 20px; font-weight: 800; color: #0f172a; letter-spacing: -0.02em; margin-bottom: 10px; }
    .phone-details-glass { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
    .phone-details-glass span { display: flex; align-items: center; gap: 6px; font-size: 11px; color: #64748b; font-weight: 500; }
    .phone-details-glass mat-icon { font-size: 14px; width:14px; height:14px; }
    .price-glass-row { display: flex; justify-content: space-between; align-items: center; padding: 14px; background: rgba(248,250,252,0.8); backdrop-filter: blur(10px); border: 1px solid rgba(0,0,0,0.04); border-radius: 14px; margin-bottom: 14px; }
    .price-item { display: flex; flex-direction: column; align-items: center; gap: 2px; }
    .price-item small { font-size: 9px; font-weight: 700; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.06em; }
    .price-item span { font-weight: 700; font-size: 13px; }
    .price-item .buy { color: #64748b; }
    .price-item .sell { color: #0f172a; font-size: 16px; font-weight: 800; }
    .price-item.profit .profit { color: #059669; font-size: 15px; font-weight: 800; }
    .price-divider { width:1px; height: 28px; background: rgba(0,0,0,0.06); }
    .seller-glass { display: flex; align-items: center; gap: 12px; padding: 12px; background: rgba(255,255,255,0.6); border: 1px solid rgba(255,255,255,0.6); border-radius: 12px; margin-bottom: 14px; }
    .seller-avatar-glass { width: 36px; height: 36px; border-radius: 10px; background: linear-gradient(135deg, #3b82f6, #8b5cf6); color: white; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 14px; box-shadow: 0 4px 12px rgba(59,130,246,0.2); flex-shrink:0; }
    .seller-info-glass { flex:1; display: flex; flex-direction: column; gap: 2px; min-width:0; }
    .seller-name { font-size: 13px; font-weight: 700; color: #0f172a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .seller-phone { display: flex; align-items: center; gap: 4px; font-size: 11px; color: #64748b; }
    .seller-phone mat-icon { font-size: 12px; width:12px; height:12px; }
    .seller-badge-glass { padding: 4px 10px; background: rgba(0,0,0,0.04); border: 1px solid rgba(0,0,0,0.06); border-radius: 20px; font-size: 10px; font-weight: 600; color: #475569; }
    .card-actions-glass { display: flex; gap: 8px; align-items: center; }
    .action-glass { flex:1; display: flex; align-items: center; justify-content: center; gap: 6px; padding: 12px; border-radius: 12px; border: 1px solid; font-weight: 700; font-size: 12px; cursor: pointer; transition: all 0.3s; }
    .action-glass.primary { background: linear-gradient(135deg, #3b82f6, #6366f1); color: white; border-color: rgba(255,255,255,0.2); box-shadow: 0 4px 16px rgba(59,130,246,0.2); }
    .action-glass.primary:hover { transform: translateY(-1px); box-shadow: 0 8px 20px rgba(59,130,246,0.3); }
    .icon-action-glass { width: 40px; height: 40px; border-radius: 10px; background: rgba(255,255,255,0.7); border: 1px solid rgba(0,0,0,0.06); display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.3s; color: #64748b; }
    .icon-action-glass:hover { background: rgba(255,255,255,0.9); transform: translateY(-1px); color: #0f172a; }
    .card-float-emoji { position: absolute; bottom: 10px; right: 14px; font-size: 48px; opacity: 0.03; transform: rotate(-15deg); pointer-events: none; }
    .empty-glass { grid-column: 1/-1; display: flex; flex-direction: column; align-items: center; gap: 16px; padding: 60px; background: rgba(255,255,255,0.6); backdrop-filter: blur(16px); border: 1px solid rgba(255,255,255,0.6); border-radius: 24px; text-align: center; }
    .empty-icon-glass { width: 80px; height: 80px; background: rgba(0,0,0,0.04); border-radius: 20px; display: flex; align-items: center; justify-content: center; }
    .empty-icon-glass mat-icon { font-size: 40px; width:40px; height:40px; opacity:0.3; }
  `]
})
export class InventoryListComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  inventoryData: any[] = [];
  filtered: any[] = [];
  search = '';
  selectedBrand = '';
  brands: string[] = [];
  brandCount = 0;
  stockValue = 0;
  sellValue = 0;
  profit = 0;

  ngOnInit() { this.loadInventory(); }

  loadInventory() {
    this.inventoryService.getInventoryList(0, 100).subscribe({
      next: (res) => {
        const data = res.content || res.data?.content || res.data || [];
        this.inventoryData = data.length ? data : this.mockData();
        this.process();
      },
      error: () => { this.inventoryData = this.mockData(); this.process(); }
    });
  }

  mockData() {
    return [
      { id: '1', brand: 'Samsung', model: 'S23', storage: '256GB', color: 'Black', imei1: '351234567890123', buyPrice: 15000, sellingPrice: 18000, status: 'AVAILABLE', sellerName: 'Ramesh Bhai', sellerPhone: '98765 43210', createdAt: new Date(), condition: 'Excellent' },
      { id: '2', brand: 'iPhone', model: '12', storage: '128GB', color: 'White', imei1: '352345678901234', buyPrice: 22000, sellingPrice: 28000, status: 'AVAILABLE', sellerName: 'Mahesh', sellerPhone: '98765 43211', createdAt: new Date(Date.now() - 86400000), condition: 'Good' },
      { id: '3', brand: 'Redmi', model: 'Note 13', storage: '128GB', color: 'Blue', imei1: '353456789012345', buyPrice: 10000, sellingPrice: 13500, status: 'AVAILABLE', sellerName: 'Suresh', sellerPhone: '98765 43212', createdAt: new Date(Date.now() - 2*86400000), condition: 'Good' },
      { id: '4', brand: 'OnePlus', model: '11R', storage: '256GB', color: 'Green', imei1: '354567890123456', buyPrice: 18000, sellingPrice: 22000, status: 'AVAILABLE', sellerName: 'Amit', sellerPhone: '98765 43213', createdAt: new Date(Date.now() - 3*86400000), condition: 'Excellent' },
      { id: '5', brand: 'Samsung', model: 'A54', storage: '128GB', color: 'Black', imei1: '355678901234567', buyPrice: 12000, sellingPrice: 15000, status: 'AVAILABLE', sellerName: 'Vijay', sellerPhone: '98765 43214', createdAt: new Date(Date.now() - 5*86400000), condition: 'Average' },
    ];
  }

  process() { this.filtered = [...this.inventoryData]; this.brands = [...new Set(this.inventoryData.map(i => i.brand || i.device?.brand).filter(Boolean))]; this.brandCount = this.brands.length; this.calcValues(); }
  calcValues() { this.stockValue = this.filtered.reduce((s, i) => s + (i.buyPrice || 0), 0); this.sellValue = this.filtered.reduce((s, i) => s + (i.sellingPrice || 0), 0); this.profit = this.sellValue - this.stockValue; }
  filter() { this.applyFilters(this.search.toLowerCase(), this.selectedBrand); }
  filterByBrand(brand: string) { this.selectedBrand = brand; this.applyFilters(this.search.toLowerCase(), brand); }
  applyFilters(searchQ: string, brand: string) { this.filtered = this.inventoryData.filter(item => { const matchesSearch = !searchQ || `${item.brand} ${item.model} ${item.imei1} ${item.sellerName}`.toLowerCase().includes(searchQ); const matchesBrand = !brand || (item.brand || item.device?.brand) === brand; return matchesSearch && matchesBrand; }); this.calcValues(); }
  countByBrand(brand: string) { return this.inventoryData.filter(i => (i.brand || i.device?.brand) === brand).length; }
  daysAgo(date: any) { if (!date) return 0; const diff = Date.now() - new Date(date).getTime(); return Math.floor(diff / 86400000); }
}
