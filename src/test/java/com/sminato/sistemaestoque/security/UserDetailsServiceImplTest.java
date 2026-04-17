package com.sminato.sistemaestoque.security;

import com.sminato.sistemaestoque.entity.Usuario;
import com.sminato.sistemaestoque.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    // Armazena objeto instanciado sem re-criar a todo load
    private Usuario usuarioFicticio;

    @BeforeEach
    void setUp() {
        // Preenche com uma conta genérica existente
        usuarioFicticio = new Usuario();
        usuarioFicticio.setEmail("teste@teste.com");
        usuarioFicticio.setSenha("senha123");
    }

    @Test
    @DisplayName("loadUserByUsername_Sucesso - encontra usuário no BD e re-encapsula ele em Spring UserDetails")
    void loadUserByUsername_Sucesso() {
        // Arrange: Fornece o mock instanciado de BD
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuarioFicticio));

        // Act: Interface solicitada do AuthManager e implementada pelo Impl retorna as infos convertidas 
        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@teste.com");

        // Assert: As credenciais tem que bater com entity orginal.
        assertNotNull(userDetails);
        assertEquals("teste@teste.com", userDetails.getUsername());
        assertEquals("senha123", userDetails.getPassword());
        
        // Verifica que o repository foi acionado para efetuar a validação
        verify(usuarioRepository, times(1)).findByEmail("teste@teste.com");
    }

    @Test
    @DisplayName("loadUserByUsername_UsuarioNaoEncontrado - expulsa acesso e solta Exception no não encontro.")
    void loadUserByUsername_UsuarioNaoEncontrado() {
        // Arrange: DB Empty para qualquer inexistente simulado
        when(usuarioRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        // Act + Assert: Confirma estourar exception pedida contratualmente, cancelando Autenticação
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("inexistente@teste.com");
        });

        // Verificado apenas uma consulta pra evitar gargalos.
        verify(usuarioRepository, times(1)).findByEmail("inexistente@teste.com");
    }
}
