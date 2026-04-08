package com.sminato.sistemaestoque.repository;

import com.sminato.sistemaestoque.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    /*
        O Spring Security vai chamar esse métod0 para buscar o usuário pelo email.
        Query Method gerado automaticamente: SELECT * FROM usuario WHERE usuario_email = ?
        Retorna Optional porque o usuário pode não existir.
     */
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
