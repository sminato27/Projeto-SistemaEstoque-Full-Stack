package com.sminato.sistemaestoque.controller;

import com.sminato.sistemaestoque.dto.ProdutoRequestDTO;
import com.sminato.sistemaestoque.dto.ProdutoResponseDTO;
import com.sminato.sistemaestoque.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Produtos", description = "CRUD de produtos do estoque")
@RestController
@RequestMapping("/api/produtos")
// O Controller é a camada de apresentação que trabalha apenas com as DTOs (Request e Response)
public class ProdutoController {

    // O Controller depende e injeta o Service.
    // O Controller não acessa o Repository diretamente.
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // GET /api/produtos > lista todos os produtos
    // ResponseEntity = controle total sobre a resposta HTTP
    // List<ProdutoResponseDTO> = o corpo da resposta é uma lista de produtos
    // ResponseEntity.ok() = status 200 OK
    @Operation(summary = "Listar todos os produtos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    })
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/5 > busca o produto com ID 5
    // @PathVariable = captura o {id} da URL
    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    // POST /api/produtos > cria um novo produto
    // @RequestBody = o corpo da requisição HTTP (JSON) é convertido automaticamente para um objeto ProdutoRequestDTO
    // @Valid = dispara as validações do ProdutoRequestDTO (@NotBlanc, etc...). Se inválido, retorna 400 antes de chegar no mét0do.
    // ResponseEntity.status(HttpStatus.CREATED).body(produto):
    // - 201 Created = código HTTP correto para criação de recurso
    // - Retorna o produto criado (com ID) no corpo da resposta
    @Operation(
            summary = "Criar novo produto",
            description = "Requer autenticação JWT. Cria um produto no estoque."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "401", description = "Não autenticado. Faça login primeiro."),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para fazer isso.")
    })
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO request) {
        ProdutoResponseDTO produto = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    // PUT /api/produtos/5 > atualiza o produto com id 5
    // Recebe o ID pela URL e os novos dados pelo corpo (JSON)
    // Retorna 200 OK com o produto atualizado
    @Operation(
            summary = "Atualizar produto existente",
            description = "Requer autenticação JWT. Atualiza um produto do estoque.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "401", description = "Não autenticado. Faça login primeiro."),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para fazer isso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO request) {
        ProdutoResponseDTO produto = produtoService.atualizar(id, request);
        return ResponseEntity.ok(produto);
    }

    // DELETE /api/produto/5 > deleta o produto com id 5
    // ReponseEntity<Void> = a resposta não tem corpo (Void)
    // 204 No Content = código HTTP correto para deleção bem-sucedida: "deu certo, mas não tenho nada pra te retornar"
    @Operation(
            summary = "Deletar produto pelo ID",
            description = "Requer autenticação JWT. Deleta um produto do estoque."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autenticado. Faça login primeiro."),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para fazer isso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
