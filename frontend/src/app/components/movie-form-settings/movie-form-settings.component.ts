import {Component, Input} from '@angular/core';
import {Movie} from "../../models/Movie";
import {Resolution} from "../../models/Resolution";
import {MovieService} from "../../services/movie.service";
import {Translation} from "../../models/Translation";

@Component({
  selector: 'app-movie-form-settings',
  templateUrl: './movie-form-settings.component.html',
  styleUrls: ['./movie-form-settings.component.scss']
})
export class MovieFormSettingsComponent {
  // @ts-ignore
  @Input() movie: Movie

  constructor(private movieService: MovieService) {}

  selectedTranslation: any = this.movieService.selectedTranslation;
  selectedResolution: any = this.movieService.selectedResolution;

  selectTranslation(translation: any) {
    this.selectedTranslation = this.movieService.selectedTranslation = translation;
  }

  selectResolution(resolution: any) {
    this.selectedResolution = this.movieService.selectedResolution = resolution;
  }

  getTranslationResolutions(): Resolution[] {
    return this.selectedTranslation.resolutions.reverse();
  }
}
