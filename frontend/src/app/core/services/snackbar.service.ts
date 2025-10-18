import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class SnackbarService {
  constructor(private snackBar: MatSnackBar) {}

  success(message: string, action = 'Close'): void {
    this.snackBar.open(message, action, {
      duration: 3000,
      panelClass: ['snackbar-success']
    });
  }

  error(message: string, action = 'Dismiss'): void {
    this.snackBar.open(message, action, {
      duration: 5000,
      panelClass: ['snackbar-error']
    });
  }
}

