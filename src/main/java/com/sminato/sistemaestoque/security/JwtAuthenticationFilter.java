package com.sminato.sistemaestoque.security;

import com.sminato.sistemaestoque.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
    O filtro JWT é executado em cada requisição, antes de chegar ao Controller.
    OncePerRequestFilter garante que o filtro executa exatamente uma vez por requisição, mesmo que
    o Spring tente aplicá-lo múltiplas vezes internamente.

    O fluxo que esse filtro executa:
    1- Pega o header "Authorization" da requisição.
    2- Extrai o token JWT (remove o "Bearer" do início)
    3- Extrai o email do token
    4- Busca o usuário no banco pelo email
    5- Valida o token
    6- Se válido: registra o usuário como autenticado no Spring Security
    7- Deixa a requisição continuar para o Controller
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Pega o header Authorization da requisição
        // Ex.: "Bearer eyJhedHeh..."
        final String authHeader = request.getHeader("Authorization");

        // Se não tem header Authorization ou não começa com "Bearer ",
        // não é uma requisição autenticada por JWT, então deixa passar
        // (o Spring Security vai bloquear depois se o endpoint exigir auth)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " (7 caracteres) para pegar só o token
        final String jwt = authHeader.substring(7);

        // Extrai o email do token (sem ainda validar a assinatura completa)
        final String userEmail;
        try {
            userEmail = jwtService.extrairUsername(jwt);
        } catch (Exception e) {
            // Token malformado ou adulterado > não autentica, deixa continuar
            // O Spring Security vai bloquear se o endpoint exigir auth
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Busca o usuário no banco pelo email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Valida o token: email bate? não expirou?
            if (jwtService.isTokenValido(jwt, userDetails)) {

                /*
                    Cria um objeto de autenticação do Spring Security.
                    UsernamePasswordAuthenticationToken representa um usuário autenticado.
                    Parâmetros usados:
                    1- principal: o UserDetails (quem é o usuário)
                    2- credentials: null (não precisa da senha aqui)
                    3- authorities: as permissões do usuário
                 */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Adiciona detalhes da requisição HTTP ao token de autenticação
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /*
                    Registra a autenticação no SecurityContextHolder.
                    É aqui que o Spring Security sabe que o usuário está autenticado.
                    A partir daqui, qualquer parte do código pode chamar:
                    SecurityContextHolder.getContext().getAuthentication() para saber quem está logado.
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Passa a requisição para o próximo filtro ou Controller.
        filterChain.doFilter(request, response);
    }
}
