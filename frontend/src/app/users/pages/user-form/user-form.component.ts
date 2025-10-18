import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';

import { SnackbarService } from '../../../core/services/snackbar.service';
import { User, UserRequest, UserRole, UserStatus } from '../../models/user.model';
import { UsersApiService } from '../../services/users-api.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserFormComponent implements OnInit, OnDestroy {
  readonly roles: { label: string; value: UserRole }[] = [
    { label: 'Admin', value: 'ADMIN' },
    { label: 'Manager', value: 'MANAGER' },
    { label: 'Viewer', value: 'VIEWER' }
  ];

  readonly statuses: { label: string; value: UserStatus }[] = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' }
  ];

  form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    role: this.fb.nonNullable.control<UserRole>('VIEWER', Validators.required),
    status: this.fb.nonNullable.control<UserStatus>('ACTIVE', Validators.required)
  });

  isEditMode = false;
  isLoading = false;
  isSaving = false;
  private userId?: string;
  private readonly destroy$ = new Subject<void>();

  get title(): string {
    return this.isEditMode ? 'Edit User' : 'Create User';
  }

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly usersApi: UsersApiService,
    private readonly snackbar: SnackbarService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.userId = id;
        this.loadUser(id);
      } else {
        this.isEditMode = false;
        this.userId = undefined;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSubmit(): void {
    if (this.form.invalid || this.isSaving) {
      this.form.markAllAsTouched();
      return;
    }
    const payload: UserRequest = this.form.getRawValue();
    this.isSaving = true;

    const request$ =
      this.isEditMode && this.userId
        ? this.usersApi.update(this.userId, payload)
        : this.usersApi.create(payload);

    request$
      .pipe(
        finalize(() => {
          this.isSaving = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (user: User) => {
          this.snackbar.success(`User ${this.isEditMode ? 'updated' : 'created'} successfully`);
          this.router.navigate(['/users', user.id, 'view']);
          this.cdr.markForCheck();
        },
        error: () => {
          // handled by interceptor
          this.cdr.markForCheck();
        }
      });
  }

  onCancel(): void {
    if (this.isEditMode && this.userId) {
      this.router.navigate(['/users', this.userId, 'view']);
    } else {
      this.router.navigate(['/users']);
    }
  }

  controlHasError(controlName: string, errorCode: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.hasError(errorCode) && (control.dirty || control.touched);
  }

  private loadUser(id: string): void {
    this.isLoading = true;
    this.usersApi
      .get(id)
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (user: User) => {
          this.form.patchValue({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            role: user.role,
            status: user.status
          });
          this.cdr.markForCheck();
        },
        error: () => {
          this.snackbar.error('Unable to load user details.');
          this.router.navigate(['/users']);
        }
      });
  }
}
