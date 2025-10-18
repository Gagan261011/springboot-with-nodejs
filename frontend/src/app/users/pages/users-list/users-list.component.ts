import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { SnackbarService } from '../../../core/services/snackbar.service';
import { PageResponse, User, UserRole, UserStatus } from '../../models/user.model';
import { UsersApiService } from '../../services/users-api.service';

interface TableState {
  pageIndex: number;
  pageSize: number;
  sort?: Sort;
}

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersListComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns = ['firstName', 'lastName', 'email', 'role', 'status', 'updatedAt', 'actions'];
  data: User[] = [];
  totalItems = 0;
  isLoading = false;

  searchControl = new FormControl<string>('', { nonNullable: true });
  roleControl = new FormControl<UserRole | null>(null);
  statusControl = new FormControl<UserStatus | null>(null);

  readonly roleOptions: { label: string; value: UserRole }[] = [
    { label: 'Admin', value: 'ADMIN' },
    { label: 'Manager', value: 'MANAGER' },
    { label: 'Viewer', value: 'VIEWER' }
  ];

  readonly statusOptions: { label: string; value: UserStatus }[] = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' }
  ];

  private readonly destroy$ = new Subject<void>();
  private tableState: TableState = { pageIndex: 0, pageSize: 10 };

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private readonly usersApi: UsersApiService,
    private readonly snackbar: SnackbarService,
    private readonly dialog: MatDialog,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.listenToFilters();
  }

  ngAfterViewInit(): void {
    this.sort.sortChange.pipe(takeUntil(this.destroy$)).subscribe((sort) => {
      this.tableState.sort = sort.direction ? sort : undefined;
      this.paginator.firstPage();
      this.loadUsers();
    });

    this.paginator.page.pipe(takeUntil(this.destroy$)).subscribe((pageEvent: PageEvent) => {
      this.tableState.pageIndex = pageEvent.pageIndex;
      this.tableState.pageSize = pageEvent.pageSize;
      this.loadUsers();
    });

    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCreateUser(): void {
    this.router.navigate(['/users/new']);
  }

  onViewUser(user: User): void {
    this.router.navigate(['/users', user.id, 'view']);
  }

  onEditUser(user: User): void {
    this.router.navigate(['/users', user.id, 'edit']);
  }

  onDeleteUser(user: User): void {
    const dialogData: ConfirmDialogData = {
      title: 'Delete user',
      message: `Are you sure you want to deactivate ${user.firstName} ${user.lastName}?`,
      confirmText: 'Deactivate'
    };
    this.dialog
      .open(ConfirmDialogComponent, { data: dialogData, width: '360px' })
      .afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.usersApi.delete(user.id).subscribe({
            next: () => {
              this.snackbar.success('User marked as inactive');
              this.loadUsers();
            },
            error: () => {
              // handled by interceptor
            }
          });
        }
      });
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.roleControl.setValue(null);
    this.statusControl.setValue(null);
  }

  trackByUserId(_: number, item: User): string {
    return item.id;
  }

  private listenToFilters(): void {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => this.resetAndLoad());

    this.roleControl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => this.resetAndLoad());
    this.statusControl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => this.resetAndLoad());
  }

  private resetAndLoad(): void {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.tableState.pageIndex = 0;
    this.loadUsers();
  }

  private loadUsers(): void {
    this.isLoading = true;
    this.cdr.markForCheck();
    const query = {
      page: this.tableState.pageIndex,
      size: this.tableState.pageSize,
      search: this.searchControl.value?.trim() || undefined,
      role: this.roleControl.value || undefined,
      status: this.statusControl.value || undefined,
      sort: this.buildSortParams()
    };

    this.usersApi.list(query).subscribe({
      next: (response: PageResponse<User>) => {
        this.data = response.items;
        this.totalItems = response.totalItems;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  private buildSortParams(): string[] | undefined {
    if (!this.tableState.sort || !this.tableState.sort.direction) {
      return ['updatedAt,desc'];
    }
    return [`${this.tableState.sort.active},${this.tableState.sort.direction}`];
  }
}
