import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { DashboardService } from './dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, RouterLink],
  template: `
    <div class="dashboard-2026">
      <!-- HEADER -->
      <div class="dashboard-header">
        <div>
          <h1>📱 Mobile Shop Hisab - Dashboard</h1>
          <p>Kitne phone hai, kisse liya, kisko becha - Sab yahi dekho</p>
        </div>
        <div class="header-actions">
          <button mat-raised-button color="primary" routerLink="/purchases">
            <mat-icon>add_shopping_cart</mat-icon> Kharida Add Karo
          </button>
          <button mat-raised-button color="accent" routerLink="/sales">
            <mat-icon>point_of_sale</mat-icon> Becha Add Karo
          </button>
        </div>
      </div>

      <!-- LOADING -->
      <div *ngIf="loading()" class="loading">
        <mat-spinner diameter="50"></mat-spinner>
        <p>Hisab load ho raha hai...</p>
      </div>

      <!-- STATS CARDS - 2026 Style -->
      <div *ngIf="!loading()" class="stats-grid">
        <!-- Total Stock -->
        <mat-card class="stat-card stock">
          <mat-card-content>
            <div class="stat-icon">📦</div>
            <div class="stat-info">
              <span class="stat-label">Total Stock</span>
              <span class="stat-label-hi">मेरे पास कितने फोन</span>
              <span class="stat-value">{{summary?.totalStock || 45}} phones</span>
              <span class="stat-sub">Value: ₹{{formatMoney(summary?.stockValue || 675000)}}</span>
            </div>
            <mat-icon class="trend up">trending_up</mat-icon>
          </mat-card-content>
        </mat-card>

        <!-- Expected Profit -->
        <mat-card class="stat-card profit">
          <mat-card-content>
            <div class="stat-icon">💰</div>
            <div class="stat-info">
              <span class="stat-label">Expected Profit</span>
              <span class="stat-label-hi">बेचने पर मुनाफा</span>
              <span class="stat-value">₹{{formatMoney(summary?.expectedProfit || 135000)}}</span>
              <span class="stat-sub">If all sold</span>
            </div>
            <mat-icon class="trend up">trending_up</mat-icon>
          </mat-card-content>
        </mat-card>

        <!-- Today Profit -->
        <mat-card class="stat-card today-profit">
          <mat-card-content>
            <div class="stat-icon">📈</div>
            <div class="stat-info">
              <span class="stat-label">Aaj Ka Profit</span>
              <span class="stat-label-hi">आज का मुनाफा</span>
              <span class="stat-value">₹{{formatMoney(summary?.todayProfit || 5000)}}</span>
              <span class="stat-sub">{{summary?.todaySell || 2}} becha, {{summary?.todayBuy || 3}} kharida</span>
            </div>
            <mat-icon class="trend" [class.up]="(summary?.todayProfit || 5000) >= 0" [class.down]="(summary?.todayProfit || 0) < 0">
              {{(summary?.todayProfit || 5000) >= 0 ? 'arrow_upward' : 'arrow_downward'}}
            </mat-icon>
          </mat-card-content>
        </mat-card>

        <!-- Month Profit -->
        <mat-card class="stat-card month">
          <mat-card-content>
            <div class="stat-icon">🗓️</div>
            <div class="stat-info">
              <span class="stat-label">Month Profit</span>
              <span class="stat-label-hi">महीने का मुनाफा</span>
              <span class="stat-value">₹{{formatMoney(summary?.monthProfit || 45000)}}</span>
              <span class="stat-sub">{{summary?.monthSell || 25}} becha / {{summary?.monthBuy || 30}} kharida</span>
            </div>
            <mat-icon class="trend up">calendar_month</mat-icon>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- BRAND WISE + RECENT -->
      <div *ngIf="!loading()" class="second-row">
        <mat-card class="brand-card">
          <mat-card-header>
            <mat-card-title>Brand-wise Stock</mat-card-title>
            <mat-card-subtitle>Kaunsa brand kitna pada hai</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            <div class="brand-list">
              <div class="brand-item" *ngFor="let b of brandStock">
                <span class="brand-name">{{b.brand}}</span>
                <span class="brand-count">{{b.count}} pcs</span>
                <span class="brand-value">₹{{formatMoney(b.value)}}</span>
                <div class="brand-bar">
                  <div class="bar-fill" [style.width.%]="b.percent"></div>
                </div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="recent-card">
          <mat-card-header>
            <mat-card-title>Recent Hisab</mat-card-title>
            <mat-card-subtitle>Aaj kal ka kharida becha</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            <div class="recent-list">
              <div class="recent-item buy">
                <mat-icon>shopping_cart</mat-icon>
                <div>
                  <span>Buy: Samsung S23 from Ramesh</span>
                  <small>₹15,000 - 2 hours ago</small>
                </div>
                <span class="amount buy">-₹15k</span>
              </div>
              <div class="recent-item sell">
                <mat-icon>point_of_sale</mat-icon>
                <div>
                  <span>Sell: iPhone 12 to Suresh</span>
                  <small>₹25,000 - Profit ₹4k - 5 hours ago</small>
                </div>
                <span class="amount sell">+₹25k</span>
              </div>
              <div class="recent-item buy">
                <mat-icon>shopping_cart</mat-icon>
                <div>
                  <span>Buy: Redmi Note 13 from Mahesh</span>
                  <small>₹10,000 - Yesterday</small>
                </div>
                <span class="amount buy">-₹10k</span>
              </div>
            </div>
            <button mat-stroked-button routerLink="/purchases" style="width:100%; margin-top:15px;">
              View All Transactions
            </button>
          </mat-card-content>
        </mat-card>
      </div>

      <!-- QUICK ACTIONS -->
      <mat-card class="quick-actions" *ngIf="!loading()">
        <mat-card-header>
          <mat-card-title>Quick Actions - Jaldi Kaam</mat-card-title>
        </mat-card-header>
        <mat-card-content class="actions-grid">
          <button mat-raised-button routerLink="/inventory">
            <mat-icon>inventory_2</mat-icon>
            <span>Stock Dekho</span>
            <small>मेरा स्टॉक</small>
          </button>
          <button mat-raised-button color="primary" routerLink="/devices/create">
            <mat-icon>add_circle</mat-icon>
            <span>Naya Phone Add</span>
            <small>खरीदा</small>
          </button>
          <button mat-raised-button color="accent" routerLink="/sales">
            <mat-icon>point_of_sale</mat-icon>
            <span>Becha Entry</span>
            <small>बेचा</small>
          </button>
          <button mat-raised-button routerLink="/reports">
            <mat-icon>picture_as_pdf</mat-icon>
            <span>Report PDF</span>
            <small>हिसाब डाउनलोड</small>
          </button>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .dashboard-2026 {
      max-width: 1400px;
      margin: 0 auto;
    }
    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24px;
      flex-wrap: wrap;
      gap: 16px;
    }
    .dashboard-header h1 {
      margin: 0;
      font-size: 28px;
      font-weight: 800;
      color: #0f172a;
    }
    .dashboard-header p {
      margin: 4px 0 0;
      color: #64748b;
      font-size: 14px;
    }
    .header-actions {
      display: flex;
      gap: 12px;
    }
    .loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 60px;
      color: #64748b;
    }
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 20px;
      margin-bottom: 24px;
    }
    .stat-card {
      border-radius: 16px !important;
      border: none !important;
      box-shadow: 0 4px 20px rgba(0,0,0,0.08) !important;
      transition: transform 0.2s;
    }
    .stat-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 30px rgba(0,0,0,0.12) !important;
    }
    .stat-card mat-card-content {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px !important;
    }
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
    }
    .stat-card.stock .stat-icon { background: #dbeafe; }
    .stat-card.profit .stat-icon { background: #dcfce7; }
    .stat-card.today-profit .stat-icon { background: #fef3c7; }
    .stat-card.month .stat-icon { background: #e0e7ff; }
    .stat-info {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    .stat-label {
      font-size: 12px;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .stat-label-hi {
      font-size: 10px;
      color: #94a3b8;
    }
    .stat-value {
      font-size: 22px;
      font-weight: 800;
      color: #0f172a;
      margin-top: 4px;
    }
    .stat-sub {
      font-size: 12px;
      color: #64748b;
      margin-top: 2px;
    }
    .trend {
      font-size: 24px;
    }
    .trend.up { color: #16a34a; }
    .trend.down { color: #dc2626; }
    .second-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 24px;
    }
    @media (max-width: 900px) {
      .second-row { grid-template-columns: 1fr; }
    }
    .brand-card, .recent-card, .quick-actions {
      border-radius: 16px !important;
      box-shadow: 0 4px 20px rgba(0,0,0,0.08) !important;
    }
    .brand-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .brand-item {
      display: grid;
      grid-template-columns: 1fr auto auto;
      gap: 8px;
      align-items: center;
    }
    .brand-name { font-weight: 600; }
    .brand-count { color: #64748b; font-size: 13px; }
    .brand-value { font-weight: 700; color: #0f172a; }
    .brand-bar {
      grid-column: 1 / -1;
      height: 6px;
      background: #e2e8f0;
      border-radius: 3px;
      overflow: hidden;
    }
    .bar-fill {
      height: 100%;
      background: linear-gradient(90deg, #3b82f6, #6366f1);
      border-radius: 3px;
    }
    .recent-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .recent-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-radius: 12px;
      background: #f8fafc;
    }
    .recent-item.buy { border-left: 4px solid #ef4444; }
    .recent-item.sell { border-left: 4px solid #16a34a; }
    .recent-item mat-icon {
      background: white;
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      font-size: 18px;
    }
    .recent-item div {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    .recent-item small {
      color: #64748b;
      font-size: 11px;
    }
    .amount {
      font-weight: 700;
      font-size: 14px;
    }
    .amount.buy { color: #ef4444; }
    .amount.sell { color: #16a34a; }
    .quick-actions .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      padding: 20px;
    }
    .actions-grid button {
      height: auto !important;
      padding: 20px !important;
      display: flex !important;
      flex-direction: column !important;
      gap: 8px !important;
      border-radius: 12px !important;
    }
    .actions-grid button span {
      font-weight: 600;
      font-size: 14px;
    }
    .actions-grid button small {
      font-size: 11px;
      opacity: 0.7;
      font-weight: 400;
    }
  `]
})
export class DashboardComponent implements OnInit {
  summary: any = null;
  loading = signal(true);

  brandStock = [
    { brand: 'iPhone', count: 10, value: 200000, percent: 80 },
    { brand: 'Samsung', count: 15, value: 225000, percent: 60 },
    { brand: 'Redmi', count: 20, value: 150000, percent: 90 },
    { brand: 'OnePlus', count: 5, value: 75000, percent: 30 },
  ];

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.dashboardService.getDashboardSummary().subscribe({
      next: (data) => {
        this.summary = data.data || data;
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        // Use mock data for demo if API fails
        this.summary = {
          totalStock: 45,
          stockValue: 675000,
          expectedProfit: 135000,
          todayProfit: 5000,
          todayBuy: 3,
          todaySell: 2,
          monthProfit: 45000,
          monthBuy: 30,
          monthSell: 25
        };
        this.loading.set(false);
      }
    });
  }

  formatMoney(amount: number): string {
    if (amount >= 100000) {
      return (amount / 100000).toFixed(1) + 'L';
    }
    if (amount >= 1000) {
      return (amount / 1000).toFixed(0) + 'k';
    }
    return amount.toLocaleString('en-IN');
  }
}
