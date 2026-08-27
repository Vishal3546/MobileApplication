import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SettlementService } from '../../services/settlement.service';

@Component({
  selector: 'app-settlement-payment',
  templateUrl: './settlement-payment.component.html',
  styleUrls: ['./settlement-payment.component.scss']
})
export class SettlementPaymentComponent {
  @Input() settlementId!: string;
  @Input() remainingAmount!: number;
  @Output() paymentSuccess = new EventEmitter<void>();
  
  paymentForm: FormGroup;
  isSubmitting = false;
  paymentModes = ['CASH', 'UPI', 'BANK_TRANSFER', 'OTHER'];
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private settlementService: SettlementService
  ) {
    this.paymentForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      paymentMode: ['BANK_TRANSFER', Validators.required],
      referenceNumber: [''],
    });
  }

  // Generate idempotency key for this intentional payment UI render
  idempotencyKey = crypto.randomUUID();

  onSubmit(): void {
    if (this.paymentForm.valid) {
      if (this.paymentForm.value.amount > this.remainingAmount) {
        this.errorMessage = 'Payment amount cannot exceed remaining balance';
        return;
      }
      
      this.isSubmitting = true;
      this.errorMessage = '';
      
      const payload = {
        ...this.paymentForm.value,
        idempotencyKey: this.idempotencyKey
      };

      this.settlementService.createPayment(this.settlementId, payload).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.paymentForm.reset({ paymentMode: 'BANK_TRANSFER' });
          this.idempotencyKey = crypto.randomUUID(); // reset key for next payment
          this.paymentSuccess.emit();
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.error?.message || 'Failed to record payment. Please try again.';
          // Intentionally NOT resetting the idempotencyKey to allow safe retry of the same intended transaction
        }
      });
    }
  }
}
