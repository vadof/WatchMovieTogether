import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Chat} from "../models/Chat";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(
    private api: ApiService
  ) { }

  public sendMessage(chat: Chat, message: string) {
    this.api.sendPostRequest('/chat/' + chat.id, message).subscribe(
      res => {
        console.log(res)
      }, err => {
        console.log(err)}
    )
  }
}
