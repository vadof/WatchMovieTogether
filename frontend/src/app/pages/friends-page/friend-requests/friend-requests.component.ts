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
      });
  }

  public replyToFriendRequestRequest(user: User, accept: boolean) {
    this.friendService.replyToFriendRequest(user, accept)
    this.friendRequests.splice(this.friendRequests.indexOf(user), 1);
  }
}
