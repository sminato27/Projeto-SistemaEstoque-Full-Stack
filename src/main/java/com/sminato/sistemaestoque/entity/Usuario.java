package com.sminato.sistemaestoque.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
    A entidade usuário representa quem pode acessar o sistema.
    UserDetails = interface do Spring Security que define o "contrato" que o Spring precisa
    para autenticar alguém: getUsername(), getPassword(), getAuthorities(), e os métodos is*().

    A implementação do UserDetails direto na entidade serve para o Spring Security
    usar essa classe diretamente para verificar credenciais e montar o contexto de segurança.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements org.springframework.security.core.userdetails.UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_nome", nullable = false, length = 100)
    private String nome;

    // O email é o username no Spring Security.
    // unique = true garante que não existem dois usuários com o mesmo email.
    @Column(name = "usuario_email", unique = true, nullable = false, length = 100)
    private String email;

    // Aqui é salvo o hash BCrypt da senha, o Spring Security compara os hashes.
    @Column(name = "usuario_senha", nullable = false)
    private String senha;

    // Métodos do contrato UserDetails

    // getAuthorities() = retorna as permissões/role do usuário.
    // Por enquanto não tem nenhuma role, senão deveriam ser criadas ROLE_ADMIN, ROLE_USER, etc.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // getUsername() = retorna o identificador único do usuário.
    // Nesse caso, o email é o identificador único.
    @Override
    public String getUsername() {
        return this.email;
    }

    // getPassword() = retorna a senha (hash) para o Spring comparar.
    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    // Métodos para controlar o estado da conta.
    // Em produção, é interessante ter lógica de bloqueio de conta.
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
