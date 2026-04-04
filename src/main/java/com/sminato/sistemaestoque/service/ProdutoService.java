package com.sminato.sistemaestoque.service;

import com.sminato.sistemaestoque.dto.ProdutoRequestDTO;
import com.sminato.sistemaestoque.dto.ProdutoResponseDTO;
import com.sminato.sistemaestoque.entity.Produto;
import com.sminato.sistemaestoque.exception.ProdutoNotFoundException;
import com.sminato.sistemaestoque.repository.IProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/*   O service é a camada de regras de negócio, onde recebe a RequestDTO (ProdutoRequestDTO),
    converte para Entity, faz a regra de negócio e converte de volta para ResponseDTO (ProdutoResponseDTO).
*/
public class ProdutoService {

    private final IProdutoRepository produtoRepository;

    public ProdutoService(IProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(ProdutoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
        return new ProdutoResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO request) {
        // Converte DTO -> Entity
        Produto produto = new Produto();

        // Faz as alterações conforme o request
        produto.setNome(request.getNome());
        produto.setPreco(request.getPreco());
        produto.setQuantidade(request.getQuantidade());
        produto.setDescricao(request.getDescricao());

        // Salva no database
        Produto produtoSalvo = produtoRepository.save(produto);

        // Converte Entity -> ResponseDTO (ProdutoResponseDTO) e retorna
        return new ProdutoResponseDTO(produtoSalvo);
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));

        produto.setNome(request.getNome());
        produto.setPreco(request.getPreco());
        produto.setQuantidade(request.getQuantidade());
        produto.setDescricao(request.getDescricao());

        Produto produtoAtualizado = produtoRepository.save(produto);

        return new ProdutoResponseDTO(produtoAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if(!produtoRepository.existsById(id)) {
            throw new ProdutoNotFoundException(id);
        }
        produtoRepository.deleteById(id);
    }
}
