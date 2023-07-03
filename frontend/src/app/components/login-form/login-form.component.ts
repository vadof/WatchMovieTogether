import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../auth/auth.service";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../auth/token-storage.service";
import {LoginRequest} from "./login-request";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {

  constructor(
    private authService: AuthService,
    private router: Router,
    private storage: TokenStorageService
  ) {
  }

  public errorMessage = ''

  loginForm = new FormGroup({
    username: new FormControl<string>('', Validators.required),
    password: new FormControl<string>('', Validators.required)
  })

  login() {
    if (this.loginForm.valid) {
      const request = new LoginRequest(this.loginForm.value.username as string,
        this.loginForm.value.password as string)
      this.authService.login(request).subscribe(
        response => {
          this.storage.saveToken(response.token);
          this.router.navigate([''])
        },
        error => {
          if (error.status === 403) {
            this.errorMessage = 'Invalid Username or Password!';
          } else {
            this.errorMessage = 'An error occurred. Please try again later.';
          }
          this.loginForm.controls.password.reset();
        })
    } else {
      this.errorMessage = 'Fill in the empty fields!'
    }

  }
}
