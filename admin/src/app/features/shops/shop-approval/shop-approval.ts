import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-shop-approval',
  styleUrl: './shop-approval.css',
  templateUrl: './shop-approval.html',
})
export class ShopApproval {
  isApproving = false;
  isRejecting = false;
  hasSuperAdminPermission = false;

  onApprove() { this.isApproving = true; }
  onReject() { this.isRejecting = true; }
  canPerformAction() { return this.hasSuperAdminPermission; }
}
