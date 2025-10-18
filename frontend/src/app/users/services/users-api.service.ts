import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  PageResponse,
  User,
  UserPatchRequest,
  UserRequest,
  UsersQueryParams
} from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UsersApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient) {}

  list(params: UsersQueryParams): Observable<PageResponse<User>> {
    let httpParams = new HttpParams()
      .set('page', params.page)
      .set('size', params.size);

    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    if (params.role) {
      httpParams = httpParams.set('role', params.role);
    }
    if (params.status) {
      httpParams = httpParams.set('status', params.status);
    }
    if (params.sort && params.sort.length > 0) {
      params.sort.forEach((sort) => {
        httpParams = httpParams.append('sort', sort);
      });
    }

    return this.http.get<PageResponse<User>>(this.baseUrl, { params: httpParams });
  }

  get(id: string): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  create(payload: UserRequest): Observable<User> {
    return this.http.post<User>(this.baseUrl, payload);
  }

  update(id: string, payload: UserRequest): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, payload);
  }

  patch(id: string, payload: UserPatchRequest): Observable<User> {
    return this.http.patch<User>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

