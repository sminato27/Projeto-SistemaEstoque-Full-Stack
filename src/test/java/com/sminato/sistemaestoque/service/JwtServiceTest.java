package com.sminato.sistemaestoque.service;

import com.sminato.sistemaestoque.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Objetos repassados em variáveis de instância limitando re-escrita
    private Usuario usuarioFicticio;
    private String tokenGerado;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Configurando a secretKey real no serviço mockado
        ReflectionTestUtils.setField(jwtService, "secretKey", "K0Swsf5Dv4g2UUeBQsxdfcPObAcXII0N9gjbkyKbCho=");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hora
        
        // Criando usuário fictício 
        usuarioFicticio = new Usuario();
        usuarioFicticio.setEmail("test@exemplo.com");

        // Geração prévia de Token
        tokenGerado = jwtService.gerarToken(usuarioFicticio);
    }

    @Test
    @DisplayName("testGerarToken - deve gerar um token não nulo e com o username correto guardado")
    void testGerarToken() {
        // Act: Cria um token a partir do usuário pré inicializado no Antes de Cada (BeforeEach)
        String token = jwtService.gerarToken(usuarioFicticio);

        // Assert: Valida se o token não veio vazio e se o claim de subject reflete o próprio username passado sem adulterações
        assertNotNull(token);
        String usernameExtraido = jwtService.extrairUsername(token);
        assertEquals("test@exemplo.com", usernameExtraido);
    }

    @Test
    @DisplayName("testIsTokenValido - verifica se match de username entre token e o user retorna true para um token bem estruturado")
    void testIsTokenValido() {
        // Act + Assert: Tenta submeter a validade entre a verificação de claims de um token legítimo já criado
        assertTrue(jwtService.isTokenValido(tokenGerado, usuarioFicticio));
    }

    @Test
    @DisplayName("testExtrairUsername - deve extrair o claim de username em um token que reflete as infos do criador")
    void testExtrairUsername() {
        // Act: Processa o Jwt pre-existente
        String username = jwtService.extrairUsername(tokenGerado);

        // Assert: Analisa a consistencia da info retornada pelo parser
        assertEquals("test@exemplo.com", username);
    }
}
