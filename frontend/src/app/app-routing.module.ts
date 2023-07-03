import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegisterFormComponent} from "./components/register-form/register-form.component";
import {LoginFormComponent} from "./components/login-form/login-form.component";
import {MainPageComponent} from "./components/main-page/main-page.component";
import {AuthGuard} from "./auth/auth-guard";

const routes: Routes = [
  {path: '', component: MainPageComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginFormComponent},
  {path: 'register', component: RegisterFormComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
