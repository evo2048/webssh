import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RestapiService } from '../services/rest-api/restapi.service';
import { FormControl, FormGroup } from '@angular/forms';
import { AuthenticationGuard } from '../authguard/authentication.guard';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit{

  emailElement: any;

  registerForm = new FormGroup({
    userId: new FormControl(''),
    username: new FormControl(''),
    email: new FormControl(''),
    password: new FormControl(''),
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    profilePicture: new FormControl('')
  })

  constructor(private route: ActivatedRoute, private router: Router, private restiApi: RestapiService, private guard: AuthenticationGuard) { }

  ngOnInit() {
    if(this.guard.canActivate())
      this.router.navigate(['/profile']);
  }


  register() {
    this.restiApi.register(this.registerForm.value).subscribe(result => this.gotoHome())
  }

  gotoHome() {
    this.router.navigate(['/home'])
  }

}
