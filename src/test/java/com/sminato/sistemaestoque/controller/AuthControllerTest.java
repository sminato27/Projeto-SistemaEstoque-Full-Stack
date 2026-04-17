package com.sminato.sistemaestoque.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sminato.sistemaestoque.dto.auth.AuthResponseDTO;
import com.sminato.sistemaestoque.dto.auth.LoginRequestDTO;
import com.sminato.sistemaestoque.dto.auth.RegistroRequestDTO;
import com.sminato.sistemaestoque.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sminato.sistemaestoque.service.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desabilita os filtros de segurança (JWT) para testar apenas o controller
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // Beans de autenticação mockados e importados, pois é usado no application-stack
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    // Variáveis que abstraem repetições nos testes instanciadas globalmente pra test suite
    private RegistroRequestDTO registroRequest;
    private LoginRequestDTO loginRequest;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        // Define requests e devolução padronizada para Auth
        registroRequest = new RegistroRequestDTO();
        registroRequest.setNome("Teste");
        registroRequest.setEmail("teste@teste.com");
        registroRequest.setSenha("senha123");

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("teste@teste.com");
        loginRequest.setSenha("senha123");

        // Um DTO base pro controlador espelhar as simulações
        authResponseDTO = new AuthResponseDTO("tokenJWT", 3600L, "teste@teste.com", "Teste");
    }

    @Test
    @DisplayName("registrar_Sucesso - deve receber as requisições HTTP REST do registrar repassando Json Status")
    void registrar_Sucesso() throws Exception {
        // Arrange: Diz ao authService o que devolver quando algo é registrado (mockagem de Service)
        when(authService.registrar(any(RegistroRequestDTO.class))).thenReturn(authResponseDTO);

        // Act & Assert: Realiza e valida a chamada ao Endpoint no MockMvc validando devoluções no map de resposta JSON $
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("tokenJWT"))
                .andExpect(jsonPath("$.email").value("teste@teste.com"));
    }

    @Test
    @DisplayName("login_Sucesso - consome HTTP para rota login repassando Body correto como Request Object")
    void login_Sucesso() throws Exception {
        // Arrange: Diz ao authService o que devolver quando logado com sucesso (AuthResponse Payload)
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponseDTO);

        // Act & Assert: Dispara o fluxo POST usando as chamadas limpas da WebMvc e conferindo os matchs $ 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tokenJWT"))
                .andExpect(jsonPath("$.email").value("teste@teste.com"));
    }
}
