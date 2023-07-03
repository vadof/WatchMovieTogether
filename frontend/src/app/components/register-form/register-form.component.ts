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

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent {

  // @ts-ignore
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder
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

  register(): void {
    if (this.registerForm.valid) {
      console.log('Valid')
    } else {
      console.log('Invalid')
    }
    // console.log("SUBMITED")
  }
}
