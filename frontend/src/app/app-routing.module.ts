import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegisterFormComponent} from "./pages/register-page/register-form.component";
import {LoginFormComponent} from "./pages/login-page/login-form.component";
import {MainPageComponent} from "./pages/main-page/main-page.component";
import {AuthGuard} from "./auth/auth-guard";
import {UserGroupsPageComponent} from "./pages/user-groups-page/user-groups-page.component";
import {GroupPageComponent} from "./pages/group-page/group-page.component";
import {FriendsPageComponent} from "./pages/friends-page/friends-page.component";

const routes: Routes = [
  {path: '', component: MainPageComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginFormComponent},
  {path: 'register', component: RegisterFormComponent},
  {path: 'groups', component: UserGroupsPageComponent, canActivate: [AuthGuard]},
  {path: 'group/:id', component: GroupPageComponent, canActivate: [AuthGuard]},
  {path: 'friends', component: FriendsPageComponent, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
