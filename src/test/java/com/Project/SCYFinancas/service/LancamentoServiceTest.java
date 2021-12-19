package com.Project.SCYFinancas.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Project.SCYFinancas.exceptions.RegraDeNegocioException;
import com.Project.SCYFinancas.model.entity.Lancamento;
import com.Project.SCYFinancas.model.entity.Usuario;
import com.Project.SCYFinancas.model.enums.StatusLancamento;
import com.Project.SCYFinancas.model.enums.TipoLancamento;
import com.Project.SCYFinancas.model.repository.LancamentoRepository;
import com.Project.SCYFinancas.model.repository.LancamentoRepositoryTest;
import com.Project.SCYFinancas.service.impl.LancamentoServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmlancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamento);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		when(repository.save(lancamento)).thenReturn(lancamentoSalvo);
		
		Lancamento lancamentoRetornado = service.salvar(lancamento);
		
		assertEquals(lancamentoRetornado.getId(), lancamentoSalvo.getId());
		assertThat(lancamentoRetornado.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarQuandoHouverErroDeValidacao() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		doThrow(RegraDeNegocioException.class).when(service).validar(lancamento);
		
		catchThrowableOfType(() -> service.salvar(lancamento), RegraDeNegocioException.class);
		
		verify(repository, never()).save(lancamento);

	}
	
	@Test
	public void deveAtualizarLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		doNothing().when(service).validar(lancamento);
	
		when(repository.save(lancamento)).thenReturn(lancamento);
		
		service.atualizar(lancamento);
		
		verify(repository, times(1)).save(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoNaoSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		
		verify(repository, never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		assertDoesNotThrow(() -> {
			Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
			lancamento.setId(1l);
			
			service.deletar(lancamento);
			
			verify(repository).delete(lancamento);
		});
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoNaoSalvo() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(() -> service.deletar(lancamento) , NullPointerException.class);
		
		verify(repository, never()).delete(lancamento);
		
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lancamentos = Arrays.asList(lancamento);
		
		when(repository.findAll(any(Example.class))).thenReturn(lancamentos);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		assertThat(resultado)
		.isNotEmpty()
		.hasSize(1)
		.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {	
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		assertEquals(lancamento.getStatus(), novoStatus);
		verify(service).atualizar(lancamento);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> lancamentoRecebido = service.obterPorId(id);
		
		assertTrue(lancamentoRecebido.isPresent());
		assertEquals(lancamento.getId(), id);
		verify(service).obterPorId(id);
		
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaExiste() {
		
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> lancamentoRecebido = service.obterPorId(id);
		
		assertFalse(lancamentoRecebido.isPresent());
		verify(service).obterPorId(id);
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		String[] erros = {
				"Informe uma Descrição válida",
				"Informe um Mês válido",
				"Informe um Ano válido",
				"Informe um Usuário",
				"Informe um Valor válido",
				"Informe um Tipo de Lançamento"
		};
		
		int testeErro = 0;
		for(int i = 0; i< 12 ; i++) {
			
			Throwable erro = catchThrowable(() -> service.validar(lancamento));
			assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage(erros[testeErro]);
			
			switch (i) {
			case 0:
				lancamento.setDescricao("");
				break;
			case 1:
				lancamento.setDescricao("Descricao");
				testeErro++;
				break;
			case 2:
				lancamento.setMes(0);
				break;
			case 3:
				lancamento.setMes(13);
				break;
			case 4:
				lancamento.setMes(1);
				testeErro++;
				break;
			case 5:
				lancamento.setAno(123);
				break;
			case 6:
				lancamento.setAno(2021);
				testeErro++;
				break;
			case 7:
				lancamento.setUsuario(new Usuario());
				break;
			case 8:
				lancamento.getUsuario().setId(1l);
				testeErro++;
				break;
			case 9:
				lancamento.setValor(BigDecimal.ZERO);
				break;
			case 10:
				lancamento.setValor(BigDecimal.valueOf(1));
				testeErro++;
				break;
			} 
		}

	}
	
}
