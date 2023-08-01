import { Component } from '@angular/core';
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss', '../../../styles.scss']
})
export class NavbarComponent {
  username: string;

  constructor(private storage: TokenStorageService,
              private router: Router
  ) {
    this.username = storage.getUsername();
  }

  public logout() {
    this.storage.signOut();
    this.router.navigate(['/login'])
  }
}
