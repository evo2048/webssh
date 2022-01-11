package com.sda.connection.interceptor;

import com.sda.connection.constant.ConstantPool;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if(serverHttpRequest instanceof ServletServerHttpRequest) {

            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) serverHttpRequest;
            HttpHeaders headers = serverRequest.getHeaders();
            String header = headers.getOrigin();
            System.out.println("\n\n" + headers + "\n\n");

            if(header != null && header.equals("http://localhost:4200")) {
                String username = serverRequest.getURI().toString().substring(33);
                map.put(ConstantPool.USER_ID, username);
                return true;
            } else {
                System.out.println("Origin header null or different than http://localhost:4200");
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
    }

}
