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
import { FooterComponent } from './components/footer/footer.component';
import { GroupComponent } from './components/group/group.component';
import { GroupsPageComponent } from './pages/groups-page/groups-page.component';
import { GroupFormComponent } from './components/group-form/group-form.component';
import { MovieFormComponent } from './components/movie-form/movie-form.component';

@NgModule({
  declarations: [
    AppComponent,
    RegisterFormComponent,
    LoginFormComponent,
    MainPageComponent,
    NavbarComponent,
    FooterComponent,
    GroupComponent,
    GroupsPageComponent,
    GroupFormComponent,
    MovieFormComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    FontAwesomeModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
