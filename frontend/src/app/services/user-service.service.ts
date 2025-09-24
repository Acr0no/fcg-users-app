import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppConstants} from '../constants/app-constants';

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {

  baseAPIHref: string = AppConstants.apiUrl;

  constructor(private http: HttpClient) {}

  getUsers() {
    console.log(this.baseAPIHref);
  }
}
