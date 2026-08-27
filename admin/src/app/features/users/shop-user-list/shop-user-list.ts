import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-shop-user-list',
  styleUrl: './shop-user-list.css',
  templateUrl: './shop-user-list.html',
})
export class ShopUserList {
  visibilityScope = '';
  hasAccess = false;

  checkVisibility(shop: string) { return this.visibilityScope === shop; }
  checkAccess() { return this.hasAccess; }
}
