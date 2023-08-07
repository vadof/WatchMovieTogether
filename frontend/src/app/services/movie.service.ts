import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";
import {Translation} from "../models/Translation";
import {Resolution} from "../models/Resolution";
import {Series} from "../models/Series";
import {SeriesTranslation} from "../models/SeriesTranslation";
import {Season} from "../models/Seson";

@Injectable({
  providedIn: 'root'
})
export class MovieService {

  movie: Movie | null = null;
  series: Series | null = null;

  selectedTranslation: Translation | null = null;
  selectedSeriesTranslation: SeriesTranslation | null = null;

  constructor(private api: ApiService) { }

  public getMovie(link: string): Promise<Movie | Series> {
    return new Promise<Movie | Series>((resolve, reject) => {
      this.api.sendPostRequest('/movie', link).subscribe(
        res => {
          if (res.seriesTranslations) {
            let series: Series = {
              type: 'series',
              link: res.link,
              name: res.name,
              seriesTranslations: res.seriesTranslations
            };
            this.setSeries(series);
            resolve(series);
          } else {
            let movie: Movie = {
              type: 'movie',
              link: res.link,
              name: res.name,
              translations: res.translations
            }
            this.setMovie(movie)
            resolve(movie);
          }
        },
        err => {
          reject(err.error);
        }
      );
    });
  }

  private setMovie(movie: Movie) {
    this.movie = movie;
    this.selectedTranslation = movie.translations[0];

    this.series = null;
    this.selectedSeriesTranslation = null
  }

  private setSeries(series: Series) {
    this.series = series;
    this.selectedSeriesTranslation = series.seriesTranslations[0]

    this.movie = null;
    this.selectedTranslation = null;
  }

  public getMovieLink(groupId: number, resolution: Resolution) {
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

  public getSeriesLink(groupId: number, resolution: Resolution, season: Season, episode: number) {
    return new Promise<string>((resolve, reject) => {
      this.api.sendGetRequest(`/series/${groupId}/${resolution.value}/${season.number}/${episode}`).subscribe(
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
