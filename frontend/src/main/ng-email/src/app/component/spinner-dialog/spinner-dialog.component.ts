import { Component, ViewEncapsulation } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'spinner-dialog',
  templateUrl: './spinner-dialog.component.html',
  styleUrls: ['./spinner-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SpinnerDialogComponent {
  public static readonly RESULT_CONFIRM: string = "Close";

  constructor(
    private alertRef: MatDialogRef<SpinnerDialogComponent>){ }

  onCloseConfirm() {
    this.alertRef.close(SpinnerDialogComponent.RESULT_CONFIRM);
  }
}
