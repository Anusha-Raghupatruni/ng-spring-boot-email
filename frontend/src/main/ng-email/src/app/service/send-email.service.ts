import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import 'rxjs/add/observable/throw';
import { Observable } from 'rxjs/Observable';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';
import { catchError, tap } from 'rxjs/operators';
import { AppError } from '../model/app-error';
import { AppErrorCode } from '../model/app-error-code';
import { Email } from '../model/email';
import { Person } from '../model/person';

@Injectable()
export class SendEmailService {
  private static readonly ENDPOINT_BASE_URL: string = (process.env.MAIL_SERVER_URL || '') + '/email/v1';

  constructor(private http: HttpClient) { }

  checkServerStatus(): Observable<Object> {
    const url = SendEmailService.ENDPOINT_BASE_URL + '/status';
    return this.http.get<Object>(url)
      .pipe(
        tap(data => {
          console.log(`checkServerStatus returned: ${JSON.stringify(data)}`);
        }),
        catchError(this.errorHandler('checkServerStatus', url))
      );
  }

  sendEmail(formData: any): Observable<HttpResponse<Object>> {
    formData.sender = this.createPerson(formData.sender);
    formData.recipients = this.createPerson(formData.recipients);
    formData.ccs = this.createPerson(formData.ccs);
    formData.bccs = this.createPerson(formData.bccs);

    const url = SendEmailService.ENDPOINT_BASE_URL + '/send';
    return this.http.post(url, this.createEmail(formData), {observe: 'response'})
      .pipe(
        tap(data => {
          console.log(`sendEmail returned: ${JSON.stringify(data)}`)
        }),
        catchError(this.errorHandler('sendEmail', url))
      );
  }

  private createPerson(emails: string[]) {
    return emails.map((email) => {
      return new Person(email);
    });
  }

  private createEmail(jsonData: any): Email {
    return new Email().map(jsonData)
  }

  private errorHandler(action: string, url: string) {
    return (err: any) => {
      console.log(`Error in ${action} for ${url}`, err);

      if (err instanceof HttpErrorResponse) {
        return this.handleHttpError(err);
      } else {
        console.log(`Other error - name: ${err.name}, message: ${err.message}`);
        return this.throwError(
          AppErrorCode.AppInternal,
          `Application error during email submission. Please contact support with error details.`);
      }
    }
  }

  private handleHttpError(err: HttpErrorResponse): ErrorObservable {
    console.log(`HTTP error - status: ${err.status}, text: ${err.statusText}, message: ${err.message}`);
    if (err.error.data) {
      console.log(`Email server message: ${err.error.data['message']}`);
    }

    const appErrorCode = this.getAppErrorCodeByHttpStatus(err.status);
    switch (appErrorCode) {
      case AppErrorCode.ServerGatewayDown:
        return this.throwError(
          appErrorCode, `Server is down. Please try again later.`);
      case AppErrorCode.BadRequest:
        return this.throwError(
          appErrorCode, `Email was not accepted. Please check that all email addresses are valid. 
            If problem persist, please contact support with error details.`);
      case AppErrorCode.LargePayload:
        return this.throwError(
          appErrorCode, `Email was not accepted as the content is too large. Please try separating into multiple email and try again. 
            If problem persist, please contact support with error details.`);
      case AppErrorCode.ServerInternal:
        return this.throwError(
          appErrorCode, `Internal email server error. Please contact support.`);
      case AppErrorCode.AppInternal:
        return this.throwError(
          appErrorCode, `Application error during email processing. Please contact support.`);
    }
  }

  private getAppErrorCodeByHttpStatus(httpStatus: number): AppErrorCode {
    if (httpStatus === 400) {
      return AppErrorCode.BadRequest
    } else if (httpStatus === 413) {
      return AppErrorCode.LargePayload
    } else if (httpStatus === 504) {
      return AppErrorCode.ServerGatewayDown;
    } else if (httpStatus >= 500) {
      return AppErrorCode.ServerInternal;
    } else {
      return AppErrorCode.AppInternal
    }
  }

  private throwError(appErrorCode: AppErrorCode, message: string): ErrorObservable {
    return Observable.throw(new AppError(
      appErrorCode, message
    ));
  }
}
