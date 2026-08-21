import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PermissionService } from '../../../core/auth/permission.service';

@Component({
  selector: 'app-masked-imei',
  standalone: true,
  imports: [CommonModule],
  template: '<span>{{ displayImei }}</span>'
})
export class MaskedImeiComponent implements OnInit {
  @Input() fullImei?: string;
  @Input() maskedImei!: string; // From backend

  displayImei: string = '';

  constructor(private permissionService: PermissionService) {}

  ngOnInit() {
    if (this.fullImei && this.permissionService.hasPermission('VIEW_FULL_DEVICE_IMEI')) {
      this.displayImei = this.fullImei;
    } else {
      this.displayImei = this.maskedImei || '***';
    }
  }
}
