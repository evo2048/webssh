package com.sda.services.jwt;

import com.sda.services.userdetails.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String username = null;
        String token = null;
        String path = request.getRequestURI();
        Cookie[] cookies = request.getCookies();

        if(path.equals("/register") || path.equals("/auth/authenticate") || path.equals("/auth/logout") || path.equals("/register/check_email")) {

            chain.doFilter(request, response);
            return;

        } else {

            if(!(cookies == null || cookies.length < 1)) {

                for(Cookie cookie: cookies) {
                    if(cookie.getName().equals("JWTOKEN")) {
                        token = cookie.getValue();
                        username = jwtUtil.extractUsername(token);
                    }

                }
                if(username != null) {
                    if(SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
                        if(jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                }
            }
            chain.doFilter(request, response);
        }
    }
}
