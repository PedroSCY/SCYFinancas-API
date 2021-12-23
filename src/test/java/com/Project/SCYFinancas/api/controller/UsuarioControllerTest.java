package com.Project.SCYFinancas.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.Project.SCYFinancas.api.dto.UsuarioDTO;
import com.Project.SCYFinancas.exceptions.ErroAutenticacao;
import com.Project.SCYFinancas.exceptions.RegraDeNegocioException;
import com.Project.SCYFinancas.model.entity.Usuario;
import com.Project.SCYFinancas.service.LancamentoService;
import com.Project.SCYFinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

	
	private static final String API = "/api/usuarios";
	private static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UsuarioService service;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		String email = "usuario@user.com";
		String senha = "senha";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
		.andExpect( MockMvcResultMatchers.status().isOk())
		.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		String email = "usuario@user.com";
		String senha = "senha";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
		.andExpect( MockMvcResultMatchers.status().isBadRequest());
		
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		String email = "usuario@user.com";
		String senha = "senha";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		when(service.salvarUsuario(any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API)
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
		.andExpect( MockMvcResultMatchers.status().isCreated())
		.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
		String email = "usuario@user.com";
		String senha = "senha";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		when(service.salvarUsuario(any(Usuario.class))).thenThrow(RegraDeNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API)
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
		.andExpect( MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveObterOSaldoDeUmUsuario() throws Exception {
		
		BigDecimal saldo = BigDecimal.valueOf(10);
		Usuario usuario = Usuario.builder().id(1l).email("usuario@user.com").senha( "senha").build();
		when(service.obterPorId(1L)).thenReturn(Optional.of(usuario));
		when(lancamentoService.obterSaldoPorUsuario(1l)).thenReturn(saldo);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get(API.concat("/1/saldo"))
													.accept(JSON)
													.contentType(JSON);
				
		mvc
		.perform(request)
		.andExpect( MockMvcResultMatchers.status().isOk())
		.andExpect( MockMvcResultMatchers.content().string("10"));
		
	}
	
	@Test
	public void deveRetornarNotFoundQuandoOUsuarioNaoExistirParaPegarOSaldo() throws Exception {
		
	when(service.obterPorId(1l)).thenReturn(Optional.empty());
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get( API.concat("/1/saldo")  )
													.accept( JSON )
													.contentType( JSON );
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isNotFound() );
	}
	
	
}
