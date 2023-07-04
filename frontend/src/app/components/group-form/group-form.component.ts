import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ApiService} from "../../services/api.service";
import {Router} from "@angular/router";
import {GroupsService} from "../../services/groups.service";
import {GroupsPageComponent} from "../../pages/groups-page/groups-page.component";
import {Group} from "../../models/Group";

@Component({
  selector: 'app-group-form',
  templateUrl: './group-form.component.html',
  styleUrls: ['./group-form.component.scss']
})
export class GroupFormComponent {

  // @ts-ignore
  groupForm: FormGroup;
  errorMessage = ''
  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private groupService: GroupsService,
    private router: Router,
    private groupPage: GroupsPageComponent
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
    if (this.groupForm.valid) {
      let group: Group | null = this.groupService.createGroup(this.groupForm.value.name);
      if (group !== null) {
        this.groupPage.groups.push(group)
      }
    } else {
      this.errorMessage = 'Fill in the empty fields!'
    }
  }
}
