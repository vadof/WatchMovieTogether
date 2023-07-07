import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";

@Injectable({
  providedIn: 'root'
})
export class MovieService {

  movie: Movie | null = null;

  constructor(private api: ApiService) { }

  public getMovie(link: string): Movie {
    // @ts-ignore
    return new Promise<Movie>((resolve, reject) => {
      this.api.sendPostRequest('/movie', link).subscribe(
        res => {
          resolve(this.convertResponseToObject(res));
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
    return movie
  }
}
