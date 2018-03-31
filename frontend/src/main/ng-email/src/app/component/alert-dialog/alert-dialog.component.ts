import { Component, Inject, ViewEncapsulation } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'alert-dialog',
  templateUrl: './alert-dialog.component.html',
  styleUrls: ['./alert-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AlertDialogComponent {
  public static readonly RESULT_CONFIRM: string = "Confirm";
  public static readonly RESULT_CANCEL: string = "Cancel";

  constructor(
    private alertRef: MatDialogRef<AlertDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onCloseConfirm() {
    this.alertRef.close(AlertDialogComponent.RESULT_CONFIRM);
  }

  onCloseCancel() {
    this.alertRef.close(AlertDialogComponent.RESULT_CANCEL);
  }
}
