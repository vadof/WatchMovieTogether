import {Component, Input, OnInit} from '@angular/core';
import {Group} from "../../models/Group";
import {Router} from "@angular/router";

@Component({
  selector: 'app-group',
  templateUrl: './group-item.component.html',
  styleUrls: ['./group-item.component.scss']
})
export class GroupItemComponent implements OnInit {
  @Input() group!: Group

  name: string | null = null

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    if (this.group.groupSettings.movieSettings) {
      this.name = this.group.groupSettings.movieSettings.selectedMovie.name
    } else if (this.group.groupSettings.seriesSettings) {
      this.name = this.group.groupSettings.seriesSettings.selectedSeries.name
    }
  }

  navigateToGroup(id: number) {
    this.router.navigate(['/group', id])
  }
}
