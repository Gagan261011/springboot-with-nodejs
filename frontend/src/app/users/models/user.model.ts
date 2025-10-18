export type UserRole = 'ADMIN' | 'MANAGER' | 'VIEWER';
export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
  updatedAt: string;
}

export interface UserRequest {
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
}

export interface UserPatchRequest {
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  role?: UserRole | null;
  status?: UserStatus | null;
}

export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
  sort: string[];
}

export interface UsersQueryParams {
  page: number;
  size: number;
  search?: string | null;
  role?: UserRole | null;
  status?: UserStatus | null;
  sort?: string[];
}
