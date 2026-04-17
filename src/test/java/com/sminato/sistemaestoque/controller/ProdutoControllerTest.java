package com.sminato.sistemaestoque.controller;

import com.sminato.sistemaestoque.dto.ProdutoRequestDTO;
import com.sminato.sistemaestoque.dto.ProdutoResponseDTO;
import com.sminato.sistemaestoque.entity.Produto;
import com.sminato.sistemaestoque.exception.GlobalExceptionHandler;
import com.sminato.sistemaestoque.exception.ProdutoNotFoundException;
import com.sminato.sistemaestoque.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sminato.sistemaestoque.service.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProdutoController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
/*
    @WebMvcTest = sobe apenas a camada web do Spring. Ideal para testar endpoints HTTP de forma isolada.
 */
public class ProdutoControllerTest {

    /*
        MockMvc = simula requisições HTTP sem precisar de um servidor real.
        GET, POST, PUT, DELETE e verica respostas.

        @Autowired = Spring injeta automaticamente.
     */
    @Autowired
    private MockMvc mockMvc;

    /*
        ObjectMapper = converte objetos Java para JSON (e vice versa).
        Nesse caso, utilizado para converter o ProdutoRequestDTO para JSON e mandar no corpo do request.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /*
        @MockitoBean = cria um mock do ProdutoService e o registra no contexto do Spring. O Controller vai receber esse
        mock no lugar do Service real.

        @Mock = Mockito puro, fora do Spring.
        @MockitoBean = mock integrado ao contexto do Spring. Utilizado quando o Spring precisa gerenciar a injeção.
     */
    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    // Dados reutilizáveis
    private ProdutoResponseDTO responseDTO;
    private ProdutoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Produto fictício para o Service retornar
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Mouse");
        produto.setPreco(new BigDecimal("149.99"));
        produto.setQuantidade(10);
        produto.setDescricao("Mouse Gamer com RGB");

        responseDTO = new ProdutoResponseDTO(produto);
        requestDTO = new ProdutoRequestDTO();

        requestDTO.setNome("Mouse");
        requestDTO.setPreco(new BigDecimal("149.99"));
        requestDTO.setQuantidade(10);
        requestDTO.setDescricao("Mouse Gamer com RGB");

    }

    // Testes do GET /api/produtos
    @Test
    @DisplayName("GET /api/produtos - deve retornar 200 e lista de produtos")
    void listarTodos_deveRetornar200ComListaDeProdutos() throws Exception {
        // Arrange
        when(produtoService.listarTodos()).thenReturn(List.of(responseDTO));

        // Act + Assert
        /*
            mockMvc.perform() = executa a requisição HTTP simulada
            get("/api/produtos") = simula um GET nessa URL
            .andExpect() = verifica condições da resposta
            status().isOk() = verifica que o statos é 200 OK
            content().contentType(MediaType.APPLICATION_JSON) = verifica que a resposta é JSON
            jsonPath("$") = acessa o corpo JSON
            "$" = raíz do JSON
            "$[0].nome" = primeiro elemento, campo "nome"
         */
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Mouse"))
                .andExpect(jsonPath("$[0].preco").value(149.99))
                .andExpect(jsonPath("$[0].quantidade").value(10));
    }

    @Test
    @DisplayName("GET /api/produtos - deve retornar 200 e lista vazia")
    void listarTodos_deveRetornar200ComListaVazia() throws Exception {
        // Arrange
        when(produtoService.listarTodos()).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // Testes do GET /api/produtos/{id}
    @Test
    @DisplayName("GET /api/produtos/1 - deve retornar 200 e produto")
    void buscarPorId_deveRetornar200QuandoProdutoExiste() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(1L)).thenReturn(responseDTO);

        // Act + Assert
        mockMvc.perform(get("/api/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Mouse"));
    }

    @Test
    @DisplayName("GET /api/produtos/99 - deve retornar 404 quando produto não existe")
    void buscarPorId_deveRetornar404QuandoProdutoNaoExiste() throws Exception {
        // Arrange
        /*
            Quando buscarPorId(99) for chamado, lance a exceção.
            O GlobalExceptionHandler vai capturar e retornar 404.
         */
        when(produtoService.buscarPorId(99L)).thenThrow(new ProdutoNotFoundException(99L));

        // Act + Assert
        mockMvc.perform(get("/api/produtos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto com id 99 não encontrado."));
    }

    // Testes do POST /api/produtos
    @Test
    @DisplayName("POST /api/produtos - deve retornar 201 e produto criado")
    void criar_deveRetornar201ComProdutoCriado() throws Exception {
        when(produtoService.criar(any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        // objectMapper.writeValueAsString() = converte o requestDTO para JSON.
        String json = objectMapper.writeValueAsString(requestDTO);

        /*
            .contentType(MediaType.APPLICATION_JSON) = diz que está mandando JSON no corpo (o Content-Type do header HTTP)
            .content(json) = o corpo da requisição
         */
        mockMvc.perform(post("/api/produtos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Mouse"));
    }

    @Test
    @DisplayName("POST /api/produtos - deve retornar 400 quando preço é negativo")
    void criar_deveRetornar400QuandoPrecoNegativo() throws Exception {
        requestDTO.setPreco(new BigDecimal("-10.00")); // Viola @Positive

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/produtos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.preco").exists());
    }

    // Testes do PUT /api/produtos/{id}
    @Test
    @DisplayName("PUT /api/produtos/1 - deve retornar 200 com produto atualizado")
    void atualizar_deveRetornar200ComProdutoAtualizado() throws Exception {
        /*
            eq(1L) = "equal" - o primeiro argumento deve ser exatamente 1L.
            Usa-se eq() pra ser específico sobre um argumento e any() para os demais.
         */
        when(produtoService.atualizar(eq(1L), any(ProdutoRequestDTO.class)))
                .thenReturn(responseDTO);

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(put("/api/produtos/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mouse"));
    }

    @Test
    @DisplayName("PUT /api/produtos/99 - deve retornar 404 quando produto não existe")
    void atualizar_deveAtualizar404QuandoProdutoNaoExiste() throws Exception {
        when(produtoService.atualizar(eq(99L), any(ProdutoRequestDTO.class)))
                .thenThrow(new ProdutoNotFoundException(99L));

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(put("/api/produtos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // Testes do DELETE /api/produtos/{id}
    @Test
    @DisplayName("DELETE /api/produtos/1 - deve retornar 204 quando deletado")
    void deletar_deveRetornar204QuandoDeletado() throws Exception {
        // deletar() é void - doNothing() para métod0s void
        doNothing().when(produtoService).deletar(1L);

        mockMvc.perform(delete("/api/produtos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/produtos/99 - deve retornar 404 quando produto não existe")
    void deletar_deveRetornar404QuandoProdutoNaoExiste() throws Exception {
        /*
            doThrow() = para métod0s void que devem lançar exceção. Não usar when().thenThrow() em mét0dos void.
         */
        doThrow(new ProdutoNotFoundException(99L))
                .when(produtoService).deletar(99L);

        mockMvc.perform(delete("/api/produtos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
