package com.sminato.sistemaestoque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SistemaEstoqueApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verifica se o contexto da aplicação Spring Boot carrega com sucesso.
		// Se houver algum erro de injeção de dependência ou configuração, o contexto não subirá e o teste falhará.
		assertThat(applicationContext).isNotNull();
	}

}
