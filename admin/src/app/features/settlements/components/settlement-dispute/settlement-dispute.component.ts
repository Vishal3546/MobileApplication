import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SettlementService } from '../../services/settlement.service';

@Component({
  selector: 'app-settlement-dispute',
  templateUrl: './settlement-dispute.component.html',
  styleUrls: ['./settlement-dispute.component.scss']
})
export class SettlementDisputeComponent {
  @Input() settlementId!: string;
  @Input() canResolve = false;
  @Output() disputeSuccess = new EventEmitter<void>();
  
  disputeForm: FormGroup;
  resolveForm: FormGroup;
  isSubmitting = false;
  disputeReasons = ['AMOUNT_MISMATCH', 'TRANSFER_MISMATCH', 'PAYMENT_MISMATCH', 'OTHER'];

  constructor(
    private fb: FormBuilder,
    private settlementService: SettlementService
  ) {
    this.disputeForm = this.fb.group({
      reason: ['AMOUNT_MISMATCH', Validators.required],
      claimedAmount: [''],
    });

    this.resolveForm = this.fb.group({
      resolution: ['', Validators.required]
    });
  }

  onRaiseDispute(): void {
    if (this.disputeForm.valid) {
      this.isSubmitting = true;
      this.settlementService.raiseDispute(this.settlementId, this.disputeForm.value).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.disputeForm.reset({ reason: 'AMOUNT_MISMATCH' });
          this.disputeSuccess.emit();
        },
        error: () => {
          this.isSubmitting = false;
        }
      });
    }
  }

  onResolveDispute(disputeId: string): void {
    if (this.resolveForm.valid) {
      this.isSubmitting = true;
      this.settlementService.resolveDispute(disputeId, this.resolveForm.value).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.resolveForm.reset();
          this.disputeSuccess.emit();
        },
        error: () => {
          this.isSubmitting = false;
        }
      });
    }
  }
}
