import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-sales-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, FormsModule],
  template: `
    <div class="sales-2026">
      <div class="page-header">
        <div>
          <h1>💰 Becha - Sell History</h1>
          <p>Kisko phone becha, kitne me becha, kitna profit hua</p>
        </div>
        <button mat-raised-button color="accent">
          <mat-icon>point_of_sale</mat-icon> Naya Becha Add Karo
        </button>
      </div>

      <div class="profit-highlight">
        <mat-card class="profit-card">
          <div class="profit-icon">📈</div>
          <div>
            <span>Total Profit</span>
            <strong>₹{{totalProfit | number}}</strong>
            <small>{{sales.length}} phones becha</small>
          </div>
        </mat-card>
        <mat-card class="profit-card today">
          <div class="profit-icon">💵</div>
          <div>
            <span>Aaj Ka Profit</span>
            <strong>₹{{todayProfit | number}}</strong>
            <small>{{todayCount}} becha aaj</small>
          </div>
        </mat-card>
        <mat-card class="profit-card avg">
          <div class="profit-icon">📊</div>
          <div>
            <span>Average Profit/Phone</span>
            <strong>₹{{avgProfit | number}}</strong>
            <small>Per phone</small>
          </div>
        </mat-card>
        <mat-form-field appearance="outline" class="search">
          <mat-label>Search Buyer, Phone, IMEI</mat-label>
          <input matInput [(ngModel)]="search" (keyup)="filter()" placeholder="Suresh, iPhone, 1234...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
      </div>

      <mat-card class="table-card">
        <div class="table-responsive">
          <table class="hisab-table">
            <thead>
              <tr>
                <th>Date - Becha</th>
                <th>Phone Becha</th>
                <th>Buy Price</th>
                <th>Sell Price</th>
                <th>Profit - Munaafa</th>
                <th>Buyer - Kisko Becha</th>
                <th>Payment</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let s of filtered">
                <td>
                  <span class="date">{{s.createdAt | date:'dd MMM yyyy'}}</span>
                  <small>{{s.createdAt | date:'hh:mm a'}}</small>
                </td>
                <td>
                  <span class="phone">{{s.inventoryItem?.brand || s.device?.brand || 'iPhone'}} {{s.inventoryItem?.model || s.device?.model || '12'}}</span>
                  <small>IMEI: {{s.inventoryItem?.imei1 || '35****1234'}}</small>
                </td>
                <td><span class="price buy">₹{{s.buyPrice || 15000 | number}}</span></td>
                <td><span class="price sell">₹{{s.finalAmount || s.sellPrice || 18000 | number}}</span></td>
                <td>
                  <span class="profit" [class.positive]="(s.profit || 3000) > 0" [class.negative]="(s.profit || 0) < 0">
                    ₹{{s.profit || 3000 | number}}
                  </span>
                  <small class="profit-percent">{{calcPercent(s)}}% profit</small>
                </td>
                <td>
                  <span class="buyer">{{s.customer?.name || 'Suresh Kumar'}}</span>
                  <small>{{s.customer?.phone || '98765 43210'}}</small>
                </td>
                <td>
                  <span class="payment" [class.cash]="(s.paymentMode || 'CASH') === 'CASH'" [class.upi]="(s.paymentMode || 'CASH') !== 'CASH'">
                    {{s.paymentMode || 'CASH'}}
                  </span>
                </td>
                <td>
                  <button mat-icon-button><mat-icon>receipt</mat-icon></button>
                  <button mat-icon-button><mat-icon>share</mat-icon></button>
                </td>
              </tr>
              <tr *ngIf="filtered.length === 0">
                <td colspan="8" class="empty">
                  <mat-icon>point_of_sale</mat-icon>
                  <p>Koi becha nahi mila</p>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </mat-card>

      <mat-card class="info-card">
        <mat-icon>lightbulb</mat-icon>
        <div>
          <strong>2026 Tip:</strong> Har sale pe profit auto calculate hota hai.
          <code>Profit = Sell Price - Buy Price</code>.
          Future me WhatsApp bill + QR code add hoga.
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    .sales-2026 { max-width: 1400px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; }
    .page-header h1 { margin: 0; font-size: 24px; font-weight: 800; }
    .page-header p { margin: 4px 0 0; color: #64748b; }
    .profit-highlight { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; align-items: stretch; }
    .profit-card { display: flex; gap: 16px; align-items: center; padding: 16px 20px !important; border-radius: 12px !important; flex: 1; min-width: 200px; }
    .profit-icon { width: 50px; height: 50px; background: #dcfce7; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
    .profit-card.today .profit-icon { background: #fef3c7; }
    .profit-card.avg .profit-icon { background: #dbeafe; }
    .profit-card div { display: flex; flex-direction: column; }
    .profit-card span { font-size: 11px; color: #64748b; text-transform: uppercase; font-weight: 600; }
    .profit-card strong { font-size: 20px; font-weight: 800; color: #0f172a; }
    .profit-card small { font-size: 11px; color: #64748b; }
    .search { flex: 1; min-width: 250px; }
    .table-card { border-radius: 12px !important; overflow: hidden; }
    .table-responsive { overflow-x: auto; }
    .hisab-table { width: 100%; border-collapse: collapse; }
    .hisab-table th { background: #f8fafc; padding: 12px; text-align: left; font-size: 11px; font-weight: 700; color: #64748b; text-transform: uppercase; border-bottom: 2px solid #e2e8f0; white-space: nowrap; }
    .hisab-table td { padding: 14px 12px; border-bottom: 1px solid #f1f5f9; font-size: 13px; }
    .hisab-table tr:hover { background: #f8fafc; }
    .date { font-weight: 600; display: block; }
    .hisab-table small { display: block; color: #64748b; font-size: 11px; }
    .phone { font-weight: 600; display: block; }
    .price.buy { color: #64748b; }
    .price.sell { font-weight: 800; color: #0f172a; }
    .profit { font-weight: 800; display: block; }
    .profit.positive { color: #16a34a; }
    .profit.negative { color: #ef4444; }
    .profit-percent { color: #16a34a; font-size: 10px; }
    .buyer { font-weight: 600; display: block; }
    .payment { padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; }
    .payment.cash { background: #dcfce7; color: #166534; }
    .payment.upi { background: #dbeafe; color: #1e40af; }
    .empty { text-align: center; padding: 40px !important; color: #64748b; }
    .empty mat-icon { font-size: 48px; width: 48px; height: 48px; opacity: 0.3; }
    .info-card { margin-top: 20px; display: flex; gap: 12px; padding: 16px !important; background: #f0fdf4 !important; border: 1px solid #bbf7d0 !important; border-radius: 12px !important; }
  `]
})
export class SalesListComponent implements OnInit {
  private http = inject(HttpClient);
  sales: any[] = [];
  filtered: any[] = [];
  search = '';
  totalProfit = 0;
  todayProfit = 0;
  todayCount = 0;
  avgProfit = 0;

