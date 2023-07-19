import {Component, Input, OnInit} from '@angular/core';
import {Chat} from "../../../models/Chat";
import {ChatService} from "../../../services/chat.service";
import {TokenStorageService} from "../../../auth/token-storage.service";
import {Subscription} from "rxjs";
import {WebSocketService} from "../../../services/web-socket.service";
import {Message} from "../../../models/Message";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  // @ts-ignore
  @Input() chat: Chat
  message: string = ''
  // @ts-ignore
  private subscription: Subscription;

  constructor(
    private chatService: ChatService,
    private tokenStorage: TokenStorageService,
    private wsService: WebSocketService
  ) {}

  ngOnInit() {
    this.subscription = this.wsService.getMessage().subscribe((msg) => {
      let message: Message = JSON.parse(msg);
      this.chat.messages.push(message);
    })
  }

  public sendMessage() {
    // console.log(this.message)
    // this.chatService.sendMessage(this.chat, this.message)
    this.wsService.sendMessage(this.message);
    this.message = ''
  }

  public getUsernameFromStorage(): string {
    return this.tokenStorage.getUsername()
  }

  public formatTime(time: string): string {
    return time.split(' ')[1]
  }
}
