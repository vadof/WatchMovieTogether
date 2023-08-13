import {Component, Input} from '@angular/core';
import {Series} from "../../models/Series";
import {MovieService} from "../../services/movie.service";

@Component({
  selector: 'app-series-form-settings',
  templateUrl: './series-form-settings.component.html',
  styleUrls: ['./series-form-settings.component.scss']
})
// TODO not actual info button

export class SeriesFormSettingsComponent {
  @Input() series!: Series

  selectedTranslation: any = this.movieService.selectedSeriesTranslation

  constructor(private movieService: MovieService) {}

  selectTranslation(translation: any) {
    this.selectedTranslation = this.movieService.selectedSeriesTranslation = translation;
  }
}
