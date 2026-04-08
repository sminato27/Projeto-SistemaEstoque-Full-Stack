package com.sminato.sistemaestoque.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
    O que vai ser retornado após loin ou registro bem-sucedido.
    O front vai guardar esse token e usar em todas as próximas requisições.
 */
@Getter
@AllArgsConstructor
public class AuthResponseDTO {
    /*
        O token JWT gerado.
        Authorization: Bearer eyJhGci...
     */
    private String token;

    /*
        Tempo de expiração em milissegundos.
        O front pode usar isso para saber quando o token vai expirar e fazer refresh antes disso.
     */
    private Long expiracao;

    private String email;
    private String nome;
}
