import {Component, HostListener, Input, OnInit, ViewChild} from '@angular/core';
import {Group} from "../../../models/Group";
import {Resolution} from "../../../models/Resolution";
import {MovieService} from "../../../services/movie.service";
import {WebSocketService} from "../../../services/web-socket.service";
import {VgApiService, VgStates} from "@videogular/ngx-videogular/core";
import {VideoAction} from "../VideoAction";
import {
  VgControlsComponent,
  VgScrubBarComponent,
  VgVolumeComponent
} from "@videogular/ngx-videogular/controls";
import {UserConfigService} from "../../../config/user-config.service";
import {TokenStorageService} from "../../../auth/token-storage.service";
import {MovieSettings} from "../../../models/MovieSettings";
import {SeriesSettings} from "../../../models/SeriesSettings";

const trackedKeys: string[] = [' ', 'p', 'm', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown']

@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.scss']
})
export class VideoPlayerComponent implements OnInit {
  @Input() group!: Group

  @ViewChild(VgApiService, { static: true }) vgPlayer!: VgApiService;
  @ViewChild(VgControlsComponent, {static: true}) vgControls!: VgControlsComponent;
  @ViewChild(VgVolumeComponent, {static: true}) vgVolume!: VgVolumeComponent;
  @ViewChild(VgScrubBarComponent, {static: true}) vgBar!: VgScrubBarComponent;

  private movieSettings: MovieSettings | null = null;
  private seriesSettings: SeriesSettings | null = null;

  public selectedResolution: any;
  public resolutions: Resolution[] = []
  public videoLink: string = ''
  private body = document.querySelector('body');

  constructor(
    private movieService: MovieService,
    private wsService: WebSocketService,
    private userConfig: UserConfigService,
    private tokenStorage: TokenStorageService
  ) {}

