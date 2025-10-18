import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';

import { SnackbarService } from '../../../core/services/snackbar.service';
import { User } from '../../models/user.model';
import { UsersApiService } from '../../services/users-api.service';

@Component({
  selector: 'app-user-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserViewComponent implements OnInit, OnDestroy {
  user?: User;
  isLoading = false;
  private readonly destroy$ = new Subject<void>();

  constructor(
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
        this.loadUser(id);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onEdit(user: User): void {
    this.router.navigate(['/users', user.id, 'edit']);
  }

  onBack(): void {
    this.router.navigate(['/users']);
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
        next: (user) => {
          this.user = user;
          this.cdr.markForCheck();
        },
        error: () => {
          this.snackbar.error('User not found or already removed.');
          this.router.navigate(['/users']);
          this.cdr.markForCheck();
        }
      });
  }
}
