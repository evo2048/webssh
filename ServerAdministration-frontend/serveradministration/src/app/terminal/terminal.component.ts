import { Component, ElementRef, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { timer } from 'rxjs';
import { Terminal } from 'xterm';
import { ServerConn } from '../models/server-conn/serverconn';
import { userDetails } from '../models/user-model/user-details';
import { RestapiService } from '../services/rest-api/restapi.service';
import { WebsocketService } from '../services/websockets/websocket.service';

@Component({
  selector: 'app-terminal',
  templateUrl: './terminal.component.html',
  styleUrls: ['./terminal.component.css'],
  encapsulation: ViewEncapsulation.None
})

export class TerminalComponent implements OnInit {

  @ViewChild('terminal') terminalDiv: ElementRef;

  user: userDetails;
  closeClient = null;

  public server = {
    serverId: '',
    masterPassword: '',
  };

  constructor(public websocket: WebsocketService, public service: RestapiService) {
    this.service.getUserDetails().subscribe(data => {
      sessionStorage.username = data.username;
    });
  }

  ngOnInit(): void {
    const response = this.service.getUserDetails();
    response.subscribe(data => {
      this.user = data;
    });
  }

  open(serverId) {
    this.server.serverId = serverId;
    this.openTerminal(this.server);
  }

  close() {
    this.closeClient.close();
  }

  public connected() {
    if (this.closeClient !== null) {
      return false;
    }
    return true;
  }

  openTerminal(server: ServerConn) {

    const client = new WebsocketService();
    const term = new Terminal();

    this.closeClient = client;

    term.onData( function(data) {
      client.sendClientData(data);
    });

    term.open(this.terminalDiv.nativeElement);

    term.write('Connecting...\n\r');

    client.connect ({

      onerror(error: string) {
        term.write('Error: ' + error + ' \r\n');
      },

      onopen() {
        client.sendInitData(server);
      },

      onclose() {
        term.write('Connection closed.\n\r');
        term.clear();
        term.write('\n\n\r- - - - - - - - - - - - - - - - -\n\n\r        Reloading page...\n\n\r- - - - - - - - - - - - - - - - -');
        timer(2000).subscribe(x => {
          window.location.reload();
        });
      },

      onmessage(data) {
        term.write(data);
      },

    });
  }

}
