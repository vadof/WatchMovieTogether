import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Chat} from "../../../models/Chat";
import {TokenStorageService} from "../../../auth/token-storage.service";
import {WebSocketService} from "../../../services/web-socket.service";
import {Message} from "../../../models/Message";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  @Input() chat!: Chat
  message: string = ''
  @ViewChild('chatContent', { static: true }) chatContentRef!: ElementRef;

  constructor(
    private tokenStorage: TokenStorageService,
    private wsService: WebSocketService
  ) {}

  ngOnInit() {
    this.handleMessages()
    this.setScrollbarToBottom();
  }

  private handleMessages() {
    this.wsService.getMessageSubject().subscribe((msg) => {
      const needToScroll = this.isScrollbarAtBottom();
      let message: Message = JSON.parse(msg);
      this.chat.messages.push(message);

      if (needToScroll) {
        this.setScrollbarToBottom();
      }
    })
  }

  public sendMessage() {
    if (this.message && this.message.trim()) {
      this.wsService.sendMessage(this.message);
      this.message = ''
    }
  }

  public getUsernameFromStorage(): string {
    return this.tokenStorage.getUsername()
  }

  public formatTime(time: string): string {
    return time.split(' ')[1]
  }

  private isScrollbarAtBottom(): boolean {
    const chatContentEl: HTMLElement = this.chatContentRef.nativeElement;
    const buffer = 5;
    return chatContentEl.scrollTop + chatContentEl.clientHeight + buffer >= chatContentEl.scrollHeight;
  }

  private setScrollbarToBottom() {
    setTimeout(() => {
      const chatContentEl: HTMLElement = this.chatContentRef.nativeElement;
      chatContentEl.scrollTop = chatContentEl.scrollHeight;
    }, 0)
  }
}
