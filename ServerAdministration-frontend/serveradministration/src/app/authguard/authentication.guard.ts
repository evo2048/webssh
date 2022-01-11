import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {

  constructor(private router: Router, private service: AuthService){ }

  canActivate() {
    return this.service.isTokenValid().pipe(map(data => {

      if(data.toString() == "true")
        return true;
      return false;

    }))
  }

}
