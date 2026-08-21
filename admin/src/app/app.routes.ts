import { Routes } from '@angular/router';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout';
import { MainLayoutComponent } from './layouts/main-layout/main-layout';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { 
        path: 'dashboard', 
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) 
      },
      { 
        path: 'customers', 
        loadComponent: () => import('./features/customers/customer-list/customer-list.component').then(m => m.CustomerListComponent).catch(() => import('./features/customers/customers-list').then(m => m.CustomersListComponent as any))
      },
      { 
        path: 'users', 
        loadComponent: () => import('./features/users/user-list/user-list.component').then(m => m.UserListComponent) 
      },
      { 
        path: 'roles', 
        loadComponent: () => import('./features/roles/role-list/role-list.component').then(m => m.RoleListComponent) 
      },
      { 
        path: 'permissions', 
        loadComponent: () => import('./features/permissions/permission-list/permission-list.component').then(m => m.PermissionListComponent) 
      },
      { 
        path: 'branches', 
        loadComponent: () => import('./features/branches/branch-list/branch-list.component').then(m => m.BranchListComponent) 
      },
      { 
        path: 'inventory', 
        loadComponent: () => import('./features/inventory/inventory-list').then(m => m.InventoryListComponent) 
      },
      { 
        path: 'sales', 
        loadComponent: () => import('./features/sales/sales-list').then(m => m.SalesListComponent) 
      }
    ]
  },
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
