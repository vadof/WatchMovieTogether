import {Component, Input} from '@angular/core';
import {Movie} from "../../models/Movie";
import {MovieService} from "../../services/movie.service";

@Component({
  selector: 'app-movie-form-settings',
  templateUrl: './movie-form-settings.component.html',
  styleUrls: ['./movie-form-settings.component.scss']
})
export class MovieFormSettingsComponent {
  @Input() movie!: Movie;
  public updateStatus: string = '';

  selectedTranslation: any = this.movieService.selectedTranslation

  constructor(private movieService: MovieService) {}

  selectTranslation(translation: any) {
    this.selectedTranslation = this.movieService.selectedTranslation = translation;
  }

  // TODO don't working at group page
  public updateMovieInfo() {
    this.updateStatus = 'Updating...';

    let movie = this.movieService.movie;
    if (movie) {
      this.movieService.updateMovieInfo()
        .then(() => {
          let movie = this.movieService.movie;
          if (movie) {
            this.movie = movie;
            this.selectedTranslation = this.movieService.selectedTranslation;
            this.updateStatus = '';
          }
        })
        .catch(err => {
        this.updateStatus = err.error;
      });
    }
  }
}
