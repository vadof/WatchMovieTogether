import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {User} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class FriendService {

  constructor(
    private api: ApiService
  ) { }

  public async findFriends(username: string): Promise<User[]> {
    return await new Promise<User[]>((resolve) => {
      let users: User[] = []
      this.api.sendGetRequest('/users/search/' + username).subscribe(response => {
        response.forEach((u: User) => {
          let user: User = {
            firstname: u.firstname,
            lastname: u.lastname,
            username: u.username
          }
          users.push(user);
        })
        resolve(users)
      })
    })
  }

  public sendFriendRequest(user: User) {
    this.api.sendPostRequest('/users/friend_requests', user).subscribe()
  }

  public async getFriendRequests(): Promise<User[]> {
    return await new Promise<User[]>((resolve) => {
      let users: User[] = []
      this.api.sendGetRequest('/users/friend_requests').subscribe(response => {
        response.forEach((u: User) => {
          let user: User = {
            firstname: u.firstname,
            lastname: u.lastname,
            username: u.username
          }
          users.push(user);
        })
        resolve(users)
      })
    })
  }

  public replyToFriendRequest(user: User, accept: boolean) {
    let url: string = accept ? '/users/friend_requests/accept' : '/users/friend_requests/deny'
    this.api.sendPostRequest(url, user).subscribe()
  }
}
