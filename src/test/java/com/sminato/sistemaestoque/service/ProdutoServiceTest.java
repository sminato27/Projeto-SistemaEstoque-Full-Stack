package com.sminato.sistemaestoque.service;

import com.sminato.sistemaestoque.dto.ProdutoRequestDTO;
import com.sminato.sistemaestoque.dto.ProdutoResponseDTO;
import com.sminato.sistemaestoque.entity.Produto;
import com.sminato.sistemaestoque.exception.ProdutoNotFoundException;
import com.sminato.sistemaestoque.repository.IProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/*
    @ExtendWith(MockitoExtension.class) = Diz ao JUnit para usar o Mockito como extensão.
    Isso ativa as anotações @Mock e @InjectMocks.
 */
@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    /*
        @Mock = Cria um objeto falso no IProdutoRepository.
        Esse objeto não faz nada por conta própria.
        O comportamento é programado em cada teste.
     */
    @Mock
    private IProdutoRepository produtoRepository;

    /*
        @InjectMocks = Cria uma instância real do ProdutoService e injeta automaticamente os @Mocks declarados acima.
        Nesse caso, o Service é rea, o Repository é falso, então o teste é só o comportamento do Service.
     */
    @InjectMocks
    private ProdutoService produtoService;

    /*
        Declaração dos objetos que serão utilizados nos testes.
     */
    private Produto produto;
    private ProdutoRequestDTO request;

    /*
        @BeforeEach = Executa antes de cada teste. Bom para não ficar repetindo dados.
     */
    @BeforeEach
    void setUp() {
        // Criação de um produto fictício:
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto de teste Mock");
        produto.setPreco(new BigDecimal("299.90"));
        produto.setQuantidade(20);
        produto.setDescricao("Produto carão só pra teste");

        // Criação de um request fictício que simula o que o frontend enviaria:
        request = new ProdutoRequestDTO();
        request.setNome("Produto de teste Mock");
        request.setPreco(new BigDecimal("299.90"));
        request.setQuantidade(20);
        request.setDescricao("Produto carão só pra teste");
    }

    /*
        Estrutura de um teste: Arrange > Act > Assert
        - Arrange: configura o cenário
        - Act: executa o que está sendo testado
        - Assert: confirma que o resultado é o esperado
     */
    @Test
    @DisplayName("Deve retornar uma lista com todos os produtos")
    void listarTodos_deveRetornarListaDeProdutos() {
        // Arrange
        /*
            when(...).thenReturn(...): programa o mock. "Quando o repository chamar findAll(), retorne essa lista."
         */
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        // Act
        /*
            Chama o mét0do real do Service (que vai usar o mock interno)
         */
        List<ProdutoResponseDTO> resultado = produtoService.listarTodos();

        // Assert
        /*
            Verifica se o resultado não é nulo e tem tamanho 1.
            Verifica se o objeto na posição 0 tem nome igual a "Produto de teste Mock".
            Verifica se o objeto na posição 0 tem preço igual a "299.90".
         */
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Produto de teste Mock");
        assertThat(resultado.get(0).getPreco()).isEqualByComparingTo("299.90");

        /*
            verify = Confirma que o mock foi chamado do jeito esperado.
            "Verifique que findAll() foi chamado exatamente 1x."

            Serve pra garantir que o Service realmente usou o Repository.
         */
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há produtos")
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaProdutos() {
        // Arrange
        when(produtoRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProdutoResponseDTO> resultado = produtoService.listarTodos();

        // Assert
        // Verifica se o resultado não é nulo e se está vazio.
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar produto quando ID existe")
    void buscarPorId_deveRetornarProdutoQuandoIdExiste() {
        // Arrange
        /*
            findById retorna Optional<Produto>.
            Optional.of(produto) = "existe um produto aqui dentro"
         */
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act
        ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

        // Assert
        /*
            Verifica se o resultado não é nulo, se o ID é igual a 1L e se o nome é igual a "Produto de teste Mock".
         */
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Produto de teste Mock");

        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ProdutoNotFoundException quando ID não existe")
    void buscarPorId_deveLancarExcecaoQuandoIdNaoExiste(){
        // Arrange
        /*
            Optional.empty() = "não tem nada aqui"
            Simula o banco não encontrando o produto.
         */
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert + Act juntos com assertThatThrownBy
        /*
            assertThatThrownBy = Verifica que o bloco de código lança uma exceção.
            "Verifique que ao chamar buscarPorId(99), uma ProdutoNotFoundException é lançada com essa mensagem"
         */
        assertThatThrownBy(() -> produtoService.buscarPorId(99L))
                .isInstanceOf(ProdutoNotFoundException.class)
                .hasMessageContaining("99");

        verify(produtoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar produto e retornar response com ID gerado")
    void criar_deveSalvarERetornarProdutoCriado() {
        // Arrange
        /*
            any(Produto.class) = Um "coringa" do Mockito.
            "Quando save() for chamado com qualquer objeto Produto, retorne o produto fictício.

            Usa-se any() porque no Service, criamos um new Produto() e não tem acesso a essa instância exata no teste.
         */
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Produto de teste Mock");
        assertThat(resultado.getPreco()).isEqualByComparingTo("299.90");
        assertThat(resultado.getQuantidade()).isEqualTo(20);

        // verify sem times() = Verifica que foi chamado pelo menos uma vez. Igual a times(1).
        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto existente e retornar dados atualizados")
    void atualizar_deveAtualizarERetornarProdutoAtualizado() {
        // Arrange
        ProdutoRequestDTO requestAtualizado = new ProdutoRequestDTO();
        requestAtualizado.setNome("Mouse Gamer");
        requestAtualizado.setPreco(new BigDecimal("199.90"));
        requestAtualizado.setQuantidade(5);

        // Produto que vai "vir do banco" na busca.
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setId(1L);
        produtoAtualizado.setNome("Mouse Gamer");
        produtoAtualizado.setPreco(new BigDecimal("199.90"));
        produtoAtualizado.setQuantidade(5);

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoAtualizado);

        // Act
        ProdutoResponseDTO resultado = produtoService.atualizar(1L, requestAtualizado);

        // Assert
        assertThat(resultado.getNome()).isEqualTo("Mouse Gamer");
        assertThat(resultado.getPreco()).isEqualByComparingTo("199.90");
        assertThat(resultado.getQuantidade()).isEqualTo(5);

        verify(produtoRepository).findById(1L);
        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar produto inexistente")
    void atualizar_deveLancarExcecaoQuandoProdutoNaoExiste() {
        // Arrange
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> produtoService.atualizar(99L, request))
                .isInstanceOf(ProdutoNotFoundException.class)
                .hasMessageContaining("99");

        // Garante que save() nunca foi chamado (não faz sentido salvar se não achou)
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar produto quando ID existe")
    void deletar_deveDeletarQuandoIdExiste() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(true);

        /*
            doNothing() = Para mét0dos void, sempre usar doNothing().
            "Quando deleteById() for chamado, não faça nada."
         */
        doNothing().when(produtoRepository).deleteById(1L);

        // Act
        produtoService.deletar(1L);

        // Assert
        // Verifica que os dois métodos foram chamados corretamente.
        verify(produtoRepository).existsById(1L);
        verify(produtoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    void deletar_deveLancarExcecaoQuandoIdNaoExiste() {
        // Arrange
        when(produtoRepository.existsById(99L)).thenReturn(false);

        // Assert + Act
        assertThatThrownBy(() -> produtoService.deletar(99L))
                .isInstanceOf(ProdutoNotFoundException.class)
                .hasMessageContaining("99");

        // Garante que deleteById nunca foi chamado (não deve deletar o que não existe)
        verify(produtoRepository, never()).deleteById(any());
    }

}
