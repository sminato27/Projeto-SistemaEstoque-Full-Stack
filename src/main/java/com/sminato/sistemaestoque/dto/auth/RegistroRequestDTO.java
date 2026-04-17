package com.sminato.sistemaestoque.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    Dados necessários para cadastrar um novo usuário.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegistroRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, min = 2, message = "O nome deve ter no mínimo 2 e no máximo 100 caractéres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    private String email;


    // A senha que o usuário digita chega em texto puro. No Service é feito o hash antes de salvar no banco.
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caractéres")
    private String senha;
}
