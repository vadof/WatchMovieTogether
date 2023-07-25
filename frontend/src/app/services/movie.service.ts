import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";
import {Translation} from "../models/Translation";
import {Resolution} from "../models/Resolution";

@Injectable({
  providedIn: 'root'
})
export class MovieService {

  movie: Movie | null = null;
  selectedTranslation: Translation | null = null;

  constructor(private api: ApiService) { }

  public getMovie(link: string): Promise<Movie> {
    return new Promise<Movie>((resolve, reject) => {
      this.api.sendPostRequest('/movie', link).subscribe(
        res => {
          let movie = this.convertResponseToObject(res)
          this.selectedTranslation = movie.translations[0]
          resolve(movie);
        },
        err => {
          reject(err.error);
        }
      );
    });
  }

  private convertResponseToObject(res: string): Movie {
    let movie: Movie = {
      // @ts-ignore
      link: res.link,
      // @ts-ignore
      name: res.name,
      // @ts-ignore
      resolutions: res.resolutions,
      // @ts-ignore
      translations: res.translations
    }
    this.movie = movie;
    return movie
  }

  public getVideoLink(groupId: number, resolution: Resolution) {
    return new Promise<string>((resolve, reject) => {
      this.api.sendGetRequest(`/movie/${groupId}/${resolution.value}`).subscribe(
        res => {
          resolve(res);
        },
        err => {
          reject(err.error);
        }
      );
    });
  }


}
