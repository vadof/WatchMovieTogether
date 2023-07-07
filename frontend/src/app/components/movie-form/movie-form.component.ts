import { Component } from '@angular/core';
import {MovieService} from "../../services/movie.service";
import {Movie} from "../../models/Movie";

@Component({
  selector: 'app-movie-form',
  templateUrl: './movie-form.component.html',
  styleUrls: ['./movie-form.component.scss']
})
export class MovieFormComponent {
  loading = false;
  linkValue: string = '';
  movie: Movie | null = null
  error: any = ''
  foundMovieLink: string = ''

  constructor(private movieService: MovieService) {
  }

  async find() {
    this.error = ''
    try {
      this.loading = true;
      this.foundMovieLink = this.linkValue;
      this.movie = await this.movieService.getMovie(this.linkValue);
    } catch (error) {
      this.error = error;
    } finally {
      this.loading = false;
    }
  }
}