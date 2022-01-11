import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationGuard } from '../authguard/authentication.guard';
import { AuthService } from '../services/auth-service/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent implements OnInit {

  constructor(private service: AuthService, private authGuard: AuthenticationGuard, private router: Router) { }

  ngOnInit(): void {
    if(this.authGuard.canActivate())
      this.router.navigate(['/profile'])
  }

  public login() {
    this.router.navigate(['/login']);
  }

  public register() {
    this.router.navigate(['/register']);
  }

}
