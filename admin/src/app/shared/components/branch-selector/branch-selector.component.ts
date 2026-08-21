import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BranchScopeService } from '../../../core/services/branch-scope.service';
import { PermissionService } from '../../../core/auth/permission.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-branch-selector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: '<select *ngIf="canSelectBranch" [(ngModel)]="selectedBranch" (change)="onBranchChange()"><option [ngValue]="null">All Branches</option><option value="b1">Branch 1</option></select>'
})
export class BranchSelectorComponent implements OnInit {
  canSelectBranch = false;
  selectedBranch: string | null = null;
  constructor(private branchScope: BranchScopeService, private perm: PermissionService) {}
  ngOnInit() {
    this.canSelectBranch = this.perm.hasAnyPermission(['SUPER_ADMIN', 'VIEW_BRANCHES']);
    this.selectedBranch = this.branchScope.getCurrentBranchId();
  }
  onBranchChange() {
    this.branchScope.setBranchScope(this.selectedBranch);
  }
}
