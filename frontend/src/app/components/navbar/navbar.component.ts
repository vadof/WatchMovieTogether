import { Component } from '@angular/core';
import {TokenStorageService} from "../../auth/token-storage.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss', '../../../styles.scss']
})
export class NavbarComponent {
  username: string;

  constructor(private storage: TokenStorageService) {
    this.username = storage.getUsername();
  }

}
