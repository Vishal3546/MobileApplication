import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PermissionService } from '../../../core/auth/permission.service';

@Component({
  selector: 'app-permission-gate',
  standalone: true,
  imports: [CommonModule],
  template: '<ng-container *ngIf="hasAccess"><ng-content></ng-content></ng-container>'
})
export class PermissionGateComponent {
  @Input() permissions!: string | string[];

  constructor(private permissionService: PermissionService) {}

  get hasAccess(): boolean {
    if (!this.permissions) return true;
    const perms = Array.isArray(this.permissions) ? this.permissions : [this.permissions];
    return this.permissionService.hasAnyPermission(perms);
  }
}
