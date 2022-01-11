package com.sda.connection.info;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class ConnectionInfo {

    private WebSocketSession webSocketSession;
    private JSch jSch;
    private Channel channel;
    private boolean connected;

}
