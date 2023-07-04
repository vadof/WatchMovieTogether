import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {AuthService} from "../../auth/auth.service";
import {TokenStorageService} from "../../auth/token-storage.service";
import {RegisterRequest} from "./register-request";
import {solid} from "@fortawesome/fontawesome-svg-core/import.macro";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register-page',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent {

  // @ts-ignore
  registerForm: FormGroup;
  errorMessage = ''
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private storage: TokenStorageService,
    private router: Router
  ) {
    this.createForm();
  }

  private createForm() {
    this.registerForm = this.fb.group({
      firstname: ['', [
        Validators.required,
      ]],
      lastname: ['', [
        Validators.required,
      ]],
      username: ['', [
        Validators.required,
        Validators.pattern('[A-Za-z0-9]*')
      ]],
      email: ['', [
        Validators.required,
        Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(6)
      ]],
      confirmPassword: ['', [
        Validators.required
      ]],
    })
  }

  get passwordsMatch() {
    const password = this.registerForm.get('password')?.value;
    const confirmPassword = this.registerForm.get('confirmPassword')?.value;

    return (password === '' || confirmPassword === '') || password === confirmPassword;
  }

  get formIsValid() {
    return this.registerForm.valid && this.passwordsMatch;
  }

  get passwordLengthValid() {
    const password = this.registerForm.get('password')?.value;
    return password.length >= 6 || password === '';
  }

  get emailIsValid() {
    const email = this.registerForm.get('email')?.value;
    let r = new RegExp("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")
    return r.test(email) || email === '';
  }

  register(): void {
    if (this.formIsValid) {
      let rr: RegisterRequest = new RegisterRequest(this.registerForm.value.firstname,
        this.registerForm.value.lastname,
        this.registerForm.value.username,
        this.registerForm.value.email,
        this.registerForm.value.password)

      this.authService.register(rr).subscribe(
        response => {
          console.log(response)
          this.storage.saveToken(response.token)
          this.storage.saveUsername(this.registerForm.value.username)
          this.router.navigate([''])
        }, error => {
          this.errorMessage = error.error.error;
        })
    } else {
      this.errorMessage = 'Fill in the empty fields!'
    }
  }
}
