package com.mobile.pontoGestao.Infra;

import com.mobile.pontoGestao.Erros.AuthorizationTokenInvalidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
      
        String token = recoverToken(request);
        
        if (token != null) {
            try {
                String subject = tokenService.validateToken(token);
    
                if (subject != null && !subject.isEmpty()) {
                    var authentication = new UsernamePasswordAuthenticationToken(subject, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }


    private String recoverToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token == null){
            return null;
        }else{
            return token.replace("Bearer ", "");
        }
    }
}
