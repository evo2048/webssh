package com.sda.connection.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.sda.connection.constant.ConstantPool;
import com.sda.connection.info.Command;
import com.sda.connection.info.ConnectionInfo;
import com.sda.connection.info.ConnectionData;
import com.sda.connection.service.WebSocketTransportService;
import com.sda.entities.ServerCredentialsEntity;
import com.sda.repositories.ServerRepository;
import com.sda.services.passwords.EncryptionDecryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebSocket implements WebSocketTransportService {

    @Autowired
    private EncryptionDecryption encryptionDecryption;
    @Autowired
    private ServerRepository serverRepository;

    private ConnectionInfo connectionInfo = null;
    private Logger logger = LoggerFactory.getLogger(WebSocket.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static Map<String, Object> connections = new ConcurrentHashMap<>();

    @Override
    public void initConnection(WebSocketSession session) {
        JSch jSch = new JSch();
        connectionInfo = new ConnectionInfo();
        connectionInfo.setJSch(jSch);
        connectionInfo.setWebSocketSession(session);
        connectionInfo.setConnected(false);
        String uuid = String.valueOf(session.getAttributes().get(ConstantPool.USER_ID));
        connections.put(uuid, connectionInfo);
        try {
            session.sendMessage(new TextMessage("Websocket connected! \n\r"));
        } catch(IOException e) {
            logger.error("Could not send init message.");
        }
    }

    @Override
    public void recvHandle(String message, WebSocketSession session) {

        ObjectMapper objectMapper = new ObjectMapper();
        String user = String.valueOf(session.getAttributes().get(ConstantPool.USER_ID));
        connectionInfo = (ConnectionInfo) connections.get(user);

        if(connectionInfo != null && connectionInfo.isConnected()) {
            String command = null;

            try {
                command = objectMapper.readValue(message, Command.class).getCommand();
            } catch(IOException e) {
                logger.error("Could not get data from the frontend.");
                logger.error("Wrong data type recieved.");
            }

            try {
                transToSSH(connectionInfo.getChannel(), command);
            } catch (IOException e) {

                logger.error("SSH connection exception");
                logger.error("Exception information: {}", e.getMessage());

                try {
                    session.sendMessage(new TextMessage("Connection closed."));
                } catch (Exception ex) {
                    logger.error("Could not close connection. \n {}", ex.getMessage());
                }

                close(session);
            }
        }

        if(!connectionInfo.isConnected()) {
            ConnectionData connectionData = null;

            try {
                connectionData = objectMapper.readValue(message, ConnectionData.class);
            } catch (IOException e) {
                logger.error ("JSON conversion exception");
                logger.error ("exception information: {}", e.getMessage());
                return;
            }

            ConnectionInfo connectionInfo = (ConnectionInfo) connections.get(user);

            Optional<ServerCredentialsEntity> optional = serverRepository.findById(connectionData.getServerId());
            ServerCredentialsEntity server = optional.get();

            SecretKey key = encryptionDecryption.generateKey(connectionData.getMasterPassword(), user);

            if(key != null) {
                if(server.isWithPKey()) {
                    try {
                        String pvKey = encryptionDecryption.decrypt(server.getPrivateKey(), key, server.getIv());
                        server.setPrivateKey(pvKey);
                    } catch(Exception e) {
                        logger.error("Could not DECRYPT private key.");
                        try{
                            sendMessage(session, "Wrong master password.\n\r".getBytes(StandardCharsets.UTF_8));
                        } catch(Exception ex) {
                            System.out.println(e.getMessage());
                        }
                        close(session);
                    }
                } else {
                    try {
                        server.setPassword(encryptionDecryption.decrypt(server.getPassword(), key, server.getIv()));
                    } catch(Exception e) {
                        logger.error("Could not DECRYPT password.");
                        try{
                            sendMessage(session, "Wrong master password.\n\r".getBytes(StandardCharsets.UTF_8));
                        } catch(Exception ex) {
                            System.out.println(e.getMessage());
                        }
                        close(session);
                    }
                }
            }
            if(server.getUserEntity().getUsername().equals(user))
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connectToSSH(connectionInfo, server, session);
                        } catch (JSchException | IOException e) {
                            logger.error("SSH connection exception\n");
                            logger.error("Exception information: {}", e.getMessage());
                            close(session);
                        }
                    }
                });


        }

    }

    @Override
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    @Override
    public void close(WebSocketSession session) {
        String user = String.valueOf(session.getAttributes().get(ConstantPool.USER_ID));
        ConnectionInfo connectionInfo = (ConnectionInfo) connections.get(user);
        if (connectionInfo != null) {
            if (connectionInfo.getChannel() != null)
                connectionInfo.getChannel().disconnect();
            connections.remove(user);
        }
        try{
            session.close();
        } catch(Exception e) {
            logger.error("Error info: {}", e.getMessage());
        }
    }

    private void connectToSSH(ConnectionInfo connectionInfo, ServerCredentialsEntity server, WebSocketSession webSocketSession) throws JSchException, IOException {

        String user = String.valueOf(webSocketSession.getAttributes().get(ConstantPool.USER_ID));

        Session session = null;
        Channel channel = null;

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        if(server.isWithPKey()) {

            connectionInfo.getJSch().addIdentity(user, server.getPrivateKey().getBytes(StandardCharsets.UTF_8), null, null);
            session = connectionInfo.getJSch().getSession(server.getUsername(), server.getServerIp(), server.getPort());
            session.setConfig(config);
            session.connect(5000);

            channel = session.openChannel("shell");
            channel.connect(5000);

        } else {

            session = connectionInfo.getJSch().getSession(server.getUsername(), server.getServerIp(), server.getPort());
            session.setPassword(server.getPassword());
            session.setConfig(config);
            session.connect(5000);

            channel = session.openChannel("shell");
            channel.connect(5000);

        }

        connectionInfo.setChannel(channel);
        connectionInfo.setConnected(true);
        connections.put(user, connectionInfo);

        transToSSH(channel, "");

        InputStream inputStream = channel.getInputStream();

        try {

            byte[] buffer = new byte[1024];
            int i = 0;
            while((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {

            session.disconnect();
            channel.disconnect();

            if (inputStream != null)
                inputStream.close();

        }

    }

    private void transToSSH(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }
}
