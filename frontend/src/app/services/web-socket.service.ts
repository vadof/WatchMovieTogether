import {Injectable} from '@angular/core';
import {Client, StompHeaders, StompSubscription} from "@stomp/stompjs";
import {TokenStorageService} from "../auth/token-storage.service";
import { Subject } from 'rxjs';
import {VideoAction} from "../pages/group-page/VideoAction";
import {User} from "../models/User";
import {MovieSettings} from "../models/MovieSettings";
import {SeriesSettings} from "../models/SeriesSettings";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private groupId!: number;

  private messageSubject: Subject<string> = new Subject<string>();
  private videoActionSubject: Subject<string> = new Subject<string>();
  private movieSubject: Subject<MovieSettings> = new Subject<MovieSettings>();
  private seriesSubject: Subject<SeriesSettings> = new Subject<SeriesSettings>();
  private rewindSubject: Subject<number> = new Subject<number>();
  private privilegesSubject: Subject<User[]> = new Subject<User[]>();
  private userLeaveSubject: Subject<User> = new Subject<User>();
  private userAddSubject: Subject<User> = new Subject<User>();
  private videoTimeSubject: Subject<string> = new Subject<string>();
  private videoStateSubject: Subject<string> = new Subject<string>();

  private subscriptions: StompSubscription[] = [];

  constructor(
    private tokenStorage: TokenStorageService,
  ) {
    this.client = new Client();
  }

  public setGroupId(groupId: number) {
    this.groupId = groupId;
  }

  public connect() {
    const headers = {
      'Authorization': `Bearer ${this.tokenStorage.getToken()}`,
      'groupId': this.groupId.toString()
    }
    if (this.groupId) {

      this.client.configure({
        brokerURL: `ws://localhost:8080/websocket/group/${this.groupId}`,
        connectHeaders: headers,
        onConnect: () => {
          this.subscribeToGroupChat();
          this.subscribeToMovieAction();
          this.subscribeToRewind();
          this.subscribeToPrivilegeChange();
          this.subscribeToUserAdd();
          this.subscribeToUserLeave();
          this.subscribeToMovieChange();
          this.subscribeToSeriesChange();
          this.subscribeToMovieTime();
          this.subscribeToMovieState();
        },
      });

      this.client.activate();
    }
  }

  private subscribeToGroupChat() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/chat`, (msg) => {
        this.messageSubject.next(msg.body);
    }))
  }

  private subscribeToMovieAction() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/movie/action`, (action) => {
        this.videoActionSubject.next(action.body)
    }))

  }

  private subscribeToMovieChange() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/movie`, (ms) => {
        const movieSettings: MovieSettings = JSON.parse(ms.body);
        this.movieSubject.next(movieSettings);
    }))
  }

  private subscribeToRewind() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/movie/rewind`, (time) => {
        this.rewindSubject.next(Number(time.body))
      })
    )
  }

  private subscribeToPrivilegeChange() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/user/privileges`,
        (usersWithPrivileges) => {
          let users: User[] = JSON.parse(usersWithPrivileges.body);
          this.privilegesSubject.next(users)
      })
    )
  }

  private subscribeToUserAdd() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/user/add`,
        (leftUser) => {
          let user: User = JSON.parse(leftUser.body);
          this.userAddSubject.next(user)
      })
    )
  }

  private subscribeToUserLeave() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/user/leave`,
        (leftUser) => {
          let user: User = JSON.parse(leftUser.body);
          this.userLeaveSubject.next(user)
      })
    )
  }

  private subscribeToMovieTime() {
    this.subscriptions.push(
      this.client.subscribe(`/topic/${this.groupId}/${this.tokenStorage.getUsername()}/movie/time`,
        (time) => {
          this.videoTimeSubject.next(time.body)
        })
    )
  }

  private subscribeToMovieState() {
    this.subscriptions.push(
      this.client.subscribe(`/topic/${this.groupId}/${this.tokenStorage.getUsername()}/movie/state`,
        (state) => {
          this.videoStateSubject.next(state.body)
        })
    )
  }

  private subscribeToSeriesChange() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/series`, (ss) => {
        const seriesSettings: SeriesSettings = JSON.parse(ss.body);
        this.seriesSubject.next(seriesSettings);
      }))
  }

  public getMessageSubject(): Subject<string> {
    return this.messageSubject;
  }

  public getMovieSubject(): Subject<MovieSettings> {
    return this.movieSubject;
  }

  public getSeriesSubject(): Subject<SeriesSettings> {
    return this.seriesSubject;
  }

  public getVideoActionSubject(): Subject<string> {
    return this.videoActionSubject;
  }

  public getVideoRewindSubject(): Subject<number> {
    return this.rewindSubject;
  }

  public getPrivilegesSubject(): Subject<User[]> {
    return this.privilegesSubject;
  }

  public getUserLeaveSubject(): Subject<User> {
    return this.userLeaveSubject;
  }

  public getUserAddSubject(): Subject<User> {
    return this.userAddSubject;
  }

  public getVideoTimeSubject(): Subject<string> {
    return this.videoTimeSubject;
  }

  public getVideoStateSubject(): Subject<string> {
    return this.videoStateSubject;
  }

  public sendMessage(message: string): void {
    this.client.publish({
      destination: `/app/${this.groupId}/chat`,
      body: message,
      headers: {
        'username': this.tokenStorage.getUsername()
      }
    })
  }

  public sendCurrentMovieTime(time: string) {
    if (time) {
      this.client.publish({
        destination: `/app/${this.groupId}/movie/time/set`,
        body: time
      })
    }
  }

  public getCurrentMovieTime() {
    this.client.publish({
      destination: `/app/${this.groupId}/${this.tokenStorage.getUsername()}/movie/time`
    })
  }

  public getMovieState() {
    this.client.publish({
      destination: `/app/${this.groupId}/${this.tokenStorage.getUsername()}/movie/state`
    })
  }

  public addUserToGroup(user: User) {
    this.client.publish({
      destination: `/app/${this.groupId}/user/add`,
      body: JSON.stringify(user),
    })
  }

  public removeUserFromGroup(user: User, whoRemovedUsername: string) {
    this.client.publish({
      destination: `/app/${this.groupId}/user/leave`,
      body: JSON.stringify(user),
      headers: {
        'username': whoRemovedUsername
      }
    })
  }

  public sendMovieAction(action: VideoAction) {
    this.client.publish({
      destination: `/app/${this.groupId}/movie/action`,
      body: action
    })
  }

  public sendMovieRewind(movieTime: string) {
    this.client.publish({
      destination: `/app/${this.groupId}/movie/rewind`,
      body: movieTime
    })
  }

  public changeUserPrivileges(user: User) {
    this.client.publish({
      destination: `/app/${this.groupId}/user/privileges`,
      body: JSON.stringify(user),
      headers: {
        'username': this.tokenStorage.getUsername()
      }
    })
  }

  public disconnect(): void {
    if (this.client.active) {
      this.client.deactivate();

      this.subscriptions.forEach(s => s.unsubscribe());
      this.subscriptions = []

      this.messageSubject = new Subject<string>();
      this.videoActionSubject = new Subject<string>();
      this.movieSubject = new Subject<MovieSettings>();
      this.seriesSubject = new Subject<SeriesSettings>();
      this.rewindSubject = new Subject<number>();
      this.privilegesSubject = new Subject<User[]>();
      this.userLeaveSubject = new Subject<User>();
      this.userAddSubject = new Subject<User>();
      this.videoTimeSubject = new Subject<string>();
      this.videoStateSubject = new Subject<string>();
    }
  }

  public synchronizeVideo() {
    this.getMovieState();
    this.getCurrentMovieTime();

    setTimeout(() => {this.getCurrentMovieTime()}, 1000)
  }
}
