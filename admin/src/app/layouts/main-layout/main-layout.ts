import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';

interface MenuItem {
  label: string;
  labelHi: string;
  icon: string;
  route: string;
  badge?: string;
  permissions?: string[];
  isNew?: boolean;
}

interface MenuSection {
  title: string;
  titleHi: string;
  icon: string;
  items: MenuItem[];
  expanded: boolean;
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet, RouterLink, RouterLinkActive,
    MatSidenavModule, MatToolbarModule, MatListModule,
    MatIconModule, MatButtonModule, MatDividerModule,
    MatBadgeModule, MatTooltipModule
  ],
  template: `
    <mat-sidenav-container class="sidenav-glass-container">
      <!-- GLASS SIDENAV -->
      <mat-sidenav #sidenav mode="side" opened class="sidenav-glass" [class.collapsed]="isCollapsed()">
        <!-- Aurora for sidenav -->
        <div class="sidenav-aurora">
          <div class="aurora-dot dot-1"></div>
          <div class="aurora-dot dot-2"></div>
        </div>

        <!-- LOGO GLASS -->
        <div class="logo-glass">
          <div class="logo-icon-glass">
            <span class="logo-float">📱</span>
            <div class="logo-shine"></div>
          </div>
          <div class="logo-text" *ngIf="!isCollapsed()">
            <h2 class="logo-title">MobileBiz</h2>
            <span class="logo-sub">2nd Hand Hub • 2026 Glass</span>
            <div class="logo-badge">
              <span class="live-pulse"></span>
              <span>Live Hisab</span>
            </div>
          </div>
        </div>

        <!-- USER GLASS -->
        <div class="user-glass" *ngIf="!isCollapsed()">
          <div class="user-avatar-glass">
            <span>👤</span>
            <div class="avatar-ring"></div>
          </div>
          <div class="user-details">
            <span class="user-name">Shop Owner</span>
            <span class="user-role">Admin • Main Branch</span>
            <div class="user-stats">
              <span><mat-icon>smartphone</mat-icon> 45 phones</span>
            </div>
          </div>
          <div class="user-status">
            <span class="status-dot"></span>
          </div>
        </div>

        <!-- MENU GLASS -->
        <div class="menu-glass-container">
          <div *ngFor="let section of menuSections(); let sIndex = index" class="menu-section-glass" [style.animation-delay]="sIndex*80+'ms'">
            <!-- Section Header Glass -->
            <div class="section-header-glass" (click)="toggleSection(section)" *ngIf="!isCollapsed()">
              <div class="section-icon-glass">
                <mat-icon>{{section.icon}}</mat-icon>
              </div>
              <div class="section-text">
                <span class="section-title">{{section.title}}</span>
                <span class="section-hi">{{section.titleHi}}</span>
              </div>
              <mat-icon class="expand-icon-glass" [class.expanded]="section.expanded">
                {{section.expanded ? 'expand_less' : 'expand_more'}}
              </mat-icon>
            </div>

            <!-- Section Items Glass -->
            <mat-nav-list *ngIf="section.expanded || isCollapsed()" class="section-items-glass">
              <a mat-list-item
                 *ngFor="let item of section.items; let i = index"
                 [routerLink]="item.route"
                 routerLinkActive="active-glass"
                 [matTooltip]="isCollapsed() ? item.label + ' - ' + item.labelHi : ''"
                 matTooltipPosition="right"
                 class="menu-item-glass"
                 [style.animation-delay]="i*40+'ms'">
                <div class="item-icon-glass" matListItemIcon>
                  <mat-icon [matBadge]="item.badge" [matBadgeHidden]="!item.badge" matBadgeColor="warn" matBadgeSize="small">
                    {{item.icon}}
                  </mat-icon>
                  <div class="icon-glow"></div>
                </div>
                <div matListItemTitle *ngIf="!isCollapsed()" class="menu-item-content-glass">
                  <span class="item-label">{{item.label}}</span>
                  <span class="item-label-hi">{{item.labelHi}}</span>
                  <span *ngIf="item.isNew" class="new-badge-glass">NEW</span>
                </div>
                <div *ngIf="!isCollapsed()" class="item-arrow">
                  <mat-icon>chevron_right</mat-icon>
                </div>
              </a>
            </mat-nav-list>
          </div>
        </div>

        <!-- Collapse Glass -->
        <div class="collapse-glass">
          <button class="collapse-btn-glass" (click)="toggleCollapse()" matTooltip="Toggle Sidebar">
            <mat-icon>{{isCollapsed() ? 'chevron_right' : 'chevron_left'}}</mat-icon>
            <span *ngIf="!isCollapsed()">Collapse</span>
          </button>
          <div class="version-glass" *ngIf="!isCollapsed()">
            <span>v1.0.60 Glass • Sept 2026</span>
          </div>
        </div>
      </mat-sidenav>

      <!-- MAIN CONTENT GLASS -->
      <mat-sidenav-content class="main-content-glass">
        <!-- Top Toolbar Glass -->
        <mat-toolbar class="top-toolbar-glass">
          <button class="toolbar-btn-glass" mat-icon-button (click)="sidenav.toggle()">
            <mat-icon>menu</mat-icon>
          </button>

          <div class="toolbar-title-glass">
            <div class="title-icon-glass-small">
              <mat-icon>auto_awesome</mat-icon>
            </div>
            <div>
              <span class="title-main">Second Hand Mobile Shop - Hisab System</span>
              <span class="title-sub">Glassmorphism Edition • Kitne phone hai, kisse liya, kisko becha</span>
            </div>
          </div>

          <span class="spacer"></span>

          <!-- Quick Actions Glass -->
          <div class="toolbar-actions-glass">
            <button class="action-glass buy-action" matTooltip="Add Purchase - Kharida" routerLink="/purchases">
              <mat-icon>add_shopping_cart</mat-icon>
              <span class="action-ripple"></span>
            </button>
            <button class="action-glass sell-action" matTooltip="Add Sale - Becha" routerLink="/sales">
              <mat-icon>point_of_sale</mat-icon>
              <span class="action-ripple"></span>
            </button>
            <button class="action-glass stock-action" matTooltip="Inventory - Stock Dekho" routerLink="/inventory">
              <mat-icon>inventory_2</mat-icon>
              <span class="action-ripple"></span>
            </button>
          </div>

          <div class="toolbar-divider-glass"></div>

          <button class="toolbar-btn-glass logout" (click)="logout()" matTooltip="Logout">
            <mat-icon>logout</mat-icon>
          </button>
        </mat-toolbar>

        <!-- Stats Bar Glass -->
        <div class="stats-bar-glass" *ngIf="showStatsBar()">
          <div class="stat-glass stock-stat">
            <div class="stat-icon-mini"><mat-icon>smartphone</mat-icon></div>
            <span>Stock: 45 phones</span>
            <div class="stat-pulse"></div>
          </div>
          <div class="stat-glass profit-stat">
            <div class="stat-icon-mini profit"><mat-icon>trending_up</mat-icon></div>
            <span>Aaj ka Profit: ₹5k</span>
          </div>
          <div class="stat-glass buy-stat">
            <div class="stat-icon-mini buy"><mat-icon>shopping_cart</mat-icon></div>
            <span>Aaj Kharida: 3</span>
          </div>
          <div class="stat-glass sell-stat">
            <div class="stat-icon-mini sell"><mat-icon>point_of_sale</mat-icon></div>
            <span>Aaj Becha: 2</span>
          </div>
          <div class="stat-glass live-stat">
            <span class="live-dot-glass"></span>
            <span>Live • Sept 2026</span>
          </div>
        </div>

        <div class="content-wrapper-glass">
          <router-outlet></router-outlet>
        </div>

        <!-- Footer Glass -->
        <div class="footer-glass-bar">
          <span>© 2026 MobileBiz - Glassmorphism Hisab • Version 1.0.60 • Made for Shop Owners with ❤️</span>
          <div class="footer-actions">
            <span class="footer-badge glass"><span class="dot green"></span> System Online</span>
            <span class="footer-badge"><mat-icon>auto_awesome</mat-icon> Glass Edition</span>
          </div>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    /* ===== 2026 GLASSMORPHISM LAYOUT SYSTEM ===== */
    .sidenav-glass-container {
      height: 100vh;
      background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 25%, #f1f5f9 50%, #e0f2fe 75%, #f0fdfa 100%);
      position: relative;
    }
    .sidenav-glass-container::before {
      content: '';
      position: fixed;
      inset: 0;
      background:
        radial-gradient(circle at 20% 20%, rgba(59,130,246,0.08) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(139,92,246,0.08) 0%, transparent 50%),
        radial-gradient(circle at 40% 80%, rgba(6,182,212,0.06) 0%, transparent 50%);
      pointer-events: none;
      z-index: 0;
    }

    /* Sidenav Glass */
    .sidenav-glass {
      width: 320px !important;
      background: rgba(15, 23, 42, 0.85) !important;
      backdrop-filter: blur(24px) saturate(180%) !important;
      -webkit-backdrop-filter: blur(24px) saturate(180%) !important;
      border-right: 1px solid rgba(255,255,255,0.1) !important;
      box-shadow: 0 8px 32px rgba(0,0,0,0.2), inset 0 1px 0 rgba(255,255,255,0.1) !important;
      color: white !important;
      display: flex !important;
      flex-direction: column !important;
      position: relative !important;
      overflow: hidden !important;
      transition: width 0.5s cubic-bezier(0.16, 1, 0.3, 1) !important;
    }
    .sidenav-glass.collapsed {
      width: 80px !important;
    }
    .sidenav-aurora {
      position: absolute;
      inset: 0;
      overflow: hidden;
      pointer-events: none;
    }
    .aurora-dot {
      position: absolute;
      border-radius: 50%;
      filter: blur(40px);
      opacity: 0.15;
      animation: floatAurora 20s infinite ease-in-out;
    }
    .dot-1 {
      width: 300px;
      height: 300px;
      background: radial-gradient(circle, #3b82f6, transparent 70%);
      top: -100px;
      left: -100px;
    }
    .dot-2 {
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, #8b5cf6, transparent 70%);
      bottom: -100px;
      right: -100px;
      animation-delay: -10s;
    }
    @keyframes floatAurora {
      0%, 100% { transform: translate(0,0) scale(1); }
      50% { transform: translate(20px, -20px) scale(1.1); }
    }

    /* Logo Glass */
    .logo-glass {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 24px 20px;
      position: relative;
      z-index: 1;
      border-bottom: 1px solid rgba(255,255,255,0.06);
    }
    .logo-icon-glass {
      width: 56px;
      height: 56px;
      background: linear-gradient(135deg, rgba(59,130,246,0.2), rgba(139,92,246,0.2));
      backdrop-filter: blur(12px);
      border: 1px solid rgba(255,255,255,0.15);
      border-radius: 18px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      position: relative;
      overflow: hidden;
      box-shadow: 0 8px 24px rgba(59,130,246,0.2), inset 0 1px 0 rgba(255,255,255,0.2);
      flex-shrink: 0;
    }
    .logo-shine {
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
      animation: shine 3s infinite;
    }
    @keyframes shine {
      0% { left: -100%; }
      100% { left: 100%; }
    }
    .logo-float {
      animation: floatLogo 3s infinite ease-in-out;
      display: block;
    }
    @keyframes floatLogo {
      0%, 100% { transform: translateY(0) rotate(0deg); }
      50% { transform: translateY(-3px) rotate(3deg); }
    }
    .logo-text {
      display: flex;
      flex-direction: column;
      gap: 2px;
      min-width: 0;
    }
    .logo-title {
      margin: 0;
      font-size: 20px;
      font-weight: 800;
      letter-spacing: -0.02em;
      background: linear-gradient(135deg, #fff 0%, #cbd5e1 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .logo-sub {
      font-size: 10px;
      color: #94a3b8;
      font-weight: 500;
      letter-spacing: 0.05em;
    }
    .logo-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-top: 6px;
      padding: 4px 10px;
      background: rgba(16,185,129,0.1);
      border: 1px solid rgba(16,185,129,0.2);
      border-radius: 20px;
      font-size: 9px;
      font-weight: 700;
      color: #6ee7b7;
      letter-spacing: 0.05em;
      width: fit-content;
    }
    .live-pulse {
      width: 6px;
      height: 6px;
      background: #10b981;
      border-radius: 50%;
      animation: pulseLive 2s infinite;
    }
    @keyframes pulseLive {
      0% { box-shadow: 0 0 0 0 rgba(16,185,129,0.5); }
      70% { box-shadow: 0 0 0 6px rgba(16,185,129,0); }
      100% { box-shadow: 0 0 0 0 rgba(16,185,129,0); }
    }

    /* User Glass */
    .user-glass {
      display: flex;
      align-items: center;
      gap: 14px;
      margin: 16px;
      padding: 16px;
      background: rgba(255,255,255,0.06);
      backdrop-filter: blur(16px);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 18px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.1), inset 0 1px 0 rgba(255,255,255,0.1);
      position: relative;
      z-index: 1;
      transition: all 0.3s;
    }
    .user-glass:hover {
      background: rgba(255,255,255,0.08);
      transform: translateY(-1px);
      box-shadow: 0 8px 24px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.15);
    }
    .user-avatar-glass {
      width: 46px;
      height: 46px;
      background: linear-gradient(135deg, rgba(255,255,255,0.1), rgba(255,255,255,0.05));
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.1);
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      position: relative;
      flex-shrink: 0;
    }
    .avatar-ring {
      position: absolute;
      inset: -2px;
      border-radius: 14px;
      padding: 2px;
      background: linear-gradient(135deg, #3b82f6, #8b5cf6);
      mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      mask-composite: xor;
      -webkit-mask-composite: xor;
      opacity: 0.6;
    }
    .user-details {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 2px;
      min-width: 0;
    }
    .user-name {
      font-weight: 700;
      font-size: 13px;
      color: white;
      letter-spacing: -0.01em;
    }
    .user-role {
      font-size: 10px;
      color: #94a3b8;
      font-weight: 500;
    }
    .user-stats {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-top: 4px;
      font-size: 10px;
      color: #64748b;
    }
    .user-stats mat-icon {
      font-size: 12px;
      width: 12px;
      height: 12px;
    }
    .user-status {
      display: flex;
      align-items: center;
    }
    .status-dot {
      width: 8px;
      height: 8px;
      background: #10b981;
      border-radius: 50%;
      box-shadow: 0 0 0 3px rgba(16,185,129,0.2);
      animation: pulseLive 2s infinite;
    }

    /* Menu Glass */
    .menu-glass-container {
      flex: 1;
      overflow-y: auto;
      padding: 8px 0 16px;
      position: relative;
      z-index: 1;
    }
    .menu-glass-container::-webkit-scrollbar { width: 3px; }
    .menu-glass-container::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 3px; }
    .menu-section-glass {
      margin-bottom: 6px;
      animation: fadeInSection 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;
    }
    @keyframes fadeInSection {
      from { opacity: 0; transform: translateX(-20px); }
      to { opacity: 1; transform: translateX(0); }
    }
    .section-header-glass {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px 20px 8px;
      cursor: pointer;
      user-select: none;
      transition: all 0.3s;
      border-radius: 12px;
      margin: 0 8px;
    }
    .section-header-glass:hover {
      background: rgba(255,255,255,0.04);
    }
    .section-icon-glass {
      width: 28px;
      height: 28px;
      background: rgba(255,255,255,0.06);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .section-icon-glass mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
      color: #94a3b8;
    }
    .section-text {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 1px;
    }
    .section-title {
      font-size: 10px;
      font-weight: 800;
      letter-spacing: 0.1em;
      color: #64748b;
      text-transform: uppercase;
    }
    .section-hi {
      font-size: 9px;
      color: #475569;
      font-weight: 500;
    }
    .expand-icon-glass {
      font-size: 18px;
      color: #475569;
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
    }
    .expand-icon-glass.expanded {
      transform: rotate(180deg);
      color: #94a3b8;
    }
    .section-items-glass {
      padding: 4px 12px !important;
    }
    .menu-item-glass {
      position: relative !important;
      margin: 3px 0 !important;
      border-radius: 14px !important;
      height: 48px !important;
      background: transparent !important;
      border: 1px solid transparent !important;
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1) !important;
      overflow: hidden !important;
      animation: slideInItem 0.5s cubic-bezier(0.16, 1, 0.3, 1) both !important;
    }
    @keyframes slideInItem {
      from { opacity: 0; transform: translateX(-10px) scale(0.95); }
      to { opacity: 1; transform: translateX(0) scale(1); }
    }
    .menu-item-glass:hover {
      background: rgba(255,255,255,0.06) !important;
      border-color: rgba(255,255,255,0.08) !important;
      transform: translateX(4px) !important;
      box-shadow: 0 4px 16px rgba(0,0,0,0.1), inset 0 1px 0 rgba(255,255,255,0.08) !important;
    }
    .menu-item-glass.active-glass {
      background: linear-gradient(135deg, rgba(59,130,246,0.15), rgba(99,102,241,0.15)) !important;
      border-color: rgba(59,130,246,0.2) !important;
      box-shadow: 0 4px 20px rgba(59,130,246,0.15), inset 0 1px 0 rgba(255,255,255,0.1) !important;
      color: white !important;
    }
    .menu-item-glass.active-glass::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 60%;
      background: linear-gradient(180deg, #3b82f6, #8b5cf6);
      border-radius: 0 3px 3px 0;
    }
    .item-icon-glass {
      position: relative !important;
      width: 36px !important;
      height: 36px !important;
      background: rgba(255,255,255,0.06) !important;
      border: 1px solid rgba(255,255,255,0.08) !important;
      border-radius: 10px !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      transition: all 0.3s !important;
      flex-shrink: 0 !important;
    }
    .menu-item-glass:hover .item-icon-glass {
      background: rgba(255,255,255,0.1) !important;
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
    }
    .menu-item-glass.active-glass .item-icon-glass {
      background: linear-gradient(135deg, #3b82f6, #6366f1) !important;
      border-color: rgba(255,255,255,0.2) !important;
      box-shadow: 0 4px 16px rgba(59,130,246,0.3) !important;
      color: white !important;
    }
    .icon-glow {
      position: absolute;
      inset: 0;
      border-radius: 10px;
      background: radial-gradient(circle at center, rgba(59,130,246,0.3), transparent 70%);
      opacity: 0;
      transition: opacity 0.3s;
    }
    .menu-item-glass.active-glass .icon-glow { opacity: 1; }
    .item-icon-glass mat-icon {
      font-size: 18px !important;
      width: 18px !important;
      height: 18px !important;
      color: #94a3b8 !important;
      transition: all 0.3s !important;
      z-index: 1;
    }
    .menu-item-glass.active-glass .item-icon-glass mat-icon { color: white !important; }
    .menu-item-glass:hover .item-icon-glass mat-icon { color: #e2e8f0 !important; }
    .menu-item-content-glass {
      display: flex !important;
      flex-direction: column !important;
      gap: 2px !important;
      line-height: 1.2 !important;
      margin-left: 12px !important;
      flex: 1 !important;
      min-width: 0 !important;
    }
    .item-label {
      font-size: 13px !important;
      font-weight: 600 !important;
      color: #cbd5e1 !important;
      letter-spacing: -0.01em !important;
      white-space: nowrap !important;
      overflow: hidden !important;
      text-overflow: ellipsis !important;
    }
    .menu-item-glass.active-glass .item-label { color: white !important; font-weight: 700 !important; }
    .item-label-hi {
      font-size: 10px !important;
      color: #64748b !important;
      font-weight: 500 !important;
      white-space: nowrap !important;
      overflow: hidden !important;
      text-overflow: ellipsis !important;
    }
    .menu-item-glass.active-glass .item-label-hi { color: #a5b4fc !important; }
    .new-badge-glass {
      position: absolute;
      top: -4px;
      right: -8px;
      background: linear-gradient(135deg, #ef4444, #f87171);
      color: white;
      font-size: 8px;
      font-weight: 800;
      padding: 3px 7px;
      border-radius: 10px;
      letter-spacing: 0.05em;
      box-shadow: 0 2px 8px rgba(239,68,68,0.3);
      animation: pulseBadge 2s infinite;
    }
    @keyframes pulseBadge {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.05); }
    }
    .item-arrow {
      opacity: 0;
      transform: translateX(-10px);
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      color: #475569;
    }
    .menu-item-glass:hover .item-arrow {
      opacity: 1;
      transform: translateX(0);
      color: #94a3b8;
    }
    .menu-item-glass.active-glass .item-arrow {
      opacity: 1;
      transform: translateX(0);
      color: white;
    }
    .item-arrow mat-icon {
      font-size: 16px !important;
      width: 16px !important;
      height: 16px !important;
    }

    /* Collapse Glass */
    .collapse-glass {
      padding: 16px;
      border-top: 1px solid rgba(255,255,255,0.06);
      display: flex;
      flex-direction: column;
      gap: 12px;
      position: relative;
      z-index: 1;
      background: rgba(0,0,0,0.1);
      backdrop-filter: blur(10px);
    }
    .collapse-btn-glass {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 14px;
      background: rgba(255,255,255,0.06);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 12px;
      color: #94a3b8;
      font-size: 12px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;
      width: 100%;
      justify-content: center;
    }
    .collapse-btn-glass:hover {
      background: rgba(255,255,255,0.1);
      color: white;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    .version-glass {
      text-align: center;
      font-size: 9px;
      color: #475569;
      font-weight: 500;
      letter-spacing: 0.05em;
    }

    /* Main Content Glass */
    .main-content-glass {
      background: transparent !important;
      position: relative;
      z-index: 1;
    }
    .top-toolbar-glass {
      background: rgba(255,255,255,0.7) !important;
      backdrop-filter: blur(20px) saturate(180%) !important;
      -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
      border-bottom: 1px solid rgba(0,0,0,0.04) !important;
      box-shadow: 0 1px 0 rgba(255,255,255,0.8), 0 4px 20px rgba(0,0,0,0.04) !important;
      color: #0f172a !important;
      height: 72px !important;
      padding: 0 24px !important;
      position: sticky !important;
      top: 0 !important;
      z-index: 100 !important;
    }
    .toolbar-btn-glass {
      width: 44px !important;
      height: 44px !important;
      background: rgba(0,0,0,0.04) !important;
      backdrop-filter: blur(10px) !important;
      border: 1px solid rgba(0,0,0,0.06) !important;
      border-radius: 12px !important;
      color: #475569 !important;
      transition: all 0.3s !important;
    }
    .toolbar-btn-glass:hover {
      background: rgba(0,0,0,0.08) !important;
      transform: translateY(-1px) !important;
      box-shadow: 0 4px 12px rgba(0,0,0,0.08) !important;
      color: #0f172a !important;
    }
    .toolbar-btn-glass.logout {
      background: rgba(239,68,68,0.08) !important;
      border-color: rgba(239,68,68,0.12) !important;
      color: #dc2626 !important;
    }
    .toolbar-btn-glass.logout:hover {
      background: rgba(239,68,68,0.12) !important;
      color: #b91c1c !important;
    }
    .toolbar-title-glass {
      display: flex;
      align-items: center;
      gap: 14px;
      margin-left: 16px;
    }
    .title-icon-glass-small {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, rgba(59,130,246,0.1), rgba(139,92,246,0.1));
      border: 1px solid rgba(59,130,246,0.15);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #3b82f6;
      box-shadow: 0 2px 12px rgba(59,130,246,0.1);
    }
    .title-main {
      font-size: 15px !important;
      font-weight: 800 !important;
      color: #0f172a !important;
      letter-spacing: -0.01em !important;
      line-height: 1.2 !important;
    }
    .title-sub {
      font-size: 11px !important;
      color: #64748b !important;
      font-weight: 500 !important;
      line-height: 1.2 !important;
    }
    .spacer { flex: 1 1 auto; }
    .toolbar-actions-glass {
      display: flex;
      gap: 10px;
      align-items: center;
    }
    .action-glass {
      position: relative;
      width: 44px;
      height: 44px;
      border-radius: 12px;
      border: 1px solid;
      backdrop-filter: blur(10px);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
      overflow: hidden;
      background: rgba(255,255,255,0.8);
    }
    .action-glass:hover {
      transform: translateY(-2px) scale(1.05);
      box-shadow: 0 8px 20px rgba(0,0,0,0.1);
    }
    .buy-action { border-color: rgba(239,68,68,0.15); color: #dc2626; background: rgba(239,68,68,0.08); }
    .sell-action { border-color: rgba(16,185,129,0.15); color: #059669; background: rgba(16,185,129,0.08); }
    .stock-action { border-color: rgba(59,130,246,0.15); color: #2563eb; background: rgba(59,130,246,0.08); }
    .action-ripple {
      position: absolute;
      inset: 0;
      background: radial-gradient(circle at center, rgba(255,255,255,0.4), transparent 70%);
      opacity: 0;
      transition: opacity 0.3s;
    }
    .action-glass:hover .action-ripple { opacity: 1; }
    .toolbar-divider-glass {
      width: 1px;
      height: 24px;
      background: rgba(0,0,0,0.08);
      margin: 0 12px;
    }

    /* Stats Bar Glass */
    .stats-bar-glass {
      display: flex;
      gap: 12px;
      padding: 14px 24px;
      background: rgba(255,255,255,0.5);
      backdrop-filter: blur(16px);
      border-bottom: 1px solid rgba(0,0,0,0.04);
      overflow-x: auto;
      position: sticky;
      top: 72px;
      z-index: 90;
    }
    .stats-bar-glass::-webkit-scrollbar { display: none; }
    .stat-glass {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 16px;
      background: rgba(255,255,255,0.7);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,0.6);
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      color: #475569;
      white-space: nowrap;
      box-shadow: 0 2px 12px rgba(0,0,0,0.04), inset 0 1px 0 rgba(255,255,255,0.8);
      transition: all 0.3s;
      position: relative;
      overflow: hidden;
    }
    .stat-glass:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 16px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.9);
      background: rgba(255,255,255,0.85);
    }
    .stat-icon-mini {
      width: 28px;
      height: 28px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(0,0,0,0.04);
      border: 1px solid rgba(0,0,0,0.06);
    }
    .stat-icon-mini mat-icon { font-size: 16px; width: 16px; height: 16px; }
    .stat-icon-mini.profit { background: rgba(16,185,129,0.1); color: #059669; border-color: rgba(16,185,129,0.15); }
    .stat-icon-mini.buy { background: rgba(239,68,68,0.1); color: #dc2626; border-color: rgba(239,68,68,0.15); }
    .stat-icon-mini.sell { background: rgba(16,185,129,0.1); color: #059669; border-color: rgba(16,185,129,0.15); }
    .stat-pulse {
      width: 6px;
      height: 6px;
      background: #10b981;
      border-radius: 50%;
      animation: pulseLive 2s infinite;
      margin-left: 4px;
    }
    .profit-stat { background: rgba(16,185,129,0.08) !important; border-color: rgba(16,185,129,0.15) !important; color: #065f46 !important; }
    .live-stat { background: rgba(59,130,246,0.08) !important; border-color: rgba(59,130,246,0.15) !important; color: #1e40af !important; }
    .live-dot-glass {
      width: 6px;
      height: 6px;
      background: #3b82f6;
      border-radius: 50%;
      box-shadow: 0 0 0 3px rgba(59,130,246,0.2);
      animation: pulseLive 2s infinite;
    }

    /* Content */
    .content-wrapper-glass {
      padding: 24px;
      min-height: calc(100vh - 160px);
      position: relative;
      z-index: 1;
    }

    /* Footer Glass */
    .footer-glass-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 24px;
      background: rgba(255,255,255,0.5);
      backdrop-filter: blur(16px);
      border-top: 1px solid rgba(0,0,0,0.04);
      font-size: 11px;
      color: #94a3b8;
      font-weight: 500;
      flex-wrap: wrap;
      gap: 12px;
    }
    .footer-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
    .footer-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: rgba(255,255,255,0.7);
      border: 1px solid rgba(0,0,0,0.06);
      border-radius: 20px;
      font-size: 10px;
      font-weight: 600;
    }
    .footer-badge.glass {
      background: rgba(16,185,129,0.08);
      border-color: rgba(16,185,129,0.15);
      color: #065f46;
    }
    .footer-badge .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
    }
    .dot.green { background: #10b981; box-shadow: 0 0 0 3px rgba(16,185,129,0.2); animation: pulseLive 2s infinite; }
    .footer-badge mat-icon { font-size: 14px; width: 14px; height: 14px; }

    /* Responsive */
    @media (max-width: 900px) {
      .sidenav-glass { width: 280px !important; }
      .top-toolbar-glass { padding: 0 16px !important; height: 64px !important; }
      .toolbar-title-glass { display: none; }
      .stats-bar-glass { top: 64px; padding: 10px 16px; }
      .content-wrapper-glass { padding: 16px; }
    }
  `]
})
export class MainLayoutComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  isCollapsed = signal(false);
  showStatsBar = signal(true);

  menuSections = signal<MenuSection[]>([
    {
      title: 'MAIN HISAB',
      titleHi: 'मुख्य हिसाब',
      icon: 'dashboard',
      expanded: true,
      items: [
        { label: 'Dashboard', labelHi: 'डैशबोर्ड', icon: 'dashboard', route: '/dashboard' },
        { label: 'Stock (Inventory)', labelHi: 'स्टॉक - मेरे पास फोन', icon: 'inventory_2', route: '/inventory', badge: '!' },
        { label: 'Reports - Profit/Loss', labelHi: 'मुनाफा-नुकसान', icon: 'bar_chart', route: '/reports', isNew: true },
      ]
    },
    {
      title: 'BUY & SELL - KHARIDA BECHA',
      titleHi: 'खरीदा बेचा',
      icon: 'swap_horiz',
      expanded: true,
      items: [
        { label: 'Buy - Kharida', labelHi: 'खरीदा - किससे लिया', icon: 'shopping_cart', route: '/purchases', badge: '2' },
        { label: 'Sell - Becha', labelHi: 'बेचा - किसको दिया', icon: 'point_of_sale', route: '/sales' },
        { label: 'Phone Models', labelHi: 'फोन मॉडल लिस्ट', icon: 'smartphone', route: '/devices' },
        { label: 'Add New Phone', labelHi: 'नया फोन ऐड करो', icon: 'add_circle', route: '/devices/create', isNew: true },
      ]
    },
    {
      title: 'CUSTOMERS - LOG',
      titleHi: 'ग्राहक',
      icon: 'people',
      expanded: false,
      items: [
        { label: 'Customers', labelHi: 'जिससे लिया/दिया', icon: 'people', route: '/customers' },
        { label: 'KYC (Optional)', labelHi: 'पहचान पत्र', icon: 'verified_user', route: '/kyc' },
      ]
    },
    {
      title: 'NETWORK - DUSRE SHOPS',
      titleHi: 'नेटवर्क',
      icon: 'hub',
      expanded: false,
      items: [
        { label: 'My Shops', labelHi: 'मेरी दुकानें', icon: 'store', route: '/shops' },
        { label: 'Branches', labelHi: 'ब्रांच', icon: 'location_on', route: '/branches' },
        { label: 'Network Stock', labelHi: 'दूसरी दुकान का स्टॉक', icon: 'language', route: '/network-inventory' },
        { label: 'Transfers', labelHi: 'माल ट्रांसफर', icon: 'transfer_within_a_station', route: '/transfers' },
      ]
    },
    {
      title: 'ADMIN & FINANCE',
      titleHi: 'एडमिन',
      icon: 'admin_panel_settings',
      expanded: false,
      items: [
        { label: 'Staff Users', labelHi: 'स्टाफ', icon: 'manage_accounts', route: '/users' },
        { label: 'Shop Users', labelHi: 'दुकान यूजर', icon: 'person', route: '/shop-users' },
        { label: 'Roles', labelHi: 'रोल', icon: 'security', route: '/roles' },
        { label: 'Permissions', labelHi: 'परमिशन', icon: 'key', route: '/permissions' },
        { label: 'Settlements', labelHi: 'हिसाब सेटलमेंट', icon: 'account_balance', route: '/settlements' },
        { label: 'Invoices', labelHi: 'बिल', icon: 'receipt_long', route: '/invoices' },
        { label: 'Payments', labelHi: 'पेमेंट', icon: 'payments', route: '/payments' },
        { label: 'Audit Log', labelHi: 'लॉग', icon: 'history', route: '/audit' },
      ]
    }
  ]);

  toggleSection(section: MenuSection) {
    section.expanded = !section.expanded;
    this.menuSections.update(s => [...s]);
  }

  toggleCollapse() {
    this.isCollapsed.update(v => !v);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