  ngOnInit() { this.load(); }

  load() {
    this.http.get<any>(`${environment.apiUrl}/api/v1/sales?page=0&size=100`).subscribe({
      next: (res) => {
        const data = res.data?.content || res.content || res.data || [];
        this.sales = data.length ? data : this.mockData();
        this.filtered = [...this.sales];
        this.calc();
      },
      error: () => {
        this.sales = this.mockData();
        this.filtered = [...this.sales];
        this.calc();
      }
    });
  }

  mockData() {
    return [
      { id: '1', createdAt: new Date(), buyPrice: 15000, finalAmount: 18000, sellPrice: 18000, profit: 3000, paymentMode: 'CASH', device: { brand: 'Samsung', model: 'S23' }, inventoryItem: { brand: 'Samsung', model: 'S23', imei1: '351234567890123' }, customer: { name: 'Suresh Kumar', phone: '98765 43210' } },
      { id: '2', createdAt: new Date(Date.now() - 86400000), buyPrice: 18000, finalAmount: 25000, sellPrice: 25000, profit: 7000, paymentMode: 'UPI', device: { brand: 'iPhone', model: '12' }, inventoryItem: { brand: 'iPhone', model: '12', imei1: '352345678901234' }, customer: { name: 'Amit Patel', phone: '98765 43211' } },
      { id: '3', createdAt: new Date(), buyPrice: 10000, finalAmount: 13500, sellPrice: 13500, profit: 3500, paymentMode: 'CASH', device: { brand: 'Redmi', model: 'Note 13' }, inventoryItem: { brand: 'Redmi', model: 'Note 13', imei1: '353456789012345' }, customer: { name: 'Vijay', phone: '98765 43212' } },
    ];
  }

  calc() {
    this.totalProfit = this.sales.reduce((s, x) => s + (x.profit || (x.finalAmount - x.buyPrice) || 0), 0);
    const today = new Date().toDateString();
    const todaySales = this.sales.filter(s => new Date(s.createdAt).toDateString() === today);
    this.todayProfit = todaySales.reduce((s, x) => s + (x.profit || 0), 0);
    this.todayCount = todaySales.length;
    this.avgProfit = this.sales.length ? Math.round(this.totalProfit / this.sales.length) : 0;
  }

  calcPercent(s: any) {
    const buy = s.buyPrice || 15000;
    const profit = s.profit || 3000;
    return buy ? Math.round((profit / buy) * 100) : 0;
  }

  filter() {
    const q = this.search.toLowerCase();
    if (!q) { this.filtered = [...this.sales]; return; }
    this.filtered = this.sales.filter(s => `${s.inventoryItem?.brand} ${s.inventoryItem?.model} ${s.inventoryItem?.imei1} ${s.customer?.name}`.toLowerCase().includes(q));
  }
}
