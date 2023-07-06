import {Component, OnInit} from '@angular/core';
import {GroupsService} from "../../services/groups.service";
import {Group} from "../../models/Group";

@Component({
  selector: 'app-groups-page',
  templateUrl: './groups-page.component.html',
  styleUrls: ['./groups-page.component.scss']
})
export class GroupsPageComponent implements OnInit {

  createGroup = false;
  // @ts-ignore
  groups: Group[] = null;

  constructor(private service: GroupsService) {
  }

  ngOnInit(): void {
    // TODO Do not send request if groups are not null
    this.groups = this.service.getAllGroups()
    console.log(this.groups);
  }

}
