import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Group} from "../../../models/Group";
import {Resolution} from "../../../models/Resolution";
import {MovieService} from "../../../services/movie.service";
import {WebSocketService} from "../../../services/web-socket.service";
import {Subscription} from "rxjs";
import {Movie} from "../../../models/Movie";
import {VgApiService, VgStates} from "@videogular/ngx-videogular/core";

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
  public playSubscription: Subscription
  // @ts-ignore
  public pauseSubscription: Subscription

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

      this.getNewVideoLink();
    }
  }

  private async getNewVideoLink() {
    await this.movieService.getVideoLink(this.group.id, this.selectedResolution)
      .then(res => this.videoLink = res);
  }

  public playPause() {
    this.vgPlayer.state === VgStates.VG_PAUSED ? this.play() : this.pause();
  }

  private play() {
    this.vgPlayer.play();
  }

  private pause() {
    this.vgPlayer.pause();
    this.currentMovieTime = this.vgPlayer.currentTime;
  }

  public changeResolution(resolution: Resolution) {
    if (this.selectedResolution.value !== resolution.value) {
      this.pause();
      this.selectedResolution = resolution;

      this.getNewVideoLink().then(() => {
        setTimeout(() => {
          this.vgPlayer.currentTime = this.currentMovieTime;
          this.play();
        }, 3000)
      });
    }
  }
}
