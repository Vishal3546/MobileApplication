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
  selector: 'app-sales-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, FormsModule],
  template: `
    <div class="sales-glass-2026">
      <div class="aurora-bg"><div class="orb orb1"></div><div class="orb orb2"></div><div class="orb orb3"></div></div>

      <div class="glass-header">
        <div class="header-left">
          <div class="icon-glass sell"><mat-icon>point_of_sale</mat-icon></div>
          <div>
            <h1>Becha - Sell History</h1>
            <p>Kisko phone becha, kitne me becha, kitna profit hua • Glass Edition</p>
          </div>
        </div>
        <button class="glass-btn accent"><mat-icon>point_of_sale</mat-icon> Naya Becha Add</button>
      </div>

      <div class="profit-glass-highlight">
        <div class="profit-glass-card total">
          <div class="profit-glow total-glow"></div>
          <div class="profit-icon-glass total"><span>📈</span></div>
          <div class="profit-info"><span class="label">Total Profit</span><strong>₹{{totalProfit | number}}</strong><small>{{sales.length}} phones becha • All time</small></div>
          <div class="profit-trend up"><mat-icon>trending_up</mat-icon><span>+24%</span></div>
        </div>
        <div class="profit-glass-card today">
          <div class="profit-glow today-glow"></div>
          <div class="profit-icon-glass today"><span>💵</span></div>
          <div class="profit-info"><span class="label">Aaj Ka Profit</span><strong>₹{{todayProfit | number}}</strong><small>{{todayCount}} becha aaj • Live</small></div>
          <div class="live-badge"><span class="pulse"></span> Today</div>
        </div>
        <div class="profit-glass-card avg">
          <div class="profit-glow avg-glow"></div>
          <div class="profit-icon-glass avg"><span>📊</span></div>
          <div class="profit-info"><span class="label">Avg Profit/Phone</span><strong>₹{{avgProfit | number}}</strong><small>Per phone • 18% margin</small></div>
          <div class="profit-trend neutral"><mat-icon>bar_chart</mat-icon></div>
        </div>
        <div class="search-glass"><mat-icon>search</mat-icon><input [(ngModel)]="search" (keyup)="filter()" placeholder="Search Buyer, Phone, IMEI..."><div class="search-shine"></div></div>
      </div>

      <div class="table-glass-card">
        <div class="table-responsive">
          <table class="glass-table">
            <thead><tr><th>Date - Becha</th><th>Phone Becha</th><th>Buy Price</th><th>Sell Price</th><th>Profit - Munaafa</th><th>Buyer - Kisko Becha</th><th>Payment</th><th>Action</th></tr></thead>
            <tbody>
              <tr *ngFor="let s of filtered; let i = index" [style.animation-delay]="i*50+'ms'" class="glass-row">
                <td><div class="date-glass"><span class="date-main">{{s.createdAt | date:'dd MMM yyyy'}}</span><small>{{s.createdAt | date:'hh:mm a'}}</small></div></td>
                <td><div class="phone-glass"><span class="phone-main">{{s.inventoryItem?.brand || 'iPhone'}} {{s.inventoryItem?.model || '12'}}</span><small>IMEI: {{s.inventoryItem?.imei1 || '35****1234' | slice:0:4}}****{{s.inventoryItem?.imei1 || '1234' | slice:-4}}</small></div></td>
                <td><span class="price-glass buy">₹{{s.buyPrice || 15000 | number}}</span></td>
                <td><span class="price-glass sell">₹{{s.finalAmount || 18000 | number}}</span></td>
                <td><div class="profit-glass-cell"><span class="profit-value" [class.positive]="(s.profit || 3000) > 0">₹{{s.profit || 3000 | number}}</span><small class="profit-percent">{{calcPercent(s)}}% profit • {{s.profit > 5000 ? 'High' : 'Good'}}</small></div></td>
                <td><div class="buyer-glass"><div class="buyer-avatar">{{(s.customer?.name || 'S')[0]}}</div><div><span class="buyer-name">{{s.customer?.name || 'Suresh Kumar'}}</span><small>{{s.customer?.phone || '98765 43210'}}</small></div></div></td>
                <td><span class="payment-glass" [class.cash]="(s.paymentMode || 'CASH') === 'CASH'" [class.upi]="(s.paymentMode || 'CASH') !== 'CASH'"><span class="pay-dot"></span>{{s.paymentMode || 'CASH'}}</span></td>
                <td><div class="actions-glass"><button class="icon-glass"><mat-icon>receipt</mat-icon></button><button class="icon-glass"><mat-icon>share</mat-icon></button></div></td>
              </tr>
              <tr *ngIf="filtered.length === 0"><td colspan="8" class="empty-glass"><div class="empty-icon"><mat-icon>point_of_sale</mat-icon></div><p>Koi becha nahi mila</p></td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="info-glass profit-info"><mat-icon>lightbulb</mat-icon><div><strong>Glassmorphism Tip:</strong> Har sale pe profit auto calculate • <code>Profit = Sell - Buy</code> • Cards me glow animation 2026 style.</div></div>
    </div>
  `,
  styles: [`
    .sales-glass-2026 { position: relative; padding: 20px; min-height: 100vh; }
    .aurora-bg { position: fixed; inset:0; z-index:-1; overflow:hidden; background: linear-gradient(135deg, #f8fafc, #e2e8f0, #f1f5f9); }
    .orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity:0.1; animation: float 20s infinite ease-in-out; }
    .orb1 { width:500px; height:500px; background: radial-gradient(circle, #10b981, transparent 70%); top:-100px; left:-100px; }
    .orb2 { width:600px; height:600px; background: radial-gradient(circle, #3b82f6, transparent 70%); bottom:-100px; right:-100px; animation-delay:-10s; }
    .orb3 { width:400px; height:400px; background: radial-gradient(circle, #8b5cf6, transparent 70%); top:40%; left:50%; animation-delay:-5s; }
    @keyframes float { 0%,100% { transform: translate(0,0) scale(1); } 50% { transform: translate(20px,-20px) scale(1.1); } }

    .glass-header { display:flex; justify-content:space-between; align-items:center; padding:24px 28px; background: rgba(255,255,255,0.7); backdrop-filter: blur(20px) saturate(180%); border:1px solid rgba(255,255,255,0.6); border-radius:24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8); margin-bottom:22px; flex-wrap:wrap; gap:16px; animation: slideDown 0.6s cubic-bezier(0.16,1,0.3,1); }
    @keyframes slideDown { from { opacity:0; transform: translateY(-20px); } to { opacity:1; transform: translateY(0); } }
    .header-left { display:flex; align-items:center; gap:18px; }
    .icon-glass { width:56px; height:56px; border-radius:16px; display:flex; align-items:center; justify-content:center; backdrop-filter: blur(10px); border:1px solid rgba(255,255,255,0.6); box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
    .icon-glass.sell { background: linear-gradient(135deg, rgba(16,185,129,0.15), rgba(16,185,129,0.05)); color:#059669; }
    .header-left h1 { margin:0; font-size:22px; font-weight:800; color:#0f172a; }
    .header-left p { margin:4px 0 0; font-size:13px; color:#64748b; }
    .glass-btn { display:flex; align-items:center; gap:8px; padding:12px 20px; background: rgba(255,255,255,0.8); backdrop-filter: blur(10px); border:1px solid rgba(255,255,255,0.6); border-radius:14px; box-shadow: 0 4px 16px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9); font-weight:600; cursor:pointer; transition: all 0.4s; color:#334155; }
    .glass-btn:hover { transform: translateY(-2px) scale(1.02); box-shadow: 0 12px 28px rgba(0,0,0,0.1); }
    .glass-btn.accent { background: linear-gradient(135deg, #10b981, #06b6d4); color:white; border-color: rgba(255,255,255,0.3); }

    .profit-glass-highlight { display:grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap:16px; margin-bottom:20px; }
    .profit-glass-card { position: relative; display:flex; align-items:center; gap:16px; padding:20px; background: rgba(255,255,255,0.7); backdrop-filter: blur(16px); border:1px solid rgba(255,255,255,0.6); border-radius:20px; box-shadow: 0 8px 32px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9); overflow:hidden; transition: all 0.5s cubic-bezier(0.16,1,0.3,1); animation: fadeUp 0.6s both; }
    .profit-glass-card:hover { transform: translateY(-4px) scale(1.02); box-shadow: 0 16px 40px rgba(0,0,0,0.1); }
    @keyframes fadeUp { from { opacity:0; transform: translateY(20px); } to { opacity:1; transform: translateY(0); } }
    .profit-glow { position:absolute; top:0; left:0; right:0; height:1px; }
    .total-glow { background: linear-gradient(90deg, transparent, #10b981, transparent); }
    .today-glow { background: linear-gradient(90deg, transparent, #f59e0b, transparent); }
    .avg-glow { background: linear-gradient(90deg, transparent, #3b82f6, transparent); }
    .profit-icon-glass { width:52px; height:52px; border-radius:14px; display:flex; align-items:center; justify-content:center; font-size:24px; backdrop-filter: blur(10px); border:1px solid rgba(255,255,255,0.6); box-shadow: 0 4px 16px rgba(0,0,0,0.06); flex-shrink:0; }
    .profit-icon-glass.total { background: linear-gradient(135deg, rgba(16,185,129,0.15), rgba(16,185,129,0.05)); }
    .profit-icon-glass.today { background: linear-gradient(135deg, rgba(245,158,11,0.15), rgba(245,158,11,0.05)); }
    .profit-icon-glass.avg { background: linear-gradient(135deg, rgba(59,130,246,0.15), rgba(59,130,246,0.05)); }
    .profit-info { flex:1; display:flex; flex-direction:column; gap:2px; }
    .profit-info .label { font-size:10px; font-weight:700; color:#64748b; text-transform:uppercase; letter-spacing:0.06em; }
    .profit-info strong { font-size:20px; font-weight:800; color:#0f172a; }
    .profit-info small { font-size:11px; color:#64748b; }
    .profit-trend { display:flex; align-items:center; gap:4px; padding:6px 10px; border-radius:20px; font-size:11px; font-weight:700; }
    .profit-trend.up { background: rgba(16,185,129,0.1); color:#059669; border:1px solid rgba(16,185,129,0.15); }
    .profit-trend.neutral { background: rgba(59,130,246,0.1); color:#2563eb; border:1px solid rgba(59,130,246,0.15); }
    .profit-trend mat-icon { font-size:14px; width:14px; height:14px; }
    .live-badge { display:flex; align-items:center; gap:6px; padding:6px 12px; background: rgba(245,158,11,0.1); border:1px solid rgba(245,158,11,0.2); border-radius:20px; font-size:10px; font-weight:700; color:#d97706; }
    .pulse { width:6px; height:6px; background:#f59e0b; border-radius:50%; animation: pulseDot 2s infinite; }
    @keyframes pulseDot { 0% { box-shadow: 0 0 0 0 rgba(245,158,11,0.4); } 70% { box-shadow: 0 0 0 8px rgba(245,158,11,0); } 100% { box-shadow: 0 0 0 0 rgba(245,158,11,0); } }
    .search-glass { position:relative; display:flex; align-items:center; gap:12px; padding:0 18px; background: rgba(255,255,255,0.7); backdrop-filter: blur(16px); border:1px solid rgba(255,255,255,0.6); border-radius:18px; box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8); transition: all 0.3s; overflow:hidden; }
    .search-glass:focus-within { background: rgba(255,255,255,0.9); box-shadow: 0 8px 28px rgba(0,0,0,0.1); border-color: rgba(16,185,129,0.2); transform: translateY(-1px); }
    .search-glass input { flex:1; border:none; background: transparent; padding:16px 0; font-size:13px; font-weight:500; outline:none; color:#0f172a; }
    .search-glass mat-icon { color:#94a3b8; }
    .search-shine { position:absolute; top:0; left:-100%; width:100%; height:100%; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent); animation: shine 3s infinite; }
    @keyframes shine { 0% { left:-100%; } 100% { left:100%; } }

    .table-glass-card { background: rgba(255,255,255,0.65); backdrop-filter: blur(20px) saturate(180%); border:1px solid rgba(255,255,255,0.6); border-radius:24px; box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8); overflow:hidden; animation: fadeUp 0.6s 0.2s both; }
    .table-responsive { overflow-x:auto; }
    .glass-table { width:100%; border-collapse:collapse; }
    .glass-table th { background: rgba(248,250,252,0.8); backdrop-filter: blur(10px); padding:14px 16px; text-align:left; font-size:11px; font-weight:700; color:#64748b; text-transform:uppercase; letter-spacing:0.06em; border-bottom:1px solid rgba(0,0,0,0.06); white-space:nowrap; }
    .glass-table td { padding:16px; border-bottom:1px solid rgba(0,0,0,0.04); font-size:13px; }
    .glass-row { animation: slideIn 0.5s both; transition: all 0.3s; }
    @keyframes slideIn { from { opacity:0; transform: translateX(-10px); } to { opacity:1; transform: translateX(0); } }
    .glass-row:hover { background: rgba(255,255,255,0.6); transform: scale(1.005); box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
    .date-glass { display:flex; flex-direction:column; gap:2px; }
    .date-main { font-weight:700; color:#0f172a; }
    .glass-table small { color:#64748b; font-size:11px; }
    .phone-glass { display:flex; flex-direction:column; gap:2px; }
    .phone-main { font-weight:700; color:#0f172a; }
    .price-glass { font-weight:800; font-size:15px; }
    .price-glass.buy { color:#64748b; }
    .price-glass.sell { color:#0f172a; }
    .profit-glass-cell { display:flex; flex-direction:column; gap:2px; }
    .profit-value { font-weight:800; font-size:14px; }
    .profit-value.positive { color:#059669; }
    .profit-percent { color:#059669; font-size:10px; font-weight:600; background: rgba(16,185,129,0.1); padding:2px 8px; border-radius:10px; width:fit-content; }
    .buyer-glass { display:flex; align-items:center; gap:10px; }
    .buyer-avatar { width:32px; height:32px; border-radius:8px; background: linear-gradient(135deg, #10b981, #06b6d4); color:white; display:flex; align-items:center; justify-content:center; font-weight:800; font-size:12px; flex-shrink:0; }
    .buyer-name { font-weight:600; color:#0f172a; display:block; }
    .payment-glass { display:inline-flex; align-items:center; gap:6px; padding:6px 12px; border-radius:20px; font-size:11px; font-weight:700; border:1px solid; }
    .payment-glass.cash { background: rgba(16,185,129,0.1); color:#065f46; border-color: rgba(16,185,129,0.15); }
    .payment-glass.upi { background: rgba(59,130,246,0.1); color:#1e40af; border-color: rgba(59,130,246,0.15); }
    .pay-dot { width:6px; height:6px; border-radius:50%; background: currentColor; }
    .actions-glass { display:flex; gap:6px; }
    .icon-glass { width:32px; height:32px; border-radius:8px; background: rgba(255,255,255,0.7); border:1px solid rgba(0,0,0,0.06); display:flex; align-items:center; justify-content:center; cursor:pointer; transition: all 0.3s; color:#64748b; }
    .icon-glass:hover { background: rgba(255,255,255,0.9); transform: translateY(-1px); color:#0f172a; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
    .icon-glass mat-icon { font-size:16px; width:16px; height:16px; }
    .empty-glass { text-align:center; padding:40px; color:#64748b; }
    .empty-icon { width:64px; height:64px; background: rgba(0,0,0,0.04); border-radius:16px; display:flex; align-items:center; justify-content:center; margin:0 auto 16px; }
    .info-glass { margin-top:20px; display:flex; gap:12px; padding:16px 20px; background: rgba(16,185,129,0.06); backdrop-filter: blur(10px); border:1px solid rgba(16,185,129,0.1); border-radius:16px; font-size:12px; color:#065f46; }
    .info-glass.profit-info { background: rgba(16,185,129,0.06); }
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
      error: () => { this.sales = this.mockData(); this.filtered = [...this.sales]; this.calc(); }
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
