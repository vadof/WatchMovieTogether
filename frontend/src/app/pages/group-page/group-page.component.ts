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
import {SeriesSettings} from "../../models/SeriesSettings";

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

  selectedSeason: any = null;
  selectedEpisode: any = null;
  seriesChanges: boolean = false;

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
          this.setEpisodeAndSeries();
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
        this.handleSeriesSubscription();
      } else {
        this.router.navigate([''])
      }
    });
  }

  ngOnDestroy() {
    this.wsService.disconnect();
  }

  private handleSeriesSubscription() {
    this.wsService.getSeriesSubject().subscribe((sso) => {
      this.group.groupSettings.movieSettings = null!;
      this.group.groupSettings.seriesSettings = sso;

      this.setEpisodeAndSeries();
      this.refreshSeasonEpisodesArray();
    })
  }

  private setEpisodeAndSeries() {
    this.selectedSeason = this.group.groupSettings.seriesSettings.selectedSeason
    this.selectedEpisode = this.group.groupSettings.seriesSettings.selectedEpisode
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

  public setMovie() {
    this.chooseAnotherMovie = false;

    let selectedTranslation: any = this.movieService.selectedTranslation
    let movie = this.movieService.movie

    if (selectedTranslation && movie) {
      this.groupService.selectMovieForGroup(this.group, movie, selectedTranslation)
      this.movieService.setMovie(movie, selectedTranslation)
    } else {
      this.setSeries();
    }
  }

  private setSeries() {
    let translation: any = this.movieService.selectedSeriesTranslation;
    let series: any = this.movieService.series;

    if (translation && series) {
      this.groupService.selectSeriesForGroup(this.group, series, translation,
        translation.seasons[0], 1);
      this.movieService.setSeries(series, translation);
    }
  }

  changeMovieTranslation() {
    const newTranslation = this.movieService.selectedTranslation;
    const currentTranslation = this.group.groupSettings.movieSettings.selectedTranslation;
    if (newTranslation && currentTranslation.name !== newTranslation.name) {
      this.groupService.changeMovieTranslation(newTranslation, this.group)
    }
  }

  public changeSeriesTranslation() {
    const newTranslation = this.movieService.selectedSeriesTranslation;
    const currentTranslation = this.group.groupSettings.seriesSettings.selectedTranslation;
    if (newTranslation && currentTranslation.name !== newTranslation.name) {
      this.groupService.changeSeriesTranslation(newTranslation, this.group);
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
    const episodes = this.selectedSeason.episodes;
    for (let i = 1; i <= episodes; i++) {
      this.seasonEpisodes.push(i);
    }
  }

  public changeSeason(season: Season) {
    this.selectedSeason = season;
    this.selectedEpisode = 1;
    this.refreshSeasonEpisodesArray();

    this.seriesChanges = this.episodeOrSeasonChanged();
  }

  public changeEpisode(episode: number) {
    this.selectedEpisode = episode;
    this.seriesChanges = this.episodeOrSeasonChanged();
  }

  private episodeOrSeasonChanged(): boolean {
    const seriesSettings = this.group.groupSettings.seriesSettings;
    const sameSeason = seriesSettings.selectedSeason.number === this.selectedSeason.number;
    const sameEpisode = seriesSettings.selectedEpisode === this.selectedEpisode;

    return !sameSeason || !sameEpisode;
  }

  public saveSeriesChanges() {
    if (this.seriesChanges && this.isAdmin()) {
      let currentSettings: SeriesSettings = { ...this.group.groupSettings.seriesSettings };
      currentSettings.selectedSeason = this.selectedSeason;
      currentSettings.selectedEpisode = this.selectedEpisode;

      this.seriesChanges = false;
      this.groupService.changeSeriesEpisode(currentSettings, this.group);
    }
  }
}
