package com.mobile.pontoGestao.Infra;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.mobile.pontoGestao.Erros.AuthorizationTokenInvalidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        if(token != null){
            String subject = tokenService.validateToken(token);

            if (subject.isEmpty()) throw new AuthorizationTokenInvalidException("Token inválido");
            String role = tokenService.getPermissao(token);
            List<GrantedAuthority> grantedAuthorities = List.of();
            if (role.equals("ADMIN")) {
                grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }else {
                grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_FUNCIONARIO"));
            }
            var authentication = new UsernamePasswordAuthenticationToken(subject,null,grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
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