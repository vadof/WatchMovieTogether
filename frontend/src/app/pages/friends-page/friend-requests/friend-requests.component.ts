import {Component, OnInit} from '@angular/core';
import {User} from "../../../models/User";
import {FriendService} from "../../../services/friend.service";

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.scss']
})
export class FriendRequestsComponent implements OnInit {

  friendRequests: User[] = []

  constructor(
    private friendService: FriendService,
  ) {}

  ngOnInit(): void {
    this.friendService.getFriendRequests()
      .then(res => {
        this.friendRequests = res;
        // let u: User = {
        //   username: 'dyracho',
        //   firstname: 'Anton',
        //   lastname: 'Petuhov'
        // }
        // this.friendRequests.push(u);
      });
  }

  public replyToFriendRequestRequest(user: User, accept: boolean) {
    this.friendService.replyToFriendRequest(user, accept)

    const index = this.friendRequests.indexOf(user);
    if (index !== -1) {
      this.friendRequests.splice(index, 1);
    }
  }
}
