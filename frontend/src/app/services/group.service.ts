import {Injectable} from '@angular/core';
import {Group} from "../models/Group";
import {User} from "../models/User";
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";
import {Translation} from "../models/Translation";
import {Chat} from "../models/Chat";

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  groups: Group[] = []

  constructor(
    private api: ApiService
  ) { }

  public async getAllGroups(): Promise<Group[]> {
    return await new Promise<Group[]>((resolve, reject) => {
      let groups: Group[] = []
      this.api.sendPostRequest('/users/groups', null).subscribe(response => {
        response.forEach((group: any) => {
          groups.push(this.saveGroup(group));
        })
        this.groups = groups;
        resolve(this.groups)
      }, error => {
        reject(error)
      })
    })
  }

  public getGroupChat(group: Group) {
    return new Promise<Chat>((resolve, reject) => {
      this.api.sendGetRequest('/groups/chat/' + group.id).subscribe(
        res => {
          resolve(this.getChatFromResponse(res))
        }
      )
    })
  }

  private getChatFromResponse(res: any): Chat {
    return {
      id: res.id,
      messages: res.messages
    }
  }

  public async getGroupById(id: number): Promise<Group | undefined> {
    const groups = await this.getAllGroups();
    return groups.find(group => group.id === id);
  }

  public createGroup(name: string): Promise<Group> {
    return new Promise<Group>((resolve, reject) => {
      this.api.sendPostRequest('/groups', name).subscribe(
        res => {
          resolve(this.saveGroup(res));
        },
        err => {
          reject(err.error);
        }
      );
    });
  }

  public selectMovieForGroup(group: Group, movie: Movie,
                             selectedTranslation: Translation) {
    let requestObj = {
      groupId: group.id,
      movie,
      selectedTranslation
    }
    this.api.sendPostRequest('/groups/movie', requestObj).subscribe(res => {
      console.log(res)
    }, err => {
      console.log(err)
    });
  }

  private saveGroup(response: any): Group {
    const group: Group = {
      id: response.id,
      name: response.name,
      admin: response.admin,
      groupSettings: response.groupSettings,
      users: [],
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

  public addUserToGroup(group: Group, user: User) {
    this.api.sendPostRequest('/groups/' + group.id + '/users', user).subscribe()
  }

  removeUserFromGroup(groupId: number, username: string) {
    this.api.sendDeleteRequest('/groups/' + groupId + '/users/' + username).subscribe()
  }
}
