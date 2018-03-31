import { ENTER } from '@angular/cdk/keycodes';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorStateMatcher, MatChipInputEvent, MatDialog } from '@angular/material';
import { MatDialogRef } from '@angular/material/dialog/typings/dialog-ref';
import { AppError } from '../../model/app-error';
import { AppErrorCode } from '../../model/app-error-code';
import { SendEmailService } from '../../service/send-email.service';
import { AlertDialogComponent } from '../alert-dialog/alert-dialog.component';
import { SpinnerDialogComponent } from '../spinner-dialog/spinner-dialog.component';
import { FieldValidator } from '../util/field-validator';
import { FormErrorStateMatcher } from '../util/form-error-state-matcher';

@Component({
  selector: 'send-email-form',
  templateUrl: './send-email.component.html',
  styleUrls: ['./send-email.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SendEmailComponent implements OnInit {
  public isServerUp: boolean;
  public emailForm: FormGroup;
  public sendFormErrorsMatcher: ErrorStateMatcher;
  public chipListConfig;
  public enableEdit: boolean;

  private alertDialogResult: string;
  private fieldValidator: FieldValidator;

  constructor(
    private sendEmailService: SendEmailService,
    private formBuilder: FormBuilder,
    private alert: MatDialog) {

    this.sendFormErrorsMatcher = new FormErrorStateMatcher();
    this.fieldValidator = new FieldValidator();

    this.chipListConfig = {
      visible: true,
      selectable: false,
      removable: true,
      addOnBlur: true,
      separatorKeysCodes: [ENTER]
    };

    this.enableEdit = false;
  }

  public ngOnInit() {
    this.initForm();
    this.checkServerStatus(false);
  }

  private initForm() {
    this.emailForm = this.formBuilder.group({
      sender: this.formBuilder.array([], ),
      senderInput: ['', [Validators.required, Validators.email]],

      recipients: this.formBuilder.array([]),
      recipientsInput: ['', [Validators.required, Validators.email]],

      ccs: this.formBuilder.array([]),
      ccsInput: ['', this.fieldValidator.blankOrEmail],

      bccs: this.formBuilder.array([]),
      bccsInput: ['', this.fieldValidator.blankOrEmail],

      // Max length as recommended in SendGrid API, which refers to:
      // https://stackoverflow.com/questions/1592291/what-is-the-email-subject-length-limit#answer-1592310
      subject: ['', Validators.maxLength(78)],

      content: ['', [this.fieldValidator.blank]]
    });
  }

  public checkServerStatus(showSpinner: boolean) {
    let spinner;
    if (showSpinner) {
      spinner = this.openSpinner();
    }

    this.sendEmailService.checkServerStatus().subscribe(data => {
      this.isServerUp = true;

      if (spinner) {
        spinner.close();
      }
    }, err => {
      this.isServerUp = false;
      if (spinner) {
        spinner.close();
      }

      this.openErrorDialog(`Unfortunately server is unavailable, please try again later.`, false)
    });
  }

  public reset() {
    this.emailForm.reset();

    this.emailForm.controls['senderInput'].enable();

    this.clearChipListField('sender');
    this.clearChipListField('recipients');
    this.clearChipListField('ccs');
    this.clearChipListField('bccs');

    this.enableEdit = false;
  }

  private clearChipListField(fieldName: string) {
    const formArray = this.emailForm.controls[fieldName] as FormArray;
    while (formArray.length !== 0) {
      formArray.removeAt(0)
    }
  }

  private resetInEditMode(enableEdit: boolean) {
    this.emailForm.reset(this.emailForm.value);
    this.enableEdit = enableEdit;
  }

  public edit() {
    this.enableEdit = false;

    Object.keys(this.emailForm.controls).forEach(field => {
      const formControl = this.emailForm.controls[field];
      formControl.markAsTouched({ onlySelf: true });
      formControl.updateValueAndValidity();

      // Special case for 'Required' Chip input, need to run the Required Chip validator to get the real validity
      if (field === 'senderInput') {
        const formArray = this.emailForm.controls['senders'] as FormArray;
        this.updateRequiredChipValidity(formControl, formArray);
      }

      if (field === 'recipientsInput') {
        const formArray = this.emailForm.controls['recipients'] as FormArray;
        this.updateRequiredChipValidity(formControl, formArray);
      }
    });
  }

  public submit() {
    const spinner = this.openSpinner();

    this.sendEmailService.sendEmail(this.packageData()).subscribe(
      (response) => {
        spinner.close('Closed');
        this.openSuccessDialog();
      },
      (error: AppError) => {
        if (error.status === AppErrorCode.ServerGatewayDown) {
          this.isServerUp = false;
        }

        spinner.close('Closed');
        this.openErrorDialog(error.message, true);
      }
    );
  }

  public disableSubmit(): boolean {
    const isFormReallyInvalid = this.emailForm.invalid
      || this.isFieldFormArrayEmpty('sender')
      || this.isFieldFormArrayEmpty('recipients');
    return !this.isServerUp || (isFormReallyInvalid || this.enableEdit)
  }

  private isFieldFormArrayEmpty(fieldName: string): boolean {
    return (this.emailForm.controls[fieldName].value as FormArray).length < 1;
  }

  private packageData(): any {
    const packedData = Object.assign({}, this.emailForm.value);

    // Fields used for front-end only should not be passed
    delete packedData.senderInput;
    delete packedData.recipientsInput;
    delete packedData.ccsInput;
    delete packedData.bccsInput;

    return packedData;
  }

  public openSpinner(): MatDialogRef<SpinnerDialogComponent> {
    return this.alert.open(SpinnerDialogComponent, {
      panelClass: 'transparent'
    });
  }

  public openSuccessDialog() {
    const alertRef = this.alert.open(AlertDialogComponent, {
      width: '400px',
      data: {
        title: `Email is sent`,
        message: `Discard the email content?`,
        closeButton: `Discard`,
        cancelButton: `Keep`
      },
      autoFocus: false
    });

    alertRef.afterClosed().subscribe(result => {
      this.alertDialogResult = result;

      if (this.alertDialogResult
          && AlertDialogComponent.RESULT_CONFIRM.toLowerCase() === this.alertDialogResult.toLowerCase()) {

        this.reset(); // Discard email content
      } else {

        this.resetInEditMode(true); // Keep email content
      }
    });
  }

  public openErrorDialog(errorMessage: string, enableEdit: boolean) {
    const alertRef = this.alert.open(AlertDialogComponent, {
      width: '400px',
      data: {
        title: `Encountered error`,
        message: (errorMessage ? errorMessage : `Please try again`),
        closeButton: `Close`,
        cancelButton: null
      },
      autoFocus: false
    });

    alertRef.afterClosed().subscribe(result => {
      this.resetInEditMode(enableEdit);
    });
  }

  private updateRequiredChipValidity(chipInput: AbstractControl, chipFormArray: FormArray): void {
      if (this.isRequiredChipValid(chipInput, chipFormArray)) {
        chipInput.setErrors(null);
      } else {
        chipInput.markAsTouched({ onlySelf: true });
      }
  }

  private isRequiredChipValid(chipInput: AbstractControl, chipFormArray: FormArray): boolean {
    return chipInput.invalid && chipInput.errors['required'] && chipFormArray.value.length > 0;
  }

  addChip(event: MatChipInputEvent, fieldName: string): void {
    const formArray = this.emailForm.controls[fieldName] as FormArray;
    const chipInput = this.emailForm.controls[fieldName + 'Input'];

    if (this.isRequiredChipValid(chipInput, formArray)) {
      chipInput.setErrors(null);
      return;
    } else if (chipInput.errors) {
      // Value does not pass Input validator, no need to continue checking validation
      return;
    }

    const value = event.value;
    if (value === '') {
      return;
    }

    if (formArray && ((value || '').trim().length > 0)) {

      // Special case for sender, only allow one value
      if ('sender' === fieldName.toLowerCase() && formArray.value.length >= 1) {
        chipInput.setErrors({
          single: {valid: false}
        });

      // Check for too many entries
      } else if (formArray.value.length === 1000) {
        chipInput.setErrors({
          maxsize: {valid: false}
        });

      // Check for duplicate entries
      } else if (formArray.value.indexOf(value) >= 0) {
        chipInput.setErrors({
          duplicate: {valid: false}
        });

      // Pass validations, add the chip
      } else {
        const trimmedValue = value.trim();

        this.doAddChip(trimmedValue, event, chipInput, formArray);

        // If the field is Sender, then disable the field as only one sender is allowed
        if('sender' === fieldName.toLowerCase()) {
          chipInput.disable();
        }
      }
    }
  }

  private doAddChip(value: string, event: MatChipInputEvent, chipInput: AbstractControl, chipFormArray: FormArray) {
    chipFormArray.push(this.formBuilder.control(value));

    if (event.input) {
      event.input.value = '';
      chipInput.setValue('');
      chipInput.markAsPristine();

      // Need to reset the errors because setting the value with blank above will cause validator to run
      // and fails the Required validator for the Input control.
      chipInput.setErrors(null);
    }
  }

  removeChip(index: any, fieldName: string): void {
    const chipFormArray = this.emailForm.controls[fieldName] as FormArray;
    const chipInput = this.emailForm.controls[fieldName + 'Input'];

    chipInput.setErrors(null);

    if (chipFormArray && index >= 0) {
      chipFormArray.removeAt(index);
      this.updateRequiredChipValidity(chipInput, chipFormArray);
      chipInput.updateValueAndValidity();
    }

    if('sender' === fieldName.toLowerCase()) {
      chipInput.enable();
    }
  }
}
