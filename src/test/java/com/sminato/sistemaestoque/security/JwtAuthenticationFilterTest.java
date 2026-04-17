package com.sminato.sistemaestoque.security;

import com.sminato.sistemaestoque.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Valores padrão úteis nos testes
    private String tokenSimulado;
    private String userEmail;

    @BeforeEach
    void setUp() {
        // Limpar o contexto de segurança antes de cada teste para não vazar info do ambiente
        SecurityContextHolder.clearContext();
        tokenSimulado = "tokenValid";
        userEmail = "teste@teste.com";
    }

    @AfterEach
    void tearDown() {
        // Esvazia dps que rodou. Garantindo proteção de estado do escopo do teste.
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("doFilterInternal_ComTokenValido - processa chain retirando Bearer e alimentando TokenContext de sessão")
    void doFilterInternal_ComTokenValido() throws ServletException, IOException {
        // Arrange: configurando dependências vitais do Spring Security pra essa req filtrada
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenSimulado);
        when(jwtService.extrairUsername(tokenSimulado)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValido(tokenSimulado, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptySet());

        // Act: Envia a chain ao filtro simulando o processo do servlet 
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert: Filtro precisa preencher o contexto SecurityContext do Spring, e prosseguir request via FilterChain.do
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal_SemHeaderAuthorization - descarta validação pro interceptador de HTTP não estourar Exception no Authless")
    void doFilterInternal_SemHeaderAuthorization() throws ServletException, IOException {
        // Arrange: Mock da request não traz Header HTTP preenchido
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act: Roda a interceptação do filtro pra bater na trava de segurança de retorno vazio
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert: Ninguém validado no Context Holder, logo ele aciona o blockador subjacente do config de rotas depois (anyRequestAuthenticated)
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
