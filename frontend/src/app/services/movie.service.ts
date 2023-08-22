import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Movie} from "../models/Movie";
import {Translation} from "../models/Translation";
import {Resolution} from "../models/Resolution";
import {Series} from "../models/Series";
import {SeriesTranslation} from "../models/SeriesTranslation";
import {Season} from "../models/Seson";
import {Group} from "../models/Group";

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
            this.setSeries(series, null);
            resolve(series);
          } else {
            let movie: Movie = {
              type: 'movie',
              link: res.link,
              name: res.name,
              translations: res.translations
            }
            this.setMovie(movie, null)
            resolve(movie);
          }
        },
        err => {
          reject(err.error);
        }
      );
    });
  }

  public setMovie(movie: Movie, translation: Translation | null) {
    this.refresh();
    this.movie = movie;
    this.selectedTranslation = translation ? translation : movie.translations[0];
  }

  public setSeries(series: Series, seriesTranslation: SeriesTranslation | null) {
    this.refresh();
    this.series = series;
    this.selectedSeriesTranslation = seriesTranslation ? seriesTranslation : series.seriesTranslations[0]
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

  public refresh() {
    this.movie = null;
    this.selectedTranslation = null;

    this.series = null;
    this.selectedSeriesTranslation = null
  }

  public updateMovieInfo(movie: Movie | null) {
    return new Promise<Movie>((resolve, reject) => {
      this.api.sendPutRequest("/movie", movie ? movie : this.movie).subscribe(
        res => {
          let movie: Movie = {
            type: 'movie',
            link: res.link,
            name: res.name,
            translations: res.translations
          }
          this.setMovie(movie, movie.translations[0]);
          resolve(movie);
        },
        err => {
          reject(err);
        }
      );
    });
  }

  public updateSeriesInfo(series: Series | null) {
    return new Promise<Series>((resolve, reject) => {
      this.api.sendPutRequest("/series", series ? series : this.series).subscribe(
        res => {
          let series: Series = {
            type: 'series',
            link: res.link,
            name: res.name,
            seriesTranslations: res.seriesTranslations
          };
          this.setSeries(series, series.seriesTranslations[0]);
          resolve(series);
        },
        err => {
          reject(err);
        }
      );
    });
  }
}
