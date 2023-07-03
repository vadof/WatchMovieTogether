import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthorizationService} from "../../auth/authorization.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {

  constructor(
    private authService: AuthorizationService,
    private router: Router
  ) {
  }

  public message = ''

  loginForm = new FormGroup({
    username: new FormControl<string>('', Validators.required),
    password: new FormControl<string>('', Validators.required)
  })

  login() {
    if (this.loginForm.valid) {
      const username = this.loginForm.value.username as string
      const password = this.loginForm.value.password as string

      this.authService.login(username, password).subscribe(
        response => {
          this.router.navigate([''])
        },
        error => {
          if (error.status === 403) {
            this.message = 'Invalid Username or Password!';
          } else {
            this.message = 'An error occurred. Please try again later.';
          }
          this.loginForm.controls.password.reset();
        })
    } else {
      this.message = 'Fill in the empty fields!'
    }

  }
}
