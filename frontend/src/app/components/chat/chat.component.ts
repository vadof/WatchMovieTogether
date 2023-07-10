import {Component, Input} from '@angular/core';
import {Chat} from "../../models/Chat";
import {ChatService} from "../../services/chat.service";
import {TokenStorageService} from "../../auth/token-storage.service";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent {
  // @ts-ignore
  @Input() chat: Chat
  message: string = ''

  constructor(
    private chatService: ChatService,
    private tokenStorage: TokenStorageService
  ) {setTimeout(() => {
    console.log(this.chat.messages)}, 3000)}

  public sendMessage() {
    console.log(this.message)
    this.chatService.sendMessage(this.chat, this.message)
    this.message = ''
  }

  public getUsernameFromStorage(): string {
    return this.tokenStorage.getUsername()
  }
}
