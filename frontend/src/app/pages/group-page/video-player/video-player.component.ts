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
import {Season} from "../../../models/Seson";

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

  public movie: any;

  public series: any;
  public season: any;
  public episode: any;

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
      this.movie = this.group.groupSettings.movieSettings.selectedMovie;
      this.resolutions = this.group.groupSettings.movieSettings.selectedTranslation.resolutions.reverse();
    } else if (this.group.groupSettings.seriesSettings) {
      this.series = this.group.groupSettings.seriesSettings.selectedSeries;
      this.season = this.group.groupSettings.seriesSettings.selectedSeason;
      this.episode = this.group.groupSettings.seriesSettings.selectedEpisode;
      this.resolutions = this.group.groupSettings.seriesSettings.selectedTranslation.resolutions.reverse();
    }

    if (this.movie || this.series) {
      this.setInitialResolution();
      this.movie ? this.getNewMovieLink() : this.getNewSeriesLink();
      this.setPreferredVolume();

      setTimeout(() => {this.wsService.synchronizeVideo()}, 3000)
    }

    this.handleMovieActionSubscription();
    this.handleRewindSubscription();
    this.handleMovieSubscription();
    this.handleMovieTimeSubscription();
    this.handleMovieStateSubscription();
  }

  public onPlayerReady() {

  }

  private setInitialResolution() {
    let preferredResolution = this.userConfig.getPreferredResolution();
    if (!preferredResolution || !this.resolutions
      .find((r) => r.value === preferredResolution)) {
      preferredResolution = '1080p'
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
      if (!this.group.groupSettings.movieSettings.selectedMovie) {
        window.location.reload()
      } else {
        this.group.groupSettings.movieSettings.selectedMovie = ms.movie;
        this.group.groupSettings.movieSettings.selectedTranslation = ms.selectedTranslation;
        this.resolutions = ms.selectedTranslation.resolutions.reverse();
        this.setInitialResolution();
        this.movie ? this.getNewMovieLink() : this.getNewSeriesLink();
      }
    })
  }

  private handleMovieTimeSubscription() {
    this.wsService.getMovieTimeSubject().subscribe((time) => {
      this.vgPlayer.currentTime = time;
    })
  }

  private handleMovieStateSubscription() {
    this.wsService.getMovieStateSubject().subscribe((state) => {
      if (state === MovieAction.PLAY) {
        this.vgPlayer.play()
      } else if (state === MovieAction.PAUSE) {
        this.vgPlayer.pause();
      }
    })
  }

  private handleMovieActionSubscription() {
    this.wsService.getMovieActionSubject().subscribe((action) => {
      if (action === MovieAction.PLAY) {
        this.vgPlayer.play();
      } else if (action === MovieAction.PAUSE) {
        this.vgPlayer.pause();
      }
    })
  }

  private handleRewindSubscription() {
    this.wsService.getMovieRewindSubject().subscribe((time) => {
      this.vgPlayer.pause();
      this.vgPlayer.currentTime = time;
      setTimeout(() => {this.vgPlayer.play()}, 1000)
    })
  }

  private async getNewMovieLink() {
    await this.movieService.getMovieLink(this.group.id, this.selectedResolution)
      .then(res => this.videoLink = res);
  }

  private async getNewSeriesLink() {
    await this.movieService.getSeriesLink(this.group.id, this.selectedResolution, this.season, this.episode)
      .then(res => this.videoLink = res);
  }

  public playPause() {
    this.vgPlayer.state === VgStates.VG_PAUSED ? this.play() : this.pause()
  }

  private play() {
    if (this.hasPrivileges()) {
      this.wsService.sendMovieAction(MovieAction.PLAY)
      this.wsService.sendCurrentMovieTime(this.getCurrentMovieTime())
    }
  }

  private pause() {
    if (this.hasPrivileges()) {
      this.wsService.sendMovieAction(MovieAction.PAUSE)
      this.wsService.sendCurrentMovieTime(this.getCurrentMovieTime())
    }
  }

  // TODO if video 00:00 and change resolution, it still should be 00:00
  public changeResolution(resolution: Resolution) {
    if (this.selectedResolution.value !== resolution.value) {
      this.vgPlayer.pause();
      this.selectedResolution = resolution;
      this.userConfig.setPreferredResolution(resolution.value);

      if (this.movie) {
        this.getNewMovieLink().then(() => {
          setTimeout(() => {
            this.wsService.synchronizeVideo();
          }, 5000)
        });
      } else {
        this.getNewSeriesLink()
      }
    }
  }

  public rewind(value: number) {
    if (this.hasPrivileges()) {
      this.wsService.sendMovieRewind(value.toString())
    }
  }

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
}
