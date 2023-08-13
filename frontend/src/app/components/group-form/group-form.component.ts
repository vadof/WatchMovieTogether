import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ApiService} from "../../services/api.service";
import {Router} from "@angular/router";
import {GroupService} from "../../services/group.service";
import {MovieService} from "../../services/movie.service";
import {Movie} from "../../models/Movie";
import {Series} from "../../models/Series";
import {Season} from "../../models/Seson";
import {SeriesTranslation} from "../../models/SeriesTranslation";

@Component({
  selector: 'app-group-form',
  templateUrl: './group-form.component.html',
  styleUrls: ['./group-form.component.scss', '../../../styles.scss']
})
export class GroupFormComponent {

  groupForm!: FormGroup;
  chooseMovie = false;
  errorMessage = ''

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private groupService: GroupService,
    private router: Router,
    private movieService: MovieService
  ) {
    this.createForm();
  }

  private createForm() {
    this.groupForm = this.fb.group({
      name: ['', [
        Validators.required,
      ]]
    })
  }
  
  public create() {
    const movie: Movie | null = this.movieService.movie
    const series: Series | null = this.movieService.series

    if (this.groupForm.valid) {
      if (movie) {
        this.createGroup()
          .then(group=> {
            this.groupService.selectMovieForGroup(group, movie, this.movieService.selectedTranslation!)
            this.router.navigate(['group/' + group.id]).then(() => window.location.reload())
        })
      } else if (series) {
        this.createGroup()
          .then(group => {
            let translation: SeriesTranslation = this.movieService.selectedSeriesTranslation!
            let season: Season = translation.seasons[0]
            let episode = 1;
            this.groupService.selectSeriesForGroup(group, series, translation, season, episode)
            this.router.navigate(['group/' + group.id]).then(() => window.location.reload())
        })
      }
    } else {
      this.errorMessage = 'Fill in the empty fields!'
    }
  }

  private async createGroup() {
    return await this.groupService.createGroup(this.groupForm.value.name);
  }
}
