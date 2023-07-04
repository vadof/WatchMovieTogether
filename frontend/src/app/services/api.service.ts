import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {TokenStorageService} from "../auth/token-storage.service";
import {Observable} from "rxjs";
import {error} from "@angular/compiler-cli/src/transformers/util";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private API_URL = 'http://localhost:8080/api'
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.storage.getToken()}`
    })
  }

  constructor(
    private http: HttpClient,
    private storage: TokenStorageService
  ) { }

  public sendPostRequest(url: string, body: any): Observable<any> {
    return this.http.post(this.API_URL + url, body, this.httpOptions)
  }

  // public sendPostRequest(url: string, body: any): Observable<any> {
  //   console.log(this.httpOptions.headers.get('Authorization'))
  //   return this.http.post(this.API_URL + url, body, {headers: this.httpOptions.headers})
  // }

  // public sendGetRequest(url: string) {
  //   this.http.get(this.API_URL + url, this.httpOptions).subscribe(req => {
  //     console.log(req)}, error => {})
  // }

  // public sendGetRequest(url: string) {
  //
  // }
}