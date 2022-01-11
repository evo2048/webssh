import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  baseUrl: string;
  tokenUrl: string;
  authCheckUrl: string;
  logOutUrl: string;

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    responseType: "text" as "json",
    withCredentials: true, 
    observe: 'response' as 'response'
  };

  constructor(private http: HttpClient, private router: Router) {
    this.baseUrl = 'http://localhost:8080';
    this.tokenUrl = '/auth/authenticate';
    this.authCheckUrl = '/isAuthenticated';
    this.logOutUrl = '/auth/logout';
  }

  public authenticate(request) {

    return this.http.post(this.baseUrl + this.tokenUrl, request, this.httpOptions);

  }

  public logOut() {

    this.http.get(this.baseUrl + this.logOutUrl, this.httpOptions).subscribe(data => {
      console.log(data.body);
    })

  }

  public isTokenValid() {

    return this.http.get(this.baseUrl + this.authCheckUrl, {responseType: 'text' as 'json', withCredentials: true });

  }

}
