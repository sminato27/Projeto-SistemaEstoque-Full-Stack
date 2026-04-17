package com.sminato.sistemaestoque.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
    JwtService é responsável por tudo relacionado ao token JWT:
    - Gerar o token após o login
    - Extrair informações do token (quem é o usuário e quando expira)
    - Validar se o token ainda é válido

    Esse serviço não tem lógica de negócio, por isso fica separado do AuthService.
 */
@Service
public class JwtService {
    /*
        @Value lê o valor do application.properties.
        "${jwt.secret}" busca a propriedade "jwt.secret".
     */
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Geração do token
    /*
        Gera um token JWT para um usuário.
        O token contém:
        - subject: o email do usuário (identificador)
        - issuedAt: quando foi criado
        - expiration: quando vai expirar
        - assinado com a secretKey
     */
    public String gerarToken(UserDetails userDetails) {
        return gerarToken(new HashMap<>(), userDetails);
    }

    /*
        Versão que aceita claims extras (informações adicionais no token).
        Pode colocar o role do usuário como extraClaim.
        O Payload do JWT é codificado em Base64, não criptografado.
        Qualquer pessoa pode decodificar e ler o conteúdo.
        O que garante a segurança é a assinatura, não o conteúdo, então é interessante não colocar dados sensíveis aqui.
     */
    public String gerarToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Validação do token
    /*
        Valida se o token é válido para aquele usuário.
        Um token é válido quando:
        - O subject (email) bate com o username do usuário.
        - O token não expirou.
     */
    public boolean isTokenValido(String token, UserDetails userDetails) {
        final String username = extrairUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpirado(token);
    }

    public boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    // Extração das informações do token
    /*
        Extrai o email (subject) do token.
        Usado pelo filtro JWT para saber quem está fazendo a requisição.
     */
    public String extrairUsername(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    private Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    /*
        Métod0 genérico para extrair qualquer claim do token.
        Recebe uma função que diz "qual claim extrair".
        Ex.: extrairClaim(token, Claims::getSubject) > pega o subject
             extrairClaim(token, Claims::getExpiration) > pega a expiração
     */
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    /*
        Decodifica e valida a assinatura do token, retornando todos os claims.
        Se a assinatura for inválida ou o token adulterado, lança exceção.
     */
    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
        Converte a secretKey (String) em um objeto SecretKey.
        A biblioteca JWT precisa de um SecretKey, não de uma String.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getExpiration() {
        return expiration;
    }

}
