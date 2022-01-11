import { Injectable } from '@angular/core';
import { ServerConn } from 'src/app/models/server-conn/serverconn';

@Injectable({
  providedIn: 'root'
})

export class WebsocketService{

  protocol: string;

  constructor() { }

  private connection: WebSocket;

  generateEndpoint() {
    if (window.location.protocol == 'https:') {
      this.protocol = 'wss://';
    } else {
      this.protocol = 'ws://';
    }
    let endpoint = this.protocol + 'localhost:8080/api/webssh';
    return endpoint;
  }

  connect(options) {

    let endpoint = this.generateEndpoint() + '/' + sessionStorage.username;

    if (window.WebSocket) {
        this.connection = new WebSocket(endpoint);
    } else {
       options.onerror('WebSocket Not Supported');
       return;
    }

    this.connection.onopen = function() {
      options.onopen();
    };

    this.connection.onmessage = function(data) {
      data = data.data.toString();
      options.onmessage(data);
    };

    this.connection.onclose = function() {
      if (this.readyState === 3){
        options.onclose();
      } else {
        console.log('Something went wrong.');
      }
    };

    this.connection.onerror = function(e) {
      options.onerror(e);
    };

  }

  close() {
    this.connection.close();
  }

  send(data) {
    this.connection.send(JSON.stringify(data));
  }

  sendInitData(server: ServerConn) {
    this.connection.send(JSON.stringify(server));
  }

  sendClientData(data) {
    this.connection.send(JSON.stringify(
      {
        command: data
      })
    );
  }

}
