package com.sminato.sistemaestoque.service;

import com.sminato.sistemaestoque.dto.auth.AuthResponseDTO;
import com.sminato.sistemaestoque.dto.auth.LoginRequestDTO;
import com.sminato.sistemaestoque.dto.auth.RegistroRequestDTO;
import com.sminato.sistemaestoque.entity.Usuario;
import com.sminato.sistemaestoque.repository.IUsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(IUsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /*
        Registro de novo usuário.
        1- Verifica se o email já está em uso.
        2- Cria o usuário com a senha hasheada
        3- Salva no banco
        4- Gera e retorna o token JWT (loga o usuário após o registro)
     */
    @Transactional
    public AuthResponseDTO registrar(RegistroRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        /*
            passwordEncoder.encode() faz o hash BCrypt da senha.
            Nunca salva a senha em texto puro.
            Nunca é possível reverter o hash para obter a senha original.
         */
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Gera o token JWT para o usuário recém-criado
        String token = jwtService.gerarToken(usuarioSalvo);

        return new AuthResponseDTO(token, jwtService.getExpiration(), usuarioSalvo.getEmail(), usuarioSalvo.getNome());

    }

    /*
        Login do usuário.
        1- AuthenticationManager verifica email+senha (busca no banco e compara o hash BCrypt).
        2-
     */
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        /*
            authenticate() faz tod0 o trabalho pesado:
            - Chama userDetailsService.loadUserByUsername(email)
            - Compara a senha com o hash no banco via passwordEncoder.matches()
            - Se qualquer coisa falhar > lança AuthenticationException
         */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        // Se chegou aqui, a autenticação foi bem-sucedida
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario);

        return new AuthResponseDTO(token, jwtService.getExpiration(), usuario.getEmail(), usuario.getNome());
    }
}
