package com.Project.SCYFinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.Project.SCYFinancas.model.entity.Lancamento;
import com.Project.SCYFinancas.model.enums.StatusLancamento;

public interface LancamentoService {

	public Lancamento salvar (Lancamento lancamento);
	
	public Lancamento atualizar (Lancamento lancamento);
	
	public void deletar (Lancamento lancamento);
	
	public List<Lancamento> buscar( Lancamento lancamentoFiltro);
	
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	public void validar(Lancamento lancamento);
	
	public Optional<Lancamento> obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
	
}
