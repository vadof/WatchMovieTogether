import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../services/user-service";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {

  constructor(
    private userService: UserService
  ) {
  }

  public message = ''

  loginForm = new FormGroup({
    username: new FormControl<string>('', Validators.required),
    password: new FormControl<string>('', Validators.required)
  })

  login() {
    const username = this.loginForm.value.username as string
    const password = this.loginForm.value.password as string
    this.userService.loginUser(username, password).subscribe(
      response => {
        console.log('Success')
      },
      error => {
        this.message = error.error;
        this.loginForm.value.password = ''
      })
  }
}
