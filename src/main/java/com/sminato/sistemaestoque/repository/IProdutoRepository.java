package com.sminato.sistemaestoque.repository;

import com.sminato.sistemaestoque.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProdutoRepository extends JpaRepository<Produto, Long> {

    /*
       Query Methods — o Spring Data cria a query pelo nome do método.

       findByNomeContainingIgnoreCase:
       - findBy         = SELECT ... WHERE
       - Nome           = campo "nome" da entidade
       - Containing     = LIKE '%valor%'
       - IgnoreCase     = sem diferenciar maiúsculas/minúsculas

       SQL gerado automaticamente:
       SELECT * FROM produto WHERE LOWER(produto_nome) LIKE LOWER('%?%')

       Útil para uma funcionalidade de busca por nome na tela.
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    /*
       Verifica se já existe um produto com esse nome exato.
       Útil para evitar duplicatas ao criar um produto.

       SQL gerado: SELECT COUNT(*) > 0 FROM produto WHERE produto_nome = ?
     */
    boolean existsByNomeIgnoreCase(String nome);
}
