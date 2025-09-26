import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AppConstants} from '../constants/app-constants';
import {Observable} from 'rxjs';
import {User} from '../interfaces/user';
import {Page, QueryParams} from '../interfaces/page';

@Injectable({
  providedIn: 'root'
})
/**
 * Service responsible for interacting with the backend User API.
 */
export class UserService {

  /**
   * Base API URL for all requests, {@link AppConstants.apiUrl}.
   */
  baseAPIHref: string = AppConstants.apiUrl;

  constructor(private httpClient: HttpClient) {
  }

  /**
   * Retrieves a paginated list of users from the backend.
   *
   * @param endpoint endpoint path
   * @param queryParams Query parameters for pagination, sorting and filtering.
   * @returns Observable that emits a {@link Page} of {@link User}.
   */
  getUsers(endpoint: string, queryParams: QueryParams): Observable<Page<User>> {
    let params = new HttpParams()
      .set('page', queryParams.page.toString())
      .set('size', queryParams.size.toString());

    if (queryParams.sort)
      params = params.set('sort', queryParams.sort);
    if (queryParams.name)
      params = params.set('name', queryParams.name);
    if (queryParams.surname)
      params = params.set('surname', queryParams.surname);

    return this.httpClient.get<Page<User>>(`${this.baseAPIHref}${endpoint}`, {params});
  }

  /**
   * Adds a new user.
   *
   * @param endpoint endpoint path
   * @param user User payload to be created.
   * @returns Observable that emits the created {@link User}.
   */
  addUser(endpoint: string, user: User): Observable<User> {
    return this.httpClient.post<User>(`${this.baseAPIHref}${endpoint}`, user);
  }

  /**
   * Retrieves a user by its unique ID.
   *
   * @param endpoint endpoint path
   * @param userId id of the user.
   * @returns Observable that emits the requested {@link User}.
   */
  getUserById(endpoint: string, userId: number): Observable<User> {
    return this.httpClient.get<User>(`${this.baseAPIHref}${endpoint}/${userId}`);
  }

  /**
   * Updates an existing user.
   *
   * @param endpoint endpoint path
   * @param userId id of the user to update.
   * @param user Updated user payload.
   * @returns Observable that emits the updated {@link User}.
   */
  updateUser(endpoint: string, userId: number, user: User): Observable<User> {
    return this.httpClient.put<User>(`${this.baseAPIHref}${endpoint}/${userId}`, user);
  }

  /**
   * Uploads a CSV file to import users.
   *
   * @param endpoint endpoint path
   * @param file file to upload.
   * @returns Observable that emits the BE response
   */
  uploadUsersCsv(endpoint: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.httpClient.post<any>(`${this.baseAPIHref}${endpoint}`, formData);
  }

  /**
   * Deletes a user by its ID.
   *
   * @param endpoint endpoint path
   * @param userId id of the user to delete.
   * @returns Observable that emits the deleted {@link User}
   */
  deleteUserById(endpoint: string, userId: number): Observable<User> {
    return this.httpClient.delete<User>(`${this.baseAPIHref}${endpoint}/${userId}`);
  }
}
