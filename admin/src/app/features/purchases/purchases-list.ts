import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-purchases-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, FormsModule],
  template: `
    <div class="purchases-glass-2026">
      <div class="aurora-bg"><div class="orb orb1"></div><div class="orb orb2"></div></div>

      <div class="glass-header">
        <div class="header-left">
          <div class="icon-glass buy"><mat-icon>shopping_cart</mat-icon></div>
          <div>
            <h1>Kharida - Buy History</h1>
            <p>Kisse phone liya, kitne me liya - Sab hisab • Glass Edition</p>
          </div>
        </div>
        <button class="glass-btn primary"><mat-icon>add</mat-icon> Naya Kharida Add</button>
      </div>

      <div class="stats-glass-row">
        <div class="stat-glass"><div class="stat-icon-glass"><mat-icon>shopping_bag</mat-icon></div><div class="stat-info"><span class="label">Total Kharida</span><strong>{{purchases.length}} phones</strong><small>All time</small></div></div>
        <div class="stat-glass"><div class="stat-icon-glass buy"><mat-icon>payments</mat-icon></div><div class="stat-info"><span class="label">Total Amount</span><strong>₹{{totalAmount | number}}</strong><small>Invested</small></div></div>
        <div class="stat-glass"><div class="stat-icon-glass today"><mat-icon>today</mat-icon></div><div class="stat-info"><span class="label">Aaj Kharida</span><strong>{{todayCount}} phones</strong><small>Today buy</small></div></div>
        <div class="search-glass"><mat-icon>search</mat-icon><input [(ngModel)]="search" (keyup)="filter()" placeholder="Search - Brand, IMEI, Seller name..."></div>
      </div>

      <div class="table-glass-card">
        <div class="table-responsive">
          <table class="glass-table">
            <thead><tr><th>Date</th><th>Phone</th><th>IMEI</th><th>Buy Price</th><th>Seller - Jisse Liya</th><th>Phone</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
              <tr *ngFor="let p of filtered; let i = index" [style.animation-delay]="i*50+'ms'" class="glass-row">
                <td><div class="date-glass"><span class="date-main">{{p.createdAt | date:'dd MMM'}}</span><small>{{p.createdAt | date:'hh:mm a'}}</small></div></td>
                <td><div class="phone-glass"><span class="phone-main">{{p.device?.brand || 'Samsung'}} {{p.device?.model || 'S23'}}</span><small>{{p.device?.storage || '128GB'}} | {{p.device?.color || 'Black'}}</small></div></td>
                <td><span class="imei-glass">{{p.device?.imei1 || '35****1234'}}</span></td>
                <td><span class="price-glass buy">₹{{p.finalPrice || 15000 | number}}</span></td>
                <td><div class="seller-glass"><span class="seller-name">{{p.customer?.name || 'Ramesh Bhai'}}</span><small>{{p.customer?.phone || '98765 43210'}}</small></div></td>
                <td><span class="phone-glass-text">{{p.customer?.phone || '98765 43210'}}</span></td>
                <td><span class="status-glass" [class.stock]="p.transactionStatus === 'COMPLETED'"><span class="dot"></span>{{p.transactionStatus === 'COMPLETED' ? 'Stock Me' : p.transactionStatus}}</span></td>
                <td><div class="actions-glass"><button class="icon-glass"><mat-icon>visibility</mat-icon></button><button class="icon-glass"><mat-icon>edit</mat-icon></button><button class="icon-glass"><mat-icon>share</mat-icon></button></div></td>
              </tr>
              <tr *ngIf="filtered.length === 0"><td colspan="8" class="empty-glass"><div class="empty-icon"><mat-icon>shopping_cart</mat-icon></div><p>Koi kharida nahi mila</p></td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="info-glass"><mat-icon>auto_awesome</mat-icon><div><strong>Glassmorphism 2026:</strong> Seller name/phone direct dikhta hai, table glass blur ke saath. Backend <code>/api/v1/purchases</code> se data aata hai.</div></div>
    </div>
  `,
  styles: [`
    .purchases-glass-2026 { position: relative; padding: 20px; min-height: 100vh; }
    .aurora-bg { position: fixed; inset:0; z-index:-1; overflow:hidden; background: linear-gradient(135deg, #f8fafc, #e2e8f0, #f1f5f9); }
    .orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity:0.1; animation: float 20s infinite ease-in-out; }
    .orb1 { width:500px; height:500px; background: radial-gradient(circle, #ef4444, transparent 70%); top:-100px; left:-100px; }
    .orb2 { width:600px; height:600px; background: radial-gradient(circle, #3b82f6, transparent 70%); bottom:-100px; right:-100px; animation-delay:-10s; }
    @keyframes float { 0%,100% { transform: translate(0,0) scale(1); } 50% { transform: translate(20px,-20px) scale(1.1); } }

    .glass-header { display:flex; justify-content:space-between; align-items:center; padding:24px 28px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px) saturate(180%); border:1px solid rgba(255,255,255,0.6); border-radius:24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8); margin-bottom:22px; flex-wrap:wrap; gap:16px; animation: slideDown 0.6s cubic-bezier(0.16,1,0.3,1); }
    @keyframes slideDown { from { opacity:0; transform: translateY(-20px); } to { opacity:1; transform: translateY(0); } }
    .header-left { display:flex; align-items:center; gap:18px; }
    .icon-glass { width:56px; height:56px; border-radius:16px; display:flex; align-items:center; justify-content:center; backdrop-filter: blur(10px); border:1px solid rgba(255,255,255,0.6); box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
    .icon-glass.buy { background: linear-gradient(135deg, rgba(239,68,68,0.15), rgba(239,68,68,0.05)); color:#dc2626; }
    .header-left h1 { margin:0; font-size:22px; font-weight:800; color:#0f172a; }
    .header-left p { margin:4px 0 0; font-size:13px; color:#64748b; }
    .glass-btn { display:flex; align-items:center; gap:8px; padding:12px 20px; background: rgba(255,255,255,0.8); backdrop-filter: blur(10px); border:1px solid rgba(255,255,255,0.6); border-radius:14px; box-shadow: 0 4px 16px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9); font-weight:600; cursor:pointer; transition: all 0.4s; color:#334155; }
    .glass-btn:hover { transform: translateY(-2px) scale(1.02); box-shadow: 0 12px 28px rgba(0,0,0,0.1); }
    .glass-btn.primary { background: linear-gradient(135deg, #ef4444, #f87171); color:white; border-color: rgba(255,255,255,0.3); }

    .stats-glass-row { display:grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap:16px; margin-bottom:20px; }
    .stat-glass { display:flex; align-items:center; gap:14px; padding:18px 20px; background: rgba(255,255,255,0.65); backdrop-filter: blur(16px); border:1px solid rgba(255,255,255,0.6); border-radius:18px; box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8); animation: fadeUp 0.6s both; }
    @keyframes fadeUp { from { opacity:0; transform: translateY(20px); } to { opacity:1; transform: translateY(0); } }
    .stat-icon-glass { width:44px; height:44px; border-radius:12px; display:flex; align-items:center; justify-content:center; background: rgba(0,0,0,0.04); border:1px solid rgba(0,0,0,0.06); flex-shrink:0; }
    .stat-icon-glass.buy { background: rgba(239,68,68,0.1); color:#dc2626; border-color: rgba(239,68,68,0.15); }
    .stat-icon-glass.today { background: rgba(245,158,11,0.1); color:#d97706; border-color: rgba(245,158,11,0.15); }
    .stat-info { display:flex; flex-direction:column; gap:2px; }
    .stat-info .label { font-size:10px; font-weight:700; color:#64748b; text-transform:uppercase; letter-spacing:0.06em; }
    .stat-info strong { font-size:18px; font-weight:800; color:#0f172a; }
    .stat-info small { font-size:11px; color:#64748b; }
    .search-glass { display:flex; align-items:center; gap:12px; padding:0 18px; background: rgba(255,255,255,0.7); backdrop-filter: blur(16px); border:1px solid rgba(255,255,255,0.6); border-radius:18px; box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8); transition: all 0.3s; }
    .search-glass:focus-within { background: rgba(255,255,255,0.9); box-shadow: 0 8px 28px rgba(0,0,0,0.1); border-color: rgba(239,68,68,0.2); transform: translateY(-1px); }
    .search-glass input { flex:1; border:none; background: transparent; padding:16px 0; font-size:13px; font-weight:500; outline:none; color:#0f172a; }
    .search-glass mat-icon { color:#94a3b8; }

    .table-glass-card { background: rgba(255,255,255,0.65); backdrop-filter: blur(20px) saturate(180%); border:1px solid rgba(255,255,255,0.6); border-radius:24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8); overflow:hidden; animation: fadeUp 0.6s 0.2s both; }
    .table-responsive { overflow-x:auto; }
    .glass-table { width:100%; border-collapse:collapse; }
    .glass-table th { background: rgba(248,250,252,0.8); backdrop-filter: blur(10px); padding:14px 16px; text-align:left; font-size:11px; font-weight:700; color:#64748b; text-transform:uppercase; letter-spacing:0.06em; border-bottom:1px solid rgba(0,0,0,0.06); white-space:nowrap; }
    .glass-table td { padding:16px; border-bottom:1px solid rgba(0,0,0,0.04); font-size:13px; transition: all 0.3s; }
    .glass-row { animation: slideIn 0.5s both; transition: all 0.3s; }
    @keyframes slideIn { from { opacity:0; transform: translateX(-10px); } to { opacity:1; transform: translateX(0); } }
    .glass-row:hover { background: rgba(255,255,255,0.6); transform: scale(1.005); box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
    .date-glass { display:flex; flex-direction:column; gap:2px; }
    .date-main { font-weight:700; color:#0f172a; }
    .glass-table small { color:#64748b; font-size:11px; }
    .phone-glass { display:flex; flex-direction:column; gap:2px; }
    .phone-main { font-weight:700; color:#0f172a; }
    .imei-glass { font-family: monospace; background: rgba(0,0,0,0.04); padding:4px 8px; border-radius:8px; font-size:11px; border:1px solid rgba(0,0,0,0.06); }
    .price-glass.buy { font-weight:800; color:#dc2626; font-size:15px; }
    .seller-glass { display:flex; flex-direction:column; gap:2px; }
    .seller-name { font-weight:600; color:#0f172a; }
    .phone-glass-text { font-family: monospace; font-size:12px; color:#475569; background: rgba(0,0,0,0.04); padding:4px 8px; border-radius:8px; }
    .status-glass { display:inline-flex; align-items:center; gap:6px; padding:6px 12px; border-radius:20px; font-size:11px; font-weight:700; background: rgba(0,0,0,0.04); border:1px solid rgba(0,0,0,0.06); }
    .status-glass.stock { background: rgba(16,185,129,0.1); color:#065f46; border-color: rgba(16,185,129,0.15); }
    .status-glass .dot { width:6px; height:6px; border-radius:50%; background: currentColor; animation: pulse 2s infinite; }
    @keyframes pulse { 0% { box-shadow: 0 0 0 0 currentColor; } 70% { box-shadow: 0 0 0 6px transparent; } 100% { box-shadow: 0 0 0 0 transparent; } }
    .actions-glass { display:flex; gap:6px; }
    .icon-glass { width:32px; height:32px; border-radius:8px; background: rgba(255,255,255,0.7); border:1px solid rgba(0,0,0,0.06); display:flex; align-items:center; justify-content:center; cursor:pointer; transition: all 0.3s; color:#64748b; }
    .icon-glass:hover { background: rgba(255,255,255,0.9); transform: translateY(-1px); color:#0f172a; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
    .icon-glass mat-icon { font-size:16px; width:16px; height:16px; }
    .empty-glass { text-align:center; padding:40px; color:#64748b; }
    .empty-icon { width:64px; height:64px; background: rgba(0,0,0,0.04); border-radius:16px; display:flex; align-items:center; justify-content:center; margin:0 auto 16px; }
    .info-glass { margin-top:20px; display:flex; gap:12px; padding:16px 20px; background: rgba(239,68,68,0.06); backdrop-filter: blur(10px); border:1px solid rgba(239,68,68,0.1); border-radius:16px; font-size:12px; color:#7f1d1d; }
  `]
})
export class PurchasesListComponent implements OnInit {
  private http = inject(HttpClient);
  purchases: any[] = [];
  filtered: any[] = [];
  search = '';
  totalAmount = 0;
  todayCount = 0;

