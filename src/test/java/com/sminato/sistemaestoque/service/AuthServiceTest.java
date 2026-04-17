package com.sminato.sistemaestoque.service;

import com.sminato.sistemaestoque.dto.auth.AuthResponseDTO;
import com.sminato.sistemaestoque.dto.auth.LoginRequestDTO;
import com.sminato.sistemaestoque.dto.auth.RegistroRequestDTO;
import com.sminato.sistemaestoque.entity.Usuario;
import com.sminato.sistemaestoque.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    // Dados reutilizáveis
    private RegistroRequestDTO registroRequest;
    private LoginRequestDTO loginRequest;
    private Usuario usuarioFicticio;
    private String tokenSimulado;

    @BeforeEach
    void setUp() {
        // Instancia requisições padrões para cadastro
        registroRequest = new RegistroRequestDTO();
        registroRequest.setNome("Usuario Teste");
        registroRequest.setEmail("teste@exemplo.com");
        registroRequest.setSenha("senha123");

        // Instancia requisição de login
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("teste@exemplo.com");
        loginRequest.setSenha("senha123");

        // Model simulada do que supostamente sai do banco
        usuarioFicticio = new Usuario();
        usuarioFicticio.setId(1L);
        usuarioFicticio.setNome("Usuario Teste");
        usuarioFicticio.setEmail("teste@exemplo.com");
        usuarioFicticio.setSenha("hashSenha");

        // Retornos Mockados
        tokenSimulado = "tokenJWT-teste";
    }

    @Test
    @DisplayName("registrar_Sucesso - deve registrar novo usuario e retornar informações do Token acoplado")
    void testRegistrar_Sucesso() {
        // Arrange: configurando o que os mocks rebatem baseado na requisição
        when(usuarioRepository.existsByEmail(registroRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registroRequest.getSenha())).thenReturn("hashSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioFicticio);
        when(jwtService.gerarToken(any(Usuario.class))).thenReturn(tokenSimulado);
        when(jwtService.getExpiration()).thenReturn(3600000L);

        // Act: Envia a requisição de cadastro pronta para o AuthService
        AuthResponseDTO response = authService.registrar(registroRequest);

        // Assert: Garantindo que as tratativas ocorreram e o Token foi passado adiante pelo save
        assertNotNull(response);
        assertEquals(tokenSimulado, response.getToken());
        assertEquals("teste@exemplo.com", response.getEmail());
        
        // Verifica que testou e logou 1 vez no banco
        verify(usuarioRepository, times(1)).existsByEmail(registroRequest.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrar_EmailJaExiste - deve lançar exception Runtime no momento em que já ter email ocupado")
    void testRegistrar_EmailJaExiste() {
        // Arrange: Simula check pelo email batendo 'true'
        when(usuarioRepository.existsByEmail(registroRequest.getEmail())).thenReturn(true);

        // Act + Assert: Exceção é laçada, travando execução
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registrar(registroRequest));
        assertEquals("Email já está em uso: teste@exemplo.com", exception.getMessage());
        
        // Valida que ao travar na exceção a entity NUNCA é salva
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("login_Sucesso - deve encontrar o user pelo banco, injetar JWT e aprovar")
    void testLogin_Sucesso() {
        // Arrange: mockando um retorno presente para o respectivo usuário
        when(usuarioRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuarioFicticio));
        when(jwtService.gerarToken(usuarioFicticio)).thenReturn(tokenSimulado);
        when(jwtService.getExpiration()).thenReturn(3600000L);

        // Act: Autenticação via login service
        AuthResponseDTO response = authService.login(loginRequest);

        // Assert: Processos batendo
        assertNotNull(response);
        assertEquals(tokenSimulado, response.getToken());
        assertEquals("teste@exemplo.com", response.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login_UsuarioNaoEncontradoAposAutenticar - deve estourar se magicamente o banco não o encontrar ")
    void testLogin_UsuarioNaoEncontradoAposAutenticar() {
        // Arrange: muda o objeto de busca para um alvo divergente (retorna empty result do banco)
        loginRequest.setEmail("inexistente@exemplo.com");
        when(usuarioRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act + Assert: Estoura uma mensagem exception sem devolver responses válidos
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        
        // Porém, bateu no AuthManager
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