  @HostListener('window:keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    if (this.body === document.activeElement
      && trackedKeys.find(k => event.key === k)) {
      this.handleKeyPress(event)
    }
  }

  ngOnInit(): void {
    if (this.group.groupSettings.movieSettings) {
      this.movieSettings = this.group.groupSettings.movieSettings;
      this.resolutions = this.movieSettings.selectedTranslation.resolutions.reverse();
    } else if (this.group.groupSettings.seriesSettings) {
      this.seriesSettings = this.group.groupSettings.seriesSettings;
      this.resolutions = this.seriesSettings.selectedTranslation.resolutions.reverse();
    }

    if (this.movieSettings || this.seriesSettings) {
      this.setInitialResolution();
      this.movieSettings ? this.getNewMovieLink() : this.getNewSeriesLink();
      this.setPreferredVolume();
      setTimeout(() => {this.wsService.synchronizeVideo()}, 3000)
    }

    this.handleVideoActionSubscription();
    this.handleRewindSubscription();
    this.handleMovieSubscription();
    this.handleSeriesSubscription();
    this.handleVideoTimeSubscription();
    this.handleVideoStateSubscription();
  }

  public onPlayerReady() {

  }

  private setInitialResolution() {
    let preferredResolution = this.userConfig.getPreferredResolution();
    if (!preferredResolution || !this.resolutions
      .some((r) => r.value === preferredResolution)) {
      preferredResolution = this.resolutions[0].value;
    }

    this.selectedResolution = this.resolutions
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
    this.wsService.getMovieSubject().subscribe((ms) => {
      this.group.groupSettings.seriesSettings = null!;
      this.seriesSettings = null;

      this.group.groupSettings.movieSettings = ms;
      this.movieSettings = ms;

      this.resolutions = ms.selectedTranslation.resolutions.reverse();

      this.setInitialResolution();
      this.getNewMovieLink()

      this.movieService.setMovie(ms.selectedMovie, ms.selectedTranslation)
    })
  }

  private handleSeriesSubscription() {
    this.wsService.getSeriesSubject().subscribe((sso) => {
      this.group.groupSettings.movieSettings = null!;
      this.movieSettings = null;

      this.group.groupSettings.seriesSettings = sso;
      this.seriesSettings = sso;

      this.resolutions = sso.selectedTranslation.resolutions.reverse();
      this.setInitialResolution();
      this.getNewSeriesLink();

      this.movieService.setSeries(sso.selectedSeries, sso.selectedTranslation)
    })
  }

  private handleVideoTimeSubscription() {
    this.wsService.getVideoTimeSubject().subscribe((time) => {
      this.vgPlayer.currentTime = time;
    })
  }

  private handleVideoStateSubscription() {
    this.wsService.getVideoStateSubject().subscribe((state) => {
      if (state === VideoAction.PLAY) {
        this.vgPlayer.play()
      } else if (state === VideoAction.PAUSE) {
        this.vgPlayer.pause();
      }
    })
  }

  private handleVideoActionSubscription() {
    this.wsService.getVideoActionSubject().subscribe((action) => {
      if (action === VideoAction.PLAY) {
        this.vgPlayer.play();
      } else if (action === VideoAction.PAUSE) {
        this.vgPlayer.pause();
      }
    })
  }

  private handleRewindSubscription() {
    this.wsService.getVideoRewindSubject().subscribe((time) => {
      this.vgPlayer.pause();
      this.vgPlayer.currentTime = time;
      setTimeout(() => {this.vgPlayer.play()}, 1000)
    })
  }

  private async getNewMovieLink() {
    this.videoLink = '';
    await this.movieService.getMovieLink(this.group.id, this.selectedResolution)
      .then((res) => {
        this.videoLink = res;
      });
  }

  private async getNewSeriesLink() {
    this.videoLink = '';
    if (this.seriesSettings) {
      await this.movieService.getSeriesLink(this.group.id, this.selectedResolution,
        this.seriesSettings.selectedSeason, this.seriesSettings.selectedEpisode)
        .then(res => this.videoLink = res);
    }
  }

  public playPause() {
    this.vgPlayer.state === VgStates.VG_PAUSED ? this.play() : this.pause()
  }

  private play() {
    if (this.hasPrivileges()) {
      this.wsService.sendMovieAction(VideoAction.PLAY)
      this.wsService.sendCurrentMovieTime(this.getCurrentMovieTime())
    }
  }

  private pause() {
    if (this.hasPrivileges()) {
      this.wsService.sendMovieAction(VideoAction.PAUSE)
      this.wsService.sendCurrentMovieTime(this.getCurrentMovieTime())
    }
  }

  // TODO if video 00:00 and change resolution, it still should be 00:00
  public changeResolution(resolution: Resolution) {
    if (this.selectedResolution.value !== resolution.value) {
      // this.vgPlayer.pause();
      this.selectedResolution = resolution;
      this.userConfig.setPreferredResolution(resolution.value);

      if (this.movieSettings) {
        this.getNewMovieLink()
          .then(() => this.synchronizeVideoWithTimeout(5000));
      } else {
        this.getNewSeriesLink()
          .then(() => this.synchronizeVideoWithTimeout(5000));
      }
    }
  }

  public rewind(value: number) {
    if (this.hasPrivileges()) {
      this.wsService.sendMovieRewind(value.toString())
    }
  }

  // TODO video is paused if rewind
  public mouseUpRewind() {
    setTimeout(() => {
      this.rewind(this.getCurrentMovieTime())
    }, 100)
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

  public getCurrentMovieTime() {
    return this.vgPlayer.currentTime;
  }

  public hasPrivileges(): boolean {
    return this.group.groupSettings.usersWithPrivileges
      .some((u) => u.username === this.tokenStorage.getUsername());
  }

  private synchronizeVideoWithTimeout(ms: number) {
    setTimeout(() => {
      this.wsService.synchronizeVideo();
    }, ms)
  }
}
