import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {Group} from "../../models/Group";

@Component({
  selector: 'app-user-groups-page',
  templateUrl: './user-groups-page.component.html',
  styleUrls: ['./user-groups-page.component.scss']
})
export class UserGroupsPageComponent implements OnInit {

  createGroup = false;
  groups: Group[] = []

  constructor(
    public service: GroupService,
  ) {}

  ngOnInit(): void {
    this.service.getAllGroups().then((res) => {
      this.groups = res.sort((a, b) => b.id - a.id);
    })
  }
}
