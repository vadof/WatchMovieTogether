import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import {TokenStorageService} from "./token-storage.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private storage: TokenStorageService
    ) {}

  canActivate(): boolean {
    if (this.storage.tokenIsValid()) {
      return true;
    } else {
      this.router.navigate(['login']);
      return false;
    }
  }
}
