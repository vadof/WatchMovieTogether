import {Component, OnInit} from '@angular/core';
import {FriendService} from "../../../services/friend.service";
import {User} from "../../../models/User";
import {GroupService} from "../../../services/group.service";
import {Group} from "../../../models/Group";

@Component({
  selector: 'app-my-friends',
  templateUrl: './my-friends.component.html',
  styleUrls: ['./my-friends.component.scss']
})
export class MyFriendsComponent implements OnInit {

  friends: User[] = []
  groups: Group[] = []
  clickedUser: any = undefined;
  filteredGroups: Group[] = []

  constructor(
    private friendService: FriendService,
    private groupService: GroupService
  ) {}

  ngOnInit(): void {
    this.friendService.getFriends().then(res => this.friends = res)
    this.groupService.getAllGroups().then(res => this.groups = res)
  }

  public removeFromFriends(user: User) {
    this.friendService.removeFriend(user);
    this.friends.splice(this.friends.indexOf(user), 1);
  }

  public setClickedUser(user: any) {
    this.clickedUser = user
    this.filteredGroups = this.groups.filter(
      group => !group.users.some(
        u => u.username === this.clickedUser?.username
      ))
  }

  public addUserToGroup(group: Group) {
    if (this.clickedUser) {
      this.groupService.addUserToGroup(group, this.clickedUser)
    }

    this.filteredGroups.splice(this.filteredGroups.indexOf(group), 1);
  }
}
