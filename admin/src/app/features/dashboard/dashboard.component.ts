import { Component, OnInit, signal } from '@angular/core';
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
    <div class="dashboard-glass-2026">
      <!-- Aurora Background -->
      <div class="aurora-bg">
        <div class="aurora-orb orb-1"></div>
        <div class="aurora-orb orb-2"></div>
        <div class="aurora-orb orb-3"></div>
      </div>

      <!-- Header with Glass -->
      <div class="glass-header" data-aos="fade-down">
        <div class="header-left">
          <div class="header-icon-glass">
            <span class="icon-float">📱</span>
          </div>
          <div class="header-text">
            <h1 class="title-shimmer">Mobile Shop Hisab</h1>
            <p class="subtitle-glass">Kitne phone hai, kisse liya, kisko becha - Sab yahi <span class="live-dot"></span> Live</p>
          </div>
        </div>
        <div class="header-actions">
          <button class="glass-btn primary" routerLink="/purchases">
            <span class="btn-glow"></span>
            <mat-icon>add_shopping_cart</mat-icon>
            <span>Kharida</span>
            <small>Buy</small>
          </button>
          <button class="glass-btn accent" routerLink="/sales">
            <span class="btn-glow"></span>
            <mat-icon>point_of_sale</mat-icon>
            <span>Becha</span>
            <small>Sell</small>
          </button>
        </div>
      </div>

      <!-- Loading Glass -->
      <div *ngIf="loading()" class="glass-loading">
        <div class="loader-glass">
          <mat-spinner diameter="60"></mat-spinner>
          <div class="loader-text">
            <span>Hisab load ho raha hai...</span>
            <div class="loader-dots"><span></span><span></span><span></span></div>
          </div>
        </div>
      </div>

      <!-- Stats Grid - Glassmorphism Cards -->
      <div *ngIf="!loading()" class="stats-glass-grid">
        <!-- Stock Card -->
        <div class="glass-card stock-card" data-aos="fade-up" data-aos-delay="100">
          <div class="card-glow stock-glow"></div>
          <div class="card-inner">
            <div class="card-top">
              <div class="icon-glass stock-icon">
                <mat-icon>inventory_2</mat-icon>
              </div>
              <div class="trend-glass up">
                <mat-icon>trending_up</mat-icon>
                <span>+12%</span>
              </div>
            </div>
            <div class="card-content">
              <span class="label">Total Stock</span>
              <span class="label-hi">मेरे पास कितने फोन</span>
              <div class="value-row">
                <span class="value">{{summary?.totalStock || 45}}</span>
                <span class="unit">phones</span>
              </div>
              <div class="sub-glass">
                <span>₹{{formatMoney(summary?.stockValue || 675000)}} value</span>
                <div class="mini-bar"><div class="fill stock-fill" style="width: 75%"></div></div>
              </div>
            </div>
            <div class="card-float-icon">📦</div>
          </div>
        </div>

        <!-- Profit Card -->
        <div class="glass-card profit-card" data-aos="fade-up" data-aos-delay="200">
          <div class="card-glow profit-glow"></div>
          <div class="card-inner">
            <div class="card-top">
              <div class="icon-glass profit-icon">
                <mat-icon>account_balance_wallet</mat-icon>
              </div>
              <div class="trend-glass up">
                <mat-icon>arrow_upward</mat-icon>
                <span>₹{{formatMoney(summary?.expectedProfit || 135000)}}</span>
              </div>
            </div>
            <div class="card-content">
              <span class="label">Expected Profit</span>
              <span class="label-hi">बेचने पर मुनाफा</span>
              <div class="value-row">
                <span class="value">₹{{formatMoney(summary?.expectedProfit || 135000)}}</span>
              </div>
              <div class="sub-glass">
                <span>If all sold • Avg 20% margin</span>
                <div class="profit-pills">
                  <span class="pill up">+18% margin</span>
                </div>
              </div>
            </div>
            <div class="card-float-icon">💰</div>
          </div>
        </div>

        <!-- Today Card -->
        <div class="glass-card today-card" data-aos="fade-up" data-aos-delay="300">
          <div class="card-glow today-glow"></div>
          <div class="card-inner">
            <div class="card-top">
              <div class="icon-glass today-icon">
                <mat-icon>today</mat-icon>
              </div>
              <div class="live-badge">
                <span class="pulse"></span>
                <span>Today</span>
              </div>
            </div>
            <div class="card-content">
              <span class="label">Aaj Ka Hisab</span>
              <span class="label-hi">आज का मुनाफा</span>
              <div class="value-row">
                <span class="value">₹{{formatMoney(summary?.todayProfit || 5000)}}</span>
              </div>
              <div class="sub-glass today-stats">
                <div class="today-item buy">
                  <span class="dot buy"></span>
                  <span>{{summary?.todayBuy || 3}} Kharida</span>
                </div>
                <div class="today-item sell">
                  <span class="dot sell"></span>
                  <span>{{summary?.todaySell || 2}} Becha</span>
                </div>
              </div>
            </div>
            <div class="card-float-icon">📈</div>
          </div>
        </div>

        <!-- Month Card -->
        <div class="glass-card month-card" data-aos="fade-up" data-aos-delay="400">
          <div class="card-glow month-glow"></div>
          <div class="card-inner">
            <div class="card-top">
              <div class="icon-glass month-icon">
                <mat-icon>calendar_month</mat-icon>
              </div>
              <div class="trend-glass neutral">
                <mat-icon>bar_chart</mat-icon>
                <span>Sept 2026</span>
              </div>
            </div>
            <div class="card-content">
              <span class="label">Month Profit</span>
              <span class="label-hi">महीने का मुनाफा</span>
              <div class="value-row">
                <span class="value">₹{{formatMoney(summary?.monthProfit || 45000)}}</span>
              </div>
              <div class="sub-glass">
                <div class="month-bar">
                  <div class="month-segment buy" [style.width.%]="55"><span>Buy 30</span></div>
                  <div class="month-segment sell" [style.width.%]="45"><span>Sell 25</span></div>
                </div>
              </div>
            </div>
            <div class="card-float-icon">🗓️</div>
          </div>
        </div>
      </div>

      <!-- Second Row - Glass Panels -->
      <div *ngIf="!loading()" class="second-glass-row">
        <!-- Brand Stock Glass -->
        <div class="glass-panel brand-panel" data-aos="fade-up" data-aos-delay="500">
          <div class="panel-header">
            <div class="panel-title">
              <div class="title-icon-glass">
                <mat-icon>category</mat-icon>
              </div>
              <div>
                <h3>Brand-wise Stock</h3>
                <span>Kaunsa brand kitna pada hai</span>
              </div>
            </div>
            <div class="panel-action">
              <span class="count-badge">{{brandStock.length}} brands</span>
            </div>
          </div>
          <div class="brand-glass-list">
            <div class="brand-glass-item" *ngFor="let b of brandStock; let i = index" [style.animation-delay]="i*100+'ms'">
              <div class="brand-left">
                <div class="brand-logo-glass" [attr.data-brand]="b.brand.charAt(0)">
                  {{b.brand.charAt(0)}}
                </div>
                <div class="brand-info">
                  <span class="brand-name">{{b.brand}}</span>
                  <span class="brand-meta">{{b.count}} pcs • ₹{{formatMoney(b.value)}}</span>
                </div>
              </div>
              <div class="brand-right">
                <div class="brand-progress-glass">
                  <div class="progress-track">
                    <div class="progress-fill" [style.width.%]="b.percent">
                      <div class="progress-shimmer"></div>
                    </div>
                  </div>
                  <span class="progress-text">{{b.percent}}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Hisab Glass -->
        <div class="glass-panel recent-panel" data-aos="fade-up" data-aos-delay="600">
          <div class="panel-header">
            <div class="panel-title">
              <div class="title-icon-glass recent-icon">
                <mat-icon>history</mat-icon>
              </div>
              <div>
                <h3>Recent Hisab</h3>
                <span>Aaj kal ka kharida becha</span>
              </div>
            </div>
            <button class="glass-mini-btn" routerLink="/purchases">View All</button>
          </div>
          <div class="recent-glass-list">
            <div class="recent-glass-item buy" data-aos="slide-left" data-aos-delay="700">
              <div class="recent-icon-glass buy">
                <mat-icon>shopping_cart</mat-icon>
              </div>
              <div class="recent-content">
                <span class="recent-title">Buy: Samsung S23</span>
                <span class="recent-sub">from Ramesh • ₹15,000 • 2h ago</span>
              </div>
              <div class="recent-amount buy">
                <span>-₹15k</span>
                <small>Buy</small>
              </div>
            </div>
            <div class="recent-glass-item sell" data-aos="slide-left" data-aos-delay="800">
              <div class="recent-icon-glass sell">
                <mat-icon>point_of_sale</mat-icon>
              </div>
              <div class="recent-content">
                <span class="recent-title">Sell: iPhone 12</span>
                <span class="recent-sub">to Suresh • Profit ₹4k • 5h ago</span>
              </div>
              <div class="recent-amount sell">
                <span>+₹25k</span>
                <small>₹4k profit</small>
              </div>
            </div>
            <div class="recent-glass-item buy" data-aos="slide-left" data-aos-delay="900">
              <div class="recent-icon-glass buy">
                <mat-icon>smartphone</mat-icon>
              </div>
              <div class="recent-content">
                <span class="recent-title">Buy: Redmi Note 13</span>
                <span class="recent-sub">from Mahesh • ₹10,000 • Yesterday</span>
              </div>
              <div class="recent-amount buy">
                <span>-₹10k</span>
                <small>Buy</small>
              </div>
            </div>
            <div class="recent-glass-item sell" data-aos="slide-left" data-aos-delay="1000">
              <div class="recent-icon-glass sell">
                <mat-icon>verified</mat-icon>
              </div>
              <div class="recent-content">
                <span class="recent-title">Sell: OnePlus 11R</span>
                <span class="recent-sub">to Ankit • Profit ₹3k • Yesterday</span>
              </div>
              <div class="recent-amount sell">
                <span>+₹22k</span>
                <small>₹3k profit</small>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions - Glass Morphic -->
      <div *ngIf="!loading()" class="quick-glass-section" data-aos="fade-up" data-aos-delay="1100">
        <div class="section-title-glass">
          <h3>Quick Actions - Jaldi Kaam</h3>
          <p>Ek click me hisab entry</p>
        </div>
        <div class="quick-glass-grid">
          <button class="quick-glass-card" routerLink="/inventory">
            <div class="quick-glow"></div>
            <div class="quick-icon stock-quick">
              <mat-icon>inventory_2</mat-icon>
            </div>
            <span class="quick-label">Stock Dekho</span>
            <small>मेरा स्टॉक • {{summary?.totalStock || 45}} phones</small>
            <mat-icon class="quick-arrow">arrow_forward</mat-icon>
          </button>
          <button class="quick-glass-card primary-quick" routerLink="/purchases">
            <div class="quick-glow"></div>
            <div class="quick-icon buy-quick">
              <mat-icon>add_circle</mat-icon>
            </div>
            <span class="quick-label">Kharida Entry</span>
            <small>नया फोन खरीदा • Buy</small>
            <mat-icon class="quick-arrow">arrow_forward</mat-icon>
          </button>
          <button class="quick-glass-card accent-quick" routerLink="/sales">
            <div class="quick-glow"></div>
            <div class="quick-icon sell-quick">
              <mat-icon>point_of_sale</mat-icon>
            </div>
            <span class="quick-label">Becha Entry</span>
            <small>फोन बेचा • Sell</small>
            <mat-icon class="quick-arrow">arrow_forward</mat-icon>
          </button>
          <button class="quick-glass-card" routerLink="/customers">
            <div class="quick-glow"></div>
            <div class="quick-icon customer-quick">
              <mat-icon>people</mat-icon>
            </div>
            <span class="quick-label">Customer Add</span>
            <small>जिससे लिया/दिया</small>
            <mat-icon class="quick-arrow">arrow_forward</mat-icon>
          </button>
        </div>
      </div>

      <!-- Footer Glass -->
      <div class="footer-glass">
        <span>© 2026 MobileBiz • Glassmorphism Edition • Sept 2026 • Made with ❤️ for Shop Owners</span>
        <div class="footer-dots">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* ===== 2026 GLASSMORPHISM DESIGN SYSTEM ===== */
    .dashboard-glass-2026 {
      position: relative;
      min-height: 100vh;
      padding: 20px;
      overflow-x: hidden;
      font-family: 'Inter', system-ui, -apple-system, sans-serif;
    }

    /* Aurora Background */
    .aurora-bg {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: -1;
      overflow: hidden;
      background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 25%, #f1f5f9 50%, #e0f2fe 75%, #f0fdfa 100%);
    }
    .aurora-orb {
      position: absolute;
      border-radius: 50%;
      filter: blur(80px);
      opacity: 0.15;
      animation: floatAurora 20s infinite ease-in-out;
    }
    .orb-1 {
      width: 600px;
      height: 600px;
      background: radial-gradient(circle, #3b82f6 0%, #8b5cf6 50%, transparent 70%);
      top: -200px;
      left: -100px;
      animation-delay: 0s;
    }
    .orb-2 {
      width: 800px;
      height: 800px;
      background: radial-gradient(circle, #06b6d4 0%, #3b82f6 50%, transparent 70%);
      top: 20%;
      right: -200px;
      animation-delay: -7s;
    }
    .orb-3 {
      width: 500px;
      height: 500px;
      background: radial-gradient(circle, #8b5cf6 0%, #ec4899 50%, transparent 70%);
      bottom: -100px;
      left: 30%;
      animation-delay: -14s;
    }
    @keyframes floatAurora {
      0%, 100% { transform: translate(0, 0) scale(1) rotate(0deg); }
      33% { transform: translate(30px, -30px) scale(1.1) rotate(1deg); }
      66% { transform: translate(-20px, 20px) scale(0.9) rotate(-1deg); }
    }

    /* Glass Header */
    .glass-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px 28px;
      background: rgba(255,255,255,0.7);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border: 1px solid rgba(255,255,255,0.5);
      border-radius: 24px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8);
      margin-bottom: 28px;
      flex-wrap: wrap;
      gap: 20px;
      animation: slideDown 0.8s cubic-bezier(0.16, 1, 0.3, 1);
    }
    @keyframes slideDown {
      from { opacity: 0; transform: translateY(-30px) scale(0.95); }
      to { opacity: 1; transform: translateY(0) scale(1); }
    }
    .header-left {
      display: flex;
      align-items: center;
      gap: 20px;
    }
    .header-icon-glass {
      width: 64px;
      height: 64px;
      background: linear-gradient(135deg, rgba(59,130,246,0.1), rgba(139,92,246,0.1));
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
      box-shadow: 0 4px 20px rgba(59,130,246,0.15), inset 0 1px 0 rgba(255,255,255,0.8);
      position: relative;
      overflow: hidden;
    }
    .header-icon-glass::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
      animation: shimmer 3s infinite;
    }
    @keyframes shimmer {
      0% { left: -100%; }
      100% { left: 100%; }
    }
    .icon-float {
      animation: floatIcon 3s infinite ease-in-out;
      display: block;
    }
    @keyframes floatIcon {
      0%, 100% { transform: translateY(0) rotate(0deg); }
      50% { transform: translateY(-4px) rotate(2deg); }
    }
    .header-text h1 {
      margin: 0;
      font-size: 28px;
      font-weight: 800;
      letter-spacing: -0.02em;
      background: linear-gradient(135deg, #0f172a 0%, #334155 50%, #0f172a 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      background-size: 200% auto;
      animation: titleShimmer 4s linear infinite;
    }
    @keyframes titleShimmer {
      0% { background-position: 0% center; }
      100% { background-position: 200% center; }
    }
    .title-shimmer {
      position: relative;
    }
    .subtitle-glass {
      margin: 6px 0 0;
      color: #64748b;
      font-size: 14px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .live-dot {
      width: 8px;
      height: 8px;
      background: #10b981;
      border-radius: 50%;
      display: inline-block;
      box-shadow: 0 0 0 3px rgba(16,185,129,0.2);
      animation: pulseLive 2s infinite;
    }
    @keyframes pulseLive {
      0%, 100% { box-shadow: 0 0 0 3px rgba(16,185,129,0.2); }
      50% { box-shadow: 0 0 0 6px rgba(16,185,129,0); }
    }
    .header-actions {
      display: flex;
      gap: 14px;
    }
    .glass-btn {
      position: relative;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px 22px;
      background: rgba(255,255,255,0.8);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9);
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      overflow: hidden;
      font-weight: 600;
      color: #334155;
    }
    .glass-btn:hover {
      transform: translateY(-2px) scale(1.02);
      box-shadow: 0 12px 32px rgba(0,0,0,0.12), inset 0 1px 0 rgba(255,255,255,0.9);
      border-color: rgba(255,255,255,0.8);
    }
    .glass-btn.primary {
      background: linear-gradient(135deg, rgba(59,130,246,0.9), rgba(99,102,241,0.9));
      color: white;
      border-color: rgba(255,255,255,0.3);
    }
    .glass-btn.accent {
      background: linear-gradient(135deg, rgba(16,185,129,0.9), rgba(6,182,212,0.9));
      color: white;
      border-color: rgba(255,255,255,0.3);
    }
    .btn-glow {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
    }
    .glass-btn small {
      font-size: 10px;
      opacity: 0.8;
      font-weight: 400;
      margin-left: -4px;
    }

    /* Loading Glass */
    .glass-loading {
      display: flex;
      justify-content: center;
      padding: 80px 20px;
    }
    .loader-glass {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 20px;
      padding: 40px;
      background: rgba(255,255,255,0.6);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255,255,255,0.5);
      border-radius: 24px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.08);
    }
    .loader-text {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      color: #64748b;
      font-weight: 500;
    }
    .loader-dots {
      display: flex;
      gap: 6px;
    }
    .loader-dots span {
      width: 6px;
      height: 6px;
      background: #3b82f6;
      border-radius: 50%;
      animation: bounceDot 1.4s infinite ease-in-out;
    }
    .loader-dots span:nth-child(2) { animation-delay: 0.2s; }
    .loader-dots span:nth-child(3) { animation-delay: 0.4s; }
    @keyframes bounceDot {
      0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
      40% { transform: scale(1.2); opacity: 1; }
    }

    /* Stats Grid */
    .stats-glass-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 22px;
      margin-bottom: 28px;
    }
    .glass-card {
      position: relative;
      border-radius: 24px;
      overflow: hidden;
      background: rgba(255,255,255,0.65);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border: 1px solid rgba(255,255,255,0.6);
      box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8), inset 0 -1px 0 rgba(0,0,0,0.05);
      transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
      animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
    }
    @keyframes fadeUp {
      from { opacity: 0; transform: translateY(30px) scale(0.95); }
      to { opacity: 1; transform: translateY(0) scale(1); }
    }
    .glass-card:hover {
      transform: translateY(-6px) scale(1.02);
      box-shadow: 0 20px 60px rgba(0,0,0,0.12), inset 0 1px 0 rgba(255,255,255,0.9), inset 0 -1px 0 rgba(0,0,0,0.05);
      border-color: rgba(255,255,255,0.8);
    }
    .card-glow {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      opacity: 0.6;
    }
    .stock-glow { background: linear-gradient(90deg, transparent, #3b82f6, transparent); }
    .profit-glow { background: linear-gradient(90deg, transparent, #10b981, transparent); }
    .today-glow { background: linear-gradient(90deg, transparent, #f59e0b, transparent); }
    .month-glow { background: linear-gradient(90deg, transparent, #8b5cf6, transparent); }
    .card-inner {
      position: relative;
      padding: 26px;
    }
    .card-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 18px;
    }
    .icon-glass {
      width: 52px;
      height: 52px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      box-shadow: 0 4px 16px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8);
      position: relative;
      overflow: hidden;
    }
    .icon-glass::after {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 16px;
      padding: 1px;
      background: linear-gradient(135deg, rgba(255,255,255,0.8), transparent);
      mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      mask-composite: xor;
      -webkit-mask-composite: xor;
    }
    .stock-icon { background: linear-gradient(135deg, rgba(59,130,246,0.15), rgba(59,130,246,0.05)); color: #2563eb; }
    .profit-icon { background: linear-gradient(135deg, rgba(16,185,129,0.15), rgba(16,185,129,0.05)); color: #059669; }
    .today-icon { background: linear-gradient(135deg, rgba(245,158,11,0.15), rgba(245,158,11,0.05)); color: #d97706; }
    .month-icon { background: linear-gradient(135deg, rgba(139,92,246,0.15), rgba(139,92,246,0.05)); color: #7c3aed; }
    .trend-glass {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: rgba(255,255,255,0.8);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      box-shadow: 0 2px 12px rgba(0,0,0,0.04);
    }
    .trend-glass.up { color: #059669; background: rgba(16,185,129,0.1); border-color: rgba(16,185,129,0.2); }
    .trend-glass.neutral { color: #7c3aed; background: rgba(139,92,246,0.1); border-color: rgba(139,92,246,0.2); }
    .trend-glass mat-icon { font-size: 16px; width: 16px; height: 16px; }
    .card-content {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .label {
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: #64748b;
    }
    .label-hi {
      font-size: 10px;
      color: #94a3b8;
      font-weight: 400;
      margin-top: -2px;
    }
    .value-row {
      display: flex;
      align-items: baseline;
      gap: 8px;
      margin: 8px 0 6px;
    }
    .value {
      font-size: 32px;
      font-weight: 800;
      letter-spacing: -0.03em;
      line-height: 1;
      background: linear-gradient(135deg, #0f172a 0%, #334155 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .profit-card .value { background: linear-gradient(135deg, #059669, #10b981); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
    .today-card .value { background: linear-gradient(135deg, #d97706, #f59e0b); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
    .month-card .value { background: linear-gradient(135deg, #7c3aed, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
    .unit {
      font-size: 14px;
      font-weight: 600;
      color: #64748b;
    }
    .sub-glass {
      display: flex;
      flex-direction: column;
      gap: 10px;
      margin-top: 8px;
      font-size: 12px;
      color: #64748b;
      font-weight: 500;
    }
    .mini-bar {
      height: 4px;
      background: rgba(0,0,0,0.06);
      border-radius: 2px;
      overflow: hidden;
    }
    .mini-bar .fill {
      height: 100%;
      border-radius: 2px;
      position: relative;
      overflow: hidden;
    }
    .stock-fill { background: linear-gradient(90deg, #3b82f6, #6366f1); }
    .profit-pills {
      display: flex;
      gap: 6px;
    }
    .pill {
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 10px;
      font-weight: 700;
      backdrop-filter: blur(10px);
      border: 1px solid;
    }
    .pill.up { background: rgba(16,185,129,0.1); color: #059669; border-color: rgba(16,185,129,0.2); }
    .today-stats {
      flex-direction: row;
      gap: 16px;
    }
    .today-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 11px;
      font-weight: 600;
    }
    .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
    }
    .dot.buy { background: #ef4444; box-shadow: 0 0 0 3px rgba(239,68,68,0.15); }
    .dot.sell { background: #10b981; box-shadow: 0 0 0 3px rgba(16,185,129,0.15); }
    .month-bar {
      display: flex;
      height: 28px;
      background: rgba(0,0,0,0.04);
      border-radius: 14px;
      overflow: hidden;
      padding: 3px;
      gap: 3px;
    }
    .month-segment {
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 11px;
      font-size: 10px;
      font-weight: 700;
      color: white;
      position: relative;
      overflow: hidden;
    }
    .month-segment.buy { background: linear-gradient(135deg, #ef4444, #f87171); }
    .month-segment.sell { background: linear-gradient(135deg, #10b981, #34d399); }
    .card-float-icon {
      position: absolute;
      bottom: 16px;
      right: 16px;
      font-size: 64px;
      opacity: 0.04;
      transform: rotate(-12deg);
      pointer-events: none;
      animation: floatSlow 6s infinite ease-in-out;
    }
    @keyframes floatSlow {
      0%, 100% { transform: rotate(-12deg) translateY(0); }
      50% { transform: rotate(-8deg) translateY(-8px); }
    }
    .live-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: rgba(16,185,129,0.1);
      border: 1px solid rgba(16,185,129,0.2);
      border-radius: 20px;
      font-size: 11px;
      font-weight: 700;
      color: #059669;
    }
    .pulse {
      width: 6px;
      height: 6px;
      background: #10b981;
      border-radius: 50%;
      animation: pulseDot 2s infinite;
    }
    @keyframes pulseDot {
      0% { box-shadow: 0 0 0 0 rgba(16,185,129,0.4); }
      70% { box-shadow: 0 0 0 8px rgba(16,185,129,0); }
      100% { box-shadow: 0 0 0 0 rgba(16,185,129,0); }
    }

    /* Second Row */
    .second-glass-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 22px;
      margin-bottom: 28px;
    }
    @media (max-width: 1100px) {
      .second-glass-row { grid-template-columns: 1fr; }
    }
    .glass-panel {
      background: rgba(255,255,255,0.65);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 24px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.8);
      overflow: hidden;
      animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
    }
    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 22px 26px;
      border-bottom: 1px solid rgba(0,0,0,0.04);
    }
    .panel-title {
      display: flex;
      align-items: center;
      gap: 14px;
    }
    .title-icon-glass {
      width: 44px;
      height: 44px;
      background: linear-gradient(135deg, rgba(59,130,246,0.1), rgba(139,92,246,0.1));
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #3b82f6;
      box-shadow: 0 2px 12px rgba(59,130,246,0.1), inset 0 1px 0 rgba(255,255,255,0.8);
    }
    .title-icon-glass.recent-icon { background: linear-gradient(135deg, rgba(16,185,129,0.1), rgba(6,182,212,0.1)); color: #059669; }
    .panel-title h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 700;
      color: #0f172a;
      letter-spacing: -0.01em;
    }
    .panel-title span {
      font-size: 12px;
      color: #64748b;
      font-weight: 500;
    }
    .count-badge {
      padding: 6px 12px;
      background: rgba(0,0,0,0.04);
      border: 1px solid rgba(0,0,0,0.06);
      border-radius: 20px;
      font-size: 11px;
      font-weight: 600;
      color: #475569;
    }
    .glass-mini-btn {
      padding: 8px 16px;
      background: rgba(59,130,246,0.1);
      border: 1px solid rgba(59,130,246,0.2);
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      color: #2563eb;
      cursor: pointer;
      transition: all 0.3s;
    }
    .glass-mini-btn:hover {
      background: rgba(59,130,246,0.15);
      transform: translateY(-1px);
    }
    .brand-glass-list {
      padding: 10px 26px 26px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .brand-glass-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      background: rgba(255,255,255,0.6);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 16px;
      box-shadow: 0 2px 16px rgba(0,0,0,0.04), inset 0 1px 0 rgba(255,255,255,0.8);
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      animation: slideIn 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;
    }
    @keyframes slideIn {
      from { opacity: 0; transform: translateX(-20px); }
      to { opacity: 1; transform: translateX(0); }
    }
    .brand-glass-item:hover {
      transform: translateX(4px) scale(1.01);
      background: rgba(255,255,255,0.8);
      box-shadow: 0 8px 24px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.9);
    }
    .brand-left {
      display: flex;
      align-items: center;
      gap: 14px;
    }
    .brand-logo-glass {
      width: 44px;
      height: 44px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 16px;
      color: white;
      background: linear-gradient(135deg, #3b82f6, #8b5cf6);
      box-shadow: 0 4px 16px rgba(59,130,246,0.2);
      position: relative;
      overflow: hidden;
    }
    .brand-logo-glass::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
    }
    .brand-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .brand-name {
      font-weight: 700;
      font-size: 14px;
      color: #0f172a;
    }
    .brand-meta {
      font-size: 11px;
      color: #64748b;
      font-weight: 500;
    }
    .brand-progress-glass {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .progress-track {
      width: 80px;
      height: 6px;
      background: rgba(0,0,0,0.06);
      border-radius: 3px;
      overflow: hidden;
      position: relative;
    }
    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #3b82f6, #8b5cf6);
      border-radius: 3px;
      position: relative;
      overflow: hidden;
      transition: width 1.5s cubic-bezier(0.16, 1, 0.3, 1);
    }
    .progress-shimmer {
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
      animation: shimmer 2s infinite;
    }
    .progress-text {
      font-size: 11px;
      font-weight: 700;
      color: #475569;
      min-width: 32px;
    }
    .recent-glass-list {
      padding: 10px 26px 26px;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .recent-glass-item {
      display: flex;
      align-items: center;
      gap: 14px;
      padding: 16px;
      background: rgba(255,255,255,0.6);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 16px;
      box-shadow: 0 2px 16px rgba(0,0,0,0.04), inset 0 1px 0 rgba(255,255,255,0.8);
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      position: relative;
      overflow: hidden;
    }
    .recent-glass-item::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 3px;
      transition: width 0.3s;
    }
    .recent-glass-item.buy::before { background: linear-gradient(180deg, #ef4444, #f87171); }
    .recent-glass-item.sell::before { background: linear-gradient(180deg, #10b981, #34d399); }
    .recent-glass-item:hover {
      transform: translateX(4px);
      background: rgba(255,255,255,0.85);
      box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    }
    .recent-glass-item:hover::before { width: 5px; }
    .recent-icon-glass {
      width: 42px;
      height: 42px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      box-shadow: 0 2px 12px rgba(0,0,0,0.04);
    }
    .recent-icon-glass.buy { background: rgba(239,68,68,0.1); color: #dc2626; border-color: rgba(239,68,68,0.15); }
    .recent-icon-glass.sell { background: rgba(16,185,129,0.1); color: #059669; border-color: rgba(16,185,129,0.15); }
    .recent-icon-glass mat-icon { font-size: 20px; width: 20px; height: 20px; }
    .recent-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 3px;
    }
    .recent-title {
      font-weight: 600;
      font-size: 13px;
      color: #0f172a;
    }
    .recent-sub {
      font-size: 11px;
      color: #64748b;
      font-weight: 500;
    }
    .recent-amount {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 2px;
    }
    .recent-amount span {
      font-weight: 800;
      font-size: 14px;
    }
    .recent-amount.buy span { color: #dc2626; }
    .recent-amount.sell span { color: #059669; }
    .recent-amount small {
      font-size: 10px;
      padding: 2px 8px;
      border-radius: 10px;
      font-weight: 600;
    }
    .recent-amount.buy small { background: rgba(239,68,68,0.1); color: #dc2626; }
    .recent-amount.sell small { background: rgba(16,185,129,0.1); color: #059669; }

    /* Quick Actions Glass */
    .quick-glass-section {
      background: rgba(255,255,255,0.5);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 24px;
      padding: 26px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8);
      animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
    }
    .section-title-glass h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 800;
      color: #0f172a;
      letter-spacing: -0.01em;
    }
    .section-title-glass p {
      margin: 4px 0 20px;
      font-size: 13px;
      color: #64748b;
      font-weight: 500;
    }
    .quick-glass-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 16px;
    }
    .quick-glass-card {
      position: relative;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
      padding: 22px;
      background: rgba(255,255,255,0.7);
      backdrop-filter: blur(16px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 20px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.9);
      cursor: pointer;
      transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
      text-align: left;
      overflow: hidden;
      color: #334155;
    }
    .quick-glass-card:hover {
      transform: translateY(-4px) scale(1.02);
      background: rgba(255,255,255,0.9);
      box-shadow: 0 16px 40px rgba(0,0,0,0.1), inset 0 1px 0 rgba(255,255,255,1);
      border-color: rgba(255,255,255,0.9);
    }
    .quick-glow {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
      opacity: 0;
      transition: opacity 0.3s;
    }
    .quick-glass-card:hover .quick-glow { opacity: 1; }
    .quick-icon {
      width: 48px;
      height: 48px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      box-shadow: 0 4px 16px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.8);
    }
    .stock-quick { background: linear-gradient(135deg, rgba(59,130,246,0.15), rgba(59,130,246,0.05)); color: #2563eb; }
    .buy-quick { background: linear-gradient(135deg, rgba(239,68,68,0.15), rgba(239,68,68,0.05)); color: #dc2626; }
    .sell-quick { background: linear-gradient(135deg, rgba(16,185,129,0.15), rgba(16,185,129,0.05)); color: #059669; }
    .customer-quick { background: linear-gradient(135deg, rgba(139,92,246,0.15), rgba(139,92,246,0.05)); color: #7c3aed; }
    .quick-label {
      font-weight: 700;
      font-size: 14px;
      color: #0f172a;
    }
    .quick-glass-card small {
      font-size: 11px;
      color: #64748b;
      font-weight: 500;
      line-height: 1.3;
    }
    .quick-arrow {
      position: absolute;
      top: 20px;
      right: 20px;
      font-size: 18px;
      width: 18px;
      height: 18px;
      opacity: 0;
      transform: translateX(-10px);
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      color: #94a3b8;
    }
    .quick-glass-card:hover .quick-arrow {
      opacity: 1;
      transform: translateX(0);
    }
    .primary-quick {
      background: linear-gradient(135deg, rgba(59,130,246,0.08), rgba(99,102,241,0.08));
      border-color: rgba(59,130,246,0.15);
    }
    .accent-quick {
      background: linear-gradient(135deg, rgba(16,185,129,0.08), rgba(6,182,212,0.08));
      border-color: rgba(16,185,129,0.15);
    }

    /* Footer */
    .footer-glass {
      margin-top: 32px;
      padding: 20px 26px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: rgba(255,255,255,0.4);
      backdrop-filter: blur(16px);
      border: 1px solid rgba(255,255,255,0.5);
      border-radius: 16px;
      font-size: 11px;
      color: #94a3b8;
      font-weight: 500;
    }
    .footer-dots {
      display: flex;
      gap: 6px;
    }
    .footer-dots span {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: rgba(0,0,0,0.1);
    }
    .footer-dots span:nth-child(1) { background: #ef4444; }
    .footer-dots span:nth-child(2) { background: #f59e0b; }
    .footer-dots span:nth-child(3) { background: #10b981; }

    /* Responsive */
    @media (max-width: 768px) {
      .dashboard-glass-2026 { padding: 12px; }
      .glass-header { padding: 18px 20px; flex-direction: column; align-items: flex-start; }
      .header-actions { width: 100%; }
      .glass-btn { flex: 1; justify-content: center; }
      .stats-glass-grid { grid-template-columns: 1fr; }
      .quick-glass-grid { grid-template-columns: 1fr; }
    }

    /* Reduced motion */
    @media (prefers-reduced-motion: reduce) {
      *, *::before, *::after { animation-duration: 0.01ms !important; animation-iteration-count: 1 !important; transition-duration: 0.01ms !important; }
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
