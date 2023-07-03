import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {LoginRequest} from "../components/login-form/login-request";
import {AuthResponse} from "./auth-response";
import {Observable} from "rxjs";
import {RegisterRequest} from "../components/register-form/register-request";

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

  public login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.loginUrl, request, httpOptions);
  }

  public register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.registerUrl, request, httpOptions);
  }
}
