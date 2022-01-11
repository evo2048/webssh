import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Server } from '../../models/server-model/server';
import { userDetails } from 'src/app/models/user-model/user-details';

@Injectable({
  providedIn: 'root'
})
export class RestapiService {

  private baseUrl: string;
  private registerUrl: string;
  private getDetailsUrl: string;
  private addServerUrl: string;
  private deleteServerUrl: string;
  private checkEmailUrl: string;

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    responseType: "text" as "json",
    withCredentials: true,
    observe: 'response' as 'response'
  };

  constructor(private http: HttpClient) {
    this.baseUrl = 'http://localhost:8080';
    this.registerUrl = '/register';
    this.getDetailsUrl = '/api/getdetails';
    this.addServerUrl = '/api/servers/add-server';
    this.deleteServerUrl = "/api/servers/delete-server";
    this.checkEmailUrl = "/register/check_email?email=";
  }

  public register(picture: any) {

    return this.http.post(this.baseUrl + this.registerUrl, picture);

  }

  public addserver(server: Server) {

    return this.http.post(this.baseUrl + this.addServerUrl, server, this.httpOptions);

  }

  public deleteServer(id: any) {

    return this.http.delete(this.baseUrl + this.deleteServerUrl + "?id=" + id, this.httpOptions);

  }

  public getUserDetails() {

    return this.http.get<userDetails>(this.baseUrl + this.getDetailsUrl, { withCredentials: true });

  }

  public checkEmail(email: string) {

    return this.http.get(this.baseUrl + this.checkEmailUrl + email, {responseType: 'text' as 'json'});

  }

}
