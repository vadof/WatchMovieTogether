import {Component, OnInit} from '@angular/core';
import {FriendService} from "../../services/friend.service";
import {User} from "../../models/User";
import {TokenStorageService} from "../../auth/token-storage.service";
import {Section} from "./Section";

@Component({
  selector: 'app-friends-page',
  templateUrl: './friends-page.component.html',
  styleUrls: ['./friends-page.component.scss']
})
export class FriendsPageComponent implements OnInit {

  currentSection = Section.FRIEND_REQUESTS

  constructor(
    private friendService: FriendService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {

  }

  protected readonly Section = Section;
}
