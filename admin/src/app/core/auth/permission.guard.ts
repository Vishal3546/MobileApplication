import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { PermissionService } from './permission.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanActivate {
  constructor(private permissionService: PermissionService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const requiredPermissions = route.data['permissions'] as Array<string>;
    if (!requiredPermissions || requiredPermissions.length === 0) {
      return true; // No permissions required
    }
    
    if (this.permissionService.hasAnyPermission(requiredPermissions)) {
      return true;
    }

    this.router.navigate(['/unauthorized']); // Or dashboard
    return false;
  }
}
