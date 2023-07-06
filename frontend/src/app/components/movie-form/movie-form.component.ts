import { Component } from '@angular/core';
import {MovieService} from "../../services/movie.service";

@Component({
  selector: 'app-movie-form',
  templateUrl: './movie-form.component.html',
  styleUrls: ['./movie-form.component.scss']
})
export class MovieFormComponent {
  loading = false;
  linkValue: string = '';

  constructor(private movieService: MovieService) {
  }

  find() {
    console.log(this.linkValue)
    this.movieService.getMovie(this.linkValue);
  }
}
