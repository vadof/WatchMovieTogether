import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {User} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class FriendService {

  constructor(
    private api: ApiService
  ) {}

  public async getFriends() {
    return this.sendRequest('/users/friends', null);
  }

  public async findFriends(username: string): Promise<User[]> {
    return this.sendRequest('/users/search/' + username, null);
  }

  public sendFriendRequest(user: User) {
    this.api.sendPostRequest('/users/friend_requests', user).subscribe()
  }

  public async getFriendRequests(): Promise<User[]> {
    return this.sendRequest('/users/friend_requests', null);
  }

  public replyToFriendRequest(user: User, accept: boolean) {
    let url: string = accept ? '/users/friend_requests/accept' : '/users/friend_requests/deny'
    this.api.sendPostRequest(url, user).subscribe()
  }

  public removeFriend(user: User) {
    this.api.sendDeleteRequest('/users/friends/' + user.username).subscribe();
  }

  private async sendRequest(url: string, body: any) {
    return await new Promise<User[]>((resolve) => {
      let toSend
      if (body) {
        toSend = this.api.sendPostRequest(url, body);
      } else {
        toSend = this.api.sendGetRequest(url);
      }

      toSend.subscribe(response => {
        let users: User[] = response;
        resolve(users)
      })
    })
  }
}
