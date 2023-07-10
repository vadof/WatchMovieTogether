import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {Group} from "../../models/Group";
import {ActivatedRoute, Router} from "@angular/router";
import {Chat} from "../../models/Chat";

@Component({
  selector: 'app-group-page',
  templateUrl: './group-page.component.html',
  styleUrls: ['./group-page.component.scss']
})
export class GroupPageComponent implements OnInit {

  group: Group | undefined = undefined;
  // @ts-ignore
  chat: Chat;

  constructor(
    private groupService: GroupService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      // @ts-ignore
      const id = +params.get('id');
      const group: Group | undefined = await this.groupService.getGroupById(id)
      if (group) {
        this.group = group;
        await this.groupService.getGroupChat(this.group).then(
          chat => this.chat = chat
        )
      } else {
        this.router.navigate([''])
      }
    });
  }
}
