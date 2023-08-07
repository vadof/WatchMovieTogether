import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RegisterFormComponent } from './pages/register-page/register-form.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { LoginFormComponent } from './pages/login-page/login-form.component';
import { MainPageComponent } from './pages/main-page/main-page.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NavbarComponent } from './components/navbar/navbar.component';
import { GroupItemComponent } from './components/group/group-item.component';
import { UserGroupsPageComponent } from './pages/user-groups-page/user-groups-page.component';
import { GroupFormComponent } from './components/group-form/group-form.component';
import { MovieFormComponent } from './components/movie-form/movie-form.component';
import { MovieFormSettingsComponent } from './components/movie-form-settings/movie-form-settings.component';
import { GroupPageComponent } from './pages/group-page/group-page.component';
import { ChatComponent } from './pages/group-page/chat/chat.component';
import { FriendsPageComponent } from './pages/friends-page/friends-page.component';
import { MyFriendsComponent } from './pages/friends-page/my-friends/my-friends.component';
import { FriendRequestsComponent } from './pages/friends-page/friend-requests/friend-requests.component';
import { SearchFriendsComponent } from './pages/friends-page/search-friends/search-friends.component';
import { VideoPlayerComponent } from './pages/group-page/video-player/video-player.component';
import {VgControlsModule} from "@videogular/ngx-videogular/controls";
import {VgBufferingModule} from "@videogular/ngx-videogular/buffering";
import {VgOverlayPlayModule} from "@videogular/ngx-videogular/overlay-play";
import {VgCoreModule} from "@videogular/ngx-videogular/core";
import {VgModuloModule} from "@videogular/ngx-videogular/modulo";
import {VgImaAdsModule} from "@videogular/ngx-videogular/ima-ads";
import { SeriesFormSettingsComponent } from './components/series-form-settings/series-form-settings.component';

@NgModule({
  declarations: [
    AppComponent,
    RegisterFormComponent,
    LoginFormComponent,
    MainPageComponent,
    NavbarComponent,
    GroupItemComponent,
    UserGroupsPageComponent,
    GroupFormComponent,
    MovieFormComponent,
    MovieFormSettingsComponent,
    GroupPageComponent,
    ChatComponent,
    FriendsPageComponent,
    MyFriendsComponent,
    FriendRequestsComponent,
    SearchFriendsComponent,
    VideoPlayerComponent,
    SeriesFormSettingsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    VgControlsModule,
    VgBufferingModule,
    VgOverlayPlayModule,
    VgCoreModule,
    VgModuloModule,
    VgImaAdsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
