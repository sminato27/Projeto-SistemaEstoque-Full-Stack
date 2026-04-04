package com.sminato.sistemaestoque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
/*
    @RestControllerAdvice = intercepta exceções lançadas em qualquer
    Controller da aplicação e permite tratá-las de forma centralizada.

    Serve para controlar exatamente qual JSON de erro retornar para cada situação.
    Qualquer exceção que não foi tratada no código cai aqui.
*/
public class GlobalExceptionHandler {

    /*
       Trata o caso de produto não encontrado.
       @ExceptionHandler(ProdutoNotFoundException.class) = "quando essa
       exceção for lançada em qualquer lugar, executa esse mét0do"
       Retorna 404 Not Found com uma mensagem clara em JSON.
    */
    @ExceptionHandler(ProdutoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProdutoNotFound(ProdutoNotFoundException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", 404);
        erro.put("erro", "Produto não encontrado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /*
       Trata erros de validação (@NotBlank, @Positive, etc.)
       Quando @Valid falha, o Spring lança MethodArgumentNotValidException.
       Retorna 400 Bad Request com os erros de cada campo.
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Coleta todos os erros de validação: campo -> mensagem de erro
        Map<String, String> errosCampos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                errosCampos.put(erro.getField(), erro.getDefaultMessage()));

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", LocalDateTime.now());
        resposta.put("status", 400);
        resposta.put("erro", "Dados inválidos");
        resposta.put("campos", errosCampos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    /*
       Handler genérico — captura qualquer exceção inesperada.
       É o último recurso. Em vez de o usuário ver uma página de erro técnica e feia, ele recebe um JSON limpo.
    */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", 500);
        erro.put("erro", "Erro interno do servidor");
        erro.put("mensagem", "Algo inesperado aconteceu. Tente novamente mais tarde.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

}
