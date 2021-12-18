package com.Project.SCYFinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Project.SCYFinancas.exceptions.ErroAutenticacao;
import com.Project.SCYFinancas.exceptions.RegraDeNegocioException;
import com.Project.SCYFinancas.model.entity.Usuario;
import com.Project.SCYFinancas.model.repository.UsuarioRepository;
import com.Project.SCYFinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
//	@BeforeEach
//	public void setUp() {
//		/**
//		 * Mockando o UsuarioRepository para simular uma instancia 
//		 * do banco sem envolver a integração com o proprio.
//		 */
//		//repository = Mockito.mock(UsuarioRepository.class); PODE SER SUBSTITUIDO POR @MOCKBEAN
//		
//		/**
//		 * Criando um Spy do UsuarioService pra usar os metodos originais por padrão.
//		 */
//		//Mockito.spy(UsuarioServiceImpl.class); PODE SER SUBSTITUIDO POR @SPYBEAN
//		
//		/**
//		 * O Spring entende que o repository mockado já esta dentro do contexto, 
//		 * e o usará na injeção de dependencia do Spy-service.
//		 */
//		//service = new UsuarioServiceImpl(repository);
//	}
	
	@Test
	public void deveSalvarOUsuario() {
		
		assertDoesNotThrow(()-> {
			
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			
			Usuario usuario = Usuario.builder()
					.id(1l)
					.nome("usuario")
					.email("user@user.com")
					.senha("senha")
					.build();
			
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
			
			Usuario usuarioRetornado = service.salvarUsuario(new Usuario());
			
			assertThat(usuarioRetornado).isNotNull();
			assertEquals(usuarioRetornado.getId(), 1l);
			assertEquals(usuarioRetornado.getNome(), "usuario");
			assertEquals(usuarioRetornado.getEmail(), "user@user.com");
			assertEquals(usuarioRetornado.getSenha(), "senha");
			
		});
		
	}
	
	
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		assertThrows(RegraDeNegocioException.class, ()-> {
			
			String email = "usuario@user.com";
			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraDeNegocioException.class).when(service).validarEmail(email);
			
			service.salvarUsuario(usuario);
			
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});
		
		
	}
	
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		
		assertDoesNotThrow(()-> {
			
			String email = "usuario@user.com";
			String senha = "senha";
			
			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
			
			Usuario usuarioRecebido = service.autenticar(email, senha);
			
			assertThat(usuarioRecebido).isNotNull();
			
		});
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUmUsuarioComEmailInformado() {
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = catchThrowable(() -> service.autenticar("usuario@user.com", "senha") );
		assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado");
	}
	
	@Test
	public void deveLancarErroQuandoASenhaNaoBater() {
		
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("user@user.com").senha(senha).id(1l).build();
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable exception = catchThrowable(() -> service.autenticar("user@user.com", "SenhaErrada"));
		assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha incorreta");
		
	}
	
	@Test
	public void deveValidarEmail() {
		
		assertDoesNotThrow(() -> {
			
			//CENARIO
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);			
			

			//ACAO
			service.validarEmail("email@email.com");
			
		});
		
	}
	
	@Test
	public void deveLancarErroQuandohouverEmailCadastrado() {
		
		assertThrows( RegraDeNegocioException.class , () -> {

			//CENARIO
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
			
			//ACAO
			service.validarEmail("email@email.com");
		});
	}
	
}
