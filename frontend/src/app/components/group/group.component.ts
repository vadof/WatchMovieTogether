import {Component, Input} from '@angular/core';
import {Group} from "../../models/Group";

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent {
  // @ts-ignore
  @Input() group: Group
}
