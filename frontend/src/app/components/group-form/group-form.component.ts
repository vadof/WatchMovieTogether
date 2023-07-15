import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ApiService} from "../../services/api.service";
import {Router} from "@angular/router";
import {GroupService} from "../../services/group.service";
import {MovieService} from "../../services/movie.service";
import {Movie} from "../../models/Movie";
import {Translation} from "../../models/Translation";

@Component({
  selector: 'app-group-form',
  templateUrl: './group-form.component.html',
  styleUrls: ['./group-form.component.scss', '../../../styles.scss']
})
export class GroupFormComponent {

  // @ts-ignore
  groupForm: FormGroup;

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
    const selectedTranslation: Translation | null = this.movieService.selectedTranslation

    if (movie && selectedTranslation && this.groupForm.valid) {
      this.createGroup()
        .then(group=> {
          this.groupService.selectMovieForGroup(group, movie, selectedTranslation)
          this.router.navigate(['group/' + group.id])
        })
    } else if (this.groupForm.valid) {
      this.createGroup()
        .then(group=>
          this.router.navigate(['group/' + group.id]))
    } else {
      this.errorMessage = 'Fill in the empty fields!'
    }
  }

  private async createGroup() {
    return await this.groupService.createGroup(this.groupForm.value.name);
  }
}
