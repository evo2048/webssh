package com.sda.services.passwords;

import com.jcraft.jsch.jce.AES128CBC;
import com.sda.entities.ServerCredentialsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class EncryptionDecryption {

    @Autowired
    private Cipher cipher;

    public ServerCredentialsEntity encryptPassword(String plainText, SecretKey secretKey, ServerCredentialsEntity server) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        AlgorithmParameters parameters = cipher.getParameters();
        byte[] iv = parameters.getParameterSpec(IvParameterSpec.class).getIV();
        server.setIv(iv);
        server.setPassword(encryptedText);
        return server;
    }

    public ServerCredentialsEntity encryptPrivateKey(String plainText, SecretKey secretKey, ServerCredentialsEntity server) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        AlgorithmParameters parameters = cipher.getParameters();
        byte[] iv = parameters.getParameterSpec(IvParameterSpec.class).getIV();
        server.setIv(iv);
        server.setPrivateKey(encryptedText);
        return server;
    }

    public String decrypt(String encryptedText, SecretKey secretKey, byte[] iv) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }

    public SecretKey generateKey(String password, String username) {

        byte[] salt = username.getBytes(StandardCharsets.UTF_8);
        char[] pass = password.toCharArray();

        KeySpec spec = new PBEKeySpec(pass, salt, 65536, 256);
        SecretKey tmp = null;

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            tmp = factory.generateSecret(spec);
        } catch (Exception e) {
            System.out.println("Could not generate key.");
        }

        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }
}

