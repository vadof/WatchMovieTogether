import {Component, Input} from '@angular/core';
import {Movie} from "../../models/Movie";
import {Resolution} from "../../models/Resolution";

@Component({
  selector: 'app-movie-form-settings',
  templateUrl: './movie-form-settings.component.html',
  styleUrls: ['./movie-form-settings.component.scss']
})
export class MovieFormSettingsComponent {
  // @ts-ignore
  @Input() movie: Movie

  selectedTranslation: any;
  selectedResolution: any;

  selectTranslation(translation: any) {
    console.log(translation);
    this.selectedTranslation = translation;
  }

  selectResolution(resolution: any) {
    console.log(resolution);
    this.selectedResolution = resolution;
  }

  public getSortedResolutions(): Resolution[] {
    return this.movie.resolutions.sort((a, b) => {
      const order = ['2160p', '1440p', '1080p Ultra', '1080p', '720p', '480p', '360p'];
      return order.indexOf(a.value) - order.indexOf(b.value);
    })
  }
}
