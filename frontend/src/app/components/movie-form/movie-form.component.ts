import { Component } from '@angular/core';
import {MovieService} from "../../services/movie.service";
import {Movie} from "../../models/Movie";
import {Series} from "../../models/Series";

@Component({
  selector: 'app-movie-form',
  templateUrl: './movie-form.component.html',
  styleUrls: ['./movie-form.component.scss']
})
export class MovieFormComponent {
  loading = false;
  linkValue: string = '';
  movie: Movie | null = null
  series: Series | null = null
  error: any = ''

  constructor(private movieService: MovieService) {}

  async find() {
    this.error = ''
    try {
      this.loading = true;
      this.movie = null;
      this.series = null;

      let movieSeries = await this.movieService.getMovie(this.linkValue.trim())

      if (movieSeries?.type === 'movie') {
        this.movie = movieSeries
      } else if (movieSeries?.type === 'series') {
        this.series = movieSeries;
      }

      this.linkValue = ''
    } catch (error) {
      this.error = error;
    } finally {
      this.loading = false;
    }
  }
}