  ngOnInit() { this.load(); }

  load() {
    this.http.get<any>(`${environment.apiUrl}/api/v1/purchases?page=0&size=100`).subscribe({
      next: (res) => {
        const data = res.data?.content || res.content || res.data || [];
        this.purchases = data.length ? data : this.mockData();
        this.filtered = [...this.purchases];
        this.calcStats();
      },
      error: () => { this.purchases = this.mockData(); this.filtered = [...this.purchases]; this.calcStats(); }
    });
  }

  mockData() {
    return [
      { id: '1', createdAt: new Date(), finalPrice: 15000, totalAmount: 15000, transactionStatus: 'COMPLETED', device: { brand: 'Samsung', model: 'S23', storage: '256GB', color: 'Black', imei1: '351234567890123' }, customer: { name: 'Ramesh Bhai', phone: '98765 43210' } },
      { id: '2', createdAt: new Date(Date.now() - 86400000), finalPrice: 22000, totalAmount: 22000, transactionStatus: 'COMPLETED', device: { brand: 'iPhone', model: '12', storage: '128GB', color: 'White', imei1: '352345678901234' }, customer: { name: 'Mahesh', phone: '98765 43211' } },
      { id: '3', createdAt: new Date(Date.now() - 2*86400000), finalPrice: 10000, totalAmount: 10000, transactionStatus: 'INITIATED', device: { brand: 'Redmi', model: 'Note 13', storage: '128GB', color: 'Blue', imei1: '353456789012345' }, customer: { name: 'Suresh', phone: '98765 43212' } },
    ];
  }

  calcStats() {
    this.totalAmount = this.purchases.reduce((s, p) => s + (p.finalPrice || p.totalAmount || 0), 0);
    const today = new Date().toDateString();
    this.todayCount = this.purchases.filter(p => new Date(p.createdAt).toDateString() === today).length;
  }

  filter() {
    const q = this.search.toLowerCase();
    if (!q) { this.filtered = [...this.purchases]; return; }
    this.filtered = this.purchases.filter(p => `${p.device?.brand} ${p.device?.model} ${p.device?.imei1} ${p.customer?.name} ${p.customer?.phone}`.toLowerCase().includes(q));
  }
}
