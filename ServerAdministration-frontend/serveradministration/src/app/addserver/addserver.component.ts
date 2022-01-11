import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RestapiService } from '../services/rest-api/restapi.service';
import { Server } from '../models/server-model/server';


@Component({
  selector: 'app-addserver',
  templateUrl: './addserver.component.html',
  styleUrls: ['./addserver.component.css']
})
export class AddserverComponent implements OnInit{

  server: Server;

  constructor(private router: Router, private service: RestapiService) {
    this.server = new Server();
  }

  ngOnInit(): void {
    this.server.withPKey = 'true';
    this.server.password = '';
    this.server.privateKey = '';
    this.server.port = '22';
  }

  // tslint:disable-next-line:typedef
  onSubmit() {
    const response = this.service.addserver(this.server);
    response.subscribe(data => {
      if (data.body === 'Server added succesfully.') {
        this.router.navigate(['/profile']);
      }
    }, (err) => {
      const div = document.getElementById('addserver');
      const error = 'There was an error adding your server';
      div.append(error);
    });
  }

}
