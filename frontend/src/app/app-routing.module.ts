import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegisterFormComponent} from "./pages/register-page/register-form.component";
import {LoginFormComponent} from "./pages/login-page/login-form.component";
import {MainPageComponent} from "./pages/main-page/main-page.component";
import {AuthGuard} from "./auth/auth-guard";
import {GroupsPageComponent} from "./pages/groups-page/groups-page.component";

const routes: Routes = [
  {path: '', component: MainPageComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginFormComponent},
  {path: 'register', component: RegisterFormComponent},
  {path: 'groups', component: GroupsPageComponent, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
