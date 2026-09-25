import { Routes } from '@angular/router';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout';
import { MainLayoutComponent } from './layouts/main-layout/main-layout';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login';

/**
 * 2026 Second-Hand Mobile Shop - Complete Routing
 * Focus: Hisab = Buy (Purchase) / Sell (Sales) / Stock (Inventory)
 * All routes lazy-loaded, grouped for shop owner simplicity
 */
export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      // ============ MAIN - HISAB DASHBOARD ============
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
        data: { title: 'Dashboard - Total Hisab', icon: 'dashboard' }
      },

      // ============ STOCK ============
      {
        path: 'inventory',
        loadComponent: () => import('./features/inventory/inventory-list').then(m => m.InventoryListComponent),
        data: { title: 'Stock - Mere Paas Kitne Phone Hai', permissions: ['VIEW_INVENTORY'] }
      },
      {
        path: 'inventory/:id',
        loadComponent: () => import('./features/inventory/inventory-list').then(m => m.InventoryListComponent),
        data: { title: 'Stock Detail' }
      },

      // ============ BUY HISAB (Kharida) ============
      {
        path: 'purchases',
        loadComponent: () => import('./features/purchases/purchases-list').then(m => m.PurchasesListComponent),
        data: { title: 'Kharida - Buy History', icon: 'shopping_cart' }
      },
      {
        path: 'purchases/:id',
        loadComponent: () => import('./features/purchases/purchases-list').then(m => m.PurchasesListComponent),
        data: { title: 'Purchase Detail' }
      },

      // ============ SELL HISAB (Becha) ============
      {
        path: 'sales',
        loadComponent: () => import('./features/sales/sales-list').then(m => m.SalesListComponent),
        data: { title: 'Becha - Sell History', icon: 'point_of_sale' }
      },
      {
        path: 'sales/:id',
        loadComponent: () => import('./features/sales/sales-list').then(m => m.SalesListComponent),
        data: { title: 'Sale Detail' }
      },

      // ============ DEVICES - PHONE MASTER ============
      {
        path: 'devices',
        loadComponent: () => import('./features/devices/device-list/device-list.component').then(m => m.DeviceListComponent),
        data: { title: 'Mobile Models - Phone Database', permissions: ['VIEW_DEVICES'] }
      },
      {
        path: 'devices/create',
        loadComponent: () => import('./features/devices/device-form/device-form.component').then(m => m.DeviceFormComponent),
        data: { title: 'Add New Phone Model' }
      },
      {
        path: 'devices/:id',
        loadComponent: () => import('./features/devices/device-detail/device-detail.component').then(m => m.DeviceDetailComponent),
        data: { title: 'Phone Detail' }
      },
      {
        path: 'devices/:id/edit',
        loadComponent: () => import('./features/devices/device-form/device-form.component').then(m => m.DeviceFormComponent),
        data: { title: 'Edit Phone' }
      },
      {
        path: 'devices/:id/condition',
        loadComponent: () => import('./features/devices/device-condition/device-condition.component').then(m => m.DeviceConditionComponent),
        data: { title: 'Phone Condition' }
      },
      {
        path: 'devices/:id/inspection',
        loadComponent: () => import('./features/devices/device-inspection/device-inspection.component').then(m => m.DeviceInspectionComponent),
        data: { title: 'Phone Inspection' }
      },

      // ============ CUSTOMER / SUPPLIER - JISSE LIYA / JISKO BECHA ============
      {
        path: 'customers',
        loadComponent: () => import('./features/customers/customer-list/customer-list.component').then(m => m.CustomerListComponent),
        data: { title: 'Customers - Kharida/Becha Jisse', icon: 'people' }
      },
      {
        path: 'customers/create',
        loadComponent: () => import('./features/customers/customer-form/customer-form.component').then(m => m.CustomerFormComponent),
        data: { title: 'Add Customer' }
      },
      {
        path: 'customers/:id',
        loadComponent: () => import('./features/customers/customer-detail/customer-detail.component').then(m => m.CustomerDetailComponent),
        data: { title: 'Customer Detail' }
      },
      {
        path: 'customers/:id/edit',
        loadComponent: () => import('./features/customers/customer-form/customer-form.component').then(m => m.CustomerFormComponent),
        data: { title: 'Edit Customer' }
      },

      // ============ KYC - OPTIONAL FOR 2ND HAND SHOP ============
      {
        path: 'kyc',
        loadComponent: () => import('./features/kyc/kyc-list/kyc-list.component').then(m => m.KycListComponent),
        data: { title: 'KYC Verification (Optional)', permissions: ['VIEW_KYC'] }
      },

      // ============ SHOPS & BRANCHES ============
      {
        path: 'shops',
        loadComponent: () => import('./features/shops/shop-list/shop-list').then(m => m.ShopList),
        data: { title: 'My Shops', permissions: ['VIEW_SHOPS'] }
      },
      {
        path: 'shops/create',
        loadComponent: () => import('./features/shops/shop-form/shop-form').then(m => m.ShopForm),
        data: { title: 'Add Shop' }
      },
      {
        path: 'shops/:id',
        loadComponent: () => import('./features/shops/shop-detail/shop-detail').then(m => m.ShopDetail),
        data: { title: 'Shop Detail' }
      },
      {
        path: 'branches',
        loadComponent: () => import('./features/branches/branch-list/branch-list.component').then(m => m.BranchListComponent),
        data: { title: 'Branches', permissions: ['VIEW_BRANCHES'] }
      },
      {
        path: 'branches/create',
        loadComponent: () => import('./features/branches/branch-form/branch-form.component').then(m => m.BranchFormComponent),
        data: { title: 'Add Branch' }
      },
      {
        path: 'branches/:id',
        loadComponent: () => import('./features/branches/branch-detail/branch-detail.component').then(m => m.BranchDetailComponent),
        data: { title: 'Branch Detail' }
      },

      // ============ NETWORK - MULTI SHOP TRANSFERS ============
      {
        path: 'network-inventory',
        loadComponent: () => import('./features/network/network-inventory-list/network-inventory-list').then(m => m.NetworkInventoryList),
        data: { title: 'Network Stock - Dusre Shops Ka Stock' }
      },
      {
        path: 'network-inventory/:id',
        loadComponent: () => import('./features/network/network-inventory-detail/network-inventory-detail').then(m => m.NetworkInventoryDetail),
        data: { title: 'Network Inventory Detail' }
      },
      {
        path: 'transfers',
        loadComponent: () => import('./features/transfers/network-transfer-list/network-transfer-list').then(m => m.NetworkTransferList),
        data: { title: 'Stock Transfers - Maal Transfer' }
      },
      {
        path: 'transfers/:id',
        loadComponent: () => import('./features/transfers/network-transfer-detail/network-transfer-detail').then(m => m.NetworkTransferDetail),
        data: { title: 'Transfer Detail' }
      },

      // ============ USERS & ROLES ============
      {
        path: 'users',
        loadComponent: () => import('./features/users/user-list/user-list.component').then(m => m.UserListComponent),
        data: { title: 'Staff - Users', permissions: ['VIEW_USERS'] }
      },
      {
        path: 'users/create',
        loadComponent: () => import('./features/users/user-form/user-form.component').then(m => m.UserFormComponent),
        data: { title: 'Add Staff' }
      },
      {
        path: 'users/:id',
        loadComponent: () => import('./features/users/user-detail/user-detail.component').then(m => m.UserDetailComponent),
        data: { title: 'User Detail' }
      },
      {
        path: 'shop-users',
        loadComponent: () => import('./features/users/shop-user-list/shop-user-list').then(m => m.ShopUserList),
        data: { title: 'Shop Users' }
      },
      {
        path: 'roles',
        loadComponent: () => import('./features/roles/role-list/role-list.component').then(m => m.RoleListComponent),
        data: { title: 'Roles', permissions: ['VIEW_ROLES'] }
      },
      {
        path: 'roles/create',
        loadComponent: () => import('./features/roles/role-form/role-form.component').then(m => m.RoleFormComponent),
        data: { title: 'Add Role' }
      },
      {
        path: 'roles/:id',
        loadComponent: () => import('./features/roles/role-detail/role-detail.component').then(m => m.RoleDetailComponent),
        data: { title: 'Role Detail' }
      },
      {
        path: 'permissions',
        loadComponent: () => import('./features/permissions/permission-list/permission-list.component').then(m => m.PermissionListComponent),
        data: { title: 'Permissions', permissions: ['VIEW_PERMISSIONS'] }
      },

      // ============ SETTLEMENTS & FINANCE ============
      {
        path: 'settlements',
        loadChildren: () => import('./features/settlements/settlements.module').then(m => m.SettlementsModule),
        data: { title: 'Settlements - Hisab Settlement' }
      },
      {
        path: 'invoices',
        loadComponent: () => import('./features/invoices/invoices-list').then(m => m.InvoicesListComponent),
        data: { title: 'Invoices' }
      },
      {
        path: 'payments',
        loadComponent: () => import('./features/payments/payments-list').then(m => m.PaymentsListComponent),
        data: { title: 'Payments' }
      },

      // ============ REPORTS - PROFIT/LOSS ============
      {
        path: 'reports',
        loadComponent: () => import('./features/reports/reports-list').then(m => m.ReportsListComponent),
        data: { title: 'Reports - Profit/Loss Hisab' }
      },
      {
        path: 'audit',
        loadComponent: () => import('./features/audit/audit-list').then(m => m.AuditListComponent),
        data: { title: 'Audit Log' }
      }
    ]
  },
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
