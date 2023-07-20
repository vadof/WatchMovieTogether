import {Component, OnDestroy, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {Group} from "../../models/Group";
import {ActivatedRoute, Router} from "@angular/router";
import {Chat} from "../../models/Chat";
import {TokenStorageService} from "../../auth/token-storage.service";
import {User} from "../../models/User";
import {FriendService} from "../../services/friend.service";
import {MovieService} from "../../services/movie.service";
import {WebSocketService} from "../../services/web-socket.service";

@Component({
  selector: 'app-group-page',
  templateUrl: './group-page.component.html',
  styleUrls: ['./group-page.component.scss']
})
export class GroupPageComponent implements OnInit, OnDestroy {

  // @ts-ignore
  group: Group
  // @ts-ignore
  chat: Chat
  notInGroupUsers: User[] = []
  checkedUsers: User[] = []
  chooseAnotherMovie: boolean = false

  constructor(
    private groupService: GroupService,
    private route: ActivatedRoute,
    private router: Router,
    private tokenStorage: TokenStorageService,
    private friendService: FriendService,
    private movieService: MovieService,
    private wsService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      // @ts-ignore
      const id = +params.get('id');
      const group: Group | undefined = await this.groupService.getGroupById(id)
      if (group) {
        this.group = group;

        if (group.groupSettings) {
          this.movieService.selectedTranslation = group.groupSettings.selectedTranslation
        }

        await this.groupService.getGroupChat(this.group)
          .then(chat => this.chat = chat)

        await this.getUsersWhoAreNotInTheGroup()

        this.wsService.setGroupId(group.id)
        this.wsService.connect();
      } else {
        this.router.navigate([''])
      }
    });
  }

  ngOnDestroy() {
    this.wsService.disconnect();
  }

  public leaveFromGroup() {
    if (this.group) {
      this.groupService.removeUserFromGroup(this.group.id, this.tokenStorage.getUsername());
      this.router.navigate(['groups'])
    }
  }

  public t() {

  }

  private async getUsersWhoAreNotInTheGroup() {
    const friends = await this.friendService.getFriends();
    const groupUsers = this.group.users;
    this.notInGroupUsers = friends.filter(friend => {
      return !groupUsers.some(groupUser => groupUser.username === friend.username);
    });
  }

  public isAdmin() {
    return this.tokenStorage.getUsername() === this.group.admin
  }

  public addUserToChecked(user: User) {
    const existingUserIndex = this.checkedUsers.findIndex(u => u.username === user.username);
    if (existingUserIndex !== -1) {
      this.checkedUsers.splice(existingUserIndex, 1);
    } else {
      this.checkedUsers.push(user)
    }
  }

  public addUsersToGroup() {
    this.checkedUsers.forEach(
      (u) => {
        this.groupService.addUserToGroup(this.group, u)
        this.group.users.push(u)
      }
    )
  }

  public removeUserFromGroup(user: User) {
    this.groupService.removeUserFromGroup(this.group.id, user.username)
    this.group.users.splice(this.group.users.findIndex(u => u.username === user.username), 1)
  }

  public userIsAdmin(user: User): boolean {
    return this.group.admin === user.username
  }

  setMovie() {
    let selectedTranslation: any = this.movieService.selectedTranslation
    let movie = this.movieService.movie
    if (selectedTranslation && movie) {
      this.groupService.selectMovieForGroup(this.group, movie, selectedTranslation)
      this.movieService.selectedTranslation = null;
      this.movieService.movie = null;
    }
  }

  changeMovieTranslation() {
    if (this.group.groupSettings.selectedTranslation.name !== this.movieService.selectedTranslation?.name
                  && this.movieService.selectedTranslation) {
      this.groupService.changeMovieTranslation(this.movieService.selectedTranslation, this.group)
    }
  }
}
