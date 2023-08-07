import {Injectable} from '@angular/core';
import {Group} from "../models/Group";
import {User} from "../models/User";
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";
import {Translation} from "../models/Translation";
import {Chat} from "../models/Chat";
import {Series} from "../models/Series";
import {SeriesTranslation} from "../models/SeriesTranslation";
import {Season} from "../models/Seson";

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
      this.api.sendPostRequest('/users/groups', null).subscribe(response => {
        this.groups = response;
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
          let chat: Chat = res;
          resolve(chat)
        }
      )
    })
  }

  public async getGroupById(id: number): Promise<Group | undefined> {
    const groups = await this.getAllGroups();
    return groups.find(group => group.id === id);
  }

  public createGroup(name: string): Promise<Group> {
    return new Promise<Group>((resolve, reject) => {
      this.api.sendPostRequest('/groups', name).subscribe(
        res => {
          let group: Group = res;
          resolve(group);
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
    this.api.sendPostRequest('/groups/movie', requestObj).subscribe();
  }

  public selectSeriesForGroup(group: Group, series: Series,
                              selectedSeriesTranslation: SeriesTranslation,
                              season: Season, episode: number) {
    let requestObj = {
      groupId: group.id,
      series,
      selectedSeriesTranslation,
      season,
      episode
    }
    this.api.sendPostRequest("/groups/series", requestObj).subscribe();
  }

  public addUserToGroup(group: Group, user: User) {
    this.api.sendPostRequest(`/groups/${group.id}/users`, user).subscribe()
  }

  public changeMovieTranslation(selectedTranslation: Translation, group: Group) {
    this.api.sendPutRequest(`/groups/${group.id}/translation`, selectedTranslation).subscribe()
  }
}
