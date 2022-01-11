package com.sda.controllers;

import com.sda.entities.AuthorityEntity;
import com.sda.entities.ServerCredentialsEntity;
import com.sda.entities.UserEntity;
import com.sda.repositories.AuthorityRepository;
import com.sda.repositories.UserRepository;
import com.sda.services.passwords.EncryptionDecryption;
import com.sda.services.userdetails.CustomUserDetailsService;
import com.sda.services.userdetails.CustomUserDetails;
import com.sda.services.jwt.JwtUtil;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class UserController {

    private UserRepository userRepository;

    private AuthorityRepository authorityRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/api/getdetails")
    public Optional<UserEntity> getLoggedInUser() throws Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
            UserEntity user = customUser.getUserEntity();
            for(ServerCredentialsEntity server: user.getServers()) {
                server.setPassword("");
            }
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @PostMapping("/register")
    public void register(@RequestBody UserEntity user) {

        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setUsername(user.getUsername());
        authorityEntity.setAuthority("USER");
        authorityEntity.setUser(user);
        userRepository.save(user);
        authorityRepository.save(authorityEntity);

    }

    @GetMapping("/register/check_email")
    public String checkEmail(String email) {
        System.out.println(email);
        UserEntity user = null;
        user = userRepository.findByEmail(email);
        if(user != null) {
            return "false";
        }
        return "true";
    }

}
