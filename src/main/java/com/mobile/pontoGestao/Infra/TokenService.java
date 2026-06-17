package com.mobile.pontoGestao.Infra;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mobile.pontoGestao.Models.Usuarios;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.jwt.secretKey}")
    private String secretKey;

    public String  generateToken(Usuarios usuario){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String token = JWT.create().withIssuer("ponto-gestao").withSubject(usuario.getId()).withExpiresAt(genExpiration()).withClaim("role",usuario.getPermissao().toString()).sign(algorithm);
            return token;
        }catch(JWTCreationException ex){
            throw new RuntimeException("Erro ao gerar o token",ex);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
    
            return JWT.require(algorithm)
                    .withIssuer("ponto-gestao")
                    .build()
                    .verify(token)
                    .getSubject();
    
        }catch(JWTVerificationException ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public String getPermissao(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm).withIssuer("ponto-gestao").build().verify(token).getClaim("role").toString();
        }catch(JWTVerificationException ex){
            return "";
        }
    }

    private Instant genExpiration(){
        return LocalDateTime.now().plusHours(6).toInstant(ZoneOffset.of("-03:00"));
    }
}
