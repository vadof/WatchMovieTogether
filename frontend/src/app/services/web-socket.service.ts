import {Injectable} from '@angular/core';
import {Client, StompSubscription} from "@stomp/stompjs";
import {TokenStorageService} from "../auth/token-storage.service";
import { Subject } from 'rxjs';
import {MovieAction} from "../pages/group-page/MovieAction";
import {User} from "../models/User";
import {MovieSelectionObject} from "../requests/MovieSelectionObject";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private groupId!: number;

  private messageSubject: Subject<string> = new Subject<string>();
  private movieActionSubject: Subject<string> = new Subject<string>();
  private movieSubject: Subject<MovieSelectionObject> = new Subject<MovieSelectionObject>();
  private rewindSubject: Subject<number> = new Subject<number>();
  private privilegesSubject: Subject<User[]> = new Subject<User[]>();
  private userLeaveSubject: Subject<User> = new Subject<User>();
  private userAddSubject: Subject<User> = new Subject<User>();

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
    if (this.groupId) {
      this.client.configure({
        brokerURL: `ws://localhost:8080/websocket/group/${this.groupId}`,
        onConnect: () => {
          this.subscribeToGroupChat();
          this.subscribeToMovieAction();
          this.subscribeToRewind();
          this.subscribeToPrivilegeChange();
          this.subscribeToUserAdd();
          this.subscribeToUserLeave();
          this.subscribeToMovieChange();
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
        this.movieActionSubject.next(action.body)
    }))

  }

  private subscribeToMovieChange() {
    this.subscriptions.push(
      this.client.subscribe(`/group/${this.groupId}/movie`, (newMovie) => {
        const msr: MovieSelectionObject = JSON.parse(newMovie.body);
        this.movieSubject.next(msr);
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

  public getMessageSubject(): Subject<string> {
    return this.messageSubject;
  }

  public getMovieSubject(): Subject<MovieSelectionObject> {
    return this.movieSubject;
  }

  public getMovieActionSubject(): Subject<string> {
    return this.movieActionSubject;
  }

  public getMovieRewindSubject(): Subject<number> {
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

  public sendMessage(message: string): void {
    this.client.publish({
      destination: `/app/${this.groupId}/chat`,
      body: message,
      headers: {
        'username': this.tokenStorage.getUsername()
      }
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

  public sendMovieAction(action: MovieAction) {
    console.log("SEND ")
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
      this.movieActionSubject = new Subject<string>();
      this.movieSubject = new Subject<MovieSelectionObject>();
      this.rewindSubject = new Subject<number>();
      this.privilegesSubject = new Subject<User[]>();
      this.userLeaveSubject = new Subject<User>();
      this.userAddSubject = new Subject<User>();
    }
  }
}
