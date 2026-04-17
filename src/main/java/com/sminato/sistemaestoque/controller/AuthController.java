package com.sminato.sistemaestoque.controller;

import com.sminato.sistemaestoque.dto.auth.AuthResponseDTO;
import com.sminato.sistemaestoque.dto.auth.LoginRequestDTO;
import com.sminato.sistemaestoque.dto.auth.RegistroRequestDTO;
import com.sminato.sistemaestoque.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    @Tag = agrupa endpoints no Swagger UI
 */
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @Operation = documenta o endpoint no Swagger
    // summary = título curto, description = descrição detalhada
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário e retorna o token JWT para uso imediato")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        AuthResponseDTO response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Fazer login", description = "Autentica o usuário e retorna o token JWT. Use o token no botão 'Authorize' do Swagger")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
