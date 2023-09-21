import {Component, OnInit} from '@angular/core';
import {TokenStorageService} from "../../auth/token-storage.service";
import {Router} from "@angular/router";
import {ApiService} from "../../services/api.service";
import {LatestMovieRelease} from "../../models/LatestMovieRelease";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss', '../../../styles.scss']
})
export class MainPageComponent implements OnInit {

  lastReleases: LatestMovieRelease[] = [];

  constructor(
    private storage: TokenStorageService,
    private router: Router,
    private api: ApiService
  ) {}

  ngOnInit(): void {
    this.api.sendGetRequest('/films/last-releases').subscribe(
      res => {
        this.lastReleases = res as LatestMovieRelease[]
      }, err => {
        console.log(err)
      }
    )
  }
}
