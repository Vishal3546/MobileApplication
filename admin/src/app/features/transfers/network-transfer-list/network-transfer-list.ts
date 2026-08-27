import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-network-transfer-list',
  styleUrl: './network-transfer-list.css',
  templateUrl: './network-transfer-list.html',
})
export class NetworkTransferList {
  canRequest = false;
  status = '';

  requestTransfer() {
    if (this.canRequest) this.status = 'REQUESTED';
  }
  checkStatus(status: string) { return this.status === status; }
}
