import {Injectable} from '@angular/core';
import {Client} from "@stomp/stompjs";
import {TokenStorageService} from "../auth/token-storage.service";
import { Subject } from 'rxjs';
import {MovieAction} from "../pages/group-page/MovieAction";
import {User} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private groupId!: number;

  private messageSubject: Subject<string> = new Subject<string>();
  private movieSubject: Subject<string> = new Subject<string>();
  private rewindSubject: Subject<number> = new Subject<number>();
  private privilegesSubject: Subject<User[]> = new Subject<User[]>();
  private userLeaveSubject: Subject<User> = new Subject<User>();
  private userAddSubject: Subject<User> = new Subject<User>();

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
          this.subscribeToMovie();
          this.subscribeToRewind();
          this.subscribeToPrivilegeChange();
          this.subscribeToUserAdd();
          this.subscribeToUserLeave();
        },
      });

      this.client.activate();
    }

  }

  private subscribeToGroupChat() {
    this.client.subscribe(`/group/${this.groupId}/chat`, (msg) => {
      this.messageSubject.next(msg.body);
    })
  }

  private subscribeToMovie() {
    this.client.subscribe(`/group/${this.groupId}/movie`, (action) => {
      this.movieSubject.next(action.body)
    })
  }

  private subscribeToRewind() {
    this.client.subscribe(`/group/${this.groupId}/movie/rewind`, (time) => {
      this.rewindSubject.next(Number(time.body))
    })
  }

  private subscribeToPrivilegeChange() {
    this.client.subscribe(`/group/${this.groupId}/user/privileges`,
      (usersWithPrivileges) => {
        let users: User[] = JSON.parse(usersWithPrivileges.body);
      this.privilegesSubject.next(users)
    })
  }

  private subscribeToUserAdd() {
    this.client.subscribe(`/group/${this.groupId}/user/add`,
      (leftUser) => {
        let user: User = JSON.parse(leftUser.body);
        this.userAddSubject.next(user)
      })
  }

  private subscribeToUserLeave() {
    this.client.subscribe(`/group/${this.groupId}/user/leave`,
      (leftUser) => {
        let user: User = JSON.parse(leftUser.body);
        this.userLeaveSubject.next(user)
      })
  }

  public getMessageSubject(): Subject<string> {
    return this.messageSubject;
  }

  public getMovieSubject(): Subject<string> {
    return this.movieSubject;
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
    this.client.publish({
      destination: `/app/${this.groupId}/movie`,
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
      this.client.deactivate()
    }
  }
}
