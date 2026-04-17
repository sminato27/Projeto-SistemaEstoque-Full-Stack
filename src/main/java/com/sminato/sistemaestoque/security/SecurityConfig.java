package com.sminato.sistemaestoque.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
    @Configuration = classe de configuração do Spring
    @EnableWebSecurity = ativa o Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*
        SecurityFilterChain = define as regras de segurança da aplicação.
        - Quais endpoints são públicos (não precisam de token)
        - Quais endpoints precisam de autenticação
        - Como gerenciar sessões
        - Quais filtros usar
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                /*
                    CSRF (Cross-Site Request Forgery) = ataque onde um site malicioso faz requisições em nome do usuário logado.
                    Em APIs Rest com JWT é interessante desabilitar o CSRF porque:
                    - O token JWT já protege contra esse tipo de ataque.
                    - APIs REST são stateless (sem sessão de servidor).
                    - CSRF só é relevante para aplicações com cookies de sessão.
                 */
                .csrf(AbstractHttpConfigurer::disable)
                /*
                    Define as regras de autorização por endpoint.
                    O Spring avalia de cima para baixo, então a ordem é importante.
                 */
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticação são públicos, qualquer um acessa
                        .requestMatchers("/api/auth/**").permitAll()
                        // Swagger e documentação OpenAPI são públicos (em produção pode ser interessante proteger isso)
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // GET nos produtos é público (qualquer um pode ver o catálogo)
                        .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
                        // Qualquer outra requisição exige autenticação
                        .anyRequest().authenticated()
                )
                /*
                   STATELESS = sem sessão de servidor.
                   Com JWT, cada requisição é independente, o token contém tudo o que o servidor precisa saber.
                   O servidor não precisa guardar estado de sessão.

                   ALWAYS (padrão) = cria sessão HTTP no servidor.
                   STATELESS = nunca cria sessão, bom para APIS REST com JWT.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Define qual AuthenticationProvider usar (nesse caso, o BCrypt)
                .authenticationProvider(authenticationProvider)
                // Adiciona o filtro JWT antes do filtro padrão de username/senha.
                // Garante que o JWT é verificado primeiro em cada requisição.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /*
        AuthenticationProvider = define como autenticar um usuário.
        DaoAuthenticationProvider usa o banco de dados para autenticar.
        1- Carrega o usuário pelo email (via UserDetailsService)
        2- Compara a senha digitada com o hash salvo no banco (via PasswordEncoder)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /*
        AuthenticationManger = gerencia o processo de autenticação.
        Usado no AuthService quando o usuário faz login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
        BCryptPasswordEncoder = faz o hash das senhas com o algoritmo BCrypt.
        - É lento de propósito (dificulta ataques bruteforce).
        - Gera um salto aleatório a cada hash (mesma senha gera hashes diferentes).
        - É o padrão do mercado.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
