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

  public getMessageSubscription(): Subject<string> {
    return this.messageSubject;
  }

  public getMovieSubscription(): Subject<string> {
    return this.movieSubject;
  }

  public getMovieRewindSubject(): Subject<number> {
    return this.rewindSubject;
  }

  public getPrivilegesSubject(): Subject<User[]> {
    return this.privilegesSubject;
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

  public changeUserPrivileges(user: User, groupId: number) {
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
