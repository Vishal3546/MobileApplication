import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-network-inventory-list',
  styleUrl: './network-inventory-list.css',
  templateUrl: './network-inventory-list.html',
})
export class NetworkInventoryList {
  itemsVisible = '';
  isImeiMasked = true;
  hasPermission = false;

  checkNetworkVisibility(visibility: string) { return this.itemsVisible === visibility; }
  checkMasking() { return this.isImeiMasked; }
  canRequest() { return this.hasPermission; }
}
