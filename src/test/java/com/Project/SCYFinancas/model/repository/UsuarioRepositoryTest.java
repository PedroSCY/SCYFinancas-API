package com.Project.SCYFinancas.model.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Project.SCYFinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveVerificarExistenciaDeEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//ação/ execução
		boolean result = repository.existsByEmail("usuario@user.com");
		
		//verificação
		assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
//		acao
		boolean result = repository.existsByEmail("usuario@user.com");
		
		assertThat(result).isFalse();
		
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		//Cenario
		Usuario usuario = criarUsuario();
		
		//Ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		//Verificação		
		assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarUsuarioPorEmail() {
		//Cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);		
		
		//Ação
		Optional<Usuario> usuarioRetornado = repository.findByEmail("usuario@user.com");

		//Verificação	
		assertThat(usuarioRetornado.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioBuscarUsuarioPorEmailQuandoNãoExisteNoBanco() {
			
		//Ação
		Optional<Usuario> usuarioRetornado = repository.findByEmail("usuario@user.com");

		//Verificação	
		assertThat(usuarioRetornado.isPresent()).isFalse();
		
	}
	
	public static Usuario criarUsuario() {
		return  Usuario
				.builder()
				.nome("Usuario")
				.email("usuario@user.com")
				.senha("senha")
				.build();
	}
	
}
