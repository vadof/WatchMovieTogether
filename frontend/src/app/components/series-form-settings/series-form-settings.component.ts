import {Component, Input} from '@angular/core';
import {Series} from "../../models/Series";
import {MovieService} from "../../services/movie.service";

@Component({
  selector: 'app-series-form-settings',
  templateUrl: './series-form-settings.component.html',
  styleUrls: ['./series-form-settings.component.scss']
})
export class SeriesFormSettingsComponent {
  @Input() series!: Series

  selectedTranslation: any = this.movieService.selectedSeriesTranslation
  public updateStatus: string = '';

  constructor(private movieService: MovieService) {}

  selectTranslation(translation: any) {
    this.selectedTranslation = this.movieService.selectedSeriesTranslation = translation;
  }

  public updateSeriesInfo() {
    this.updateStatus = 'Updating...';

    if (this.series) {
      this.movieService.updateSeriesInfo(this.series)
        .then(() => {
          let series = this.movieService.series;
          if (series) {
            this.series = series;
            this.selectedTranslation = this.movieService.selectedSeriesTranslation;
            this.updateStatus = '';
          }
        })
        .catch(err => {
          this.updateStatus = err.error;
        });
    }
  }
}
