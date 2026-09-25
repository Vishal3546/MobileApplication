import { Component, inject, signal, computed } from '@angular/core';
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
    <mat-sidenav-container class="sidenav-container">
      <!-- SIDENAV -->
      <mat-sidenav #sidenav mode="side" opened class="sidenav" [class.collapsed]="isCollapsed()">
        <!-- LOGO HEADER -->
        <div class="logo-section">
          <div class="logo-icon">📱</div>
          <div class="logo-text" *ngIf="!isCollapsed()">
            <h2>MobileBiz</h2>
            <span>2nd Hand Hub - 2026</span>
          </div>
        </div>

        <mat-divider></mat-divider>

        <!-- USER INFO -->
        <div class="user-info" *ngIf="!isCollapsed()">
          <div class="user-avatar">👤</div>
          <div class="user-details">
            <span class="user-name">Shop Owner</span>
            <span class="user-role">Admin</span>
          </div>
        </div>

        <mat-divider *ngIf="!isCollapsed()"></mat-divider>

        <!-- MENU SECTIONS -->
        <div class="menu-container">
          <div *ngFor="let section of menuSections()" class="menu-section">
            <!-- Section Header -->
            <div class="section-header" (click)="toggleSection(section)" *ngIf="!isCollapsed()">
              <mat-icon>{{section.icon}}</mat-icon>
              <span class="section-title">{{section.title}}</span>
              <span class="section-hi">{{section.titleHi}}</span>
              <mat-icon class="expand-icon" [class.expanded]="section.expanded">
                {{section.expanded ? 'expand_less' : 'expand_more'}}
              </mat-icon>
            </div>

            <!-- Section Items -->
            <mat-nav-list *ngIf="section.expanded || isCollapsed()" class="section-items">
              <a mat-list-item
                 *ngFor="let item of section.items"
                 [routerLink]="item.route"
                 routerLinkActive="active"
                 [matTooltip]="isCollapsed() ? item.label + ' - ' + item.labelHi : ''"
                 matTooltipPosition="right">
                <mat-icon matListItemIcon [matBadge]="item.badge" [matBadgeHidden]="!item.badge" matBadgeColor="warn" matBadgeSize="small">
                  {{item.icon}}
                </mat-icon>
                <div matListItemTitle *ngIf="!isCollapsed()" class="menu-item-content">
                  <span class="item-label">{{item.label}}</span>
                  <span class="item-label-hi">{{item.labelHi}}</span>
                  <span *ngIf="item.isNew" class="new-badge">NEW</span>
                </div>
              </a>
            </mat-nav-list>
          </div>
        </div>

        <!-- COLLAPSE BUTTON -->
        <div class="collapse-section">
          <button mat-icon-button (click)="toggleCollapse()" matTooltip="Toggle Sidebar">
            <mat-icon>{{isCollapsed() ? 'chevron_right' : 'chevron_left'}}</mat-icon>
          </button>
        </div>
      </mat-sidenav>

      <!-- MAIN CONTENT -->
      <mat-sidenav-content class="main-content">
        <mat-toolbar color="primary" class="top-toolbar">
          <button mat-icon-button (click)="sidenav.toggle()">
            <mat-icon>menu</mat-icon>
          </button>

          <div class="toolbar-title">
            <span class="title-main">Second Hand Mobile Shop - Hisab System</span>
            <span class="title-sub">Kitne phone hai, kisse liya, kisko becha - Sab hisab yahi</span>
          </div>

          <span class="spacer"></span>

          <!-- QUICK ACTIONS -->
          <button mat-icon-button matTooltip="Add Purchase - Kharida" routerLink="/purchases">
            <mat-icon>add_shopping_cart</mat-icon>
          </button>
          <button mat-icon-button matTooltip="Add Sale - Becha" routerLink="/sales">
            <mat-icon>point_of_sale</mat-icon>
          </button>
          <button mat-icon-button matTooltip="Inventory - Stock Dekho" routerLink="/inventory">
            <mat-icon>inventory_2</mat-icon>
          </button>

          <mat-divider vertical style="height: 30px; margin: 0 10px;"></mat-divider>

          <button mat-icon-button (click)="logout()" matTooltip="Logout">
            <mat-icon>logout</mat-icon>
          </button>
        </mat-toolbar>

        <!-- BREADCRUMB / STATS BAR -->
        <div class="stats-bar" *ngIf="showStatsBar()">
          <div class="stat-item">
            <mat-icon>smartphone</mat-icon>
            <span>Stock: Loading...</span>
          </div>
          <div class="stat-item profit">
            <mat-icon>trending_up</mat-icon>
            <span>Aaj ka Profit: ₹0</span>
          </div>
          <div class="stat-item">
            <mat-icon>shopping_cart</mat-icon>
            <span>Aaj Kharida: 0</span>
          </div>
          <div class="stat-item">
            <mat-icon>point_of_sale</mat-icon>
            <span>Aaj Becha: 0</span>
          </div>
        </div>

        <div class="content-wrapper">
          <router-outlet></router-outlet>
        </div>

        <!-- FOOTER -->
        <div class="app-footer">
          <span>© 2026 MobileBiz - Second Hand Mobile Shop Hisab | Version 1.0.60 | Made for Shop Owners</span>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    .sidenav-container {
      height: 100vh;
      background: #f5f7fb;
    }
    .sidenav {
      width: 300px;
      background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
      color: white;
      transition: width 0.3s ease;
      display: flex;
      flex-direction: column;
    }
    .sidenav.collapsed {
      width: 70px;
    }
    .logo-section {
      display: flex;
      align-items: center;
      padding: 20px;
      gap: 12px;
    }
    .logo-icon {
      font-size: 32px;
      width: 50px;
      height: 50px;
      background: linear-gradient(135deg, #3b82f6, #8b5cf6);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .logo-text h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 700;
      color: white;
    }
    .logo-text span {
      font-size: 11px;
      color: #94a3b8;
      letter-spacing: 0.5px;
    }
    .user-info {
      display: flex;
      align-items: center;
      padding: 16px 20px;
      gap: 12px;
      background: rgba(255,255,255,0.05);
      margin: 10px;
      border-radius: 12px;
    }
    .user-avatar {
      width: 40px;
      height: 40px;
      background: #334155;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
    }
    .user-details {
      display: flex;
      flex-direction: column;
    }
    .user-name {
      font-weight: 600;
      font-size: 14px;
    }
    .user-role {
      font-size: 12px;
      color: #94a3b8;
    }
    .menu-container {
      flex: 1;
      overflow-y: auto;
      padding: 10px 0;
    }
    .menu-section {
      margin-bottom: 8px;
    }
    .section-header {
      display: flex;
      align-items: center;
      padding: 12px 20px 8px;
      gap: 8px;
      cursor: pointer;
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 1px;
      color: #64748b;
      text-transform: uppercase;
      user-select: none;
    }
    .section-header:hover {
      color: #94a3b8;
    }
    .section-title {
      flex: 1;
    }
    .section-hi {
      font-size: 10px;
      color: #475569;
      font-weight: 400;
    }
    .expand-icon {
      font-size: 18px;
      transition: transform 0.2s;
    }
    .expand-icon.expanded {
      transform: rotate(180deg);
    }
    .section-items {
      padding-top: 0;
    }
    .section-items a {
      color: #cbd5e1 !important;
      margin: 2px 10px;
      border-radius: 8px;
      height: 44px !important;
    }
    .section-items a:hover {
      background: rgba(255,255,255,0.08) !important;
      color: white !important;
    }
    .section-items a.active {
      background: linear-gradient(135deg, #3b82f6, #6366f1) !important;
      color: white !important;
      box-shadow: 0 4px 12px rgba(59,130,246,0.3);
    }
    .section-items mat-icon {
      color: inherit;
    }
    .menu-item-content {
      display: flex;
      flex-direction: column;
      line-height: 1.2;
      position: relative;
    }
    .item-label {
      font-size: 13px;
      font-weight: 500;
    }
    .item-label-hi {
      font-size: 10px;
      opacity: 0.7;
      font-weight: 400;
    }
    .new-badge {
      position: absolute;
      right: -10px;
      top: 0;
      background: #ef4444;
      color: white;
      font-size: 8px;
      padding: 2px 6px;
      border-radius: 10px;
      font-weight: 700;
    }
    .collapse-section {
      padding: 10px;
      display: flex;
      justify-content: center;
      border-top: 1px solid rgba(255,255,255,0.1);
    }
    .top-toolbar {
      background: white !important;
      color: #1e293b !important;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      z-index: 10;
    }
    .toolbar-title {
      display: flex;
      flex-direction: column;
      margin-left: 16px;
      line-height: 1.2;
    }
    .title-main {
      font-size: 16px;
      font-weight: 700;
      color: #0f172a;
    }
    .title-sub {
      font-size: 11px;
      color: #64748b;
      font-weight: 400;
    }
    .spacer {
      flex: 1 1 auto;
    }
    .stats-bar {
      display: flex;
      gap: 20px;
      padding: 12px 24px;
      background: white;
      border-bottom: 1px solid #e2e8f0;
      overflow-x: auto;
    }
    .stat-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #475569;
      white-space: nowrap;
      background: #f8fafc;
      padding: 6px 12px;
      border-radius: 20px;
      border: 1px solid #e2e8f0;
    }
    .stat-item mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }
    .stat-item.profit {
      background: #dcfce7;
      color: #166534;
      border-color: #bbf7d0;
    }
    .content-wrapper {
      padding: 24px;
      min-height: calc(100vh - 140px);
    }
    .app-footer {
      padding: 16px 24px;
      text-align: center;
      font-size: 11px;
      color: #94a3b8;
      border-top: 1px solid #e2e8f0;
      background: white;
    }
    /* Scrollbar */
    .menu-container::-webkit-scrollbar {
      width: 4px;
    }
    .menu-container::-webkit-scrollbar-thumb {
      background: #334155;
      border-radius: 2px;
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
    // trigger change detection
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
