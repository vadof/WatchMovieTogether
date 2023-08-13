import { Injectable } from '@angular/core';
import {MovieSettings} from "../models/MovieSettings";

const VOLUME_KEY = "PREFERRED_VOLUME"
const RESOLUTION_KEY = "PREFERRED_RESOLUTION"

@Injectable({
  providedIn: 'root'
})
export class UserConfigService {
  constructor() { }

  public setPreferredVolume(value: number) {
    localStorage.setItem(VOLUME_KEY, value + '');
  }

  public getPreferredVolume(): number | null {
    let volume = localStorage.getItem(VOLUME_KEY);
    if (volume) {
      return parseFloat(volume);
    } else {
      return null;
    }
  }

  public setPreferredResolution(resolutionValue: string) {
    localStorage.setItem(RESOLUTION_KEY, resolutionValue)
  }

  public getPreferredResolution(): string | null {
    return localStorage.getItem(RESOLUTION_KEY)
  }

  public saveGroupMovieStreamLink(groupId: number, link: string, movieName: string) {
    const linkKey = `GROUP_${groupId}_LINK`;
    const movieKey = `GROUP_${groupId}_MOVIE`;
    const dateKey = `GROUP_${groupId}_DATE`;

    sessionStorage.setItem(linkKey, link);
    sessionStorage.setItem(movieKey, movieName);
    sessionStorage.setItem(dateKey, Date())
  }

  // TODO if any element of movie was changed return null
  public getGroupMovieStreamLink(groupId: number, movieName: string): string | null {
    const linkKey = `GROUP_${groupId}_LINK`;
    const movieKey = `GROUP_${groupId}_MOVIE`;
    const dateKey = `GROUP_${groupId}_DATE`;

    const savedLink = sessionStorage.getItem(linkKey);
    const savedDateStr = sessionStorage.getItem(dateKey);
    const savedMovieName = sessionStorage.getItem(movieKey);

    if (!savedLink || !savedDateStr || savedMovieName !== movieName) {
      return null;
    }

    const savedDate = new Date(savedDateStr);
    const currentTime = new Date();

    const minutesDifference = (currentTime.getTime() - savedDate.getTime()) / (1000 * 60);
    if (minutesDifference > 5) {
      return null;
    }

    return savedLink;
  }
}
