import { Injectable } from '@angular/core';

const TOKEN_KEY = 'AuthToken'
const USERNAME_KEY = 'AuthUsername'

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  public saveToken(token: string) {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  public saveUsername(username: string) {
    localStorage.removeItem(USERNAME_KEY);
    localStorage.setItem(USERNAME_KEY, username);
  }

  public signOut() {
    localStorage.clear();
  }

}
