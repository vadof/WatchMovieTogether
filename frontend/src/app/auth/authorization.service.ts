import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthRequest} from "./auth-request";
import {AuthResponse} from "./auth-response";
import {Observable} from "rxjs";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {

  private loginUrl = 'http://localhost:8080/api/auth/login'
  private registerUrl = 'http://localhost:8080/api/auth/register'

  constructor(private http: HttpClient) {
  }

  public login(username: string, password: string): Observable<AuthResponse> {
    let request = new AuthRequest(username, password);
    return this.http.post<AuthResponse>(this.loginUrl, request, httpOptions);
  }
}
