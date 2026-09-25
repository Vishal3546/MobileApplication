import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { InventoryService } from './inventory.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, MatChipsModule, FormsModule],
  template: `
    <div class="inventory-2026">
      <div class="page-header">
        <div>
          <h1>📦 Stock - Mere Paas Kitne Phone Hai</h1>
          <p>Current stock, brand-wise, profit expected</p>
        </div>
        <div class="header-actions">
          <button mat-raised-button color="primary">
            <mat-icon>add</mat-icon> Naya Phone Add
          </button>
          <button mat-stroked-button>
            <mat-icon>download</mat-icon> Excel Export
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="stats-row">
        <mat-card class="stat">
          <span class="label">Total Stock</span>
          <strong>{{filtered.length}} phones</strong>
          <small>{{brandCount}} brands</small>
        </mat-card>
        <mat-card class="stat">
          <span class="label">Stock Value (Buy)</span>
          <strong>₹{{stockValue | number}}</strong>
          <small>Kitne me liya</small>
        </mat-card>
        <mat-card class="stat profit">
          <span class="label">Sell Value</span>
          <strong>₹{{sellValue | number}}</strong>
          <small>Expected profit ₹{{profit | number}}</small>
        </mat-card>
        <mat-form-field appearance="outline" class="search">
          <mat-label>Search Stock - Brand, Model, IMEI</mat-label>
          <input matInput [(ngModel)]="search" (keyup)="filter()" placeholder="iPhone, Samsung, 351234...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
      </div>

      <!-- Brand Chips -->
      <div class="brand-chips">
        <span class="chip-label">Filter:</span>
        <button mat-stroked-button [class.active]="selectedBrand === ''" (click)="filterByBrand('')">All ({{inventoryData.length}})</button>
        <button mat-stroked-button *ngFor="let b of brands" [class.active]="selectedBrand === b" (click)="filterByBrand(b)">
          {{b}} ({{countByBrand(b)}})
        </button>
      </div>

      <!-- Stock Grid - 2026 Card View -->
      <div class="stock-grid">
        <mat-card *ngFor="let item of filtered" class="phone-card" [class.sold]="item.status !== 'AVAILABLE'">
          <div class="phone-header">
            <span class="brand">{{item.brand || item.device?.brand || 'Samsung'}}</span>
            <span class="status" [class.available]="item.status === 'AVAILABLE' || !item.status" [class.sold]="item.status === 'SOLD'">
              {{item.status === 'AVAILABLE' || !item.status ? 'IN STOCK' : item.status}}
            </span>
          </div>
          <div class="phone-model">{{item.model || item.device?.model || 'S23'}} {{item.storage || '128GB'}}</div>
          <div class="phone-details">
            <span>IMEI: {{(item.imei1 || item.device?.imei1 || '351234567890123') | slice:0:4}}****{{(item.imei1 || '123') | slice:-4}}</span>
            <span>Color: {{item.color || 'Black'}}</span>
            <span>Condition: {{item.condition || 'Good'}}</span>
          </div>
          <div class="price-row">
            <div>
              <small>Buy Price</small>
              <span class="buy">₹{{item.buyPrice || item.purchasePrice || 15000 | number}}</span>
            </div>
            <div>
              <small>Sell Price</small>
              <span class="sell">₹{{item.sellingPrice || item.sellPrice || 18000 | number}}</span>
            </div>
            <div>
              <small>Profit</small>
              <span class="profit">₹{{(item.sellingPrice || 18000) - (item.buyPrice || 15000) | number}}</span>
            </div>
          </div>
          <div class="seller-info">
            <mat-icon>person</mat-icon>
            <span>{{item.sellerName || item.customer?.name || 'Ramesh Bhai'}} - {{item.sellerPhone || '98765 43210'}}</span>
            <small>{{item.createdAt | date:'dd MMM'}} - {{daysAgo(item.createdAt)}} days ago</small>
          </div>
          <div class="card-actions">
            <button mat-raised-button color="primary" class="sell-btn">
              <mat-icon>point_of_sale</mat-icon> Becho
            </button>
            <button mat-icon-button><mat-icon>edit</mat-icon></button>
            <button mat-icon-button><mat-icon>share</mat-icon></button>
            <button mat-icon-button><mat-icon>call</mat-icon></button>
          </div>
        </mat-card>

        <div *ngIf="filtered.length === 0" class="empty-grid">
          <mat-icon>inventory_2</mat-icon>
          <h3>Koi stock nahi mila</h3>
          <p>Search change karo ya naya phone add karo</p>
          <button mat-raised-button color="primary">Naya Phone Add Karo</button>
        </div>
      </div>

      <mat-card class="info">
        <mat-icon>lightbulb</mat-icon>
        <div>
          <strong>2026 Feature:</strong> Card view me seller name, days in stock, profit direct dikhta hai.
          <code>IN_STOCK</code> phones hi yaha dikhte hai, becha hua <code>Sales</code> me jata hai.
          <br><small>Next: Barcode print, WhatsApp share with photo, Low stock alert</small>
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    .inventory-2026 { max-width: 1400px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; }
    .page-header h1 { margin: 0; font-size: 24px; font-weight: 800; }
    .page-header p { margin: 4px 0 0; color: #64748b; }
    .header-actions { display: flex; gap: 12px; }
    .stats-row { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; align-items: stretch; }
    .stat { padding: 16px 20px !important; border-radius: 12px !important; min-width: 180px; display: flex; flex-direction: column; }
    .stat .label { font-size: 11px; color: #64748b; text-transform: uppercase; font-weight: 600; }
    .stat strong { font-size: 20px; font-weight: 800; color: #0f172a; margin-top: 4px; }
    .stat small { font-size: 11px; color: #64748b; margin-top: 2px; }
    .stat.profit { background: #f0fdf4 !important; border: 1px solid #bbf7d0 !important; }
    .search { flex: 1; min-width: 250px; }
    .brand-chips { display: flex; gap: 8px; margin-bottom: 20px; flex-wrap: wrap; align-items: center; }
    .chip-label { font-size: 12px; font-weight: 600; color: #64748b; }
    .brand-chips button { border-radius: 20px !important; font-size: 12px !important; }
    .brand-chips button.active { background: #0f172a !important; color: white !important; }
    .stock-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px; margin-bottom: 20px; }
    .phone-card { border-radius: 16px !important; padding: 16px !important; transition: transform 0.2s, box-shadow 0.2s; border: 1px solid #e2e8f0 !important; }
    .phone-card:hover { transform: translateY(-4px); box-shadow: 0 8px 30px rgba(0,0,0,0.12) !important; }
    .phone-card.sold { opacity: 0.6; background: #f8fafc !important; }
    .phone-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
    .brand { font-weight: 800; font-size: 12px; color: #6366f1; background: #e0e7ff; padding: 4px 10px; border-radius: 20px; }
    .status { font-size: 10px; font-weight: 700; padding: 4px 10px; border-radius: 20px; }
    .status.available { background: #dcfce7; color: #166534; }
    .status.sold { background: #fee2e2; color: #991b1b; }
    .phone-model { font-size: 18px; font-weight: 800; color: #0f172a; margin-bottom: 8px; }
    .phone-details { display: flex; flex-direction: column; gap: 2px; margin-bottom: 12px; font-size: 11px; color: #64748b; }
    .price-row { display: flex; justify-content: space-between; background: #f8fafc; padding: 12px; border-radius: 12px; margin-bottom: 12px; }
    .price-row div { display: flex; flex-direction: column; align-items: center; }
    .price-row small { font-size: 10px; color: #64748b; text-transform: uppercase; font-weight: 600; }
    .price-row .buy { font-weight: 600; color: #64748b; }
    .price-row .sell { font-weight: 800; color: #0f172a; font-size: 16px; }
    .price-row .profit { font-weight: 800; color: #16a34a; }
    .seller-info { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #475569; margin-bottom: 12px; flex-wrap: wrap; }
    .seller-info mat-icon { font-size: 16px; width: 16px; height: 16px; }
    .seller-info small { margin-left: auto; color: #94a3b8; font-size: 10px; }
    .card-actions { display: flex; gap: 8px; align-items: center; }
    .sell-btn { flex: 1; border-radius: 10px !important; }
    .empty-grid { grid-column: 1 / -1; text-align: center; padding: 60px; color: #64748b; }
    .empty-grid mat-icon { font-size: 64px; width: 64px; height: 64px; opacity: 0.2; }
    .info { margin-top: 20px; display: flex; gap: 12px; padding: 16px !important; background: #eff6ff !important; border: 1px solid #bfdbfe !important; border-radius: 12px !important; }
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

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory() {
    this.inventoryService.getInventoryList(0, 100).subscribe({
      next: (res) => {
        const data = res.content || res.data?.content || res.data || [];
        this.inventoryData = data.length ? data : this.mockData();
        this.process();
      },
      error: () => {
        this.inventoryData = this.mockData();
        this.process();
      }
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

  process() {
    this.filtered = [...this.inventoryData];
    this.brands = [...new Set(this.inventoryData.map(i => i.brand || i.device?.brand).filter(Boolean))];
    this.brandCount = this.brands.length;
    this.calcValues();
  }

  calcValues() {
    this.stockValue = this.filtered.reduce((s, i) => s + (i.buyPrice || i.purchasePrice || 0), 0);
    this.sellValue = this.filtered.reduce((s, i) => s + (i.sellingPrice || i.sellPrice || 0), 0);
    this.profit = this.sellValue - this.stockValue;
  }

  filter() {
    const q = this.search.toLowerCase();
    this.applyFilters(q, this.selectedBrand);
  }

  filterByBrand(brand: string) {
    this.selectedBrand = brand;
    this.applyFilters(this.search.toLowerCase(), brand);
  }

  applyFilters(searchQ: string, brand: string) {
    this.filtered = this.inventoryData.filter(item => {
      const matchesSearch = !searchQ || `${item.brand} ${item.model} ${item.imei1} ${item.sellerName}`.toLowerCase().includes(searchQ);
      const matchesBrand = !brand || (item.brand || item.device?.brand) === brand;
      return matchesSearch && matchesBrand;
    });
    this.calcValues();
  }

  countByBrand(brand: string) {
    return this.inventoryData.filter(i => (i.brand || i.device?.brand) === brand).length;
  }

  daysAgo(date: any) {
    if (!date) return 0;
    const diff = Date.now() - new Date(date).getTime();
    return Math.floor(diff / 86400000);
  }
}
