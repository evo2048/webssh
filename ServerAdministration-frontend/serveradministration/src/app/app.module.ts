import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { RestapiService } from './services/rest-api/restapi.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AddserverComponent } from './addserver/addserver.component';
import { TerminalComponent } from './terminal/terminal.component';
import { ProfileComponent } from './profile/profile.component';
import { WebsocketService } from './services/websockets/websocket.service';
import { AuthenticationGuard } from './authguard/authentication.guard';
import { AuthService } from './services/auth-service/auth.service';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    AddserverComponent,
    TerminalComponent,
    ProfileComponent
  ],

  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],

  providers: [RestapiService, AuthenticationGuard, WebsocketService, AuthService],

  bootstrap: [AppComponent]
})
export class AppModule { }
