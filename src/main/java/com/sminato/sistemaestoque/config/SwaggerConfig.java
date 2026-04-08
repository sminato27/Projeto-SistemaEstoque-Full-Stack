package com.sminato.sistemaestoque.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/*
    @OpenAPIDefition = configura as informações gerais da documentação.
    info = metadados da API (título, versão, descrição, contato)
    security = diz que todos os endpoints usam o esquema "bearerAuth" por padrão
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de Estoque API",
                version = "1.0",
                description = "API REST para gerenciamento de estoque de produtos." +
                        "Para usar endpoints protegidos: faça login em /api/auth/login, " +
                        "copie o token retornado e cole no botão 'Authorize' acima.",
                contact = @Contact(
                        name = "Guilherme A. Paim",
                        url = "https://github.com/sminato27"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
/*
    @SecurityScheme = define como a autenticação funciona no Swagger.
    name = "bearerAuth" > nome interno, referenciado em @SecurityRequirement
    type = HTTP > tipo de esquema (HTTP, APIKEY, OAUTH2, etc.)
    scheme = "bearer" > usa o scheme Bearer (padrão para JWT)
    bearerFormat = "JWT" > documentação: diz que o bearer é um JWT
    in = HEADER > o token vai no header da requisição

    Isso faz aparecer o botão "Authorize" no Swagger UI, onde é colado o token e ele é enviado em todas as chamadas.
 */
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Cole aqui seu token JWT retornado pelo endpoint de login. " +
                "Não precisa digitar 'Bearer', o Swagger adicionar automaticamente"
)

@Configuration
public class SwaggerConfig {
    // A configuração é toda feita pelas annotations.
}