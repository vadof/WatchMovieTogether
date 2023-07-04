import {Component, OnInit} from '@angular/core';
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss', '../../../assets/styles/main.scss']
})
export class MainPageComponent {

  constructor(
    private storage: TokenStorageService,
    private router: Router
  ) {}

}
