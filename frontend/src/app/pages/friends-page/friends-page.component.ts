import {Component, OnInit} from '@angular/core';
import {FriendService} from "../../services/friend.service";
import {User} from "../../models/User";
import {TokenStorageService} from "../../auth/token-storage.service";
import {Section} from "./Section";
import {FriendRequestsComponent} from "./friend-requests/friend-requests.component";

@Component({
  selector: 'app-friends-page',
  templateUrl: './friends-page.component.html',
  styleUrls: ['./friends-page.component.scss']
})
export class FriendsPageComponent implements OnInit {

  currentSection = Section.MY_FRIENDS
  friendRequests: number = 0

  constructor(
    private friendService: FriendService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {
    this.friendService.getFriendRequests().then(
      res => this.friendRequests = res.length)
  }

  protected readonly Section = Section;
}
