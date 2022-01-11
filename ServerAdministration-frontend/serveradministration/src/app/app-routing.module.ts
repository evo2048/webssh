import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddserverComponent } from './addserver/addserver.component';
import { AuthenticationGuard } from './authguard/authentication.guard';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { ProfileComponent } from './profile/profile.component';
import { RegisterComponent } from './register/register.component';
import { TerminalComponent } from './terminal/terminal.component';

const routes: Routes = [
  { path: '', redirectTo:'home', pathMatch:'full' },
  { path: 'home', component: HomeComponent, },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'addserver', component: AddserverComponent, canActivate:[AuthenticationGuard] },
  { path: 'terminal', component: TerminalComponent, canActivate:[AuthenticationGuard] },
  { path: 'profile', component: ProfileComponent, canActivate:[AuthenticationGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
