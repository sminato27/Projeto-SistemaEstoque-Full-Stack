package com.sminato.sistemaestoque.security;

import com.sminato.sistemaestoque.repository.IUsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/*
    Classe separada para evitar ciclo dos Beans.
    1- Spring cria UserDetailsServiceImpl
    2- Spring cria JwtAuthenticationFilter (depende do UserDetailsServiceImpl que já existe)
    3- Spring cria SecurityConfig (depende de JwtAuthenticationFilter que já existe)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /*
        O Spring Security chama esse métod0 passando o "username".
        username = email

        Retorna o UserDetails (entity Usuario já implementa isso).
        Se não encontrar, lança UsernameNotFoundException.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com email: " + username));
    }
}
