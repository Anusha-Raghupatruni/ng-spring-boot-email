import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  MatButtonModule,
  MatCardModule,
  MatDialogModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule, MatListModule,
  MatProgressSpinnerModule
} from '@angular/material';
import { MatChipsModule } from '@angular/material/chips'
import { MatToolbarModule } from '@angular/material/toolbar';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { AlertDialogComponent } from './component/alert-dialog/alert-dialog.component';
import { SendEmailComponent } from './component/send-email/send-email.component';
import { SpinnerDialogComponent } from './component/spinner-dialog/spinner-dialog.component';
import { SendEmailService } from './service/send-email.service';

@NgModule({
  declarations: [
    AppComponent,
    SendEmailComponent,
    AlertDialogComponent,
    SpinnerDialogComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    MatToolbarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatListModule
  ],
  exports: [
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    MatToolbarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatListModule
  ],
  entryComponents: [AlertDialogComponent, SpinnerDialogComponent],
  providers: [SendEmailService],
  bootstrap: [AppComponent]
})

export class AppModule { }
