import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Group} from "../../../models/Group";
import {Resolution} from "../../../models/Resolution";
import {MovieService} from "../../../services/movie.service";
import {WebSocketService} from "../../../services/web-socket.service";
import {Subscription} from "rxjs";
import {VgApiService, VgStates} from "@videogular/ngx-videogular/core";
import {MovieAction} from "../MovieAction";

@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.scss']
})
export class VideoPlayerComponent implements OnInit {
  // @ts-ignore
  @Input() group: Group

  // @ts-ignore
  @ViewChild(VgApiService, { static: true }) vgPlayer: VgApiService;

  public movie: any;
  public selectedResolution: any;
  public resolutions: Resolution[] = []
  private currentMovieTime: any;

  // @ts-ignore
  public movieSubscription: Subscription

  public videoLink: string = ''

  constructor(
    private movieService: MovieService,
    private wsService: WebSocketService,
  ) {
  }

  ngOnInit(): void {
    if (this.group.groupSettings) {
      this.resolutions = this.group.groupSettings.selectedTranslation.resolutions.reverse();
      this.movie = this.group.groupSettings.selectedMovie
      this.selectedResolution = this.group.groupSettings.selectedTranslation.resolutions
        .find((n) => n.value === '1080p')

      this.getNewVideoLink(this.selectedResolution);

      this.handleMovieSubscription();
    }
  }

  private handleMovieSubscription() {
    this.movieSubscription = this.wsService.getMovieSubscription().subscribe((action) => {
      if (action === MovieAction.PLAY) {
        this.vgPlayer.play();
      } else if (action === MovieAction.PAUSE) {
        this.vgPlayer.pause();
      }
    })
  }

  private async getNewVideoLink(resolution: Resolution) {
    await this.movieService.getVideoLink(this.group.id, resolution)
      .then(res => this.videoLink = res);
  }

  public playPause() {
    this.vgPlayer.state === VgStates.VG_PAUSED ? this.play() : this.pause()
  }

  private play() {
    this.wsService.sendMovieAction(MovieAction.PLAY)
  }

  private pause() {
    this.wsService.sendMovieAction(MovieAction.PAUSE)
    this.currentMovieTime = this.vgPlayer.currentTime;
  }

  public changeResolution(resolution: Resolution) {
    if (this.selectedResolution.value !== resolution.value) {
      this.pause();
      this.selectedResolution = resolution;

      this.getNewVideoLink(resolution).then(() => {
        setTimeout(() => {
          this.vgPlayer.currentTime = this.currentMovieTime;
        }, 3000)
      });
    }
  }
}
