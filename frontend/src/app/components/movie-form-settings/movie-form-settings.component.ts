import {Component, Input} from '@angular/core';
import {Movie} from "../../models/Movie";
import {MovieService} from "../../services/movie.service";

@Component({
  selector: 'app-movie-form-settings',
  templateUrl: './movie-form-settings.component.html',
  styleUrls: ['./movie-form-settings.component.scss']
})
export class MovieFormSettingsComponent {
  @Input() movie!: Movie

  selectedTranslation: any = this.movieService.selectedTranslation

  constructor(private movieService: MovieService) {}

  selectTranslation(translation: any) {
    this.selectedTranslation = this.movieService.selectedTranslation = translation;
  }

  public updateMovieInfo() {
    const movie = this.movieService.movie;
    if (movie) {
      this.movieService.updateMovieInfo().then(
        res => console.log(res)
      );
    }
  }
}
