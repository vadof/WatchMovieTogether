import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";

@Injectable({
  providedIn: 'root'
})
export class MovieService {

  movies: Movie[] = [];

  constructor(private api: ApiService) { }

  public getMovie(link: string) {
    this.api.sendPostRequest('/movie', link).subscribe(
      res => {
        this.addMovie(res)
      }, err => {
        console.log(err)
      }
    );
  }

  private addMovie(res: string) {
    // @ts-ignore
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
    console.log(movie)
    this.movies.push(movie);
  }
}
