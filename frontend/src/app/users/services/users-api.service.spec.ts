import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';
import { UserRole, UserStatus } from '../models/user.model';
import { UsersApiService } from './users-api.service';

describe('UsersApiService', () => {
  let service: UsersApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(UsersApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call list endpoint with query params', () => {
    service
      .list({
        page: 0,
        size: 10,
        search: 'ada',
        role: UserRole.ADMIN,
        status: UserStatus.ACTIVE,
        sort: ['firstName,asc']
      })
      .subscribe();

    const req = httpMock.expectOne((request) => request.url === `${environment.apiBaseUrl}/users`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('search')).toBe('ada');
    expect(req.request.params.get('role')).toBe('ADMIN');
    expect(req.request.params.get('status')).toBe('ACTIVE');
    expect(req.request.params.getAll('sort')).toEqual(['firstName,asc']);
    req.flush({
      items: [],
      page: 0,
      size: 10,
      totalItems: 0,
      totalPages: 0,
      sort: ['firstName,asc']
    });
  });
});
