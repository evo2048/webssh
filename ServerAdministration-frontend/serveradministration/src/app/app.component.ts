import { Component, HostListener } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @HostListener("window:onbeforeunload",["$event"])
    clearLocalStorage(event){
        localStorage.clear();
    }
    
  ngOnDestroy() { 
    alert("KKk")
    localStorage.removeItem('authToken');
 }
}
