import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { timer } from 'rxjs';
import { AuthenticationGuard } from '../authguard/authentication.guard';
import { AuthService } from '../services/auth-service/auth.service';
import { RestapiService } from '../services/rest-api/restapi.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm = new FormGroup({
    username: new FormControl(''),
    password: new FormControl('')
  })

  constructor(private route: ActivatedRoute, private router: Router, private service: RestapiService, private authService: AuthService, private authGuard: AuthenticationGuard) { }

  ngOnInit() {
    var usernameEl = document.getElementById("username")
    usernameEl.addEventListener("blur", function() {
      console.log("username checked");
    }, false);

    if(this.authGuard.canActivate())
      this.router.navigate(['/profile'])
  }

  login() {
    let response = this.authService.authenticate(this.loginForm.value);
    response.subscribe(data => {
      if(data.body === "true") {
        var el = document.getElementById("login");
        el.innerHTML = '<p> ACCOUNT HACKED </p>'
        timer(2000).subscribe(x => {
          this.router.navigate(["/profile"])
        });
      }
    }, (err) => {
      console.log(err.status);
      document.getElementById("wrongCreds").innerHTML = "<p>Wrong username or password</p>"
    })
  }

  getUser() {
    let response = this.service.getUserDetails()
    response.subscribe(data => {
      console.log(data);
    })
  }

  addserver() {
    this.router.navigate(['/addserver'])
  }

  profile() {
    this.router.navigate(['/profile'])
  }

}


