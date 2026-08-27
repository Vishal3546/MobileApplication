import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-shop-list',
  styleUrl: './shop-list.css',
  templateUrl: './shop-list.html',
})
export class ShopList {
  pageIndex = 0;
  pageSize = 10;
  filter: any = {};
  hasEditPermission = false;

  onPageChange(event: any) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }
  onFilterChange(filter: any) {
    this.filter = filter;
  }
  canEditShop() { return this.hasEditPermission; }
}
