import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { PermissionService } from '../auth/permission.service';

@Injectable({
  providedIn: 'root'
})
export class BranchScopeService {
  private currentBranchIdSubject = new BehaviorSubject<string | null>(null);
  public currentBranchId$: Observable<string | null> = this.currentBranchIdSubject.asObservable();

  constructor(private authService: AuthService, private permissionService: PermissionService) {
  }

  setBranchScope(branchId: string | null) {
    if (this.permissionService.hasPermission('SUPER_ADMIN')) {
       this.currentBranchIdSubject.next(branchId);
    }
  }

  getCurrentBranchId(): string | null {
    return this.currentBranchIdSubject.value;
  }
}
