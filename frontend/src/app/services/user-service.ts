import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {FormControl, ɵValue} from "@angular/forms";
import {LoginFormComponent} from "../components/login-form/login-form.component";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/api/user'

  constructor(
    private http: HttpClient
  ) { }

  loginUser(username: string, password: string): Observable<string> {
    const loginObj = {username, password}
    const url = this.apiUrl + '/login'
    const headers = new HttpHeaders({'Content-Type': 'application/json'})

    // @ts-ignore
    return this.http.post<string>(url, loginObj, { headers, responseType: 'text' });
  }
}
