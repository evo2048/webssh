import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RestapiService } from '../services/rest-api/restapi.service';
import { userDetails } from '../models/user-model/user-details';
import { AuthService } from '../services/auth-service/auth.service';
import { timer } from 'rxjs';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

export class ProfileComponent implements OnInit {

  public user: userDetails;

  constructor(private service: RestapiService, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    const response = this.service.getUserDetails();
    response.subscribe(data => {
      this.user = data;
    });
  }

  public addServer() {
    this.router.navigate(['/addserver']);
  }

  public deleteServer(id: string) {
    this.service.deleteServer(id).subscribe(data => {
      if (data.body === 'Deleted') {
        alert('Deleted');
        timer(500).subscribe(x => {
          window.location.reload();
        });
      } else {
        alert('Server already deleted, please refresh page.');
      }
    });
  }

  public logOut() {
    this.authService.logOut();
    timer(1000).subscribe(x => {
      this.router.navigate(['/home']);
    });
  }

}
