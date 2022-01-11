package com.sda.controllers;

import com.sda.entities.ServerCredentialsEntity;
import com.sda.entities.UserEntity;
import com.sda.repositories.ServerRepository;
import com.sda.services.passwords.EncryptionDecryption;
import com.sda.services.userdetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class ServerCredentialsController {

    private ServerRepository serverRepository;
    private EncryptionDecryption encryptionDecryption;

    @Autowired
    public ServerCredentialsController(ServerRepository serverRepository, EncryptionDecryption encryptionDecryption) {
        this.serverRepository = serverRepository;
        this.encryptionDecryption = encryptionDecryption;
    }

    @PostMapping("/api/servers/add-server")
    public String addServer(@RequestBody ServerCredentialsEntity server) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.getPrincipal() instanceof CustomUserDetails) {

            CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
            UserEntity user = customUser.getUserEntity();

            SecretKey key = encryptionDecryption.generateKey(server.getMasterPassword(), user.getUsername());

            if(key != null) {
                if(server.isWithPKey()) {
                    server.setPrivateKey(eliminateSpaces(server));
                    try{
                        server = encryptionDecryption.encryptPrivateKey(server.getPrivateKey(), key, server);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Could not add server.";
                    }
                } else {
                    try{
                        server = encryptionDecryption.encryptPassword(server.getPassword(), key, server);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Could not add server.";
                    }
                }
                server.setMasterPassword("");
            } else {
                return "Key not generated";
            }

            server.setUserEntity(user);
            serverRepository.save(server);

        }

        return "Server added succesfully.";

    }

    @DeleteMapping("/api/servers/delete-server")
    public String deleteServer(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<ServerCredentialsEntity> server = serverRepository.findById(id);
        if(server.get().getUserEntity().getUsername().equals(auth.getName())) {
            serverRepository.deleteById(id);
            return "Deleted";
        }
        return "Not authorized";
    }

    private String eliminateSpaces(ServerCredentialsEntity server) {
        String[] arr = new String[3];
        char[] pKey = server.getPrivateKey().toCharArray();
        String finalPKey = null;

        if(pKey[11] == 'R') {
            arr[0] = server.getPrivateKey().substring(0, 31);
            arr[1] = server.getPrivateKey().substring(32, pKey.length - 30);
            arr[2] = server.getPrivateKey().substring(pKey.length - 29, pKey.length);
        } else if(pKey[11] == 'O'){
            arr[0] = server.getPrivateKey().substring(0, 35);
            arr[1] = server.getPrivateKey().substring(36, pKey.length - 34);
            arr[2] = server.getPrivateKey().substring(pKey.length - 33, pKey.length);
            arr[0] = arr[0].replace("OPENSSH", "RSA");
            arr[2] = arr[2].replace("OPENSSH", "RSA");
        }

        arr[1] = arr[1].replaceAll("\\s", "\n");
        finalPKey = arr[0] + "\n" + arr[1] + "\n" + arr[2];

        System.out.println(finalPKey);

        return finalPKey;
    }



}
