package com.Project.SCYFinancas.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project.SCYFinancas.api.dto.LancamentoDTO;
import com.Project.SCYFinancas.exceptions.RegraDeNegocioException;
import com.Project.SCYFinancas.model.entity.Lancamento;
import com.Project.SCYFinancas.model.entity.Usuario;
import com.Project.SCYFinancas.model.enums.StatusLancamento;
import com.Project.SCYFinancas.model.enums.TipoLancamento;
import com.Project.SCYFinancas.service.LancamentoService;
import com.Project.SCYFinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

	private LancamentoService service;
	private UsuarioService usuarioService;
	
	public LancamentoController(LancamentoService service) {
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDTO tdo) {
		try {
			Lancamento entidade = converter(tdo);
			entidade = service.salvar(entidade);
			return ResponseEntity.ok(entidade);
		} catch (RegraDeNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	private ResponseEntity atualizar(@ PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return  service.obterPorId(id).map( entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraDeNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado no bando de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar( @PathVariable("id") Long id) {
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> 
			new ResponseEntity("Lançamento não encontrado no bando de dados.", HttpStatus.BAD_REQUEST));
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
		.obterPorId(dto.getUsuario())
		.orElseThrow(() -> new RegraDeNegocioException("Usuario não encontrado para o ID informado." ));
		
		lancamento.setUsuario(usuario);
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		
		return lancamento;
	}
	
}
