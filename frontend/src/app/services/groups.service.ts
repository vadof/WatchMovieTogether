import {Injectable, OnInit} from '@angular/core';
import {Group} from "../models/Group";
import {User} from "../models/User";
import {ApiService} from "./api.service";

@Injectable({
  providedIn: 'root'
})
export class GroupsService {

  // groups: Group[] = []

  constructor(
    private api: ApiService
  ) { }

  public getAllGroups(): Group[] {
    let groups: Group[] = []

    this.api.sendPostRequest('/user/groups', null).subscribe(response => {
        response.forEach((group: any) => {
          groups.push(this.saveGroup(group));
        })
      }, error => {
      console.log(error)
    })

    return groups;
  }

  public createGroup(name: string): Group | null {
    this.api.sendPostRequest('/group', name).subscribe(res => {
      return this.saveGroup(res);
    }, error => {
      console.log(error)
    })
    return null;
  }

  private saveGroup(response: any): Group {
    const group: Group = {
      id: response.id,
      name: response.name,
      admin: response.admin,
      currentMovie: response.currentMovie || null,
      movieProgress: response.movieProgress || null,
      users: []
    };

    response.users.forEach((user: any) => {
      const newUser: User = {
        firstname: user.firstname,
        lastname: user.lastname,
        username: user.username
      };

      group.users.push(newUser);
    });

    return group;
  }
}
