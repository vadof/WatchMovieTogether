import {Injectable} from '@angular/core';
import {Client} from "@stomp/stompjs";
import {TokenStorageService} from "../auth/token-storage.service";
import { Subject } from 'rxjs';
import {MovieAction} from "../pages/group-page/MovieAction";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private messageSubject: Subject<string> = new Subject<string>();
  private movieSubject: Subject<string> = new Subject<string>();
  // @ts-ignore
  private groupId: number;

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
          console.log("CONNECT")
          this.subscribeToGroupChat();
          this.subscribeToMovie();
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

  public getMessageSubscription(): Subject<string> {
    return this.messageSubject;
  }

  public getMovieSubscription(): Subject<string> {
    return this.movieSubject;
  }

  public sendMessage(message: string): void {
    this.client.publish({
      destination: `/app/chat/${this.groupId}`,
      body: message,
      headers: {
        'username': this.tokenStorage.getUsername()
      }
    })
  }

  public sendMovieAction(action: MovieAction) {
    this.client.publish({
      destination: `/app/movie/${this.groupId}`,
      body: action
    })
  }

  public disconnect(): void {
    if (this.client.active) {
      console.log("DISCONNECT")
      this.client.deactivate()
    }
  }
}
