import {Component, HostListener, Input, OnInit, ViewChild} from '@angular/core';
import {Group} from "../../../models/Group";
import {Resolution} from "../../../models/Resolution";
import {MovieService} from "../../../services/movie.service";
import {WebSocketService} from "../../../services/web-socket.service";
import {VgApiService, VgStates} from "@videogular/ngx-videogular/core";
import {MovieAction} from "../MovieAction";
import {
  VgControlsComponent,
  VgScrubBarComponent,
  VgVolumeComponent
} from "@videogular/ngx-videogular/controls";
import {UserConfigService} from "../../../config/user-config.service";
import {TokenStorageService} from "../../../auth/token-storage.service";

const trackedKeys: string[] = [' ', 'p', 'm', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown']

@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.scss']
})
export class VideoPlayerComponent implements OnInit {
  // @ts-ignore
  @Input() group: Group

  @ViewChild(VgApiService, { static: true }) vgPlayer!: VgApiService;
  @ViewChild(VgControlsComponent, {static: true}) vgControls!: VgControlsComponent;
  @ViewChild(VgVolumeComponent, {static: true}) vgVolume!: VgVolumeComponent;
  @ViewChild(VgScrubBarComponent, {static: true}) vgBar!: VgScrubBarComponent;

  public movie: any;
  public selectedResolution: any;
  public resolutions: Resolution[] = []
  private currentMovieTime: any;

  public videoLink: string = ''

  constructor(
    private movieService: MovieService,
    private wsService: WebSocketService,
    private userConfig: UserConfigService,
    private tokenStorage: TokenStorageService
  ) {}

  @HostListener('window:keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    let msgInput = document.getElementById('message-input');
    if (msgInput !== document.activeElement
      && trackedKeys.find(k => event.key === k)) {
      this.handleKeyPress(event)
    }
  }

  ngOnInit(): void {
    if (this.group.groupSettings) {
      this.resolutions = this.group.groupSettings.selectedTranslation.resolutions.reverse();
      this.movie = this.group.groupSettings.selectedMovie

      this.setInitialResolution();
      this.getNewVideoLink(this.selectedResolution);
      this.setPreferredVolume();
      this.handleMovieSubscription();
      this.handleRewindSubscription();
    }
  }

  public t(t: string) {
    console.log(t)
    console.log(this.vgPlayer.currentTime)
    setTimeout(() => {
      console.log(this.vgPlayer.currentTime)}, 100)
  }

  public onPlayerReady() {
    this.vgPlayer.getDefaultMedia().subscriptions.play.subscribe(() => {
      this.play();
    })

    this.vgPlayer.getDefaultMedia().subscriptions.pause.subscribe(() => {
      this.pause();
    })
    // this.vgPlayer.getDefaultMedia().subscriptions.timeUpdate.subscribe(() => {
    //   console.log("TIME " + this.vgPlayer.currentTime)
    // })
  }

  private setInitialResolution() {
    let preferredResolution = this.userConfig.getPreferredResolution();
    if (!preferredResolution || !this.group.groupSettings.selectedTranslation.resolutions
      .find((r) => r.value === preferredResolution)) {
      preferredResolution = '1080p'
    }

    this.selectedResolution = this.group.groupSettings.selectedTranslation.resolutions
      .find((n) => n.value === preferredResolution)
  }

  private setPreferredVolume() {
    const volume = this.userConfig.getPreferredVolume();
    if (volume) {
      setTimeout(() => {
        this.vgVolume.setVolume(volume * 100)
      }, 100)
    }
  }

  private handleMovieSubscription() {
    this.wsService.getMovieSubscription().subscribe((action) => {
      if (action === MovieAction.PLAY) {
        this.vgPlayer.play();
      } else if (action === MovieAction.PAUSE) {
        this.vgPlayer.pause();
      }
    })
  }

  private handleRewindSubscription() {
    this.wsService.getMovieRewindSubject().subscribe((time) => {
      this.pause()
      this.vgPlayer.currentTime = time;
      setTimeout(() => {this.vgPlayer.play();}, 1000)
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
      this.userConfig.setPreferredResolution(resolution.value);

      this.getNewVideoLink(resolution).then(() => {
        setTimeout(() => {
          this.vgPlayer.currentTime = this.currentMovieTime;
        }, 3000)
      });
    }
  }

  public rewind(value: number) {
    if (this.hasAccessToRewound()) {
      this.wsService.sendMovieRewind(value.toString())
    }
  }

  public mouseUpRewind() {
    setTimeout(() => {
      this.rewind(this.getCurrentMovieTime())
    }, 500)
  }

  private handleKeyPress(event: KeyboardEvent) {
    const key = event.key;
    event.preventDefault();
    this.vgControls.show();

    if (key === ' ' || key === 'p') {
      this.playPause();
    }
    else if (key === 'ArrowLeft') {
      this.rewind(this.vgPlayer.currentTime - 5);
    }
    else if (key === 'ArrowRight') {
      this.rewind(this.vgPlayer.currentTime + 5);
    }
    else if (key === 'ArrowUp' || key === 'ArrowDown') {
      this.vgVolume.arrowAdjustVolume(event);
      this.userConfig.setPreferredVolume(this.vgVolume.getVolume() ? this.vgVolume.getVolume() : 0.05);
    } else if (key === 'm') {
      this.vgVolume.getVolume() ? this.mute() : this.unmute();
    }
  }

  private mute() {
    this.userConfig.setPreferredVolume(this.vgVolume.getVolume());
    this.vgVolume.setVolume(0);
  }

  private unmute() {
    this.vgVolume.setVolume(this.userConfig.getPreferredVolume()! * 100);
  }

  public hasAccessToRewound(): boolean {
    return this.group.admin === this.tokenStorage.getUsername()
  }

  public getCurrentMovieTime() {
    return this.vgPlayer.currentTime;
  }
}
