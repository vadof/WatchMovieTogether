import { Component } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss', '../styles/main.scss']
})
export class NavbarComponent {
  username: string = 'vadoff'
}
