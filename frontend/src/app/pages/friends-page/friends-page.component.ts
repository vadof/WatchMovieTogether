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

  friends: User[] = []
  public disabledAddFriendButtons: boolean[] = [];

  currentSection = Section.MY_FRIENDS

  usernameInput: string = ''
  showFoundFriends: boolean = false
  foundFriends: User[] = []

  constructor(
    private friendService: FriendService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {

  }

  public findFriends(): void {
    if (this.usernameInput.length > 0
      && this.tokenStorage.getUsername() !== this.usernameInput) {
      this.showFoundFriends = false
      this.friendService.findFriends(this.usernameInput)
        .then(res => {
          this.showFoundFriends = true;
          this.foundFriends = res;
        });
    }
    this.usernameInput = ''
  }

  public addFriend(user: User, index: number) {
    this.disabledAddFriendButtons[index] = true;
    this.friendService.sendFriendRequest(user);
  }

  protected readonly Section = Section;
}
