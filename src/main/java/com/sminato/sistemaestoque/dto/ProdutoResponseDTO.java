package com.sminato.sistemaestoque.dto;

import com.sminato.sistemaestoque.entity.Produto;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer quantidade;
    private String descricao;

    public ProdutoResponseDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.preco = produto.getPreco();
        this.quantidade = produto.getQuantidade();
        this.descricao = produto.getDescricao();
    }

}
