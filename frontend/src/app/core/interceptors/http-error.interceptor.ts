import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { SnackbarService } from '../services/snackbar.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private snackbar: SnackbarService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const message = this.buildErrorMessage(error);
        this.snackbar.error(message);
        return throwError(() => error);
      })
    );
  }

  private buildErrorMessage(error: HttpErrorResponse): string {
    if (error.error?.message) {
      return error.error.message;
    }
    if (error.status === 0) {
      return 'Unable to reach the server. Check your network connection.';
    }
    return error.message || 'An unexpected error occurred.';
  }
}

