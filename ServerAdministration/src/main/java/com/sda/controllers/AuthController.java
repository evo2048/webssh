package com.sda.controllers;

import com.sda.entities.UserEntity;
import com.sda.services.jwt.JwtUtil;
import com.sda.services.userdetails.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class AuthController {

    private CustomUserDetailsService customUserDetailsService;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthController(CustomUserDetailsService customUserDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/isAuthenticated")
    public boolean isAuth() {

        if(SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return false;
        }
        return true;

    }

    @PostMapping("/auth/authenticate")
    public boolean authenticate(@RequestBody UserEntity userEntity, HttpServletResponse response) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad Credentials", e);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEntity.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        Cookie jwtCookie = new Cookie("JWTOKEN", jwt);
        jwtCookie.setSecure(false);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(-1);
        jwtCookie.setPath("/");
        response.addHeader("Access-Control-Allow-Headers", "http://localhost:4200");
        response.addCookie(jwtCookie);

        return true;

    }

    @GetMapping("/auth/logout")
    public String logOut(HttpServletResponse response) {

        Cookie cookie = new Cookie("JWTOKEN", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "Logged out succesfully";

    }

}
