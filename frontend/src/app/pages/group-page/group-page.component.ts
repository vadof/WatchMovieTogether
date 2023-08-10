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
import {Season} from "../../models/Seson";
import {Series} from "../../models/Series";

@Component({
  selector: 'app-group-page',
  templateUrl: './group-page.component.html',
  styleUrls: ['./group-page.component.scss']
})
export class GroupPageComponent implements OnInit, OnDestroy {

  group!: Group
  chat!: Chat
  notInGroupUsers: User[] = []
  checkedUsers: User[] = []
  chooseAnotherMovie: boolean = false
  seasonEpisodes: number[] = []

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
      const id = +params.get('id')!;
      const group: Group | undefined = await this.groupService.getGroupById(id)
      if (group) {
        this.group = group;

        if (group.groupSettings.movieSettings) {
          this.movieService.selectedTranslation = group.groupSettings.movieSettings.selectedTranslation
        }

        if (group.groupSettings.seriesSettings) {
          this.movieService.selectedSeriesTranslation = group.groupSettings.seriesSettings.selectedTranslation
          this.refreshSeasonEpisodesArray();
        }

        await this.groupService.getGroupChat(this.group)
          .then(chat => this.chat = chat)

        await this.getUsersWhoAreNotInTheGroup()

        this.wsService.setGroupId(group.id)
        this.wsService.connect();

        this.handlePrivilegesSubscription();
        this.handleUserAddSubscription();
        this.handleUserLeaveSubscription();
      } else {
        this.router.navigate([''])
      }
    });
  }

  ngOnDestroy() {
    this.wsService.disconnect();
  }

  private handlePrivilegesSubscription() {
    this.wsService.getPrivilegesSubject().subscribe((users) => {
      this.group.groupSettings.usersWithPrivileges = users;
    })
  }

  private handleUserLeaveSubscription() {
    this.wsService.getUserLeaveSubject().subscribe((user) => {
      this.group.users
        .splice(this.group.users.findIndex(u => u.username === user.username), 1)

      if (user.username === this.tokenStorage.getUsername()) {
        this.router.navigate(['groups'])
      }
    })
  }

  private handleUserAddSubscription() {
    this.wsService.getUserAddSubject().subscribe((user) => {
      this.group.users.push(user);
    })
  }

  public leaveFromGroup() {
    let username: string = this.tokenStorage.getUsername();
    let user: User | undefined = this.group.users.find((u) => u.username === username)
    if (user) {
      this.wsService.removeUserFromGroup(user, username);
    }
  }

  private async getUsersWhoAreNotInTheGroup() {
    const friends = await this.friendService.getFriends();
    const groupUsers = this.group.users;
    this.notInGroupUsers = friends.filter(friend => {
      return !groupUsers.some(groupUser => groupUser.username === friend.username);
    });
  }

  public isAdmin() {
    return this.tokenStorage.getUsername() === this.group.admin.username;
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
        this.wsService.addUserToGroup(u);
      }
    )
    this.checkedUsers = []
  }

  public removeUserFromGroup(user: User) {
    this.wsService.removeUserFromGroup(user, this.tokenStorage.getUsername());
  }

  public userIsAdmin(user: User): boolean {
    return this.group.admin.username === user.username
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
    if (this.group.groupSettings.movieSettings.selectedTranslation.name !== this.movieService.selectedTranslation?.name
                  && this.movieService.selectedTranslation) {
      this.groupService.changeMovieTranslation(this.movieService.selectedTranslation, this.group)
    }
  }

  public userHasPrivileges(user: User): boolean {
    return this.group.groupSettings.usersWithPrivileges
      .some((u) => u.username === user.username);
  }

  public changeUserPrivileges(user: User) {
    if (this.isAdmin()) {
      this.wsService.changeUserPrivileges(user);
    }
  }

  public synchronizeMovie() {
    this.wsService.synchronizeVideo();
  }

  private refreshSeasonEpisodesArray() {
    this.seasonEpisodes.length = 0;
    const episodes = this.group.groupSettings.seriesSettings.selectedSeason.episodes;
    for (let i = 1; i <= episodes; i++) {
      this.seasonEpisodes.push(i);
    }
  }

  public changeSeason(season: Season) {
    this.group.groupSettings.seriesSettings.selectedSeason = season;
    this.group.groupSettings.seriesSettings.selectedEpisode = 1;
    this.refreshSeasonEpisodesArray();
    this.submitSeriesChanges();
  }

  public changeEpisode(episode: number) {
    this.group.groupSettings.seriesSettings.selectedEpisode = episode;
    this.submitSeriesChanges();
  }

  private submitSeriesChanges() {
    if (this.isAdmin()) {
      const seriesSettings = this.group.groupSettings.seriesSettings;
      this.groupService.selectSeriesForGroup(this.group, seriesSettings.selectedSeries,
        seriesSettings.selectedTranslation, seriesSettings.selectedSeason,
        seriesSettings.selectedEpisode);
    }
  }
}
