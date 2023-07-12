import { Component } from '@angular/core';
import {User} from "../../../models/User";
import {FriendService} from "../../../services/friend.service";
import {TokenStorageService} from "../../../auth/token-storage.service";

@Component({
  selector: 'app-search-friends',
  templateUrl: './search-friends.component.html',
  styleUrls: ['./search-friends.component.scss']
})
export class SearchFriendsComponent {

  usernameInput: string = ''
  foundFriends: User[] = []
  disabledAddFriendButtons: boolean[] = [];
  showFoundFriends = false

  constructor(
    private friendService: FriendService,
    private tokenStorage: TokenStorageService
  ) {}

  public searchFriends(): void {
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
}
