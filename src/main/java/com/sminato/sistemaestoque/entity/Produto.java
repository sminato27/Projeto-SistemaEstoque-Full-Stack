package com.sminato.sistemaestoque.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name = "produto_nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "produto_preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "produto_quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "produto_descricao", nullable = true, length = 255)
    private String descricao;

}
