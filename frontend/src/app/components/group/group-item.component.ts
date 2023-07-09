import {Component, Input} from '@angular/core';
import {Group} from "../../models/Group";
import {Router} from "@angular/router";

@Component({
  selector: 'app-group',
  templateUrl: './group-item.component.html',
  styleUrls: ['./group-item.component.scss']
})
export class GroupItemComponent {
  // @ts-ignore
  @Input() group: Group

  constructor(
    private router: Router
  ) {}

  navigateToGroup(id: number) {
    this.router.navigate(['/group', id])
  }
}
