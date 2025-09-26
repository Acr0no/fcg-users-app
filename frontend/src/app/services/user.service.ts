import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AppConstants} from '../constants/app-constants';
import {Observable} from 'rxjs';
import {User} from '../interfaces/user';
import {Page, QueryParams} from '../interfaces/page';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  baseAPIHref: string = AppConstants.apiUrl;

  constructor(private httpClient: HttpClient) {
  }


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

  addUser(endpoint: string, user: User): Observable<User> {
    return this.httpClient.post<User>(`${this.baseAPIHref}${endpoint}`, user);
  }

  getUserById(endpoint: string, userId: number): Observable<User> {
    return this.httpClient.get<User>(`${this.baseAPIHref}${endpoint}/${userId}`);
  }

  updateUser(endpoint: string, userId: number, user: User): Observable<User> {
    return this.httpClient.put<User>(`${this.baseAPIHref}${endpoint}/${userId}`, user);
  }


  uploadUsersCsv(endpoint: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append("file", file, file.name);
    return this.httpClient.post<any>(`${this.baseAPIHref}${endpoint}`, formData);
  }
}


